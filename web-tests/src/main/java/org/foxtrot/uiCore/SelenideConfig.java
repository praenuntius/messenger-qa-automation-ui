package org.foxtrot.uiCore;

import com.codeborne.selenide.Configuration;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.config.RunChannelResolver;
import org.foxtrot.config.WebConfig;

import static java.lang.Boolean.parseBoolean;

public class SelenideConfig {

    private static final WebConfig config = ConfigFactory.create(WebConfig.class);
    private static final boolean IS_REMOTE = parseBoolean(
            System.getProperty("remote", "false")
    );

    public static void configure() {
        Configuration.browserSize = "1920x1080";
        Configuration.reportsFolder = "reports/screenshots";
        Configuration.timeout = 30000;
        Configuration.pageLoadTimeout = 30000;
        Configuration.pollingInterval = 200;
        Configuration.screenshots = true;
        Configuration.savePageSource = true;

        if (RunChannelResolver.isDesktop()) {
            // WebDriver подключается к Electron в DesktopChannelExtension
            return;
        }

        // Web: eager — ждём DOMContentLoaded; при зависании рендерера — retry в openStartPage().
        Configuration.pageLoadStrategy = "eager";
        Configuration.pageLoadTimeout = 45000;

        Configuration.browser = config.browser();

        if (IS_REMOTE) {
            RemoteChromeDriverInitializer.initDriver();
        } else if (java.util.Objects.equals("chrome", config.browser())) {
            LocalChromeDriverInitializer.initDriver();
        }
    }
}
