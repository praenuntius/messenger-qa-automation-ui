package org.foxtrot.mobile.test.encryption;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import org.foxtrot.mobile.config.MobilePlatformResolver;
import org.foxtrot.mobile.test.BaseMobileTest;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * iOS network-fault SKR (DF-554). Инъекция на {@code confirmSymmetricKeyUpdate}
 * после {@code updateSymmetricKeyV2} — через mitmproxy. Зеркало WEB-010/011.
 */
@Tag("encryption-suite")
@Tag("encryption")
@Tag("mobile")
@Tag("ios")
@Tag("chaos")
@Tag("network-fault")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SymmetricKeyRotationNetworkFaultIosTest extends BaseMobileTest {

    @BeforeEach
    void iosOnly() {
        Assumptions.assumeTrue(
                MobilePlatformResolver.isIos(),
                "SKR-IOS network-fault tests run only on iOS"
        );
    }

    @Test
    @DisplayName("SKR-IOS-010: TERMINATE confirmSymmetricKeyUpdate response")
    @Description("""
            iOS: TTL/493 → SCR (updateSymmetricKeyV2 → confirm). mitm TERMINATE confirm.
            Logout/fatal нет. Mirror of SKR-WEB-010. Ждёт mitmproxy.
            """)
    void terminateUpdateSymmetricKeyResponse_observeClientOutcome() {
        Allure.step("TODO mitmproxy: terminate first confirmSymmetricKeyUpdate response", () ->
                Assumptions.abort(
                        "Waiting for mitmproxy integration (terminate confirmSymmetricKeyUpdate). "
                                + "Mirror of SKR-WEB-010."
                ));
    }

    @Test
    @DisplayName("SKR-IOS-011: timeout receiving confirmSymmetricKeyUpdate")
    @Description("""
            iOS: клиентский SCR без API-rotate → mitm THROTTLE confirm (hold &gt; 60s+20s).
            Нет logout. Mirror of SKR-WEB-011. Ждёт mitmproxy.
            """)
    void timeoutReceivingUpdateSymmetricKeyResponse_apiMismatch() {
        Allure.step("TODO mitmproxy: throttle confirmSymmetricKeyUpdate response", () ->
                Assumptions.abort(
                        "Waiting for mitmproxy integration (throttle confirmSymmetricKeyUpdate). "
                                + "Mirror of SKR-WEB-011."
                ));
    }
}
