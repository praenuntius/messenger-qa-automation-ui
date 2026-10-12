package org.foxtrot.uiCore;

import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.desktop.config.DesktopConfig;
import org.foxtrot.desktop.session.DesktopSession;
import org.foxtrot.desktop.utils.CdpBrowserVersionResolver;
import org.foxtrot.desktop.utils.ChromeDriverResolver;
import org.foxtrot.utils.Waits;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Подключает Selenide/Selenium к уже запущенному Electron по CDP.
 */
public final class DesktopSelenideDriverFactory {

    private static final Logger logger = LoggerFactory.getLogger(DesktopSelenideDriverFactory.class);
    private static final int CONNECT_RETRIES = 3;
    private static final DesktopConfig config = ConfigFactory.create(DesktopConfig.class);

    private DesktopSelenideDriverFactory() {
    }

    public static WebDriver connect(DesktopSession session) {
        String debuggerAddress = "127.0.0.1:" + session.getCdpPort();
        String browserVersion = resolveBrowserVersion(session);
        Path chromeDriver = ChromeDriverResolver.resolveExecutable(browserVersion);
        logger.info("Подключаю Selenide к Electron CDP: {}, chromium={}, chromedriver={}",
                debuggerAddress, browserVersion, chromeDriver);

        ChromeOptions options = buildOptions(debuggerAddress);
        ChromeDriverService service = new ChromeDriverService.Builder()
                .usingDriverExecutable(chromeDriver.toFile())
                .build();

        Exception lastException = null;
        for (int attempt = 1; attempt <= CONNECT_RETRIES; attempt++) {
            try {
                return new ChromeDriver(service, options);
            } catch (Exception e) {
                lastException = e;
                logger.warn("Попытка {}/{} подключения к CDP не удалась: {}", attempt, CONNECT_RETRIES, e.getMessage());
                if (attempt < CONNECT_RETRIES) {
                    Waits.sleepMillis(1000);
                }
            }
        }
        throw new IllegalStateException(
                "Не удалось подключить ChromeDriver к Electron CDP " + debuggerAddress, lastException);
    }

    private static ChromeOptions buildOptions(String debuggerAddress) {
        ChromeOptions options = new ChromeOptions();
        // Не вызываем setBrowserVersion — иначе Selenium Manager ищет драйвер сам
        options.setExperimentalOption("debuggerAddress", debuggerAddress);
        options.addArguments("--remote-allow-origins=*");
        return options;
    }

    private static String resolveBrowserVersion(DesktopSession session) {
        String configured = config.chromeDriverBrowserVersion();
        if (configured != null && !configured.isBlank()) {
            logger.info("Использую desktop.chromedriver.browser.version из конфига: {}", configured);
            return configured.trim();
        }
        if (session.getChromiumVersion() != null && !session.getChromiumVersion().isBlank()) {
            return session.getChromiumVersion();
        }
        return CdpBrowserVersionResolver.resolve(session.getCdpPort());
    }
}
