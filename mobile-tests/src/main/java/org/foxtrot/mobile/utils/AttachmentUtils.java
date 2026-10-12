package org.foxtrot.mobile.utils;

import com.codeborne.selenide.WebDriverRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.qameta.allure.Allure;
import org.foxtrot.mobile.network.MitmProxySession;
import org.foxtrot.utils.AllureConsoleListener;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;

public final class AttachmentUtils {

    private AttachmentUtils() {
    }

    /**
     * Screenshot в Allure
     */
    public static void attachScreenshot(String name) {
        if (!WebDriverRunner.hasWebDriverStarted()) {
            return;
        }
        try {
            WebDriver driver = WebDriverRunner.getWebDriver();
            if (!(driver instanceof TakesScreenshot takesScreenshot)) {
                return;
            }
            byte[] screenshot = takesScreenshot.getScreenshotAs(OutputType.BYTES);
            Allure.getLifecycle().addAttachment(name, "image/png", "png", screenshot);
        } catch (Exception e) {
            Allure.step("Не удалось снять Screenshot (" + name + "): " + e.getMessage());
        }
    }

    /**
     * Page source в Allure
     */
    public static void attachPageSource(String name) {
        if (!WebDriverRunner.hasWebDriverStarted()) {
            return;
        }
        try {
            String source = WebDriverRunner.getWebDriver().getPageSource();
            if (source != null && !source.isBlank()) {
                Allure.addAttachment(name, "text/xml", source, ".xml");
            }
        } catch (Exception e) {
            Allure.step("Не удалось снять Page source (" + name + "): " + e.getMessage());
        }
    }

    /**
     * Network traffic в Allure.
     */
    public static void attachNetworkTraffic() {
        if (isAndroid()) {
            return;
        }
        if (!MitmProxySession.isRunning() && MitmProxySession.getCaptured().isEmpty()) {
            return;
        }
        var snapshot = MitmProxySession.getCaptured();
        if (snapshot.isEmpty()) {
            throw new IllegalStateException("mobile-network-traffic пуст: трафик не прошёл через mitmproxy");
        }
        try {
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            Allure.addAttachment("mobile-network-traffic", "application/json", mapper.writeValueAsString(snapshot),
                    ".json");
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось приложить network traffic в Allure: " + e.getMessage(), e);
        }
    }

    /**
     * Публичный дамп текущего UI-состояния.
     */
    public static void dumpUiState(String tag) {
        attachPageSourceDiagnostics(tag);
    }

    /**
     * Лог прогона текущего теста (Allure steps) в Allure
     */
    public static void attachTestRunLog(String name) {
        try {
            String log = AllureConsoleListener.getTestLog();
            if (log != null && !log.isBlank()) {
                Allure.addAttachment(name, "text/plain", log, ".txt");
            }
        } catch (Exception e) {
            Allure.step("Не удалось приложить лог прогона теста (" + name + "): " + e.getMessage());
        }
    }

    /**
     * Screenshot + page source + лог прогона теста в Allure при падении
     * Вызывать до reset приложения, пока сессия драйвера жива.
     */
    public static void attachFailureArtifacts() {
        attachScreenshot("Screenshot");
        attachPageSource("Page source");
        attachTestRunLog("Test run log");
    }

    public static void attachPageSourceDiagnostics(String tag) {
        try {
            String source = WebDriverRunner.getWebDriver().getPageSource();
            Allure.addAttachment("chat-screen-page-source", "text/xml", source, ".xml");
            StringBuilder ids = new StringBuilder();
            source.lines()
                    .map(String::trim)
                    .filter(line -> line.contains("resource-id=")
                            || line.contains("name=\"")
                            || line.contains("content-desc=")
                            || line.contains("text=\""))
                    .limit(120)
                    .forEach(line -> ids.append(line).append('\n'));
            String sample = ids.toString();
            if (!sample.isEmpty()) {
                Allure.addAttachment("ui-ids-sample", "text/plain", sample, ".txt");
                System.out.println("==== ui-ids-sample (" + tag + ") ====");
                System.out.println(sample);
                System.out.println("==== end ui-ids-sample ====");
                Path out = Path.of("target", "skr-ui-dump-" + tag + ".txt");
                Files.createDirectories(out.getParent());
                Files.writeString(out, sample);
                Allure.step("UI dump written to " + out.toAbsolutePath());
            }
        } catch (Exception e) {
            Allure.step("Не удалось снять page source: " + e.getMessage());
        }
    }
}
