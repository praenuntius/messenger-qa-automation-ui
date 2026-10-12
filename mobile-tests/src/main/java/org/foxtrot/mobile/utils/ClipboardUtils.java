package org.foxtrot.mobile.utils;

import io.appium.java_client.clipboard.HasClipboard;
import io.qameta.allure.Step;
import org.foxtrot.mobile.core.driver.DriverManager;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public final class ClipboardUtils {

    private ClipboardUtils() {
    }

    @Step("Проверяю, что в буфере обмена текст '{0}'")
    public static void checkClipboardEquals(String expectedText) {
        Assertions.assertEquals(expectedText, ClipboardUtils.getText(), "Текст в буфере обмена не совпадает с ожидаемым");
    }

    private static String getText() {
        if (isIos()) {
            return String.valueOf(DriverManager.getDriver()
                    .executeScript("mobile: getPasteboard", Map.of("encoding", "utf8"))).trim();
        }
        return ((HasClipboard) DriverManager.getDriver()).getClipboardText().trim();
    }
}
