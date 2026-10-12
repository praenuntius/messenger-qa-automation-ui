package org.foxtrot.mobile.test.encryption;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import org.foxtrot.enums.MediaFileExtension;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.mobile.core.application.ApplicationUtils;
import org.foxtrot.mobile.core.driver.DriverManager;
import org.foxtrot.mobile.core.emulator.DeviceCleanupManager;
import org.foxtrot.mobile.test.BaseMobileTest;
import org.foxtrot.mobile.testdata.dictionary.AddSubscribersDictionary;
import org.foxtrot.mobile.testdata.dictionary.ChatsDictionary;
import org.foxtrot.mobile.testdata.dictionary.CreateNewGroupDictionary;
import org.foxtrot.mobile.testdata.dictionary.CreateNewMessageDictionary;
import org.foxtrot.mobile.utils.AttachmentUtils;
import org.foxtrot.mobile.utils.FileUtils;
import org.foxtrot.mobile.utils.LoaderWaiter;
import org.foxtrot.utils.SkrSharedChat;
import org.foxtrot.utils.SkrSoakTarget;
import org.foxtrot.utils.SkrTestDataCleaner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Soak: ходит по чатам и спейсам, send/inbound, пока истекает TTL ключа.
 * Спейсы на mobile создаём encrypted API (UI create workspace нет), группу — UI.
 */
@Tag("encryption-suite")
@Tag("encryption")
@Tag("soak")
@Tag("mobile")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SymmetricKeyRotationSoakMobileTest extends BaseMobileTest {

    private static final Logger log = LoggerFactory.getLogger(SymmetricKeyRotationSoakMobileTest.class);
    private static final UsersEnum TEST_USER = SkrSharedChat.resolveOwner();
    private static final UsersEnum PEER_USER = SkrSharedChat.resolvePeer();
    private static final int MAX_HOMESPACE_CHATS = 3;
    private static final int MAX_SPACES = 1;
    private static final int CREATE_EVERY_TICKS = 2;

    private final SkrTestDataCleaner dataCleaner = new SkrTestDataCleaner();
    private final List<SkrSoakTarget> targets = new ArrayList<>();
    private int inboundOk;
    private int inboundMisses;

    @AfterAll
    void afterAllCleanSkrData() {
        for (SkrSoakTarget target : targets) {
            if (target.chat() != null) {
                target.chat().deleteQuietly();
                target.chat().deleteWorkspaceQuietly();
            }
        }
        dataCleaner.cleanPrefix(SkrSoakTarget.namePrefix(), TEST_USER, PEER_USER);
        DeviceCleanupManager.cleanDeviceState();
    }

    @Test
    @Timeout(value = 8, unit = TimeUnit.HOURS)
    @DisplayName("SKR-MOB-SOAK: chats+spaces walk, SSE TTL, send/inbound")
    @Description("""
            Один чат на старте, дальше тик сам ходит/создаёт/шлёт.
            Тик: иногда create → walk → UI send → peer inbound.
            Ротация — send после TTL.
            """)
    void chronicKeyRotation_ownerUiAndPeerApi_shouldKeepWorking() throws InterruptedException {
        long soakMinutes = Long.parseLong(System.getProperty("encryption.soak.minutes", "10"));
        long tickSeconds = Long.parseLong(System.getProperty("encryption.soak.tick.seconds", "90"));

        pages.mobileLogin().loginForCurrentEnv(TEST_USER);
        pages.chats().checkChatsScreenTitle(ChatsDictionary.SCREEN_TITLE);

        FileUtils.addMediaFile(MediaFileExtension.JPG);
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        if (!pages.chats().isChatsListDisplayed(8L)) {
            pages.mobileLogin().loginForCurrentEnv(TEST_USER, false);
        }
        pages.chats().checkChatsScreenTitle(ChatsDictionary.SCREEN_TITLE);

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
                    runMobileSoakTick(tick, target, keyLog);
                    ok++;
                } catch (SoakCriticalException critical) {
                    failures.add("CRITICAL tick #" + tick + ": " + critical.getMessage());
                    Allure.addAttachment("soak-critical-" + tick, critical.toString());
                    break;
                } catch (Throwable first) {
                    log.warn("Tick #{} failed — recover: {}", tick, first.toString());
                    Allure.addAttachment("soak-tick-" + tick + "-error", first.toString());
                    try {
                        recoverMobileTick(tick, target, keyLog);
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
                    + "\n"
                    + String.join("\n", failures);
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
    }

    private void runMobileSoakTick(int tickNo, SkrSoakTarget target, List<String> keyLog) {
        maybeCreateMore(tickNo);
        walkSendInbound(tickNo, target, keyLog);
    }

    private void walkSendInbound(int tickNo, SkrSoakTarget target, List<String> keyLog) {
        openTarget(target);

        String out = "skr-mob-soak-out-" + tickNo + "-" + System.currentTimeMillis();
        pages.chat().sendTextMessage(out).checkMessageDisplayed(out);

        inboundBestEffort(tickNo, target);

        if (target.isHomespace() && tickNo % 2 == 1) {
            try {
                pages.chat().attachMediaToMessage();
            } catch (Throwable uploadStuck) {
                log.warn("Extra image tick #{} non-fatal: {}", tickNo, uploadStuck.toString());
            }
        }

        keyLog.add("tick #" + tickNo + " " + target.chatName());
        if (!sessionLooksAlive(target.chatName())) {
            throw new SoakCriticalException("session dead after tick #" + tickNo);
        }
    }

    private void openTarget(SkrSoakTarget target) {
        if (pages.chat().isChatScreenDisplayedSoft(target.chatName(), 2L)) {
            return;
        }
        escapeToChatsList();
        if (target.isHomespace()) {
            if (!pages.chats().isChatsListDisplayed(2L)) {
                reopenSpaceByTap(target);
            }
            pages.chats().openChatWhenAppears(target.chatName(), 45L);
            if (!pages.chat().isChatScreenDisplayedSoft(target.chatName(), 15L)) {
                pages.chats().openChatWhenAppears(target.chatName(), 30L);
            }
            return;
        }
        pages.bottomMenu().tapWorkspacesTabForce();
        pages.workspace().checkWorkspaceScreenTitle(target.spaceName());
        pages.chats().openChatWhenAppears(target.chatName(), 45L);
    }

    private void inboundBestEffort(int tickNo, SkrSoakTarget target) {
        String inbound = "skr-mob-soak-in-" + tickNo + "-" + System.currentTimeMillis();
        try {
            target.chat().peerSendText(inbound);
            pages.chat().checkMessageDisplayed(inbound);
            inboundOk++;
        } catch (Throwable inboundFailed) {
            inboundMisses++;
            log.warn("Inbound tick #{} non-fatal: {}", tickNo, inboundFailed.toString());
            Allure.addAttachment("soak-inbound-" + tickNo, inboundFailed.toString());
        }
    }

    private void maybeCreateMore(int tickNo) {
        if (tickNo % CREATE_EVERY_TICKS != 0) {
            return;
        }
        try {
            escapeToChatsList();
            if (!pages.chats().isChatsListDisplayed(3L)) {
                log.warn("Extra create tick #{} skipped — not on chats list", tickNo);
                return;
            }
            if (homespaceCount() < MAX_HOMESPACE_CHATS) {
                seedHomespaceChat();
                return;
            }
            if (spaceCount() < MAX_SPACES) {
                seedSpaceWithGroup();
            }
        } catch (Throwable createFailed) {
            log.warn("Extra create tick #{} non-fatal: {}", tickNo, createFailed.toString());
            reopenSpaceByTap(null);
            escapeToChatsList();
        }
    }

    private void seedHomespaceChat() {
        String chatName = SkrSoakTarget.namePrefix() + "h-" + System.currentTimeMillis();
        createGroupFromPlus(chatName, true);
        SkrSharedChat chat = SkrSharedChat.attachPeerToUiGroup(TEST_USER, PEER_USER, chatName);
        targets.add(new SkrSoakTarget(chatName, null, chat));
        log.info("Seeded homespace chat {}", chatName);
    }

    private void seedSpaceWithGroup() {
        if (spaceCount() >= MAX_SPACES || targets.isEmpty() || targets.get(0).chat() == null) {
            return;
        }
        if (!pages.bottomMenu().isTabBarPresentInTree()) {
            log.warn("Skip space seed — tab bar not in tree");
            return;
        }
        String spaceName = SkrSoakTarget.namePrefix() + "ws-" + System.currentTimeMillis();
        SkrSharedChat.encryptedApi().createWorkspace(targets.get(0).chat().getOwnerSession(), spaceName);

        escapeToChatsList();
        pages.bottomMenu().tapWorkspacesTabForce();
        pages.workspace().checkWorkspaceScreenTitle(spaceName);

        String groupName = SkrSoakTarget.namePrefix() + "wg-" + System.currentTimeMillis();
        createGroupFromPlus(groupName, false);
        SkrSharedChat chat = SkrSharedChat.attachPeerToUiGroup(TEST_USER, PEER_USER, groupName);
        targets.add(new SkrSoakTarget(groupName, spaceName, chat));
        log.info("Seeded space {} / group {}", spaceName, groupName);
    }

    private void createGroupFromPlus(String chatName, boolean homespace) {
        if (homespace) {
            pages.chats().clickPlusBtn();
            pages.createNewMessageFromHomespace()
                    .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                    .clickCreateGroupBtn();
        } else {
            pages.workspace().clickPlusBtn();
            pages.createNewMessageFromWorkspace()
                    .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_WORKSPACE)
                    .clickCreateGroupBtn();
        }
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
            return;
        }
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        if (pages.chat().isChatScreenDisplayedSoft(chatName, 8L)) {
            return;
        }
        if (pages.chats().isChatsListDisplayed(5L)) {
            pages.chats().openChatWhenAppears(chatName, 60L);
            pages.onboarding().clickOnboardingActionButtonIfNeeded();
            return;
        }
        AttachmentUtils.dumpUiState("soak-after-create-unknown-screen");
        org.junit.jupiter.api.Assertions.fail(
                "После создания группы ни экран чата, ни список (soak): " + chatName
        );
    }

    private void recoverMobileTick(int tickNo, SkrSoakTarget target, List<String> keyLog) {
        log.warn("Recover tick #{} — force tab tap / relaunch, do not retry extra create", tickNo);
        reopenSpaceByTap(target);
        boolean onList = escapeToChatsList();
        boolean onChat = pages.chat().isChatScreenDisplayedSoft(target.chatName(), 2L);
        if (!onList && !onChat) {
            log.warn("Recover: UI stuck — relaunch keeping session, tick #{}", tickNo);
            ApplicationUtils.relaunchApplicationKeepingData();
            onList = pages.chats().isChatsListDisplayed(8L);
            onChat = pages.chat().isChatScreenDisplayedSoft(target.chatName(), 2L);
        }
        if (!onList && !onChat && pages.loginWelcome().isWelcomeDisplayedSoft()) {
            log.warn("Recover: welcome shown — UI login, tick #{}", tickNo);
            pages.mobileLogin().loginForCurrentEnv(TEST_USER, false);
            pages.chats().checkChatsScreenTitle(ChatsDictionary.SCREEN_TITLE);
        }
        if (!sessionLooksAlive(target.chatName())) {
            throw new SoakCriticalException("still stuck after relaunch, tick #" + tickNo);
        }
        walkSendInbound(tickNo, target, keyLog);
    }

    private boolean sessionLooksAlive(String chatName) {
        return pages.chat().isChatScreenDisplayedSoft(chatName, 3L)
                || pages.chats().isChatsListDisplayed(3L);
    }

    /**
     * iOS: TabBar в дереве, но {@code visible=false} под оверлеем. Тап по координатам,
     * без Selenide wait-for-visible и без LoaderWaiter.
     */
    private void reopenSpaceByTap(SkrSoakTarget target) {
        if (!pages.bottomMenu().isTabBarPresentInTree()) {
            log.warn("Tab bar not in tree — skip space tap");
            return;
        }
        try {
            if (target != null && !target.isHomespace()) {
                pages.bottomMenu().tapChatsTabForce();
                pages.bottomMenu().tapWorkspacesTabForce();
                return;
            }
            pages.bottomMenu().tapWorkspacesTabForce();
            pages.bottomMenu().tapChatsTabForce();
        } catch (Exception tapFailed) {
            log.warn("Space tab tap failed: {}", tapFailed.toString());
        }
    }

    private boolean escapeToChatsList() {
        reopenSpaceByTap(null);
        if (pages.chats().isChatsListDisplayed(2L)) {
            return true;
        }
        for (int i = 0; i < 2; i++) {
            if (pages.chats().isChatsListDisplayed(1L)) {
                return true;
            }
            try {
                DriverManager.getDriver().navigate().back();
            } catch (Exception e) {
                log.warn("navigate().back() failed: {}", e.getMessage());
            }
        }
        reopenSpaceByTap(null);
        if (pages.chats().isChatsListDisplayed(2L)) {
            return true;
        }
        log.warn("Chats list still missing — relaunch keeping data");
        ApplicationUtils.relaunchApplicationKeepingData();
        return pages.chats().isChatsListDisplayed(8L);
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

    private static final class SoakCriticalException extends RuntimeException {
        SoakCriticalException(String message) {
            super(message);
        }
    }

    private static void keepAliveUntilNextTick(long deadlineMs, long tickSeconds)
            throws InterruptedException {
        long wakeAt = Math.min(deadlineMs, System.currentTimeMillis() + tickSeconds * 1000L);
        while (System.currentTimeMillis() < wakeAt) {
            try {
                DriverManager.getDriver().manage().window().getSize();
            } catch (Exception ignored) {
                // keep Appium session alive
            }
            long remaining = wakeAt - System.currentTimeMillis();
            if (remaining <= 0) {
                break;
            }
            Thread.sleep(Math.min(15_000L, remaining));
        }
    }
}
