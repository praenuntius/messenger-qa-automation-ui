package org.foxtrot.desktop.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Вспомогательный исполнитель shell-команд.
 * Аналог CommandLineManager из mobile-tests, но без platform-специфики.
 */
public class ShellRunner {

    private static final Logger logger = LoggerFactory.getLogger(ShellRunner.class);
    private static final long TIMEOUT_SECONDS = 120;

    private ShellRunner() {
    }

    /** Запускает команду, бросает RuntimeException при ненулевом exit-коде. */
    public static void run(String command) {
        execute(command, true);
    }

    /** Запускает команду, возвращает stdout, бросает RuntimeException при ошибке. */
    public static String runWithOutput(String command) {
        return execute(command, true);
    }

    /** Запускает команду, игнорирует ошибки. */
    public static void runIgnoreError(String command) {
        execute(command, false);
    }

    private static String execute(String command, boolean failOnError) {
        boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
        String[] shell = isWindows
                ? new String[]{"cmd.exe", "/c", command}
                : new String[]{"/bin/bash", "-c", command};

        ProcessBuilder pb = new ProcessBuilder(shell);
        pb.redirectErrorStream(true);
        StringBuilder output = new StringBuilder();

        try {
            Process process = pb.start();
            Thread reader = new Thread(() -> {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        output.append(line).append(System.lineSeparator());
                    }
                } catch (Exception ignored) {
                }
            });
            reader.start();

            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                String msg = "Команда не завершилась за " + TIMEOUT_SECONDS + "с: " + command;
                if (failOnError) throw new RuntimeException(msg);
                logger.warn(msg);
                return output.toString();
            }

            reader.join();
            int exitCode = process.exitValue();
            String result = output.toString().trim();

            if (exitCode != 0) {
                String msg = "Команда завершилась с кодом " + exitCode + ": " + command + "\nOutput: " + result;
                if (failOnError) throw new RuntimeException(msg);
                logger.warn(msg);
            } else {
                logger.debug("OK [{}]: {}", exitCode, command);
            }
            return output.toString();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            if (failOnError) throw new RuntimeException("Ошибка запуска команды: " + command, e);
            logger.warn("Ошибка запуска команды [ignored]: {}: {}", command, e.getMessage());
            return output.toString();
        }
    }
}
