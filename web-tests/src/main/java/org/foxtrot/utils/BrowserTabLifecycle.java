package org.foxtrot.utils;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;

/**
 * Имитация сворачивания вкладки для web SKR lifecycle
 * (visibilitychange — типичный триггер pause/reconnect SSE).
 */
public final class BrowserTabLifecycle {

    private BrowserTabLifecycle() {
    }

    @Step("Скрыть вкладку (document.hidden = true)")
    public static void hideTab() {
        Selenide.executeJavaScript("""
                Object.defineProperty(document, 'hidden', {configurable: true, get: () => true});
                Object.defineProperty(document, 'visibilityState', {configurable: true, get: () => 'hidden'});
                document.dispatchEvent(new Event('visibilitychange'));
                """);
    }

    @Step("Показать вкладку (document.hidden = false)")
    public static void showTab() {
        Selenide.executeJavaScript("""
                Object.defineProperty(document, 'hidden', {configurable: true, get: () => false});
                Object.defineProperty(document, 'visibilityState', {configurable: true, get: () => 'visible'});
                document.dispatchEvent(new Event('visibilitychange'));
                """);
    }
}
