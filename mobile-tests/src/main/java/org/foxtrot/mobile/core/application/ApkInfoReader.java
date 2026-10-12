package org.foxtrot.mobile.core.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class ApkInfoReader {

    private static final Pattern APPLICATION_ID =
            Pattern.compile("[a-zA-Z][\\w]*(\\.[a-zA-Z][\\w]*)+");

    private ApkInfoReader() {
    }

    public static String readApplicationId(Path apkPath) {
        String apkanalyzer = resolveApkanalyzer();
        ProcessBuilder processBuilder = new ProcessBuilder(
                apkanalyzer, "manifest", "application-id", apkPath.toString());
        // Не мержим stderr: Java пишет "Picked up JAVA_TOOL_OPTIONS: ..." и портит package id
        processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD);
        processBuilder.environment().remove("JAVA_TOOL_OPTIONS");
        processBuilder.environment().remove("_JAVA_OPTIONS");
        try {
            Process process = processBuilder.start();
            String output;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            )) {
                output = reader.lines()
                        .map(String::trim)
                        .filter(line -> !line.isBlank())
                        .filter(line -> !line.startsWith("Picked up JAVA_TOOL_OPTIONS"))
                        .reduce((first, second) -> second)
                        .orElse("");
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException(
                        "Не удалось получить applicationId. Exit code: " + exitCode + ", output: " + output);
            }
            String applicationId = extractApplicationId(output);
            if (applicationId == null) {
                throw new IllegalStateException(
                        "applicationId пустой/битый для APK: " + apkPath + ", raw output: [" + output + "]"
                );
            }
            return applicationId;
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Не удалось запустить apkanalyzer (" + apkanalyzer + "). "
                            + "Проверь ANDROID_HOME/cmdline-tools/.../bin в PATH агента", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Получение applicationId было прервано", e);
        }
    }

    private static String extractApplicationId(String output) {
        if (output == null || output.isBlank()) {
            return null;
        }
        String candidate = output.trim();
        return APPLICATION_ID.matcher(candidate).matches() ? candidate : null;
    }

    /**
     * Jenkins agent часто не наследует PATH из zsh — ищем бинарь сами.
     */
    private static String resolveApkanalyzer() {
        List<Path> candidates = new ArrayList<>();
        String androidHome = firstNonBlank(System.getenv("ANDROID_HOME"), System.getenv("ANDROID_SDK_ROOT"));
        if (androidHome != null) {
            candidates.add(Path.of(androidHome, "cmdline-tools", "latest", "bin", "apkanalyzer"));
            candidates.add(Path.of(androidHome, "tools", "bin", "apkanalyzer"));
        }
        String userHome = System.getProperty("user.home");
        if (userHome != null) {
            candidates.add(Path.of(userHome, "Library", "Android", "sdk", "cmdline-tools", "latest", "bin", "apkanalyzer"));
        }
        for (Path candidate : candidates) {
            if (Files.isExecutable(candidate)) {
                return candidate.toString();
            }
        }
        return "apkanalyzer";
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
