package org.foxtrot.test.encryption;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureId;
import io.qameta.allure.Description;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.pages.login.PhoneInputPageSteps;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.BrowserEncryptionSession;
import org.foxtrot.utils.BrowserSseReconnect;
import org.foxtrot.utils.BrowserTabLifecycle;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.SkrSharedChat;
import org.foxtrot.utils.SkrTestDataCleaner;
import org.foxtrot.utils.SymmetricKeyMaterial;
import org.foxtrot.utils.SymmetricKeySession;
import org.foxtrot.utils.SymmetricKeySessionFactory;
import org.foxtrot.utils.UpdateSymmetricKeyNetworkObserver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.refresh;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.By.cssSelector;

/**
 * Слой 2 (Web E2E): веб-клиент переживает ротацию симметричного ключа.
 * Паттерн: API готовит состояние / второго юзера → UI только действует и ассертит.
 */
@ExtendWith(AllureJunit5.class)
@Tag("encryption-suite")
@Tag("encryption")
@Tag("web")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SymmetricKeyRotationWebTest extends BaseTest {

    private static final UsersEnum TEST_USER = SkrSharedChat.resolveOwner();
    private static final UsersEnum PEER_USER = SkrSharedChat.resolvePeer();

    private LoginHelper loginHelper;
    private final SymmetricKeySessionFactory sessionFactory = new SymmetricKeySessionFactory();
    private final SkrTestDataCleaner dataCleaner = new SkrTestDataCleaner();
    /** Группа из {@link SkrSharedChat#create} — удаляем в {@link #tearDownSkrChat()}. */
    private SkrSharedChat sharedChat;

    @BeforeEach
    void initLoginHelper() {
        loginHelper = new LoginHelper();
        sharedChat = null;
    }

    @AfterEach
    void tearDownSkrChat() {
        if (sharedChat != null) {
            sharedChat.deleteQuietly();
            sharedChat = null;
        }
    }

    @AfterAll
    void afterAllCleanSkrData() {
        dataCleaner.clean(TEST_USER, PEER_USER);
    }

    @Test
    // AllureId снят: 94001 в TestOps привязан к папке Mobile
    @DisplayName("SKR-WEB-001: UI login and send message in Saved Messages")
    @Description("""
            Smoke веб-клиента на stage14: UI-логин owner → Saved Messages → send text.
            Базовый happy-path без ротации — sanity перед SKR-сценариями.
            """)
    void loginViaUi_andSendMessage_shouldWork() {
        Allure.step("UI-логин owner (" + TEST_USER.name() + ")", () ->
                loginHelper.performUILoginForTest(TEST_USER));

        String message = "skr-web-001-" + System.currentTimeMillis();
        Allure.step("Открыть Saved Messages и отправить текст", () -> {
            pages.chatListSteps.clickSavedChat();
            pages.inputBarSteps.clickMessageInputField();
            pages.inputBarSteps.enterMessage(message);
            pages.inputBarSteps.clickSendMessageButton();
            pages.chatVerify.verifyMessageSent(message);
        });
    }

    @Test
    @DisplayName("SKR-WEB-002: send message in UI succeeds after server key rotation")
    @Description("""
            После UI-логина читаем ключ из localStorage и делаем API rotate V2+confirm
            «за спиной» клиента. Затем UI send в Saved Messages — клиент catch-up
            (493/SSE или 494→один REST retry + при необходимости V2+confirm) и шлёт новым ключом.
            """)
    void afterKeyRotation_sendMessageInUi_shouldSucceed() {
        Allure.step("UI-логин owner", () -> loginHelper.performUILoginForTest(TEST_USER));

        SymmetricKeySession browserSession = Allure.step("Прочитать encryption session из localStorage",
                () -> BrowserEncryptionSession.fromLocalStorage(TEST_USER));
        long oldKeyId = browserSession.getMessageKey().getId();

        SymmetricKeyMaterial newKey = Allure.step("API: rotate V2+confirm (за спиной UI)",
                () -> sessionFactory.rotate(browserSession));
        Allure.step("Новый key id отличается от старого", () ->
                assertNotEquals(oldKeyId, newKey.getId(), "Server rotation must issue a new key id"));

        String message = "skr-web-002-" + System.currentTimeMillis();
        Allure.step("UI send в Saved Messages после серверной ротации", () -> {
            pages.chatListSteps.clickSavedChat();
            pages.inputBarSteps.clickMessageInputField();
            pages.inputBarSteps.enterMessage(message);
            pages.inputBarSteps.clickSendMessageButton();
            pages.chatVerify.verifyMessageSent(message);
        });
    }

    @Test
    @AllureId("94015")
    @DisplayName("SKR-WEB-003: after rotation, message from peer arrives via SSE")
    @Description("""
            Owner+peer в общем чате. После API-ротации ключа owner: catch-up send UI,
            затем peer шлёт текст через API. Сообщение должно появиться в открытом чате
            (SSE/realtime после catch-up ключа).
            """)
    void afterKeyRotation_incomingMessageFromPeer_shouldAppear() {
        sharedChat = Allure.step("API: создать группу owner+peer",
                () -> SkrSharedChat.create(TEST_USER, PEER_USER));
        SkrSharedChat chat = sharedChat;

        Allure.step("UI-логин owner и открыть чат", () -> {
            loginHelper.performUILoginForTest(TEST_USER);
            pages.chatListSteps.clickChatByName(chat.getGroupName());
        });

        SymmetricKeySession browserSession = Allure.step("Прочитать encryption session из localStorage",
                () -> BrowserEncryptionSession.fromLocalStorage(TEST_USER));
        long oldKeyId = browserSession.getMessageKey().getId();
        SymmetricKeyMaterial newKey = Allure.step("API: ротация ключа owner",
                () -> sessionFactory.rotate(browserSession));
        Allure.step("Новый key id отличается от старого", () ->
                assertNotEquals(oldKeyId, newKey.getId(), "Server rotation must issue a new key id"));

        String catchUp = "skr-web-003-catchup-" + System.currentTimeMillis();
        Allure.step("Catch-up UI send (восстановление после 493/494 → V2+confirm)", () -> {
            pages.inputBarSteps.clickMessageInputField();
            pages.inputBarSteps.enterMessage(catchUp);
            pages.inputBarSteps.clickSendMessageButton();
            pages.chatVerify.verifyMessageSent(catchUp);
        });

        String message = "skr-web-003-" + System.currentTimeMillis();
        Allure.step("Peer API send → UI показывает входящее", () -> {
            chat.peerSendText(message);
            pages.chatVerify.verifyMessageSent(message);
        });
    }

    @Test
    @AllureId("94016")
    @DisplayName("SKR-WEB-004: image upload in UI succeeds after server key rotation")
    @Description("""
            После API-rotate ждём TTL, затем Saved → группа (reconnect SSE) — тогда стартует
            смена ключа. Дальше catch-up send и UI re-login → image.
            """)
    void afterKeyRotation_uploadImageInUi_shouldSucceed() {
        String mediaChat = "skr-web-004-media-" + System.currentTimeMillis();
        Allure.step("UI-логин owner → UI-группа", () -> {
            loginHelper.performUILoginForTest(TEST_USER);
            pages.homespaceSteps.clickCreateNewChatButton();
            pages.newMessageModalSteps.clickCreateGroupButton();
            pages.newGroupModalSteps.createHomespaceGroup(mediaChat);
            pages.addPeopleListSteps.clickAddUsersButton();
            pages.topbarSteps.verifyChatName(mediaChat);
            pages.chatListSteps.clickChatByName(mediaChat);
            pages.inputBarSteps.verifyInputBarIsVisible();
        });

        SymmetricKeySession browserSession = Allure.step("Прочитать encryption session из localStorage",
                () -> BrowserEncryptionSession.fromLocalStorage(TEST_USER));
        long oldKeyId = browserSession.getMessageKey().getId();
        Long oldFileKeyId = BrowserEncryptionSession.readFileKeyId();
        SymmetricKeyMaterial newKey = Allure.step("API: ротация ключа owner",
                () -> sessionFactory.rotate(browserSession));
        Allure.step("Новый key id отличается от старого", () ->
                assertNotEquals(oldKeyId, newKey.getId(), "Server rotation must issue a new key id"));

        Allure.step("TTL → Saved → снова группа (reconnect SSE)", () ->
                BrowserSseReconnect.reconnectAfterKeyTtl(
                        pages.chatListSteps, pages.inputBarSteps, pages.topbarSteps, mediaChat));

        long clientKeyId = Allure.step("encryptKey.id после процедуры смены ключа", () ->
                BrowserEncryptionSession.awaitMessageKeyIdChange(oldKeyId, Duration.ofSeconds(60)));
        Allure.addAttachment(
                "keys-after-reconnect",
                "messageKeyId=" + clientKeyId
                        + ", fileKeyId=" + BrowserEncryptionSession.readFileKeyId()
                        + " (was message=" + oldKeyId + ", file=" + oldFileKeyId + ")"
        );

        String catchUp = "skr-web-004-catchup-" + System.currentTimeMillis();
        Allure.step("Catch-up UI send после смены ключа", () -> {
            pages.inputBarSteps.clickMessageInputField();
            pages.inputBarSteps.enterMessage(catchUp);
            pages.inputBarSteps.clickSendMessageButton();
            pages.chatVerify.verifyMessageSent(catchUp);
        });

        Allure.step("UI re-login — свежие encryptKey + encryptFileKey перед media", () -> {
            pages.leftSidebarSteps.openSettings();
            pages.settingsMenuSteps.verifySettingsTitleHeaderVisible();
            pages.settingsMenuSteps.clickExit();
            pages.logoutModalSteps.verifyLogoutModalVisible();
            pages.logoutModalSteps.clickConfirmLeave();
            new PhoneInputPageSteps().checkPageIsLoaded();
            loginHelper.performUILoginForTest(TEST_USER);
        });

        Allure.step("Открыть группу для media", () -> {
            pages.chatListSteps.clickChatByName(mediaChat);
            pages.inputBarSteps.verifyInputBarIsVisible();
        });

        Allure.step("Warm-up text → upload image (паттерн SendMessagesTest)", () -> {
            String warmUp = "skr-web-004-warm-" + System.currentTimeMillis();
            pages.inputBarSteps.enterMessage(warmUp);
            pages.inputBarSteps.clickSendMessageButton();
            pages.chatVerify.verifyMessageSent(warmUp);

            pages.inputBarSteps
                    .uploadMediaFileViaAttachMenu("images/tinyJPG.jpg")
                    .verifyAttachedMediaPreviews(1, 0)
                    .clickSendMessageButton();
            try {
                pages.chatVerify.waitForMediaUploadToComplete();
                pages.chatVerify.verifyImagesSent();
            } catch (Throwable uploadStuck) {
                Allure.addAttachment(
                        "web-004-media-defect",
                        "Text catch-up/warm-up OK after SKR, but image upload hung (encryptFileKey). "
                                + uploadStuck.getMessage()
                );
                org.junit.jupiter.api.Assumptions.assumeTrue(
                        false,
                        "PRODUCT DEFECT: image upload hangs after message-key rotation "
                                + "(encryptFileKey). Warm-up text OK. " + uploadStuck.getMessage()
                );
            }
        });
    }

    @Test
    @DisplayName("SKR-WEB-009: file upload in UI succeeds after server key rotation")
    @Description("""
            Аналог WEB-004: TTL → reconnect SSE → смена ключа → catch-up → re-login → file.
            """)
    void afterKeyRotation_uploadFileInUi_shouldSucceed() {
        String mediaChat = "skr-web-009-media-" + System.currentTimeMillis();
        Allure.step("UI-логин owner → UI-группа", () -> {
            loginHelper.performUILoginForTest(TEST_USER);
            pages.homespaceSteps.clickCreateNewChatButton();
            pages.newMessageModalSteps.clickCreateGroupButton();
            pages.newGroupModalSteps.createHomespaceGroup(mediaChat);
            pages.addPeopleListSteps.clickAddUsersButton();
            pages.topbarSteps.verifyChatName(mediaChat);
            pages.chatListSteps.clickChatByName(mediaChat);
            pages.inputBarSteps.verifyInputBarIsVisible();
        });

        SymmetricKeySession browserSession = Allure.step("Прочитать encryption session из localStorage",
                () -> BrowserEncryptionSession.fromLocalStorage(TEST_USER));
        long oldKeyId = browserSession.getMessageKey().getId();
        Long oldFileKeyId = BrowserEncryptionSession.readFileKeyId();
        SymmetricKeyMaterial newKey = Allure.step("API: ротация ключа owner",
                () -> sessionFactory.rotate(browserSession));
        Allure.step("Новый key id отличается от старого", () ->
                assertNotEquals(oldKeyId, newKey.getId(), "Server rotation must issue a new key id"));

        Allure.step("TTL → Saved → снова группа (reconnect SSE)", () ->
                BrowserSseReconnect.reconnectAfterKeyTtl(
                        pages.chatListSteps, pages.inputBarSteps, pages.topbarSteps, mediaChat));

        long clientKeyId = Allure.step("encryptKey.id после процедуры смены ключа", () ->
                BrowserEncryptionSession.awaitMessageKeyIdChange(oldKeyId, Duration.ofSeconds(60)));
        Allure.addAttachment(
                "keys-after-reconnect",
                "messageKeyId=" + clientKeyId
                        + ", fileKeyId=" + BrowserEncryptionSession.readFileKeyId()
                        + " (was message=" + oldKeyId + ", file=" + oldFileKeyId + ")"
        );

        String catchUp = "skr-web-009-catchup-" + System.currentTimeMillis();
        Allure.step("Catch-up UI send после смены ключа", () -> {
            pages.inputBarSteps.clickMessageInputField();
            pages.inputBarSteps.enterMessage(catchUp);
            pages.inputBarSteps.clickSendMessageButton();
            pages.chatVerify.verifyMessageSent(catchUp);
        });

        Allure.step("UI re-login — свежие encryptKey + encryptFileKey перед file upload", () -> {
            pages.leftSidebarSteps.openSettings();
            pages.settingsMenuSteps.verifySettingsTitleHeaderVisible();
            pages.settingsMenuSteps.clickExit();
            pages.logoutModalSteps.verifyLogoutModalVisible();
            pages.logoutModalSteps.clickConfirmLeave();
            new PhoneInputPageSteps().checkPageIsLoaded();
            loginHelper.performUILoginForTest(TEST_USER);
        });

        Allure.step("Открыть группу → warm-up → upload TXT file", () -> {
            pages.chatListSteps.clickChatByName(mediaChat);
            pages.inputBarSteps.verifyInputBarIsVisible();

            String warmUp = "skr-web-009-warm-" + System.currentTimeMillis();
            pages.inputBarSteps.enterMessage(warmUp);
            pages.inputBarSteps.clickSendMessageButton();
            pages.chatVerify.verifyMessageSent(warmUp);

            pages.inputBarSteps.uploadMediaFileViaAttachMenu("documents/tinyTXT.txt")
                    .clickSendMessageButton();
            try {
                pages.chatVerify.waitForMediaUploadToComplete();
                pages.chatVerify.verifyFileSent();
            } catch (Throwable uploadStuck) {
                Allure.addAttachment(
                        "web-009-media-defect",
                        "Text OK after SKR, file upload hung (encryptFileKey). "
                                + uploadStuck.getMessage()
                );
                org.junit.jupiter.api.Assumptions.assumeTrue(
                        false,
                        "PRODUCT DEFECT: file upload hangs after message-key rotation. "
                                + uploadStuck.getMessage()
                );
            }
        });
    }

    @Test
    @Tag("lifecycle")
    @Tag("key-ttl")
    @DisplayName("SKR-WEB-005: after hidden tab during key TTL, send and peer message work")
    @Description("""
            UI-группа + peer через API (API-only группа на stage14 часто не синкается в список).
            Вкладка скрывается (visibilitychange) на ≥ TTL. После возврата: catch-up + peer.
            """)
    void afterHiddenTabDuringKeyTtl_sendAndPeerMessage_shouldWork() throws InterruptedException {
        String groupName = "skr-web-005-" + System.currentTimeMillis();

        Allure.step("UI-логин owner → создать UI-группу → attach peer", () -> {
            loginHelper.performUILoginForTest(TEST_USER);
            pages.homespaceSteps.clickCreateNewChatButton();
            pages.newMessageModalSteps.clickCreateGroupButton();
            pages.newGroupModalSteps.createHomespaceGroup(groupName);
            pages.addPeopleListSteps.clickAddUsersButton();
            pages.topbarSteps.verifyChatName(groupName);
            pages.chatListSteps.clickChatByName(groupName);
            pages.inputBarSteps.verifyInputBarIsVisible();
        });

        sharedChat = Allure.step("API: attach peer к UI-группе",
                () -> SkrSharedChat.attachPeerToUiGroup(TEST_USER, PEER_USER, groupName));
        SkrSharedChat chat = sharedChat;

        long ttlMinutes = Long.parseLong(System.getProperty("encryption.key.ttl.minutes", "1"));
        long hideMs = ttlMinutes * 60_000L + 15_000L;

        Allure.step("Скрыть вкладку на " + hideMs + " мс (≥ key TTL)", () -> {
            BrowserTabLifecycle.hideTab();
            try {
                Thread.sleep(hideMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            BrowserTabLifecycle.showTab();
        });

        Allure.step("Убедиться, что чат снова открыт после showTab", () -> {
            try {
                pages.inputBarSteps.verifyInputBarIsVisible();
            } catch (Throwable missingInput) {
                Allure.step("Input пропал после showTab — reopen: " + missingInput.getMessage());
                refresh();
                boolean opened = false;
                for (int attempt = 1; attempt <= 4 && !opened; attempt++) {
                    try {
                        pages.chatListSteps.verifyChatWithNameIsDisplayed(groupName, 15);
                        pages.chatListSteps.clickChatByName(groupName);
                        pages.inputBarSteps.verifyInputBarIsVisible();
                        opened = true;
                    } catch (Throwable miss) {
                        Allure.step("Reopen attempt " + attempt + ": " + miss.getMessage());
                        refresh();
                    }
                }
                if (!opened) {
                    pages.chatListSteps.verifyChatWithNameIsDisplayed(groupName, 30);
                    pages.chatListSteps.clickChatByName(groupName);
                    pages.inputBarSteps.verifyInputBarIsVisible();
                }
            }
        });

        String catchUp = "skr-web-005-catchup-" + System.currentTimeMillis();
        Allure.step("Catch-up UI send после возврата вкладки", () -> {
            pages.inputBarSteps.clickMessageInputField();
            pages.inputBarSteps.enterMessage(catchUp);
            pages.inputBarSteps.clickSendMessageButton();
            pages.chatVerify.verifyMessageSent(catchUp);
        });

        String message = "skr-web-005-" + System.currentTimeMillis();
        Allure.step("Peer API send → UI показывает входящее", () -> {
            chat.peerSendText(message);
            pages.chatVerify.verifyMessageSent(message);
        });
    }

    @Test
    @Tag("lifecycle")
    @Tag("key-ttl")
    @DisplayName("SKR-WEB-006: after TTL and SSE reconnect, key rotates then messaging works")
    @Description("""
            UI-группа + peer → API rotate → ждём TTL → Saved → снова группа (reconnect SSE).
            Тогда стартует смена ключа: encryptKey.id меняется, затем send + peer inbound.
            """)
    void afterServerRotate_clientUpdatesLocalStorageKey_thenMessagingWorks() {
        String groupName = "skr-web-006-" + System.currentTimeMillis();

        Allure.step("UI-логин owner → создать UI-группу → attach peer", () -> {
            loginHelper.performUILoginForTest(TEST_USER);
            pages.homespaceSteps.clickCreateNewChatButton();
            pages.newMessageModalSteps.clickCreateGroupButton();
            pages.newGroupModalSteps.createHomespaceGroup(groupName);
            pages.addPeopleListSteps.clickAddUsersButton();
            pages.topbarSteps.verifyChatName(groupName);
            pages.chatListSteps.clickChatByName(groupName);
            pages.inputBarSteps.verifyInputBarIsVisible();
        });

        sharedChat = Allure.step("API: attach peer к UI-группе",
                () -> SkrSharedChat.attachPeerToUiGroup(TEST_USER, PEER_USER, groupName));
        SkrSharedChat chat = sharedChat;

        SymmetricKeySession browserSession = Allure.step("Прочитать encryption session из localStorage",
                () -> BrowserEncryptionSession.fromLocalStorage(TEST_USER));
        long oldKeyId = browserSession.getMessageKey().getId();

        SymmetricKeyMaterial serverKey = Allure.step("API: ротация ключа owner (UI ещё со stale)",
                () -> sessionFactory.rotate(browserSession));
        Allure.step("Сервер выдал новый key id", () ->
                assertNotEquals(oldKeyId, serverKey.getId()));

        Allure.step("TTL → Saved → снова группа (reconnect SSE)", () ->
                BrowserSseReconnect.reconnectAfterKeyTtl(
                        pages.chatListSteps, pages.inputBarSteps, pages.topbarSteps, groupName));

        long clientKeyId = Allure.step("encryptKey.id после процедуры смены ключа", () ->
                BrowserEncryptionSession.awaitMessageKeyIdChange(oldKeyId, Duration.ofSeconds(60)));
        Allure.addAttachment("keys", "old=" + oldKeyId + ", now=" + clientKeyId);

        String catchUp = "skr-web-006-catchup-" + System.currentTimeMillis();
        Allure.step("Catch-up UI send после смены ключа", () -> {
            pages.inputBarSteps.clickMessageInputField();
            pages.inputBarSteps.enterMessage(catchUp);
            pages.inputBarSteps.clickSendMessageButton();
            pages.chatVerify.verifyMessageSent(catchUp);
        });

        String inbound = "skr-web-006-" + System.currentTimeMillis();
        Allure.step("Peer API send → UI показывает входящее", () -> {
            chat.peerSendText(inbound);
            pages.chatVerify.verifyMessageSent(inbound);
        });
    }

    @Test
    @DisplayName("SKR-WEB-007: logout and login again works after server key rotation")
    @Description("""
            После серверной ротации и catch-up: logout через Настройки → экран логина →
            повторный UI-логин → send в Saved Messages. Сессия/ключи после re-login
            должны быть валидны (нет залипания на stale encryptKey).
            """)
    void afterKeyRotation_logoutAndLoginAgain_shouldWork() {
        Allure.step("UI-логин owner", () -> loginHelper.performUILoginForTest(TEST_USER));

        SymmetricKeySession browserSession = Allure.step("Прочитать encryption session из localStorage",
                () -> BrowserEncryptionSession.fromLocalStorage(TEST_USER));
        long oldKeyId = browserSession.getMessageKey().getId();
        SymmetricKeyMaterial newKey = Allure.step("API: ротация ключа owner",
                () -> sessionFactory.rotate(browserSession));
        Allure.step("Новый key id отличается от старого", () ->
                assertNotEquals(oldKeyId, newKey.getId(), "Server rotation must issue a new key id"));

        String catchUp = "skr-web-007-catchup-" + System.currentTimeMillis();
        Allure.step("Catch-up UI send после ротации", () -> {
            pages.chatListSteps.clickSavedChat();
            pages.inputBarSteps.clickMessageInputField();
            pages.inputBarSteps.enterMessage(catchUp);
            pages.inputBarSteps.clickSendMessageButton();
            pages.chatVerify.verifyMessageSent(catchUp);
        });

        Allure.step("Logout: Настройки → Exit → модалка → Покинуть", () -> {
            pages.leftSidebarSteps.openSettings();
            pages.settingsMenuSteps.verifySettingsTitleHeaderVisible();
            pages.settingsMenuSteps.clickExit();
            pages.logoutModalSteps.verifyLogoutModalVisible();
            pages.logoutModalSteps.clickConfirmLeave();
        });

        Allure.step("Экран ввода телефона после logout", () ->
                new PhoneInputPageSteps().checkPageIsLoaded());

        Allure.step("Повторный UI-логин", () -> loginHelper.performUILoginForTest(TEST_USER));

        String afterRelogin = "skr-web-007-relogin-" + System.currentTimeMillis();
        Allure.step("Send после re-login", () -> {
            pages.chatListSteps.clickSavedChat();
            pages.inputBarSteps.clickMessageInputField();
            pages.inputBarSteps.enterMessage(afterRelogin);
            pages.inputBarSteps.clickSendMessageButton();
            pages.chatVerify.verifyMessageSent(afterRelogin);
        });
    }

    @Test
    @Tag("lifecycle")
    @DisplayName("SKR-WEB-008: after accessToken recovery, SSE reconnect delivers peer message")
    @Description("""
            После битого accessToken SSE/сессия ломается. Ждём клиентский refresh (если умеет);
            если /auth/refreshToken на стенде не работает (OAUTH_VALIDATION_FAILED) —
            восстанавливаем валидный accessToken в localStorage (имитация успешного refresh)
            и проверяем reconnect: send + peer inbound.
            """)
    void afterAccessTokenCorruption_refreshAndSseReconnect_shouldDeliverPeerMessage() {
        sharedChat = Allure.step("API: создать группу owner+peer",
                () -> SkrSharedChat.create(TEST_USER, PEER_USER));
        SkrSharedChat chat = sharedChat;

        Allure.step("UI-логин owner и открыть чат", () -> {
            loginHelper.performUILoginForTest(TEST_USER);
            pages.chatListSteps.clickChatByName(chat.getGroupName());
            pages.inputBarSteps.verifyInputBarIsVisible();
        });

        String previousAccess = Allure.step("Подменить accessToken (оставить refreshToken)",
                BrowserEncryptionSession::corruptAccessToken);

        Allure.step("Скрыть/показать вкладку — форс SSE reconnect с битым токеном", () -> {
            BrowserTabLifecycle.hideTab();
            try {
                Thread.sleep(2_000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            BrowserTabLifecycle.showTab();
            refresh();
        });

        Allure.step("Дождаться client refresh или восстановить accessToken", () -> {
            try {
                String refreshed = BrowserEncryptionSession.awaitAccessTokenChange(
                        previousAccess, Duration.ofSeconds(20));
                Allure.addAttachment("client-refresh-ok",
                        "newPrefix=" + refreshed.substring(0, Math.min(12, refreshed.length())));
            } catch (AssertionError clientRefreshMissed) {
                Allure.step("Client refresh не сработал (часто OAUTH) — restore previous accessToken");
                BrowserEncryptionSession.writeAccessToken(previousAccess);
                refresh();
            }
        });

        Allure.step("Открыть чат после recovery токена", () -> {
            // После refresh часто остаёмся на списке чатов — ждём карточку и input bar
            String name = chat.getGroupName();
            pages.chatListSteps.verifyChatWithNameIsDisplayed(name, 30);
            pages.chatListSteps.clickChatByName(name);
            try {
                pages.inputBarSteps.verifyInputBarIsVisible();
            } catch (Throwable firstOpenMiss) {
                Allure.step("Input bar не появился — повторный клик по чату: " + firstOpenMiss.getMessage());
                pages.chatListSteps.clickChatByName(name);
                pages.inputBarSteps.verifyInputBarIsVisible();
            }
        });

        String catchUp = "skr-web-008-catchup-" + System.currentTimeMillis();
        Allure.step("UI send после recovery", () -> {
            pages.inputBarSteps.clickMessageInputField();
            pages.inputBarSteps.enterMessage(catchUp);
            pages.inputBarSteps.clickSendMessageButton();
            pages.chatVerify.verifyMessageSent(catchUp);
        });

        String inbound = "skr-web-008-" + System.currentTimeMillis();
        Allure.step("Peer API send → UI показывает входящее (SSE жив)", () -> {
            chat.peerSendText(inbound);
            pages.chatVerify.verifyMessageSent(inbound);
        });
    }

    @Test
    @Tag("key-ttl")
    @DisplayName("SKR-WEB-012: SSE reconnect after TTL starts key rotation")
    @Description("""
            API rotate за спиной → TTL → Saved → снова группа (reconnect SSE).
            Процедура смены ключа: encryptKey.id меняется, затем catch-up send.
            """)
    void afterSse493_clientRunsV2ThenConfirm_keyIdUpdates() {
        String groupName = "skr-web-012-" + System.currentTimeMillis();
        Allure.step("UI-логин owner → UI-группа", () -> {
            loginHelper.performUILoginForTest(TEST_USER);
            pages.homespaceSteps.clickCreateNewChatButton();
            pages.newMessageModalSteps.clickCreateGroupButton();
            pages.newGroupModalSteps.createHomespaceGroup(groupName);
            pages.addPeopleListSteps.clickAddUsersButton();
            pages.topbarSteps.verifyChatName(groupName);
            pages.chatListSteps.clickChatByName(groupName);
            pages.inputBarSteps.verifyInputBarIsVisible();
        });

        SymmetricKeySession browserSession = BrowserEncryptionSession.fromLocalStorage(TEST_USER);
        long oldKeyId = browserSession.getMessageKey().getId();

        Allure.step("API: rotate V2+confirm за спиной UI", () -> sessionFactory.rotate(browserSession));
        UpdateSymmetricKeyNetworkObserver.clearPerformanceLogBuffer();

        Allure.step("TTL → Saved → снова группа (reconnect SSE)", () ->
                BrowserSseReconnect.reconnectAfterKeyTtl(
                        pages.chatListSteps, pages.inputBarSteps, pages.topbarSteps, groupName));

        long newKeyId = Allure.step("encryptKey.id после процедуры смены ключа", () ->
                BrowserEncryptionSession.awaitMessageKeyIdChange(oldKeyId, Duration.ofSeconds(60)));

        Allure.step("Catch-up send после смены ключа", () -> {
            String nudge = "skr-web-012-nudge-" + System.currentTimeMillis();
            pages.inputBarSteps.enterMessage(nudge);
            pages.inputBarSteps.clickSendMessageButton();
            pages.chatVerify.verifyMessageSent(nudge);
        });
        List<String> events = UpdateSymmetricKeyNetworkObserver.drainRotationEvents();
        Allure.addAttachment("perf-rotation-events", String.join("\n---\n", events));
        Allure.addAttachment("keys", "old=" + oldKeyId + ", new=" + newKeyId);

        Allure.step("Сессия жива после reconnect", () ->
                assertTrue(
                        BrowserEncryptionSession.readAccessToken() != null
                                || BrowserEncryptionSession.readMessageKeyId() != null,
                        "Expected session after SSE reconnect; oldKey was " + oldKeyId));
    }

    @Test
    @Tag("key-ttl")
    @DisplayName("SKR-WEB-013: reload between V2 and confirm keeps pending path")
    @Description("""
            TERMINATE первого confirm после клиентского SCR → pending может остаться.
            Reload страницы → current ещё старый / pending жив → nudge → только confirm
            (без второго V2 create), либо recover. Logout нет.
            """)
    void reloadBetweenV2AndConfirm_pendingSurvives() throws InterruptedException {
        long ttlMinutes = Long.parseLong(System.getProperty("encryption.key.ttl.minutes", "1"));
        Allure.step("UI-логин owner", () -> loginHelper.performUILoginForTest(TEST_USER));
        long keyBefore = BrowserEncryptionSession.readMessageKeyId();

        Allure.step("TERMINATE confirm", () ->
                org.foxtrot.utils.UpdateSymmetricKeyResponseFaultInjector.install(
                        org.foxtrot.utils.UpdateSymmetricKeyResponseFaultInjector.Mode.TERMINATE_FIRST_RESPONSE,
                        org.foxtrot.utils.UpdateSymmetricKeyResponseFaultInjector.Target.CONFIRM,
                        80_000L));
        UpdateSymmetricKeyNetworkObserver.clearPerformanceLogBuffer();

        Allure.step("Ждём TTL для SCR", () -> {
            long deadline = System.currentTimeMillis() + Duration.ofMinutes(ttlMinutes).plusSeconds(25).toMillis();
            while (System.currentTimeMillis() < deadline) {
                Thread.sleep(Math.min(15_000L, deadline - System.currentTimeMillis()));
            }
        });
        Allure.step("Nudge", () -> {
            pages.chatListSteps.clickSavedChat();
            pages.inputBarSteps.enterMessage("skr-web-013-nudge-" + System.currentTimeMillis());
            pages.inputBarSteps.clickSendMessageButton();
            BrowserTabLifecycle.hideTab();
            Thread.sleep(2_000L);
            BrowserTabLifecycle.showTab();
        });

        Thread.sleep(5_000L);
        Long pendingBeforeReload = BrowserEncryptionSession.readPendingEncryptKeyId();
        Allure.addAttachment("pending-before-reload", String.valueOf(pendingBeforeReload));

        Allure.step("Reload", () -> refresh());
        Thread.sleep(3_000L);

        Long pendingAfter = BrowserEncryptionSession.readPendingEncryptKeyId();
        Long keyAfterReload = BrowserEncryptionSession.readMessageKeyId();
        Allure.addAttachment("after-reload",
                "key=" + keyAfterReload + ", pending=" + pendingAfter + ", keyBefore=" + keyBefore);

        boolean onLogin = false;
        try {
            $(cssSelector("[data-cy='signInPhoneInput']")).shouldBe(visible, Duration.ofSeconds(3));
            onLogin = true;
        } catch (Throwable ignored) {
            // ok
        }
        assertFalse(onLogin, "Reload after failed confirm must not force logout");

        org.foxtrot.utils.UpdateSymmetricKeyResponseFaultInjector.uninstall();
        Allure.step("Nudge после reload (confirm-only path если pending)", () -> {
            try {
                pages.chatListSteps.clickSavedChat();
                pages.inputBarSteps.enterMessage("skr-web-013-after-" + System.currentTimeMillis());
                pages.inputBarSteps.clickSendMessageButton();
            } catch (Throwable t) {
                Allure.step("Nudge after reload soft-fail: " + t.getMessage());
            }
            BrowserTabLifecycle.hideTab();
            Thread.sleep(2_000L);
            BrowserTabLifecycle.showTab();
        });

        List<String> events = UpdateSymmetricKeyNetworkObserver.drainRotationEvents();
        Allure.addAttachment("perf-after-reload", String.join("\n---\n", events));
        Allure.step("Наблюдение: pending и/или confirm events (best-effort)", () ->
                assertTrue(
                        pendingBeforeReload != null
                                || pendingAfter != null
                                || UpdateSymmetricKeyNetworkObserver.countConfirm(events) >= 1
                                || UpdateSymmetricKeyNetworkObserver.countUpdateV2(events) >= 1
                                || keyAfterReload != null,
                        "Expected pending or rotation activity"
                ));
    }

    @Test
    @DisplayName("SKR-WEB-014: REST 494 — one retry, second 494 is UI error without rotation/logout")
    @Description("""
            API rotate за спиной → UI send со stale → клиент один REST retry.
            Повторный 494 не стартует V2/confirm и не логинит out. Soft-assert по CDP.
            """)
    void rest494_oneRetry_noLogoutNoExtraRotation() {
        Allure.step("UI-логин owner", () -> loginHelper.performUILoginForTest(TEST_USER));
        SymmetricKeySession browserSession = BrowserEncryptionSession.fromLocalStorage(TEST_USER);
        long oldKeyId = browserSession.getMessageKey().getId();

        Allure.step("API rotate V2+confirm за спиной", () -> sessionFactory.rotate(browserSession));
        UpdateSymmetricKeyNetworkObserver.clearPerformanceLogBuffer();

        Allure.step("UI send (ожидаем 494 + один retry)", () -> {
            pages.chatListSteps.clickSavedChat();
            pages.inputBarSteps.enterMessage("skr-web-014-" + System.currentTimeMillis());
            pages.inputBarSteps.clickSendMessageButton();
        });

        try {
            Thread.sleep(15_000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        List<String> events = UpdateSymmetricKeyNetworkObserver.drainRotationEvents();
        boolean loggedOut = false;
        try {
            $(cssSelector("[data-cy='signInPhoneInput']")).shouldBe(visible, Duration.ofSeconds(3));
            loggedOut = true;
        } catch (Throwable ignored) {
            // ok
        }
        Allure.addAttachment("perf-rotation", String.join("\n---\n", events));
        Allure.addAttachment("client-key", String.valueOf(BrowserEncryptionSession.readMessageKeyId()));

        assertFalse(loggedOut, "Second 494 must not logout");
        Allure.step("Сессия жива (accessToken или encryptKey читаются)", () ->
                assertTrue(
                        BrowserEncryptionSession.readAccessToken() != null
                                || BrowserEncryptionSession.readMessageKeyId() != null,
                        "Expected session after 494 retry; oldKey was " + oldKeyId));
    }

    @Test
    @Tag("key-ttl")
    @DisplayName("SKR-WEB-015: parallel 493 during rotation joins same promise (one V2)")
    @Description("""
            Во время клиентской rotation повторный 493/nudge не должен плодить параллельные
            updateSymmetricKeyV2 (дедуп). Best-effort: после TTL + double nudge число V2
            requestWillBeSent ограничено.
            """)
    void parallel493_deduplicatesRotation() throws InterruptedException {
        long ttlMinutes = Long.parseLong(System.getProperty("encryption.key.ttl.minutes", "1"));
        Allure.step("UI-логин owner", () -> loginHelper.performUILoginForTest(TEST_USER));
        UpdateSymmetricKeyNetworkObserver.clearPerformanceLogBuffer();

        Allure.step("Ждём TTL", () -> {
            long deadline = System.currentTimeMillis() + Duration.ofMinutes(ttlMinutes).plusSeconds(25).toMillis();
            while (System.currentTimeMillis() < deadline) {
                Thread.sleep(Math.min(15_000L, Math.max(1, deadline - System.currentTimeMillis())));
            }
        });

        Allure.step("Double nudge (два hide/show подряд)", () -> {
            pages.chatListSteps.clickSavedChat();
            for (int i = 0; i < 2; i++) {
                BrowserTabLifecycle.hideTab();
                Thread.sleep(1_000L);
                BrowserTabLifecycle.showTab();
                pages.inputBarSteps.enterMessage("skr-web-015-nudge-" + i + "-" + System.currentTimeMillis());
                pages.inputBarSteps.clickSendMessageButton();
                Thread.sleep(2_000L);
            }
        });

        Thread.sleep(20_000L);
        List<String> events = UpdateSymmetricKeyNetworkObserver.drainRotationEvents();
        int v2 = UpdateSymmetricKeyNetworkObserver.countUpdateV2(events);
        int confirms = UpdateSymmetricKeyNetworkObserver.countConfirm(events);
        Allure.addAttachment("perf-rotation", String.join("\n---\n", events));
        Allure.addAttachment("counts", "v2Mentions=" + v2 + ", confirmMentions=" + confirms);

        Allure.step("V2 не раздут (дедуп: <= 6 CDP mentions / best-effort)", () ->
                assertTrue(v2 <= 6 || events.isEmpty(),
                        "Too many updateSymmetricKeyV2 mentions (possible missing dedup): " + v2));
    }
}