package org.foxtrot.desktop.extensions;

import org.foxtrot.desktop.core.DesktopSuiteLifecycle;
import org.foxtrot.desktop.playwright.PlaywrightManager;
import org.foxtrot.desktop.session.DesktopSessionManager;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Lifecycle для Playwright-тестов в desktop-tests.
 * Для общих web-тестов на Electron используйте {@code -Drun.channel=desktop} в web-tests.
 */
public class DesktopTestLifecycleExtension implements BeforeAllCallback, BeforeEachCallback,
        AfterEachCallback, AfterTestExecutionCallback, TestWatcher {

    private static final Logger logger = LoggerFactory.getLogger(DesktopTestLifecycleExtension.class);

    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(DesktopTestLifecycleExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) {
        context.getRoot()
                .getStore(NAMESPACE)
                .getOrComputeIfAbsent("desktop-suite-resource", key -> {
                    DesktopSuiteLifecycle.beforeSuite();
                    return (ExtensionContext.Store.CloseableResource) DesktopSuiteLifecycle::afterSuite;
                });
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        logger.info("→ beforeEach: {}", testName(context));
        DesktopSessionManager.acquireForCurrentThread();
        PlaywrightManager.connect();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        logger.info("← afterEach: {}", testName(context));
        try {
            PlaywrightManager.disconnect();
        } catch (Exception e) {
            logger.error("Ошибка при отключении Playwright: {}", e.getMessage(), e);
        } finally {
            DesktopSessionManager.releaseCurrentThread();
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        context.getExecutionException().ifPresent(e ->
                logger.error("Тест упал: {}", testName(context), e));
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        logger.info("✓ Тест успешен: {}", testName(context));
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        logger.error("✗ Тест упал: {}", testName(context), cause);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        logger.warn("~ Тест прерван: {}", testName(context), cause);
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        logger.info("- Тест пропущен: {}. Причина: {}", testName(context), reason.orElse("не указана"));
    }

    private static String testName(ExtensionContext context) {
        String clazz = context.getTestClass().map(Class::getSimpleName).orElse("?");
        String method = context.getTestMethod().map(Method::getName).orElse("?");
        return clazz + "." + method;
    }
}
