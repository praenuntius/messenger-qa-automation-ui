package org.foxtrot.test.encryption;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.api.dto.encryption.SymmetricKeyUpdateRequest;
import org.foxtrot.api.service.EncryptionApiService;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.BrowserEncryptionSession;
import org.foxtrot.utils.BrowserTabLifecycle;
import org.foxtrot.utils.EncryptionHelper;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.SkrSharedChat;
import org.foxtrot.utils.SkrTestDataCleaner;
import org.foxtrot.utils.SymmetricKeyMaterial;
import org.foxtrot.utils.SymmetricKeySession;
import org.foxtrot.utils.UpdateSymmetricKeyNetworkObserver;
import org.foxtrot.utils.UpdateSymmetricKeyResponseFaultInjector;
import org.foxtrot.utils.UpdateSymmetricKeyResponseFaultInjector.Mode;
import org.foxtrot.utils.UpdateSymmetricKeyResponseFaultInjector.Target;
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
import java.util.Map;
import java.util.Objects;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.By.cssSelector;

/**
 * Network-fault SKR на Web (DF-554): terminate / throttle {@code confirmSymmetricKeyUpdate}
 * после клиентского SCR (V2 → confirm). Fatal/logout не ожидаются.
 */
@ExtendWith(AllureJunit5.class)
@Tag("encryption-suite")
@Tag("encryption")
@Tag("web")
@Tag("chaos")
@Tag("network-fault")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SymmetricKeyRotationNetworkFaultWebTest extends BaseTest {

    private static final UsersEnum TEST_USER = SkrSharedChat.resolveOwner();
    private static final UsersEnum PEER_USER = SkrSharedChat.resolvePeer();
    private static final int HTTP_KEY_MISMATCH = 494;

    private LoginHelper loginHelper;
    private final EncryptionApiService encryptionApi = new EncryptionApiService();
    private final EncryptionHelper encryptionHelper = new EncryptionHelper();
    private final SkrTestDataCleaner dataCleaner = new SkrTestDataCleaner();

    @BeforeEach
    void init() {
        loginHelper = new LoginHelper();
    }

    @AfterEach
    void afterEach() {
        try {
            UpdateSymmetricKeyResponseFaultInjector.uninstall();
        } catch (Exception ignored) {
            // driver may already be gone
        }
    }

    @AfterAll
    void afterAllClean() {
        dataCleaner.clean(TEST_USER, PEER_USER);
    }

    @Test
    @Tag("key-ttl")
    @DisplayName("SKR-WEB-010: TERMINATE confirmSymmetricKeyUpdate response")
    @Description("""
            TTL/493 → клиентский SCR: updateSymmetricKeyV2 (200) → confirmSymmetricKeyUpdate.
            Первый confirm TERMINATE (сервер мог принять, клиент body не получил).
            Current в localStorage не должен переключиться без confirm 200; logout/fatal нет.
            Допустим pending + desync или поздний recover через повторный confirm.
            """)
    void terminateConfirmSymmetricKeyUpdate_observeClientOutcome() throws InterruptedException {
        long holdMs = Long.parseLong(System.getProperty("encryption.skr.abort.hold.ms", "80000"));
        long observeSec = Long.parseLong(System.getProperty("encryption.skr.logout.wait.sec", "60"));
        long ttlMinutes = Long.parseLong(System.getProperty("encryption.key.ttl.minutes", "1"));

        Allure.step("UI-логин owner", () -> loginHelper.performUILoginForTest(TEST_USER));
        long keyBefore = requireMessageKeyId();

        Allure.step("Install TERMINATE on confirmSymmetricKeyUpdate (holdMs unused=" + holdMs + ")", () ->
                UpdateSymmetricKeyResponseFaultInjector.install(
                        Mode.TERMINATE_FIRST_RESPONSE, Target.CONFIRM, holdMs));
        UpdateSymmetricKeyNetworkObserver.clearPerformanceLogBuffer();

        Allure.step("Ждём TTL ≥ " + ttlMinutes + " мин (триггер клиентского SCR)", () ->
                waitKeepingSessionAlive(Duration.ofMinutes(ttlMinutes).plusSeconds(20)));

        Allure.step("Nudge: Saved Messages send + hide/show", () -> {
            try {
                pages.chatListSteps.clickSavedChat();
                pages.inputBarSteps.verifyInputBarIsVisible();
                String nudge = "skr-web-010-nudge-" + System.currentTimeMillis();
                pages.inputBarSteps.enterMessage(nudge);
                pages.inputBarSteps.clickSendMessageButton();
            } catch (Throwable t) {
                Allure.step("Nudge send skipped: " + t.getMessage());
            }
            BrowserTabLifecycle.hideTab();
            Thread.sleep(2_000L);
            BrowserTabLifecycle.showTab();
        });

        Outcome outcome = Allure.step("Observe client outcome до " + observeSec + "с",
                () -> observeAfterConfirmFault(keyBefore, Duration.ofSeconds(observeSec)));

        Map<String, Object> stats = UpdateSymmetricKeyResponseFaultInjector.stats();
        List<String> netEvents = UpdateSymmetricKeyNetworkObserver.drainRotationEvents();
        Long pending = BrowserEncryptionSession.readPendingEncryptKeyId();
        Long keyAfter = BrowserEncryptionSession.readMessageKeyId();
        boolean loggedOut = isLoginScreenVisible(3);

        Allure.addAttachment("injector-stats", String.valueOf(stats));
        Allure.addAttachment("perf-rotation-events", String.join("\n---\n", netEvents));
        Allure.addAttachment("client-state",
                "loggedOut=" + loggedOut
                        + ", keyBefore=" + keyBefore
                        + ", keyAfter=" + keyAfter
                        + ", pending=" + pending
                        + ", outcome=" + outcome.kind() + " / " + outcome.detail());
        Allure.addAttachment("outcome", outcome.kind() + " / " + outcome.detail());

        Allure.step("Нет logout / fatal после terminate confirm", () ->
                assertFalse(loggedOut, "DF-554: failed confirm must not force logout"));

        Allure.step("Injector: confirm был перехвачен (или видны rotation events)", () ->
                assertTrue(
                        UpdateSymmetricKeyResponseFaultInjector.interceptedCount() >= 1
                                || UpdateSymmetricKeyResponseFaultInjector.terminatedCount() >= 1
                                || UpdateSymmetricKeyNetworkObserver.countConfirm(netEvents) >= 1
                                || UpdateSymmetricKeyNetworkObserver.countUpdateV2(netEvents) >= 1,
                        "Ожидали confirm/V2. stats=" + stats + ", perfHits=" + netEvents.size()
                ));

        Allure.step("Исход: pending/desync или recover без logout: " + outcome.kind(), () ->
                assertTrue(
                        outcome.kind() == OutcomeKind.PENDING_OR_DESYNC
                                || outcome.kind() == OutcomeKind.ROTATION_RECOVERED,
                        "Ожидали pending/desync или recover, got " + outcome.kind()
                                + ": " + outcome.detail()
                ));
    }

    @Test
    @Tag("key-ttl")
    @DisplayName("SKR-WEB-011: timeout receiving confirmSymmetricKeyUpdate")
    @Description("""
            Клиентский SCR (TTL/493): updateV2 200 → confirm THROTTLE holdMs = 60s+20s.
            Без API-rotate «за спиной». После timeout: нет logout; current может остаться
            старым / pending; REST от ключа клиента → 494/4xx при рассинхроне.
            """)
    void timeoutReceivingConfirm_noLogout_mismatchOrPending() throws InterruptedException {
        long holdMs = Long.parseLong(System.getProperty("encryption.skr.abort.hold.ms", "80000"));
        long retryWaitMs = Long.parseLong(System.getProperty("encryption.skr.client.retry.wait.ms", "100000"));
        long ttlMinutes = Long.parseLong(System.getProperty("encryption.key.ttl.minutes", "1"));

        Allure.step("UI-логин owner", () -> loginHelper.performUILoginForTest(TEST_USER));
        long keyBefore = requireMessageKeyId();

        Allure.step("Install THROTTLE on confirmSymmetricKeyUpdate holdMs=" + holdMs, () ->
                UpdateSymmetricKeyResponseFaultInjector.install(
                        Mode.THROTTLE_FIRST_RESPONSE, Target.CONFIRM, holdMs));
        UpdateSymmetricKeyNetworkObserver.clearPerformanceLogBuffer();

        Allure.step("Ждём TTL ≥ " + ttlMinutes + " мин (клиентский SCR, без API-rotate)", () ->
                waitKeepingSessionAlive(Duration.ofMinutes(ttlMinutes).plusSeconds(20)));

        Allure.step("Nudge: Saved Messages send + hide/show", () -> {
            pages.chatListSteps.clickSavedChat();
            pages.inputBarSteps.verifyInputBarIsVisible();
            String nudge = "skr-web-011-nudge-" + System.currentTimeMillis();
            pages.inputBarSteps.enterMessage(nudge);
            pages.inputBarSteps.clickSendMessageButton();
            BrowserTabLifecycle.hideTab();
            Thread.sleep(2_000L);
            BrowserTabLifecycle.showTab();
        });

        Allure.step("Ждём окно throttle/retry " + retryWaitMs + " мс", () ->
                waitKeepingSessionAlive(Duration.ofMillis(retryWaitMs)));

        Map<String, Object> stats = UpdateSymmetricKeyResponseFaultInjector.stats();
        List<String> netEvents = UpdateSymmetricKeyNetworkObserver.drainRotationEvents();
        boolean loggedOut = isLoginScreenVisible(5);
        Long keyAfter = BrowserEncryptionSession.readMessageKeyId();
        Long pending = BrowserEncryptionSession.readPendingEncryptKeyId();

        Allure.addAttachment("injector-stats", String.valueOf(stats));
        Allure.addAttachment("perf-rotation-events", String.join("\n---\n", netEvents));
        Allure.addAttachment(
                "client-state",
                "loggedOut=" + loggedOut
                        + ", keyBefore=" + keyBefore
                        + ", keyAfter=" + keyAfter
                        + ", pending=" + pending
                        + ", intercepted=" + UpdateSymmetricKeyResponseFaultInjector.interceptedCount()
                        + ", throttled=" + UpdateSymmetricKeyResponseFaultInjector.throttledCount()
                        + ", passed(retries)=" + UpdateSymmetricKeyResponseFaultInjector.passedCount()
                        + ", confirmEvents=" + UpdateSymmetricKeyNetworkObserver.countConfirm(netEvents)
                        + ", v2Events=" + UpdateSymmetricKeyNetworkObserver.countUpdateV2(netEvents)
        );

        Allure.step("Нет logout после throttle confirm", () ->
                assertFalse(loggedOut, "DF-554: confirm timeout must not force logout"));

        Allure.step("Throttle/intercept confirm сработал или видны rotation events", () ->
                assertTrue(
                        UpdateSymmetricKeyResponseFaultInjector.throttledCount() >= 1
                                || UpdateSymmetricKeyResponseFaultInjector.interceptedCount() >= 1
                                || UpdateSymmetricKeyNetworkObserver.countConfirm(netEvents) >= 1
                                || UpdateSymmetricKeyNetworkObserver.countUpdateV2(netEvents) >= 1,
                        "Не увидели throttle/confirm/V2. stats=" + stats
                ));

        if (keyAfter != null && Objects.equals(keyAfter, keyBefore) && pending == null
                && UpdateSymmetricKeyResponseFaultInjector.passedCount() >= 1) {
            Allure.step("Confirm retry успел пройти — current мог восстановиться; mismatch API skip");
            return;
        }

        if (keyAfter != null) {
            SymmetricKeySession clientView = BrowserEncryptionSession.fromLocalStorage(TEST_USER);
            var mismatch = Allure.step(
                    "API: updateSymmetricKeyV2 от ключа как в UI → ожидаем 494 при рассинхроне",
                    () -> {
                        SymmetricKeyMaterial candidate = encryptionHelper.generateAndSaveSymmetricKey(
                                clientView.getAccessToken(),
                                clientView.getMessageKey().getId()
                        );
                        return encryptionApi.updateSymmetricKeyV2(
                                new SymmetricKeyUpdateRequest(candidate.getId()),
                                clientView.getAccessToken(),
                                clientView.getMessageKey()
                        );
                    });
            Allure.addAttachment("api-v2-from-client-key-status",
                    mismatch.statusCode() + "\n" + mismatch.asString());
            Allure.step("Ожидаем 494/4xx или 200 если клиент уже confirm'нул", () -> {
                int code = mismatch.statusCode();
                assertTrue(
                        code == HTTP_KEY_MISMATCH || code >= 400 || code == 200 || code == 204,
                        "Unexpected status=" + code + ", body=" + mismatch.asString()
                );
                if (pending != null || Objects.equals(keyAfter, keyBefore)) {
                    assertTrue(
                            code == HTTP_KEY_MISMATCH || code >= 400,
                            "При pending/stale current ожидали 494/4xx, actual=" + code
                                    + ", body=" + mismatch.asString()
                    );
                }
            });
        }
    }

    private static long requireMessageKeyId() {
        Long id = BrowserEncryptionSession.readMessageKeyId();
        if (id == null) {
            throw new IllegalStateException("encryptKey.id missing after UI login");
        }
        return id;
    }

    private Outcome observeAfterConfirmFault(long keyBefore, Duration budget) throws InterruptedException {
        long deadline = System.currentTimeMillis() + budget.toMillis();
        while (System.currentTimeMillis() < deadline) {
            if (isLoginScreenVisible(2)) {
                return new Outcome(OutcomeKind.LOGGED_OUT, "phone input visible — unexpected for DF-554");
            }
            Long pending = BrowserEncryptionSession.readPendingEncryptKeyId();
            Long now = BrowserEncryptionSession.readMessageKeyId();
            if (now != null && now != keyBefore) {
                try {
                    pages.chatListSteps.clickSavedChat();
                    String msg = "skr-web-010-recover-" + System.currentTimeMillis();
                    pages.inputBarSteps.enterMessage(msg);
                    pages.inputBarSteps.clickSendMessageButton();
                    pages.chatVerify.verifyMessageSent(msg);
                    return new Outcome(
                            OutcomeKind.ROTATION_RECOVERED,
                            "key " + keyBefore + "→" + now + ", send ok"
                    );
                } catch (Throwable sendFailed) {
                    return new Outcome(
                            OutcomeKind.PENDING_OR_DESYNC,
                            "key changed " + keyBefore + "→" + now
                                    + " but send failed: " + sendFailed.getMessage()
                    );
                }
            }
            if (pending != null || (now != null && now == keyBefore
                    && UpdateSymmetricKeyResponseFaultInjector.terminatedCount() >= 1)) {
                // keep observing for possible late recover; remember desync state
            }
            try {
                Selenide.executeJavaScript("return document.title;");
            } catch (Exception ignored) {
                // keep polling
            }
            Thread.sleep(3_000L);
        }

        if (isLoginScreenVisible(3)) {
            return new Outcome(OutcomeKind.LOGGED_OUT, "login after budget");
        }
        Long finalKey = BrowserEncryptionSession.readMessageKeyId();
        Long pending = BrowserEncryptionSession.readPendingEncryptKeyId();
        Map<String, Object> stats = UpdateSymmetricKeyResponseFaultInjector.stats();
        if (pending != null
                || (finalKey != null && finalKey.equals(keyBefore)
                && UpdateSymmetricKeyResponseFaultInjector.terminatedCount() >= 1)) {
            return new Outcome(
                    OutcomeKind.PENDING_OR_DESYNC,
                    "key=" + finalKey + ", pending=" + pending + ", stats=" + stats
            );
        }
        if (finalKey != null && !finalKey.equals(keyBefore)) {
            return new Outcome(
                    OutcomeKind.ROTATION_RECOVERED,
                    "key " + keyBefore + "→" + finalKey + " (no send verify in budget)"
            );
        }
        return new Outcome(
                OutcomeKind.UNKNOWN_HUNG,
                "no pending/key change; key=" + finalKey + "; stats=" + stats
        );
    }

    private static boolean isLoginScreenVisible(int seconds) {
        try {
            $(cssSelector("[data-cy='signInPhoneInput']"))
                    .shouldBe(visible, Duration.ofSeconds(seconds));
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private static void waitKeepingSessionAlive(Duration duration) throws InterruptedException {
        long deadline = System.currentTimeMillis() + duration.toMillis();
        while (System.currentTimeMillis() < deadline) {
            try {
                Selenide.executeJavaScript("return document.title;");
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

    private enum OutcomeKind {
        LOGGED_OUT,
        ROTATION_RECOVERED,
        PENDING_OR_DESYNC,
        UNKNOWN_HUNG
    }

    private record Outcome(OutcomeKind kind, String detail) {
    }
}
