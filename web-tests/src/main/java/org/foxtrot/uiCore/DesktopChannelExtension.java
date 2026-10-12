package org.foxtrot.uiCore;

import com.codeborne.selenide.WebDriverRunner;
import org.foxtrot.config.RunChannelResolver;
import org.foxtrot.desktop.core.DesktopSuiteLifecycle;
import org.foxtrot.desktop.session.DesktopSession;
import org.foxtrot.desktop.session.DesktopSessionManager;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * JUnit extension для запуска web-тестов на Electron (Selenide + CDP).
 * Активен только при {@code -Drun.channel=desktop}.
 * <p>
 * Playwright-тесты в desktop-tests используют {@link org.foxtrot.desktop.extensions.DesktopTestLifecycleExtension}.
 */
public class DesktopChannelExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback {

    private static final Logger logger = LoggerFactory.getLogger(DesktopChannelExtension.class);

    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(DesktopChannelExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!RunChannelResolver.isDesktop()) {
            return;
        }
        String os = System.getProperty("os.name", "");
        if (!os.toLowerCase().contains("mac")) {
            throw new IllegalStateException(
                    "run.channel=desktop требует macOS (hdiutil). "
                            + "Web E2E на Linux: -Pstage14 или -Drun.channel=web. os=" + os);
        }
        context.getRoot()
                .getStore(NAMESPACE)
                .getOrComputeIfAbsent("desktop-channel-suite", key -> {
                    DesktopSuiteLifecycle.beforeSuite();
                    return (ExtensionContext.Store.CloseableResource) DesktopSuiteLifecycle::afterSuite;
                });
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        if (!RunChannelResolver.isDesktop()) {
            return;
        }
        logger.info("→ desktop channel beforeEach: {}", testName(context));

        DesktopSession session = DesktopSessionManager.acquireForCurrentThread();
        WebDriver driver = DesktopSelenideDriverFactory.connect(session);
        WebDriverRunner.setWebDriver(driver);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (!RunChannelResolver.isDesktop()) {
            return;
        }
        logger.info("← desktop channel afterEach: {}", testName(context));
        try {
            WebDriverRunner.closeWebDriver();
        } catch (Exception e) {
            logger.warn("Ошибка при закрытии WebDriver: {}", e.getMessage());
        } finally {
            DesktopSessionManager.releaseCurrentThread();
        }
    }

    private static String testName(ExtensionContext context) {
        String clazz = context.getTestClass().map(Class::getSimpleName).orElse("?");
        String method = context.getTestMethod().map(Method::getName).orElse("?");
        return clazz + "." + method;
    }
}
