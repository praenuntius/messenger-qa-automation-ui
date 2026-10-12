package org.foxtrot.utils;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Best-effort sniff DF-554 rotation endpoints из Chrome performance log (CDP Network.*):
 * {@code updateSymmetricKeyV2}, {@code confirmSymmetricKeyUpdate}, legacy {@code updateSymmetricKey}.
 */
public final class UpdateSymmetricKeyNetworkObserver {

    private UpdateSymmetricKeyNetworkObserver() {
    }

    public static void clearPerformanceLogBuffer() {
        try {
            WebDriverRunner.getWebDriver().manage().logs().get(LogType.PERFORMANCE);
        } catch (Exception ignored) {
            // remote / empty
        }
    }

    public static List<String> drainUpdateSymmetricKeyEvents() {
        return drainRotationEvents();
    }

    public static List<String> drainRotationEvents() {
        List<String> hits = new ArrayList<>();
        try {
            for (LogEntry entry : WebDriverRunner.getWebDriver().manage().logs().get(LogType.PERFORMANCE)) {
                String msg = entry.getMessage();
                if (msg == null) {
                    continue;
                }
                String lower = msg.toLowerCase(Locale.ROOT);
                if (lower.contains("updatesymmetrickey")
                        || lower.contains("confirmsymmetrickeyupdate")) {
                    hits.add(msg);
                }
            }
        } catch (Exception e) {
            hits.add("performance-log-unavailable: " + e.getMessage());
        }
        return hits;
    }

    public static int countMentions(List<String> events, String needle) {
        if (events == null || needle == null) {
            return 0;
        }
        String n = needle.toLowerCase(Locale.ROOT);
        int c = 0;
        for (String e : events) {
            if (e != null && e.toLowerCase(Locale.ROOT).contains(n)) {
                c++;
            }
        }
        return c;
    }

    public static int countUpdateV2(List<String> events) {
        return countMentions(events, "updateSymmetricKeyV2");
    }

    public static int countConfirm(List<String> events) {
        return countMentions(events, "confirmSymmetricKeyUpdate");
    }

    public static int countUpdateSymmetricKeyMentions(List<String> events) {
        return events == null ? 0 : events.size();
    }
}
