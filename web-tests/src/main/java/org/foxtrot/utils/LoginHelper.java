package org.foxtrot.utils;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.api.dto.auth.response.Account;
import org.foxtrot.api.dto.auth.response.SignInResponse;
import org.foxtrot.config.WebConfig;
import org.foxtrot.config.RunChannelResolver;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.pages.left_sidebar.primary_bar.elements.LeftSidebar;
import org.foxtrot.pages.login.PhoneInputPageSteps;
import org.foxtrot.pages.login.SmsCodeModalSteps;
import org.foxtrot.pages.login.TfaModalSteps;
import org.foxtrot.pages.spaces.homespace.primary.HomespaceSteps;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.foxtrot.api.constants.TestConstants.SMS_CODE;
import static org.foxtrot.api.constants.TestConstants.TFA_CODE;

public class LoginHelper {
    private static final Path SESSIONS_DIR = Path.of("target/sessions");

    private final Logger logger = LoggerFactory.getLogger(LoginHelper.class);
    private final WebConfig config = ConfigFactory.create(WebConfig.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String authToken;

    public HomespaceSteps performUILogin(String phoneNumber, String smsCode, String tfaCode) {
        logger.info("Performing UI login with phone number: {}", phoneNumber);
        PhoneInputPageSteps phoneInputPageSteps = new PhoneInputPageSteps();

        if (RunChannelResolver.isDesktop()) {
            SmsCodeModalSteps smsCodePage = new SmsCodeModalSteps();
            submitPhoneUntilSmsScreen(phoneInputPageSteps, smsCodePage, phoneNumber);
            smsCodePage.enterSmsCode(smsCode);
            return completeLoginAfterSms(tfaCode);
        }

        ensureWebLoginScreenReady(phoneInputPageSteps);
        SmsCodeModalSteps smsCodePage = new SmsCodeModalSteps();
        submitPhoneUntilSmsScreenWeb(phoneInputPageSteps, smsCodePage, phoneNumber);
        smsCodePage.enterSmsCode(smsCode);

        return completeLoginAfterSms(tfaCode);
    }

    /**
     * Web: React-форма телефона надёжнее сабмитится кнопкой «Далее», не Enter.
     * Повторный сабмит — только клик, без повторного setValue (иначе дублируется номер).
     */
    private void submitPhoneUntilSmsScreenWeb(
            PhoneInputPageSteps phonePage, SmsCodeModalSteps smsPage, String phoneNumber) {
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            if (attempt == 1) {
                phonePage.enterPhoneNumber(phoneNumber);
            } else {
                logger.warn("Экран SMS не появился (web, попытка {}/{}), повторяю сабмит", attempt, maxAttempts);
                phonePage.resubmitPhoneFormWeb();
            }
            if (smsPage.waitSmsCodeScreen(Duration.ofSeconds(attempt == 1 ? 15 : 20))) {
                return;
            }
        }
        smsPage.checkPageIsLoaded();
    }

    private void ensureWebLoginScreenReady(PhoneInputPageSteps phonePage) {
        if (!RunChannelResolver.isWeb()) {
            return;
        }
        if (phonePage.isLoginScreenReady()) {
            return;
        }
        logger.warn("Форма логина не видна перед UI-логином, переоткрываю {}", config.baseUrl());
        Selenide.open(config.baseUrl());
        phonePage.waitForLoginOrHomespace(Duration.ofSeconds(45));
        if (!phonePage.isLoginScreenReady()) {
            throw new IllegalStateException(
                    "Экран ввода телефона не появился после переоткрытия " + config.baseUrl());
        }
    }

    /** Desktop: после clear storage + refresh ждём экран телефона в Electron, без open(web URL). */
    private void ensureDesktopLoginScreenReady(PhoneInputPageSteps phonePage) {
        if (phonePage.isLoginScreenReady()) {
            return;
        }
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                phonePage.checkPageIsLoaded();
                return;
            } catch (Throwable e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
                logger.warn("Desktop: экран телефона не готов (попытка {}/{}), refresh", attempt, maxAttempts);
                Selenide.refresh();
                Selenide.sleep(1000);
            }
        }
    }

    private HomespaceSteps completeLoginAfterSms(String tfaCode) {
        TfaModalSteps TFAPage = new TfaModalSteps();
        TFAPage.checkInputTFACode()
                .enterTFACode(tfaCode)
                .pressEnter();

        HomespaceSteps homespaceSteps = new HomespaceSteps();
        if (RunChannelResolver.isDesktop()) {
            waitHomespaceLoadedWithRefresh(homespaceSteps);
        } else {
            homespaceSteps.checkPageIsLoaded();
        }

        logger.info("Логин завершен, страница Homespace загружена.");
        FeatureFlagsHelper.applyMenuFeatureFlags();
        org.foxtrot.pages.modals.WebOnboardingOverlay.dismissIfPresent();
        return homespaceSteps;
    }


    /**
     * После финального Enter desktop-клиент иногда «залипает» на промежуточном экране и не переходит на Homespace.
     * Пробуем несколько раз с refresh — каждый checkPageIsLoaded ждёт до 10с (итого до ~50с вместо прежних ~20с,
     * которых по факту хватало лишь на одну попытку без реального ретрая).
     */
    private void waitHomespaceLoadedWithRefresh(HomespaceSteps homespaceSteps) {
        int maxAttempts = 5;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                homespaceSteps.checkPageIsLoaded();
                return;
            } catch (Throwable e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
                logger.warn("Homespace не загрузился (попытка {}/{}), делаю refresh и повторяю", attempt, maxAttempts);
                try {
                    WebDriverRunner.getWebDriver().navigate().refresh();
                } catch (Throwable refreshEx) {
                    logger.warn("Не удалось выполнить refresh: {}", refreshEx.toString());
                }
            }
        }
    }

    /**
     * Иногда после ввода номера pressEnter не срабатывает (флак desktop-клиента).
     */
    private void submitPhoneUntilSmsScreen(PhoneInputPageSteps phonePage, SmsCodeModalSteps smsPage, String phoneNumber) {
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                phonePage.submitPhoneNumber(phoneNumber, attempt > 1);
            } catch (Throwable e) {
                logger.warn("Не удалось ввести номер (попытка {}/{}): {}", attempt, maxAttempts, e.toString());
            }
            if (smsPage.waitSmsCodeScreen(Duration.ofSeconds(30))) {
                return;
            }
            logger.warn("Экран ввода SMS-кода не появился после ввода номера (попытка {}/{})", attempt, maxAttempts);
        }
        smsPage.checkPageIsLoaded();
    }

    @Step("Выполнить UI-логин для пользователя {user}")
    public void performUILogin(UsersEnum user) {
        performUILogin(user.getPhoneNumber(), SMS_CODE, TFA_CODE);
    }

    @Step("Перезайти под пользователем {user} (UI-логин)")
    public void performUILoginForTest(UsersEnum user) {
        if (!WebDriverRunner.hasWebDriverStarted()) {
            Selenide.open(config.baseUrl());
        }
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
        Selenide.executeJavaScript("localStorage.clear();");
        Selenide.refresh();

        PhoneInputPageSteps phonePage = new PhoneInputPageSteps();
        if (RunChannelResolver.isWeb()) {
            ensureWebLoginScreenReady(phonePage);
        } else {
            ensureDesktopLoginScreenReady(phonePage);
        }
        performUILogin(user.getPhoneNumber(), SMS_CODE, TFA_CODE);
    }

    public boolean sessionExists(String phone) {
        Path file = SESSIONS_DIR.resolve("localStorage_" + phone + ".json");
        return Files.exists(file);
    }

    public void saveCredentials(String phone) {
        String fullLocalStorage = Selenide.executeJavaScript("return JSON.stringify(localStorage);");
        if (fullLocalStorage == null || fullLocalStorage.isEmpty()) {
            throw new IllegalStateException("localStorage is empty — auth data not found.");
        }

        try {
            Files.createDirectories(SESSIONS_DIR);
            Path targetFile = SESSIONS_DIR.resolve("localStorage_" + phone + ".json");
            Files.writeString(targetFile, fullLocalStorage);
        } catch (Exception e) {
            logger.warn("Failed to save localStorage to file", e);
        }
    }

    /**
     * Сохраняет только auth и encryptKey — минимальный «чистый» сторедж без лишних ключей (меньше гличей при восстановлении).
     */
    public void saveCredentialsMinimal(String phone) {
        String auth = Selenide.executeJavaScript("return localStorage.getItem('auth');");
        String encryptKey = Selenide.executeJavaScript("return localStorage.getItem('encryptKey');");
        if (auth == null || auth.isEmpty()) {
            throw new IllegalStateException("localStorage.auth is empty — auth data not found.");
        }

        try {
            Files.createDirectories(SESSIONS_DIR);
            Path targetFile = SESSIONS_DIR.resolve("localStorage_" + phone + ".json");
            Map<String, String> minimal = Map.of(
                    "auth", auth,
                    "encryptKey", encryptKey != null && !encryptKey.isEmpty() ? encryptKey : ""
            );
            String json = objectMapper.writeValueAsString(minimal);
            Files.writeString(targetFile, json);
            logger.info("Сохранён минимальный сторедж для {} (auth + encryptKey)", phone);
        } catch (Exception e) {
            logger.warn("Failed to save minimal localStorage to file", e);
        }
    }

    public void restoreAuthSession(UsersEnum user) {
        String savedStorage = loadFromSessions(user.getPhoneNumber());

        Selenide.open(config.baseUrl());

        // Ждем завершения выполнения JS перед переходом дальше
        Selenide.executeAsyncJavaScript(
                """
                        var callback = arguments[arguments.length - 1];
                        var storageData = JSON.parse(arguments[0]);
                        localStorage.clear();
                        ['auth', 'encryptKey'].forEach(k => {
                          if (storageData[k] !== undefined && storageData[k] !== null) {
                            localStorage.setItem(k, storageData[k]);
                          }
                        });
                        callback();
                        """,
                savedStorage
        );

        Selenide.refresh();
        Selenide.open(config.baseUrl() + "/im/home");
        Selenide.refresh(); // дополнительный refresh для стабильной загрузки сайдбара
        try {
            $(new LeftSidebar().getWorkspaceChatsButton()).shouldBe(visible);
            org.foxtrot.pages.modals.WebOnboardingOverlay.dismissIfPresent();
        } catch (Throwable e) {
            dumpBrowserDiagnostics("restoreAuthSession");
            throw e;
        }
    }

    @Step("Переключиться на пользователя {user}")
    public void restoreAuthSessionForTest(UsersEnum user) {
        String savedStorage = loadFromSessions(user.getPhoneNumber());

        Selenide.open(config.baseUrl());

        Selenide.executeJavaScript(
                """
                        var storageData = JSON.parse(arguments[0]);
                        localStorage.clear();
                        ['auth', 'encryptKey'].forEach(k => {
                          if (storageData[k] !== undefined && storageData[k] !== null) {
                            localStorage.setItem(k, storageData[k]);
                          }
                        });
                        """,
                savedStorage
        );

        Selenide.refresh();
        Selenide.open(config.baseUrl() + "/im/home");
        Selenide.refresh(); // дополнительный refresh для стабильной загрузки сайдбара
        try {
            $(new LeftSidebar().getWorkspaceChatsButton()).shouldBe(visible);
            org.foxtrot.pages.modals.WebOnboardingOverlay.dismissIfPresent();
        } catch (Throwable e) {
            dumpBrowserDiagnostics("restoreAuthSessionForTest");
            throw e;
        }
    }

    /**
     * Вход в приложение по свежим данным из API (без файла сессии).
     * Сначала создаются ключи шифрования (getPublicKey → saveSymmetricKey), затем signIn с их id.
     * Куки из ответа signIn устанавливаются в браузере (домен API), затем auth и encryptKey — в localStorage (страница приложения).
     */
    public void restoreAuthSessionFromFresh(UsersEnum user) {
        throw new UnsupportedOperationException(
                "restoreAuthSessionFromFresh() отключён: используем только старую файловую сессию " +
                        "(performUILogin + saveCredentials/saveCredentialsMinimal + restoreAuthSession)."
        );
    }

    private void dumpBrowserDiagnostics(String context) {
        try {
            String url = safe(() -> WebDriverRunner.url());
            String title = safe(() -> WebDriverRunner.getWebDriver().getTitle());
            String readyState = safeJs("return document.readyState");
            String rootHtmlLen = safeJs("return String((document.getElementById('root') && document.getElementById('root').innerHTML || '').length)");
            logger.warn("Browser diagnostics. context={}, url={}, title={}, readyState={}, rootHtmlLen={}",
                    context, url, title, readyState, rootHtmlLen);

            logger.warn("Dumping browser console logs. context={}", context);
            for (LogEntry entry : WebDriverRunner.getWebDriver().manage().logs().get(LogType.BROWSER)) {
                logger.warn("[BROWSER] {} {}", entry.getLevel(), entry.getMessage());
            }

            // Best-effort: show only failed network loads from performance log (often reveals why SPA is blank).
            try {
                for (LogEntry entry : WebDriverRunner.getWebDriver().manage().logs().get(LogType.PERFORMANCE)) {
                    String msg = entry.getMessage();
                    if (msg == null) continue;
                    if (msg.contains("\"Network.loadingFailed\"")
                            || msg.contains("ERR_")
                            || msg.contains("net::")) {
                        logger.warn("[PERF] {}", msg);
                    }
                }
            } catch (Exception perfEx) {
                logger.warn("Failed to dump performance logs. context={}", context, perfEx);
            }
        } catch (Exception ex) {
            logger.warn("Failed to dump browser diagnostics. context={}", context, ex);
        }
    }

    private static String safe(CheckedSupplier supplier) {
        try {
            Object v = supplier.get();
            return v == null ? "null" : String.valueOf(v);
        } catch (Exception e) {
            return "error:" + e.getClass().getSimpleName();
        }
    }

    private static String safeJs(String script) {
        try {
            Object v = Selenide.executeJavaScript(script);
            return v == null ? "null" : String.valueOf(v);
        } catch (Exception e) {
            return "error:" + e.getClass().getSimpleName();
        }
    }

    @FunctionalInterface
    private interface CheckedSupplier {
        Object get() throws Exception;
    }

    /**
     * Подставляет в текущую вкладку полный объект auth (mainUser + tokens + notification) из SignInResponse.
     * Перед вызовом должна быть открыта страница того же origin (например baseUrl).
     */
    public void applySignInResponseToBrowser(SignInResponse data, long encryptionKeyId) {
        if (data == null || data.getAccessToken() == null || data.getAccount() == null) {
            throw new IllegalStateException("SignInResponse, account or accessToken is null");
        }
        try {
            Account acc = data.getAccount();

            Map<String, Object> root = new HashMap<>();
            Map<String, Object> mainUser = new HashMap<>();
            mainUser.put("id", acc.getId());
            mainUser.put("additionalEmails", acc.getAdditionalEmails() != null ? acc.getAdditionalEmails() : List.of());
            mainUser.put("additionalPhones", acc.getAdditionalPhones() != null ? acc.getAdditionalPhones() : List.of());
            mainUser.put("contactDataVisibility", "ALL");
            mainUser.put("email", "");
            mainUser.put("firstName", acc.getFirstName());
            mainUser.put("middleName", acc.getMiddleName() != null ? acc.getMiddleName() : "");
            mainUser.put("lastName", acc.getLastName());
            mainUser.put("jobTitle", acc.getJobTitle() != null ? acc.getJobTitle() : "");
            mainUser.put("photo", photoEmpty());
            mainUser.put("mainAuthWay", acc.getMainAuthWay());
            mainUser.put("phone", acc.getPhone());
            mainUser.put("tfaEnabled", acc.isTfaEnabled());
            mainUser.put("photoHistory", Map.of("items", List.of()));
            mainUser.put("accountSettings", buildFullAccountSettings(acc.getAccountSettings()));
            mainUser.put("initials", buildInitials(acc.getFirstName(), acc.getLastName()));
            mainUser.put("fullName", acc.getDisplayName());

            Map<String, Object> tokens = new HashMap<>();
            tokens.put("accessToken", data.getAccessToken());
            tokens.put("refreshToken", data.getRefreshToken());
            tokens.put("accessTokenValidTill", data.getAccessTokenValidTill());
            tokens.put("encryptionKeyId", encryptionKeyId);

            Map<String, Object> notification = new HashMap<>();
            notification.put("pushToken", "");

            root.put("mainUser", mainUser);
            root.put("tokens", tokens);
            root.put("notification", notification);
            if (data.getWorkspaces() != null && !data.getWorkspaces().isEmpty()) {
                root.put("workspaces", data.getWorkspaces());
            }

            String authJson = objectMapper.writeValueAsString(root);
            Selenide.executeJavaScript("localStorage.setItem('auth', arguments[0]);", authJson);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось собрать auth из SignInResponse", e);
        }
    }

    private static Map<String, Object> photoEmpty() {
        Map<String, Object> photo = new HashMap<>();
        photo.put("id", "");
        photo.put("key", "");
        photo.put("transformation", "");
        photo.put("url", "");
        return photo;
    }

    private static String buildInitials(String firstName, String lastName) {
        if (firstName == null || firstName.isEmpty()) return "";
        String a = firstName.substring(0, 1).toLowerCase();
        if (lastName == null || lastName.isEmpty()) return a;
        String b = lastName.substring(0, 1);
        return a + (Character.isDigit(b.charAt(0)) ? b : b.toLowerCase());
    }

    private Map<String, Object> buildFullAccountSettings(Account.AccountSettings fromApi) {
        Map<String, Object> settings = new HashMap<>();
        settings.put("permissions", fromApi != null && fromApi.getPermissions() != null ? fromApi.getPermissions() : List.of());
        settings.put("unreadMessagesMode", fromApi != null && fromApi.getUnreadMessagesMode() != null ? fromApi.getUnreadMessagesMode() : "UNREAD_MESSAGES_COUNT");
        settings.put("contactDataVisibility", fromApi != null && fromApi.getContactDataVisibility() != null ? fromApi.getContactDataVisibility() : "ALL");
        settings.put("recentlyUsedGifs", new ArrayList<String>());
        settings.put("showForwardedName", false);
        settings.put("messagesPreviewDisabled", false);
        settings.put("nonContactPreviewType", "LIKE_CONTACT");
        settings.put("reactions", fromApi != null && fromApi.getReactions() != null ? fromApi.getReactions() : List.of("😎", "💖", "😱", "🙂", "🫠", "☮️", "🔥", "👌", "👍", "👎", "😁"));
        settings.put("quickReaction", fromApi != null && fromApi.getQuickReaction() != null ? fromApi.getQuickReaction() : "👍");
        settings.put("voiceMessageMode", "ALLOWED");
        Map<String, Object> stickerAccountSettings = new HashMap<>();
        stickerAccountSettings.put("addedPackIds", List.of());
        stickerAccountSettings.put("favoriteStickers", List.of());
        stickerAccountSettings.put("recentStickers", List.of());
        stickerAccountSettings.put("privatePackIds", List.of());
        settings.put("stickerAccountSettings", stickerAccountSettings);
        return settings;
    }

    private String loadFromSessions(String phone) {
        Path file = SESSIONS_DIR.resolve("localStorage_" + phone + ".json");
        if (!Files.exists(file)) {
            throw new IllegalStateException(
                    "Сессия не сохранена для пользователя " + phone + ". Сначала залогинься и вызови saveCredentials(" + phone + ")."
            );
        }
        try {
            return Files.readString(file);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось прочитать сохранённую сессию из " + file, e);
        }
    }

    public void clearCredentials() {
        authToken = null;
        Path file = SESSIONS_DIR.resolve("saved_localStorage.json");
        try {
            Files.deleteIfExists(file);
            logger.info("Учетные данные и файл {} удалены.", file);
        } catch (Exception e) {
            logger.warn("Не удалось удалить файл {}", file, e);
        }
    }
}