package org.foxtrot.uiCore;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.util.logging.Level;

public class LocalChromeDriverInitializer {

    public static void initDriver() {
        Configuration.browserCapabilities = getBrowserCapabilities();
    }

    public static MutableCapabilities getBrowserCapabilities() {
        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);

        LoggingPreferences logs = new LoggingPreferences();
        boolean soakQuiet = Boolean.parseBoolean(System.getProperty("encryption.soak.quiet.logs", "false"));
        logs.enable(LogType.BROWSER, soakQuiet ? Level.WARNING : Level.ALL);
        if (!soakQuiet) {
            logs.enable(LogType.PERFORMANCE, Level.ALL);
            logs.enable(LogType.DRIVER, Level.ALL);
        }
        options.setCapability("goog:loggingPrefs", logs);

        return options;
    }
}

