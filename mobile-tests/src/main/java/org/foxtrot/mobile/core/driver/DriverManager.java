package org.foxtrot.mobile.core.driver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.mobile.config.MobileConfig;
import org.foxtrot.mobile.core.application.capabilities.CapabilitiesCreator;
import org.foxtrot.mobile.core.driver.factory.DriverFactory;
import org.foxtrot.mobile.core.server.AppiumServer;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public final class DriverManager extends DriverFactory {
    private static final ThreadLocal<AppiumDriver> THREAD_DRIVER = new ThreadLocal<>();
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static final MobileConfig config = ConfigFactory.create(MobileConfig.class);

    private DriverManager() {
        super();
    }

    /**
     * Возвращение объекта драйвера текущего потока
     * <p>
     * Если driver == null, создается новая сессия
     */
    public static AppiumDriver getDriver() {
        AppiumDriver driver = THREAD_DRIVER.get();
        if (driver == null || driver.getSessionId() == null) {
            driver = createDriver();
            THREAD_DRIVER.set(driver);
        }
        return driver;
    }

    /**
     * Завершение сессии драйвера
     */
    public static void quitDriver() {
        AppiumDriver driver = THREAD_DRIVER.get();
        try {
            if (driver != null) {
                try {
                    logger.info("Закрываем Selenide/Appium driver для thread: {}", Thread.currentThread().getName());
                    Selenide.closeWebDriver();
                } catch (Exception e) {
                    logger.error("Ошибка при закрытии Selenide driver: {}", e.getMessage(), e);
                    try {
                        driver.quit();
                    } catch (Exception quitException) {
                        logger.error("Ошибка при driver.quit(): {}", quitException.getMessage(), quitException);
                    }
                }
            }
        } finally {
            THREAD_DRIVER.remove();
        }
    }

    private static AppiumDriver createDriver() {
        try {
            DesiredCapabilities capabilities = CapabilitiesCreator.getCapabilities();
            AppiumDriverLocalService appiumServerRef = AppiumServer.getInstance().get();
            AppiumDriver driver = DriverFactory.createSession(capabilities.getPlatformName(), appiumServerRef,
                    capabilities);
            WebDriverRunner.setWebDriver(driver);
            Configuration.timeout = Duration.ofSeconds(config.explicitWaitSeconds()).toMillis();
            Configuration.screenshots = false;
            Configuration.savePageSource = false;
            logger.info("Создали AppiumDriver для thread: {}", Thread.currentThread().getName());
            return driver;
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка при попытке создать AppiumDriver: " + e.getMessage(), e);
        }
    }

    public static boolean hasDriver() {
        AppiumDriver driver = THREAD_DRIVER.get();
        if (driver == null) {
            return false;
        }
        try {
            return driver.getSessionId() != null;
        } catch (Exception e) {
            return false;
        }
    }
}

