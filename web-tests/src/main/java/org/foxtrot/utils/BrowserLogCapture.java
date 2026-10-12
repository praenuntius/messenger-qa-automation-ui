package org.foxtrot.utils;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Сохраняет логи консоли браузера в файл после каждого теста.
 * Файлы хранятся в browser_session_logs/
 */
public final class BrowserLogCapture {

    private static final Logger log = LoggerFactory.getLogger(BrowserLogCapture.class);
    private static final Path LOGS_DIR = Path.of("browser_session_logs");
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
            .withZone(ZoneId.systemDefault());

    private BrowserLogCapture() {
    }

    /**
     * Сохраняет логи консоли браузера в файл.
     * Вызывать до закрытия WebDriver (например, в @AfterEach).
     *
     * @param testClassName  имя класса теста
     * @param testMethodName имя метода теста
     */
    public static void saveBrowserLogs(String testClassName, String testMethodName) {
        if (!WebDriverRunner.hasWebDriverStarted()) {
            return;
        }
        try {
            String className = testClassName != null ? testClassName : "Unknown";
            String methodName = testMethodName != null ? testMethodName : "unknown";
            String timestamp = TIMESTAMP_FORMAT.format(Instant.now());
            String safeName = sanitizeFileName(className) + "_" + sanitizeFileName(methodName) + "_" + timestamp;
            Path logFile = LOGS_DIR.resolve(safeName + ".log");

            StringBuilder content = new StringBuilder();
            content.append("=== Browser Console Logs ===\n");
            content.append("Test: ").append(className).append(".").append(methodName).append("\n");
            content.append("Timestamp: ").append(timestamp).append("\n\n");

            appendLogSection(content, "BROWSER", LogType.BROWSER, entry ->
                    "[" + entry.getTimestamp() + "] " + entry.getLevel() + " " + entry.getMessage());

            appendLogSection(content, "DRIVER", LogType.DRIVER, entry ->
                    "[" + entry.getTimestamp() + "] " + entry.getLevel() + " " + entry.getMessage());

            appendPerformanceFailures(content);

            Files.createDirectories(LOGS_DIR);
            Files.writeString(logFile, content.toString());
            log.debug("Browser logs saved to {}", logFile.toAbsolutePath());
        } catch (IOException e) {
            log.warn("Failed to save browser logs: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Failed to capture browser logs: {}", e.getMessage());
        }
    }

    private static void appendLogSection(StringBuilder content, String sectionName, String logType,
                                         Function<LogEntry, String> formatter) {
        try {
            LogEntries entries = WebDriverRunner.getWebDriver().manage().logs().get(logType);
            List<LogEntry> list = new ArrayList<>();
            entries.forEach(list::add);
            if (!list.isEmpty()) {
                content.append("--- ").append(sectionName).append(" ---\n");
                for (LogEntry e : list) {
                    content.append(formatter.apply(e)).append("\n");
                }
                content.append("\n");
            }
        } catch (Exception e) {
            content.append("--- ").append(sectionName).append(" (error: ").append(e.getMessage()).append(") ---\n\n");
        }
    }

    private static void appendPerformanceFailures(StringBuilder content) {
        try {
            LogEntries perfLogs = WebDriverRunner.getWebDriver().manage().logs().get(LogType.PERFORMANCE);
            content.append("--- PERFORMANCE (failed network only) ---\n");
            boolean hasFailures = false;
            for (LogEntry entry : perfLogs) {
                String msg = entry.getMessage();
                if (msg != null && (msg.contains("Network.loadingFailed") || msg.contains("ERR_") || msg.contains("net::"))) {
                    content.append(msg).append("\n");
                    hasFailures = true;
                }
            }
            if (!hasFailures) {
                content.append("(no failures)\n");
            }
        } catch (Exception e) {
            content.append("--- PERFORMANCE (error: ").append(e.getMessage()).append(") ---\n");
        }
    }

    private static String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
