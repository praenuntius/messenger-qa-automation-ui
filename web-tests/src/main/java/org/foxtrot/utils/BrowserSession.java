package org.foxtrot.utils;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.config.WebConfig;
import org.foxtrot.uiCore.LocalChromeDriverInitializer;
import org.foxtrot.uiCore.RemoteChromeDriverInitializer;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Supplier;

/**
 * Второй браузер в тесте и переключение активного WebDriver в Selenide для page objects.
 */
public final class BrowserSession implements AutoCloseable {

    private final WebDriver driver;

    private BrowserSession(WebDriver driver) {
        this.driver = driver;
    }

    public static BrowserSession open() {
        return new BrowserSession(createWebDriver());
    }

    public void run(Runnable action) {
        with(driver, () -> { action.run(); return null; });
    }

    public <T> T call(Supplier<T> action) {
        return with(driver, action);
    }

    public static void with(WebDriver driver, Runnable action) {
        with(driver, () -> { action.run(); return null; });
    }

    public static <T> T with(WebDriver driver, Supplier<T> action) {
        WebDriver previous = WebDriverRunner.getWebDriver();
        try {
            WebDriverRunner.setWebDriver(driver);
            return action.get();
        } finally {
            WebDriverRunner.setWebDriver(previous);
        }
    }

    @Override
    public void close() {
        driver.quit();
    }

    private static WebDriver createWebDriver() {
        String remote = Configuration.remote;
        if (remote != null && !remote.isBlank()) {
            WebConfig config = ConfigFactory.create(WebConfig.class);
            MutableCapabilities capabilities = RemoteChromeDriverInitializer.getBrowserCapabilities(config.browser());
            try {
                return new RemoteWebDriver(new URL(remote), capabilities);
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Invalid remote URL: " + remote, e);
            }
        }
        ChromeOptions options = (ChromeOptions) LocalChromeDriverInitializer.getBrowserCapabilities();
        return new ChromeDriver(options);
    }
}