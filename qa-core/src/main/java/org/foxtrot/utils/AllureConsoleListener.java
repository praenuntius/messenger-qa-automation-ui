package org.foxtrot.utils;

import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AllureConsoleListener implements StepLifecycleListener {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final ThreadLocal<StringBuilder> TEST_LOG = ThreadLocal.withInitial(StringBuilder::new);

    private final Map<String, Long> stepStartTimes = new ConcurrentHashMap<>();

    public static void beginTest(String testName) {
        StringBuilder log = TEST_LOG.get();
        log.setLength(0);
        appendLine(String.format("[%s] ▶️  Starting test: %s",
                LocalDateTime.now().format(FORMATTER), testName));
    }

    public static String getTestLog() {
        return TEST_LOG.get().toString();
    }

    public static void clearTestLog() {
        TEST_LOG.remove();
    }

    @Override
    public void beforeStepStart(StepResult result) {
        String line = String.format("[%s] ➡️  Starting step: %s",
                LocalDateTime.now().format(FORMATTER), result.getName());
        appendLine(line);

        String key = generateKey(result);
        stepStartTimes.put(key, System.currentTimeMillis());
    }

    @Override
    public void afterStepStop(StepResult result) {
        String key = generateKey(result);
        Long startTime = stepStartTimes.remove(key);
        String duration = "";

        if (startTime != null) {
            long durationMs = System.currentTimeMillis() - startTime;
            duration = String.format(" (⏱️  %d ms)", durationMs);
        }

        String line = String.format("[%s] ✅ Finished step: %s [%s]%s",
                LocalDateTime.now().format(FORMATTER), result.getName(), result.getStatus(), duration);
        appendLine(line);
    }

    private static void appendLine(String line) {
        System.out.println(line);
        TEST_LOG.get().append(line).append(System.lineSeparator());
    }

    private String generateKey(StepResult result) {
        long threadId = Thread.currentThread().getId();
        return result.getName() + "_" + threadId + "_" + System.identityHashCode(result);
    }
}
