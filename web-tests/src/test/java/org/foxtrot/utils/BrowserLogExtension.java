package org.foxtrot.utils;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 Extension: сохраняет логи браузера после каждого теста.
 * Выполняется после @AfterEach, поэтому переопределение tearDown в подклассах не блокирует захват логов.
 */
public class BrowserLogExtension implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) {
        String className = context.getTestClass().map(Class::getSimpleName).orElse("Unknown");
        String methodName = context.getTestMethod().map(m -> m.getName()).orElse("unknown");
        BrowserLogCapture.saveBrowserLogs(className, methodName);
        Selenide.closeWebDriver();
    }
}
