package org.foxtrot.mobile.core.driver.factory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.foxtrot.mobile.core.application.PlatformName;
import org.foxtrot.mobile.core.server.AppiumServer;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverFactory {
    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);

    protected DriverFactory() {
    }

    /**
     * Создание сессии с автоматическим запуском Appium Server
     */
    protected static AppiumDriver createSession(Platform platformName, AppiumDriverLocalService service,
            Capabilities capabilities) {
        try {
            AppiumServer.getInstance().waitForAppiumServer(service.getUrl().toURI());
            PlatformName platform = PlatformName.valueOf(platformName.toString().toUpperCase());
            return switch (platform) {
                case ANDROID -> new AndroidDriver(service, capabilities);
                case IOS -> new IOSDriver(service, capabilities);
            };
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception.getMessage(), exception);
        }
    }
}
