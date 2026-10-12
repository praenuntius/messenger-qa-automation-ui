package org.foxtrot.uiCore;

import com.codeborne.selenide.Configuration;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.config.WebConfig;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class RemoteChromeDriverInitializer {

    private static final WebConfig config = ConfigFactory.create(WebConfig.class);;

    public static void initDriver() {
        Configuration.remote = config.selenoidHubUrl();
        Configuration.browserVersion = config.browserVersion();
        Configuration.browserCapabilities = getBrowserCapabilities(config.browser());
    }


    public static MutableCapabilities getBrowserCapabilities(String browserName) {
        MutableCapabilities capabilities;
        ChromeOptions chromeOptions = getChromeOptions();
        chromeOptions.setAcceptInsecureCerts(true);

        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.BROWSER, soakQuietLogs() ? Level.WARNING : Level.ALL);
        if (!soakQuietLogs()) {
            // PERFORMANCE Level.ALL копит весь CDP Network.* в JVM — на часовом soak это OOM.
            logs.enable(LogType.PERFORMANCE, Level.ALL);
            logs.enable(LogType.DRIVER, Level.ALL);
        }
        chromeOptions.setCapability("goog:loggingPrefs", logs);

        capabilities = chromeOptions;

        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableVideo", selenoidEnableVideo());
        selenoidOptions.put("enableLog", !soakQuietLogs());
        selenoidOptions.put("sessionTimeout", selenoidSessionTimeout());
        selenoidOptions.put("keepBrowserAlive", true);

        capabilities.setCapability("selenoid:options", selenoidOptions);
        capabilities.setCapability("browserVersion", config.browserVersion());
        capabilities.setCapability("pageLoadStrategy", "eager");
        capabilities.setCapability("unhandledPromptBehavior", "accept");

        return capabilities;
    }

    private static ChromeOptions getChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--remote-allow-origins=*",
                "--no-proxy-server",
                "--disable-extensions",
                "--dns-prefetch-disable",
                "--host-resolver-rules="
                        + "MAP web-preprod.domain.net 10.28.34.200,"
                        + "MAP api-preprod.domain.net 10.28.34.200,"
                        + "MAP pusher-preprod.domain.net 10.28.34.200,"
                        + "MAP fileupload-preprod.domain.net 10.28.34.200,"
                        + "MAP s3-preprod.domain.net 10.28.34.200,"
                        + "MAP web-stage14.domain.net 10.205.98.230,"
                        + "MAP api-stage14.domain.net 10.205.98.230,"
                        + "MAP pusher-stage14.domain.net 10.205.98.230,"
                        + "MAP fileupload-stage14.domain.net 10.205.98.230,"
                        + "MAP s3-stage14.domain.net 10.205.98.230",
                "--user-data-dir=/tmp/chrome-user-data",
                "--verbose",
                "--log-path=/tmp/chrome.log",
                "--disable-gpu",
                "--disable-software-rasterizer",
                "--remote-debugging-port=0",
                "--disable-features=VizDisplayCompositor",
                "--disable-background-timer-throttling",
                "--disable-renderer-backgrounding",
                "--disable-background-networking",
                "--disable-hang-monitor",
                "--disable-popup-blocking",
                "--ignore-certificate-errors",
                "--ignore-ssl-errors-by-default",
                "--disable-web-security",
                "--allow-running-insecure-content"
        );
        return chromeOptions;
    }

    private static boolean soakQuietLogs() {
        return Boolean.parseBoolean(System.getProperty("encryption.soak.quiet.logs", "false"));
    }

    private static boolean selenoidEnableVideo() {
        return Boolean.parseBoolean(System.getProperty("selenoid.enable.video", "true"));
    }

    private static String selenoidSessionTimeout() {
        return System.getProperty("selenoid.session.timeout", "15m");
    }
}
