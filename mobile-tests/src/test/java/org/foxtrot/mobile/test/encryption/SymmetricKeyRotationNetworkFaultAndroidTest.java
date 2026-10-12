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
 * Android network-fault SKR (DF-554). Инъекция terminate/throttle на
 * {@code confirmSymmetricKeyUpdate} (после {@code updateSymmetricKeyV2}) — через mitmproxy.
 * <p>
 * Пока скелеты: фиксируют контракт кейсов (зеркало WEB-010/011) и abort’ятся до mitm.
 */
@Tag("encryption-suite")
@Tag("encryption")
@Tag("mobile")
@Tag("android")
@Tag("chaos")
@Tag("network-fault")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SymmetricKeyRotationNetworkFaultAndroidTest extends BaseMobileTest {

    @BeforeEach
    void androidOnly() {
        Assumptions.assumeTrue(
                MobilePlatformResolver.isAndroid(),
                "SKR-AND network-fault tests run only on Android"
        );
    }

    @Test
    @DisplayName("SKR-AND-010: TERMINATE confirmSymmetricKeyUpdate response")
    @Description("""
            Android: TTL/493 → клиентский SCR (updateSymmetricKeyV2 → confirm).
            mitmproxy TERMINATE первого confirm. Logout/fatal нет; pending/desync или recover.
            Ждёт интеграции mitmproxy. Mirror of SKR-WEB-010.
            """)
    void terminateUpdateSymmetricKeyResponse_observeClientOutcome() {
        Allure.step("TODO mitmproxy: terminate first confirmSymmetricKeyUpdate response", () ->
                Assumptions.abort(
                        "Waiting for mitmproxy integration (terminate confirmSymmetricKeyUpdate). "
                                + "Mirror of SKR-WEB-010."
                ));
    }

    @Test
    @DisplayName("SKR-AND-011: timeout receiving confirmSymmetricKeyUpdate")
    @Description("""
            Android: клиентский SCR без API-rotate впереди → mitm THROTTLE confirm
            (hold &gt; 60s+20s). Нет logout; при рассинхроне 494/UI error.
            Ждёт интеграции mitmproxy. Mirror of SKR-WEB-011.
            """)
    void timeoutReceivingUpdateSymmetricKeyResponse_apiMismatch() {
        Allure.step("TODO mitmproxy: throttle confirmSymmetricKeyUpdate response", () ->
                Assumptions.abort(
                        "Waiting for mitmproxy integration (throttle confirmSymmetricKeyUpdate). "
                                + "Mirror of SKR-WEB-011."
                ));
    }
}
