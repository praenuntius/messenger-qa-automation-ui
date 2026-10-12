package org.foxtrot.test.encryption;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.junit5.AllureJunit5;
import io.qameta.allure.selenide.AllureSelenide;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.pages.login.PhoneInputPageSteps;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.BrowserEncryptionSession;
import org.foxtrot.utils.BrowserSseReconnect;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.SkrSharedChat;
import org.foxtrot.utils.SkrSoakTarget;
import org.foxtrot.utils.SkrTestDataCleaner;
import org.foxtrot.utils.SymmetricKeySession;
import org.foxtrot.utils.SymmetricKeySessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Soak: юзер ходит по чатам и спейсам, пока истекает TTL ключа / SSE 493.
 * Ротация только от действия пользователя (send), не API-rotate той же сессии.
 */
@ExtendWith(AllureJunit5.class)
@Tag("encryption-suite")
@Tag("encryption")
@Tag("soak")
@Tag("web")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SymmetricKeyRotationSoakWebTest extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(SymmetricKeyRotationSoakWebTest.class);
    private static final UsersEnum TEST_USER = SkrSharedChat.resolveOwner();
    private static final UsersEnum PEER_USER = SkrSharedChat.resolvePeer();
    private static final int MAX_HOMESPACE_CHATS = 3;
    private static final int MAX_SPACES = 2;
    private static final int CREATE_EVERY_TICKS = 2;

    static {
        System.setProperty("encryption.soak.quiet.logs", "true");
        System.setProperty("selenoid.enable.video",
                System.getProperty("selenoid.enable.video", "false"));
        if (System.getProperty("selenoid.session.timeout") == null) {
            long soakMin = Long.parseLong(System.getProperty("encryption.soak.minutes", "10"));
            System.setProperty("selenoid.session.timeout", (soakMin + 45) + "m");
        }
    }

    private LoginHelper loginHelper;
    private final SymmetricKeySessionFactory sessionFactory = new SymmetricKeySessionFactory();
    private final SkrTestDataCleaner dataCleaner = new SkrTestDataCleaner();
    private final List<SkrSoakTarget> targets = new ArrayList<>();
    private int inboundOk;
    private int inboundMisses;

    @BeforeAll
    static void soakQuietUiReporting() {
        Configuration.screenshots = false;
        Configuration.savePageSource = false;
        SelenideLogger.removeListener("AllureSelenide");
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide()
                        .screenshots(false)
                        .savePageSource(false)
                        .includeSelenideSteps(false));
    }

    @BeforeEach
    void initLoginHelper() {
        loginHelper = new LoginHelper();
    }

    @AfterAll
    void afterAllCleanSkrData() {
        Set<Long> workspaces = new LinkedHashSet<>();
        SymmetricKeySession ownerSession = null;
        for (SkrSoakTarget target : targets) {
            if (target.chat() == null) {
                continue;
            }
            if (ownerSession == null) {
                ownerSession = target.chat().getOwnerSession();
            }
            if (target.chat().getWorkspaceId() != null) {
                workspaces.add(target.chat().getWorkspaceId());
            }
            target.chat().deleteQuietly();
        }
        if (ownerSession != null) {
            for (Long wsId : workspaces) {
                try {
                    SkrSharedChat.encryptedApi().post(
                            org.foxtrot.api.constants.ApiEndpoints.DELETE_WORKSPACE,
                            new org.foxtrot.api.dto.workSpace.DeleteWorkspaceRequest(wsId),
                            ownerSession
                    );
                } catch (Exception ignored) {
                    // cleaner ниже
                }
            }
        }
        dataCleaner.cleanPrefix(SkrSoakTarget.namePrefix(), TEST_USER, PEER_USER);
    }

    @Test
    @Timeout(value = 8, unit = TimeUnit.HOURS)
    @DisplayName("SKR-WEB-SOAK: chats+spaces walk, SSE TTL, send/inbound")
    @Description("""
            Один чат на старте, дальше тик сам ходит/создаёт/шлёт.
            Тик: иногда create → walk (reconnect SSE) → UI send → peer inbound.
            Ротация — send после TTL, не API-rotate той же UI-сессии.
            """)
    void chronicKeyRotation_ownerUiAndPeerApi_shouldKeepWorking() {
        long soakMinutes = Long.parseLong(System.getProperty("encryption.soak.minutes", "10"));
        long tickSeconds = Long.parseLong(System.getProperty("encryption.soak.tick.seconds", "90"));
        long ttlMinutes = Long.parseLong(System.getProperty("encryption.key.ttl.minutes", "1"));
        int rotateEveryTicks = Integer.parseInt(System.getProperty("encryption.soak.rotate.every.ticks", "0"));

        loginHelper.performUILoginForTest(TEST_USER);
        seedHomespaceChat();
        assertTrue(!targets.isEmpty(), "Soak pool must have at least one chat");

        long deadline = System.currentTimeMillis() + soakMinutes * 60_000L;
        int tick = 0;
        int ok = 0;
        List<String> failures = new ArrayList<>();
        List<String> keyLog = new ArrayList<>();

        try {
            while (System.currentTimeMillis() < deadline) {
                tick++;
                SkrSoakTarget target = targets.get((tick - 1) % targets.size());
                try {
                    log.info("Soak tick #{} → {} ({})", tick, target.chatName(),
                            target.isHomespace() ? "homespace" : "space:" + target.spaceName());
                    runWebSoakTick(tick, rotateEveryTicks, target, keyLog);
                    ok++;
                } catch (SoakCriticalException critical) {
                    failures.add("CRITICAL tick #" + tick + ": " + critical.getMessage());
                    Allure.addAttachment("soak-critical-" + tick, critical.toString());
                    break;
                } catch (Throwable first) {
                    log.warn("Tick #{} failed — recover: {}", tick, first.toString());
                    Allure.addAttachment("soak-tick-" + tick + "-error", first.toString());
                    try {
                        recoverWebTick(tick, target, keyLog);
                        ok++;
                    } catch (SoakCriticalException critical) {
                        failures.add("CRITICAL tick #" + tick + ": " + critical.getMessage());
                        Allure.addAttachment("soak-critical-" + tick, critical.toString());
                        break;
                    } catch (Throwable second) {
                        failures.add("tick #" + tick + ": " + first.getMessage()
                                + " | recover: " + second.getMessage());
                    }
                }
                keepAliveUntilNextTick(deadline, tickSeconds);
            }
        } finally {
            String summary = "minutes=" + soakMinutes
                    + ", ticks=" + tick
                    + ", ok=" + ok
                    + ", pool=" + targets.size()
                    + ", inboundOk=" + inboundOk
                    + ", inboundMisses=" + inboundMisses
                    + ", failures=" + failures.size()
                    + "\nkeys:\n" + String.join("\n", keyLog)
                    + "\nfailures:\n" + String.join("\n", failures);
            log.info("Soak summary: {}", summary.replace('\n', ' '));
            Allure.addAttachment("soak-summary", summary);
        }

        assertTrue(tick > 0, "Soak must run at least one tick");
        assertTrue(failures.isEmpty(),
                "Soak had " + failures.size() + "/" + tick + " failed ticks:\n"
                        + String.join("\n", failures));
        if (tick >= 2) {
            assertTrue(inboundOk > 0,
                    "peer inbound never succeeded after " + tick + " ticks (misses="
                            + inboundMisses + ")");
        }
        if (soakMinutes >= ttlMinutes && tick > 1) {
            assertTrue(keyIdChanged(keyLog),
                    "encryptKey.id never changed after TTL windows. keys:\n" + String.join("\n", keyLog));
        }
    }

    private void runWebSoakTick(int tickNo, int rotateEveryTicks, SkrSoakTarget target, List<String> keyLog) {
        maybeCreateMore(tickNo);
        openTarget(target);
        if (rotateEveryTicks > 0 && tickNo % rotateEveryTicks == 0) {
            tryApiRotateBestEffort(tickNo);
        }

        Long keyBefore = BrowserEncryptionSession.readMessageKeyId();
        String out = "skr-web-soak-out-" + tickNo + "-" + System.currentTimeMillis();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage(out);
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageSent(out);

        inboundBestEffort(tickNo, target);

        if (target.isHomespace() && tickNo % 2 == 1) {
            extraImageNonFatal(tickNo);
        }

        Long keyAfter = BrowserEncryptionSession.readMessageKeyId();
        keyLog.add("tick #" + tickNo + " " + target.chatName()
                + " encryptKey.id " + keyBefore + " → " + keyAfter);
        if (isLoggedOut()) {
            throw new SoakCriticalException("logged out after tick #" + tickNo);
        }
    }

    private void openTarget(SkrSoakTarget target) {
        if (target.isHomespace()) {
            try {
                pages.leftSidebarSteps.clickChatsButton();
            } catch (Throwable ignored) {
                // already on homespace
            }
            BrowserSseReconnect.reenterGroupViaSaved(
                    pages.chatListSteps, pages.inputBarSteps, pages.topbarSteps, target.chatName());
            return;
        }
        pages.leftSidebarSteps.clickSpaceByName(target.spaceName());
        pages.spaceSteps.verifySpaceName(target.spaceName());
        pages.chatListSteps.clickChatByName(target.chatName());
        pages.topbarSteps.verifyChatName(target.chatName());
        pages.inputBarSteps.verifyInputBarIsVisible();
    }

    private void inboundBestEffort(int tickNo, SkrSoakTarget target) {
        String inbound = "skr-web-soak-in-" + tickNo + "-" + System.currentTimeMillis();
        try {
            target.chat().peerSendText(inbound);
            pages.chatVerify.verifyMessageSent(inbound);
            inboundOk++;
        } catch (Throwable inboundFailed) {
            inboundMisses++;
            log.warn("Inbound tick #{} non-fatal: {}", tickNo, inboundFailed.toString());
            Allure.addAttachment("soak-inbound-" + tickNo, inboundFailed.toString());
        }
    }

    private void extraImageNonFatal(int tickNo) {
        try {
            pages.inputBarSteps
                    .uploadMediaFileViaAttachMenu("images/tinyJPG.jpg")
                    .verifyAttachedMediaPreviews(1, 0)
                    .clickSendMessageButton();
            pages.chatVerify.waitForMediaUploadToComplete();
            pages.chatVerify.verifyImagesSent();
        } catch (Throwable uploadStuck) {
            log.warn("Extra image tick #{} non-fatal: {}", tickNo, uploadStuck.toString());
        }
    }

    private void maybeCreateMore(int tickNo) {
        if (tickNo % CREATE_EVERY_TICKS != 0) {
            return;
        }
        try {
            if (homespaceCount() < MAX_HOMESPACE_CHATS) {
                seedHomespaceChat();
                return;
            }
            if (spaceCount() < MAX_SPACES) {
                seedSpaceWithGroup();
            }
        } catch (Throwable createFailed) {
            log.warn("Extra create tick #{} non-fatal: {}", tickNo, createFailed.toString());
        }
    }

    private void seedHomespaceChat() {
        goHomespace();
        String groupName = SkrSoakTarget.namePrefix() + "h-" + System.currentTimeMillis();
        pages.homespaceSteps.clickCreateNewChatButton();
        pages.newMessageModalSteps.clickCreateGroupButton();
        pages.newGroupModalSteps.createHomespaceGroup(groupName);
        pages.addPeopleListSteps.clickAddUsersButton();
        pages.topbarSteps.verifyChatName(groupName);
        pages.chatListSteps.clickChatByName(groupName);
        pages.inputBarSteps.verifyInputBarIsVisible();
        SkrSharedChat chat = SkrSharedChat.attachPeerToUiGroup(TEST_USER, PEER_USER, groupName);
        targets.add(new SkrSoakTarget(groupName, null, chat));
        log.info("Seeded homespace chat {}", groupName);
    }

    private void seedSpaceWithGroup() {
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.clickCreateNewWorkspaceButton();
        String spaceName = SkrSoakTarget.namePrefix() + "ws-" + System.currentTimeMillis();
        pages.newWorkspaceModalSteps.createWorkspace(spaceName);
        pages.spaceSteps.verifySpaceName(spaceName);

        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.clickCreateGroupButton();
        String groupName = SkrSoakTarget.namePrefix() + "wg-" + System.currentTimeMillis();
        pages.newGroupModalSteps.createGroup(groupName);
        pages.addPeopleListSteps.clickAddUsersButton();
        pages.topbarSteps.verifyChatName(groupName);

        SkrSharedChat chat = SkrSharedChat.attachPeerToUiGroup(TEST_USER, PEER_USER, groupName);
        targets.add(new SkrSoakTarget(groupName, spaceName, chat));
        log.info("Seeded space {} / group {}", spaceName, groupName);
    }

    private void goHomespace() {
        try {
            pages.leftSidebarSteps.clickChatsButton();
        } catch (Throwable ignored) {
            // already there
        }
    }

    private int homespaceCount() {
        int n = 0;
        for (SkrSoakTarget t : targets) {
            if (t.isHomespace()) {
                n++;
            }
        }
        return n;
    }

    private int spaceCount() {
        Set<String> names = new LinkedHashSet<>();
        for (SkrSoakTarget t : targets) {
            if (!t.isHomespace()) {
                names.add(t.spaceName());
            }
        }
        return names.size();
    }

    private void recoverWebTick(int tickNo, SkrSoakTarget target, List<String> keyLog) {
        try {
            BrowserSseReconnect.refreshAndReopenGroup(
                    pages.chatListSteps, pages.inputBarSteps, pages.topbarSteps, target.chatName());
        } catch (Throwable ignored) {
            openTarget(target);
        }
        if (isLoggedOut()) {
            log.warn("Recover: session dead — UI re-login, tick #{}", tickNo);
            loginHelper.performUILoginForTest(TEST_USER);
            openTarget(target);
        }
        if (isLoggedOut()) {
            throw new SoakCriticalException("still logged out after re-login, tick #" + tickNo);
        }
        runWebSoakTick(tickNo, 0, target, keyLog);
    }

    private boolean isLoggedOut() {
        try {
            if (new PhoneInputPageSteps().isLoginScreenReady()) {
                return true;
            }
        } catch (Throwable ignored) {
            // selector/driver glitch
        }
        return BrowserEncryptionSession.readAccessToken() == null
                && BrowserEncryptionSession.readMessageKeyId() == null;
    }

    private static boolean keyIdChanged(List<String> keyLog) {
        Set<String> ids = new LinkedHashSet<>();
        for (String line : keyLog) {
            int idx = line.indexOf("encryptKey.id ");
            if (idx < 0) {
                continue;
            }
            String rest = line.substring(idx + "encryptKey.id ".length());
            for (String part : rest.split(" → ")) {
                String id = part.trim();
                if (!id.isEmpty() && !"null".equals(id)) {
                    ids.add(id);
                }
            }
        }
        return ids.size() > 1;
    }

    private static final class SoakCriticalException extends RuntimeException {
        SoakCriticalException(String message) {
            super(message);
        }
    }

    private void tryApiRotateBestEffort(int tickNo) {
        try {
            var browserSession = BrowserEncryptionSession.fromLocalStorage(TEST_USER);
            sessionFactory.rotate(browserSession);
        } catch (RuntimeException first) {
            if (!isKeyMismatch(first)) {
                throw first;
            }
            log.info("494 KEY_MISMATCHED tick #{} — retry with fresh localStorage", tickNo);
            try {
                Thread.sleep(1_500L);
                var refreshed = BrowserEncryptionSession.fromLocalStorage(TEST_USER);
                sessionFactory.rotate(refreshed);
            } catch (RuntimeException second) {
                if (isKeyMismatch(second)) {
                    log.info("API-rotate skipped tick #{}: client already rotated", tickNo);
                    return;
                }
                throw second;
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ie);
            }
        }
    }

    private static boolean isKeyMismatch(Throwable t) {
        String msg = t.getMessage() == null ? "" : t.getMessage();
        return msg.contains("494") || msg.contains("KEY_MISMATCHED");
    }

    private static void keepAliveUntilNextTick(long deadlineMs, long tickSeconds) {
        long wakeAt = Math.min(deadlineMs, System.currentTimeMillis() + tickSeconds * 1000L);
        while (System.currentTimeMillis() < wakeAt) {
            try {
                com.codeborne.selenide.WebDriverRunner.getWebDriver().getTitle();
            } catch (Exception ignored) {
                // session keep-alive
            }
            long remaining = wakeAt - System.currentTimeMillis();
            if (remaining <= 0) {
                break;
            }
            try {
                Thread.sleep(Math.min(15_000L, remaining));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
