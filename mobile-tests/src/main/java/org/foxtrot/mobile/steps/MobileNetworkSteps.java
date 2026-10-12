package org.foxtrot.mobile.steps;

import io.qameta.allure.Step;
import org.foxtrot.mobile.network.MitmProxySession;
import org.foxtrot.mobile.network.NetworkCaptureEntry;

import java.util.List;

public class MobileNetworkSteps {

    @Step("Включаю network capture")
    public MobileNetworkSteps start() {
        MitmProxySession.start();
        return this;
    }

    @Step("Выключаю network capture")
    public MobileNetworkSteps stop() {
        MitmProxySession.stop();
        return this;
    }

    public boolean isRunning() {
        return MitmProxySession.isRunning();
    }

    /**
     * Подмена response клиенту по substring URL
     */
    @Step("Подменяю response для URL содержащего '{0}' → HTTP {1}")
    public MobileNetworkSteps mockResponse(String urlContains, int statusCode, String body) {
        MitmProxySession.mockResponse(urlContains, statusCode, body);
        return this;
    }

    /**
     * Request уйдёт на сервер; клиенту вернём 503.
     */
    @Step("Блокирую URL содержащий '{0}' (клиенту 503)")
    public MobileNetworkSteps blockAsServiceUnavailable(String urlContains) {
        MitmProxySession.mockResponse(urlContains, 503, "{\"error\":\"blocked-by-mobile-network-proxy\"}");
        return this;
    }

    public List<NetworkCaptureEntry> getCaptured() {
        return MitmProxySession.getCaptured();
    }
}
