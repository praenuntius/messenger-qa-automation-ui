package org.foxtrot.mobile.test.encryption;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import org.foxtrot.enums.DocumentFileExtension;
import org.foxtrot.enums.MediaFileExtension;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.mobile.allure.AllureIds;
import org.foxtrot.mobile.core.application.ApplicationUtils;
import org.foxtrot.mobile.core.driver.DriverManager;
import org.foxtrot.mobile.core.emulator.DeviceCleanupManager;
import org.foxtrot.mobile.test.BaseMobileTest;
import org.foxtrot.mobile.testdata.dictionary.AccountDictionary;
import org.foxtrot.mobile.testdata.dictionary.AddSubscribersDictionary;
import org.foxtrot.mobile.testdata.dictionary.ChatsDictionary;
import org.foxtrot.mobile.testdata.dictionary.CreateNewGroupDictionary;
import org.foxtrot.mobile.testdata.dictionary.CreateNewMessageDictionary;
import org.foxtrot.mobile.testdata.dictionary.LoginWelcomeDictionary;
import org.foxtrot.mobile.utils.AttachmentUtils;
import org.foxtrot.mobile.utils.FileUtils;
import org.foxtrot.mobile.utils.LoaderWaiter;
import org.foxtrot.utils.SkrSharedChat;
import org.foxtrot.utils.SkrTestDataCleaner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Duration;

/**
 * Слой 2 (Mobile E2E): клиент переживает ротацию симметричного ключа.
 * Логин: stage14, если baseApiUrl содержит stage14, иначе preprod.
 * <p>
 * Паттерн: UI создаёт чат (синк гарантирован) → API добавляет peer / шлёт от peer.
 */
@Tag("encryption-suite")
@Tag("encryption")
@Tag("mobile")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SymmetricKeyRotationMobileTest extends BaseMobileTest {

    private static final UsersEnum TEST_USER = SkrSharedChat.resolveOwner();
    private static final UsersEnum PEER_USER = SkrSharedChat.resolvePeer();

    private final SkrTestDataCleaner dataCleaner = new SkrTestDataCleaner();
    private SkrSharedChat sharedChat;

    @AfterEach
    void tearDownSkrChat() {
        if (sharedChat != null) {
            sharedChat.deleteQuietly();
            sharedChat = null;
        }
        DeviceCleanupManager.cleanDeviceState();
    }

    @AfterAll
    void afterAllCleanSkrData() {
        dataCleaner.clean(TEST_USER, PEER_USER);
        DeviceCleanupManager.cleanDeviceState();
    }

    @Test
    @DisplayName("SKR-MOB-001: stage14 UI login, create chat, send message")
    @Description("""
            Smoke mobile на stage14: UI-логин → создать приватную группу в UI → send text.
            Базовый happy-path без ротации; UI-создание нужно, т.к. Foxtrot не показывает API-only группы.
            """)
    void loginOnStage14_openSaved_andSendMessage_shouldSucceed() {
        Allure.step("UI-логин owner", () ->
                pages.mobileLogin().loginForCurrentEnv(TEST_USER));
        Allure.step("Проверить экран списка чатов", () ->
                pages.chats().checkChatsScreenTitle(ChatsDictionary.SCREEN_TITLE));

        String chatName = "skr-mob-" + System.currentTimeMillis();
        Allure.step("Создать приватную группу '" + chatName + "' и открыть", () ->
                createPrivateGroupAndOpen(chatName));

        String message = "skr-mob-001-" + System.currentTimeMillis();
        Allure.step("Отправить текст и проверить отображение", () ->
                pages.chat().checkChatScreenDisplayed(chatName)
                        .sendTextMessage(message)
                        .checkMessageDisplayed(message));
    }

    @Test
    @Tag("key-ttl")
    @DisplayName("SKR-MOB-002: send message in app succeeds after key TTL")
    @Description("""
            Чат открыт в UI. Ждём истечения TTL ключа (≥ encryption.key.ttl.minutes),
            держа Appium-сессию alive. Затем send text — клиент должен пережить
            протухание (493 → V2+confirm / 494 → один REST retry) без перелогина.
            """)
    void afterKeyTtl_sendMessageInApp_shouldSucceed() throws InterruptedException {
        Allure.step("UI-логин owner", () ->
                pages.mobileLogin().loginForCurrentEnv(TEST_USER));
        pages.chats().checkChatsScreenTitle(ChatsDictionary.SCREEN_TITLE);

        String chatName = "skr-mob-" + System.currentTimeMillis();
        Allure.step("Создать группу и открыть чат", () -> {
            createPrivateGroupAndOpen(chatName);
            pages.chat().checkChatScreenDisplayed(chatName);
        });

        Allure.step("Дождаться истечения key TTL (keep-alive Appium)", () -> {
            try {
                waitKeyTtlKeepingSessionAlive();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });

        String message = "skr-mob-002-" + System.currentTimeMillis();
        Allure.step("UI send после TTL", () ->
                pages.chat().sendTextMessage(message)
                        .checkMessageDisplayed(message));
    }

    @Test
    @Tag("key-ttl")
    @AllureIds(android = "94017", ios = "94017")
    @DisplayName("SKR-MOB-003: after key TTL, message from peer arrives in open chat")
    @Description("""
            UI-группа + peer через API. После TTL: catch-up send owner → peer API send.
            Входящее должно появиться в открытом чате (realtime после восстановления ключа).
            """)
    void afterKeyTtl_incomingMessageFromPeer_shouldAppear() throws InterruptedException {
        Allure.step("UI-логин owner", () ->
                pages.mobileLogin().loginForCurrentEnv(TEST_USER));
        pages.chats().checkChatsScreenTitle(ChatsDictionary.SCREEN_TITLE);

        // UI-создание: Foxtrot не показывает API-only группу в списке (даже с seed)
        String chatName = "skr-mob-003-" + System.currentTimeMillis();
        Allure.step("Создать UI-группу и открыть", () -> {
            createPrivateGroupAndOpen(chatName);
            pages.chat().checkChatScreenDisplayed(chatName);
        });

        sharedChat = Allure.step("API: attach peer к UI-группе",
                () -> SkrSharedChat.attachPeerToUiGroup(TEST_USER, PEER_USER, chatName));
        SkrSharedChat chat = sharedChat;

        Allure.step("Дождаться истечения key TTL (keep-alive Appium)", () -> {
            try {
                waitKeyTtlKeepingSessionAlive();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });

        String catchUp = "skr-mob-003-catchup-" + System.currentTimeMillis();
        Allure.step("Catch-up UI send после TTL", () ->
                pages.chat().sendTextMessage(catchUp)
                        .checkMessageDisplayed(catchUp));

        String message = "skr-mob-003-" + System.currentTimeMillis();
        Allure.step("Peer API send → UI показывает входящее", () -> {
            chat.peerSendText(message);
            pages.chat().checkMessageDisplayed(message);
        });
    }

    @Test
    @Tag("key-ttl")
    @DisplayName("SKR-MOB-004: image and file attach succeed after key TTL")
    @Description("""
            Аналог WEB-004/009 на mobile: после истечения key TTL клиент catch-up’ит
            текстовым send, затем успешно прикрепляет image (gallery) и document (TXT).
            Файлы на устройство: FileUtils.addMediaFile / addFile; в чат —
            attachMediaToMessage / attachFileToMessage.
            """)
    void afterKeyTtl_attachImageAndFile_shouldSucceed() throws InterruptedException {
        Allure.step("UI-логин owner", () ->
                pages.mobileLogin().loginForCurrentEnv(TEST_USER));
        pages.chats().checkChatsScreenTitle(ChatsDictionary.SCREEN_TITLE);

        // До создания чата: iOS addFile делает openurl + relaunch — сессия обычно жива
        Allure.step("Положить JPG и TXT на устройство", () -> {
            FileUtils.addMediaFile(MediaFileExtension.JPG);
            FileUtils.addFile(DocumentFileExtension.TXT);
        });
        Allure.step("Убедиться, что снова на списке чатов после push файлов", () -> {
            pages.onboarding().clickOnboardingActionButtonIfNeeded();
            if (!pages.chats().isChatsListDisplayed(8L)) {
                pages.mobileLogin().loginForCurrentEnv(TEST_USER, false);
            }
            pages.chats().checkChatsScreenTitle(ChatsDictionary.SCREEN_TITLE);
        });

        String chatName = "skr-mob-004-" + System.currentTimeMillis();
        Allure.step("Создать группу и открыть чат", () -> {
            createPrivateGroupAndOpen(chatName);
            pages.chat().checkChatScreenDisplayed(chatName);
        });

        Allure.step("Дождаться истечения key TTL (keep-alive Appium)", () -> {
            try {
                waitKeyTtlKeepingSessionAlive();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });

        String catchUp = "skr-mob-004-catchup-" + System.currentTimeMillis();
        Allure.step("Catch-up UI text после TTL", () ->
                pages.chat().sendTextMessage(catchUp)
                        .checkMessageDisplayed(catchUp));

        Allure.step("Attach image (JPG) после TTL", () ->
                pages.chat().attachMediaToMessage());

        Allure.step("Attach file (TXT) после TTL", () ->
                pages.chat().attachFileToMessage(DocumentFileExtension.TXT));
    }

    @Test
    @Tag("lifecycle")
    @Tag("key-ttl")
    @DisplayName("SKR-MOB-005: after background during key TTL, send and peer message work")
    @Description("""
            Чат открыт, peer через API. Приложение уходит в background на время ≥ TTL
            (SSE рвётся, ключ может протухнуть). После foreground: catch-up send +
            входящее от peer — клиент должен восстановить ключ/SSE без перелогина.

            Известный дефект клиента: после background ≥ TTL иногда пустая подложка
            (нет UI) — тест оставляем как регрессию; руками см. manual repro в Allure/чате.
            """)
    void afterBackgroundDuringKeyTtl_sendAndPeerMessage_shouldWork() {
        Allure.step("UI-логин owner", () ->
                pages.mobileLogin().loginForCurrentEnv(TEST_USER));
        pages.chats().checkChatsScreenTitle(ChatsDictionary.SCREEN_TITLE);

        String chatName = "skr-mob-005-" + System.currentTimeMillis();
        Allure.step("Создать UI-группу, открыть, attach peer", () -> {
            createPrivateGroupAndOpen(chatName);
            pages.chat().checkChatScreenDisplayed(chatName);
        });

        sharedChat = Allure.step("API: attach peer к UI-группе",
                () -> SkrSharedChat.attachPeerToUiGroup(TEST_USER, PEER_USER, chatName));
        SkrSharedChat chat = sharedChat;

        long ttlMinutes = Long.parseLong(System.getProperty("encryption.key.ttl.minutes", "1"));
        Duration backgroundFor = Duration.ofMinutes(ttlMinutes).plusSeconds(15);

        Allure.step("Background на " + backgroundFor.getSeconds() + " с (≥ key TTL)", () ->
                ApplicationUtils.backgroundApplication(backgroundFor));
        Allure.step("Foreground приложения", ApplicationUtils::foregroundApplication);

        Allure.step("Вернуться в открытый чат при необходимости", () -> {
            pages.onboarding().clickOnboardingActionButtonIfNeeded();
            boolean onChat = pages.chat().isChatScreenDisplayedSoft(chatName, 15L);
            boolean onList = pages.chats().isChatsListDisplayed(5L);
            if (!onChat && !onList) {
                org.junit.jupiter.api.Assertions.fail(
                        "PRODUCT DEFECT: после background ≥ key TTL UI пустой (нет чата и списка «Чаты»). "
                                + "Ожидалось восстановление экрана без force-stop. chat=" + chatName
                );
            }
            if (!onChat) {
                pages.chats().openChatWhenAppears(chatName, 45L);
            }
            pages.chat().checkChatScreenDisplayed(chatName);
        });

        String catchUp = "skr-mob-005-catchup-" + System.currentTimeMillis();
        Allure.step("Catch-up UI send после foreground", () ->
                pages.chat().sendTextMessage(catchUp)
                        .checkMessageDisplayed(catchUp));

        String message = "skr-mob-005-" + System.currentTimeMillis();
        Allure.step("Peer API send → UI показывает входящее", () -> {
            chat.peerSendText(message);
            pages.chat().checkMessageDisplayed(message);
        });
    }

    @Test
    @Tag("lifecycle")
    @DisplayName("SKR-MOB-007: logout and login again works after messaging")
    @Description("""
            Аналог WEB-007. Logout/login без долгого TTL-ожидания (после ≥1 мин app часто
            не на экране Account — app_logout/Alert не находятся).
            Send → Account → Выйти → welcome → re-login → send
            (тот же чат, если синканулся; иначе Saved/первый — как WEB-007 в Сохраненные).
            """)
    void afterKeyTtl_logoutAndLoginAgain_shouldWork() {
        Allure.step("UI-логин owner", () ->
                pages.mobileLogin().loginForCurrentEnv(TEST_USER));
        pages.chats().checkChatsScreenTitle(ChatsDictionary.SCREEN_TITLE);

        String chatName = "skr-mob-007-" + System.currentTimeMillis();
        Allure.step("Создать UI-группу и открыть", () -> {
            createPrivateGroupAndOpen(chatName);
            pages.chat().checkChatScreenDisplayed(chatName);
        });

        String catchUp = "skr-mob-007-catchup-" + System.currentTimeMillis();
        Allure.step("Catch-up UI send", () ->
                pages.chat().sendTextMessage(catchUp)
                        .checkMessageDisplayed(catchUp));

        Allure.step("Logout: чаты → Account → подтверждение «Выйти»", () -> {
            returnToChatsList(chatName);
            pages.bottomMenu().clickAccountButton();
            pages.account()
                    .checkAccountScreenPresent(AccountDictionary.SCREEN_TITLE)
                    .clickExitButton();
            // Это in-app модалка «Выйти из приложения?», не системный permission alert
            pages.modal()
                    .checkModalTitle(AccountDictionary.LOGOUT_MODAL_TITLE)
                    .clickActionButton();
        });

        Allure.step("Экран welcome после logout", () ->
                pages.loginWelcome().checkLoginWelcomeTitle(LoginWelcomeDictionary.SCREEN_TITLE_STAGE));

        Allure.step("Повторный UI-логин (без Server Config)", () ->
                pages.mobileLogin().loginForCurrentEnv(TEST_USER, false));

        String afterRelogin = "skr-mob-007-relogin-" + System.currentTimeMillis();
        Allure.step("Открыть чат и send после re-login", () -> {
            pages.chats().checkChatsScreenTitle(ChatsDictionary.SCREEN_TITLE);
            pages.chats().openChatWhenAppearsOrFallback(chatName, 25L);
            pages.onboarding().clickOnboardingActionButtonIfNeeded();
            pages.chat().sendTextMessage(afterRelogin)
                    .checkMessageDisplayed(afterRelogin);
        });
    }

    /** Из открытого чата назад в список «Чаты» (нужен bottom menu → Account). */
    private void returnToChatsList(String chatName) {
        if (pages.chats().isChatsListDisplayed(3L)) {
            return;
        }
        Allure.step("Назад из чата '" + chatName + "' в список", () -> {
            try {
                DriverManager.getDriver().navigate().back();
            } catch (Exception e) {
                Allure.step("navigate().back() failed: " + e.getMessage());
            }
            pages.onboarding().clickOnboardingActionButtonIfNeeded();
            if (!pages.chats().isChatsListDisplayed(8L)) {
                try {
                    DriverManager.getDriver().navigate().back();
                } catch (Exception ignored) {
                    // second back attempt
                }
            }
            pages.chats().checkChatsScreenTitle(ChatsDictionary.SCREEN_TITLE);
        });
    }

    /**
     * Создаёт приватную группу без участников.
     * foxtrot: после Next часто возвращает в список чатов — открываем группу оттуда.
     */
    private void createPrivateGroupAndOpen(String chatName) {
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace()
                .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                .clickCreateGroupBtn();
        pages.createNewGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .fillGroupName(chatName)
                .clickNextBtn();
        pages.addSubscribers()
                .checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                .clickNextBtn();
        LoaderWaiter.waitForNoLoader();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();

        if (pages.chat().isChatScreenDisplayedSoft(chatName, 10L)) {
            Allure.step("После создания сразу открыт чат '" + chatName + "'");
            return;
        }
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        if (pages.chat().isChatScreenDisplayedSoft(chatName, 8L)) {
            return;
        }
        if (pages.chats().isChatsListDisplayed(5L)) {
            Allure.step("После создания — список чатов, открываю '" + chatName + "'");
            pages.chats().openChatWhenAppears(chatName, 60L);
            pages.onboarding().clickOnboardingActionButtonIfNeeded();
            return;
        }
        AttachmentUtils.dumpUiState("after-create-unknown-screen");
        org.junit.jupiter.api.Assertions.fail(
                "После создания группы ни экран чата, ни список «Чаты». "
                        + "Смотри Allure/target skr-ui-dump-after-create-unknown-screen.txt"
        );
    }

    /** Держим Appium-сессию живой, пока ключ протухает по TTL. */
    private static void waitKeyTtlKeepingSessionAlive() throws InterruptedException {
        long ttlMinutes = Long.parseLong(System.getProperty("encryption.key.ttl.minutes", "1"));
        long waitMs = ttlMinutes * 60 * 1000L + 5_000L;
        long deadline = System.currentTimeMillis() + waitMs;
        while (System.currentTimeMillis() < deadline) {
            try {
                com.codeborne.selenide.WebDriverRunner.getWebDriver().getPageSource();
            } catch (Exception ignored) {
                // keep waiting
            }
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) {
                break;
            }
            Thread.sleep(Math.min(15_000L, remaining));
        }
    }
}
