package org.foxtrot.mobile.extensions;

import org.foxtrot.mobile.allure.MobileAllureHelper;
import org.foxtrot.mobile.config.MobilePlatformResolver;
import org.foxtrot.mobile.core.application.ApplicationUtils;
import org.foxtrot.mobile.core.application.PlatformName;
import org.foxtrot.mobile.core.driver.DriverManager;
import org.foxtrot.mobile.core.emulator.EmulatorUtils;
import org.foxtrot.mobile.core.server.AppiumServer;
import org.foxtrot.mobile.network.MitmProxySession;
import org.foxtrot.mobile.utils.AttachmentUtils;
import org.foxtrot.utils.AllureConsoleListener;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;

public class MobileTestLifecycleExtension implements ExecutionCondition, BeforeAllCallback, BeforeEachCallback,
        AfterTestExecutionCallback, TestWatcher {

    private static final Logger logger = LoggerFactory.getLogger(MobileTestLifecycleExtension.class);

    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(MobileTestLifecycleExtension.class);

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (context.getTestMethod().isEmpty()) {
            return ConditionEvaluationResult.enabled("Class-level context");
        }
        if (!MobileAllureHelper.hasAllureIdForCurrentPlatform(context)) {
            return ConditionEvaluationResult.disabled("Тест пропущен тк AllureId не задан для платформы "
                    + MobilePlatformResolver.getPlatform()
            );
        }
        return ConditionEvaluationResult.enabled("Тест доступен для платформы " + MobilePlatformResolver.getPlatform());
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        context.getRoot()
                .getStore(NAMESPACE)
                .getOrComputeIfAbsent("mobile-suite-resource", key -> {
                    beforeSuite();
                    return (ExtensionContext.Store.CloseableResource) this::afterSuite;
                });
        prepareApplicationAndDriver();
    }

    private void beforeSuite() {
        logger.info("Выполняю глобальные подготовительные шаги");
        EmulatorUtils.closeEmulator(PlatformName.ANDROID);
        EmulatorUtils.closeEmulator(PlatformName.IOS);
        EmulatorUtils.startWipedEmulator();
        AppiumServer.getInstance();
    }

    private void afterSuite() {
        logger.info("Выполняю глобальные завершающие шаги");
        DriverManager.quitDriver();
        AppiumServer.stopInstance();
        PlatformName platform = PlatformName.valueOf(MobilePlatformResolver.getPlatform());
        EmulatorUtils.closeEmulator(platform);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        AllureConsoleListener.beginTest(getTestName(context));
        MobileAllureHelper.addAllureLabels(context);
        ApplicationUtils.prepareApplication();
        DriverManager.getDriver();
        ApplicationUtils.launchApplication();
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        context.getExecutionException().ifPresent(throwable -> {
            AttachmentUtils.attachFailureArtifacts();
            logger.error("Тест упал: {}", getTestName(context), throwable);
        });
        try {
            ApplicationUtils.resetApplicationAfterTest();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сбросе приложения после теста", e);
        } finally {
            AllureConsoleListener.clearTestLog();
        }
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        logger.info("Тест успешно выполнен: {}", getTestName(context));
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        logger.error("Тест завершился ошибкой: {}", getTestName(context), cause);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        logger.warn("Тест прерван: {}", getTestName(context), cause);
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        logger.info("Тест пропущен: {}. Причина: {}", getTestName(context), reason.orElse("не указана"));
    }

    private String getTestName(ExtensionContext context) {
        return context.getTestClass()
                .map(Class::getSimpleName)
                .orElse("UnknownClass") + "." + context.getTestMethod()
                .map(Method::getName)
                .orElse("UnknownMethod");
    }

    private void prepareApplicationAndDriver() {
        ApplicationUtils.prepareApplication();
        DriverManager.getDriver();
        ApplicationUtils.launchApplication();
    }
}
