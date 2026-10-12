package org.foxtrot.mobile.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class CommandLineManager {

    private static final Logger logger = LoggerFactory.getLogger(CommandLineManager.class);
    private static final long COMMAND_TIMEOUT_SECONDS = 120;

    private CommandLineManager() {
    }

    public static String startCMDProcessWithResponse(String command) {
        return startCMDProcess(command, true);
    }

    public static String startCMDProcessIgnoreError(String command) {
        return startCMDProcess(command, false);
    }

    private static String startCMDProcess(String command, boolean failOnError) {
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
        processBuilder.redirectErrorStream(true);
        StringBuilder output = new StringBuilder();
        long startTime = System.currentTimeMillis();
        try {
            Process process = processBuilder.start();
            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append(System.lineSeparator());
                    }
                } catch (Exception e) {
                    logger.warn("Не удалось прочитать вывод команды '{}': {}", command, e.getMessage());
                }
            });
            outputThread.start();
            boolean finished = process.waitFor(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            long durationMs = System.currentTimeMillis() - startTime;
            if (!finished) {
                process.destroyForcibly();
                String message = "Команда не завершилась за %d сек. Время выполнения: %d ms. Command: %s. Output: %s"
                        .formatted(COMMAND_TIMEOUT_SECONDS, durationMs, command, output.toString().trim());
                if (failOnError) {
                    throw new RuntimeException(message);
                }
                logger.warn(message);
                return output.toString();
            }
            outputThread.join();
            int exitCode = process.exitValue();
            String result = output.toString().trim();
            if (exitCode != 0) {
                String message = "Команда завершилась с ошибкой. Command: %s. Output: %s. Exit code: %d. Время выполнения: %d ms"
                        .formatted(command, result, exitCode, durationMs);

                if (failOnError) {
                    throw new RuntimeException(message);
                }
                logger.warn(message);
                return output.toString();
            }
            logger.info("Команда выполнена успешно. Command: {}. Output: {}. Exit code: {}. Время выполнения: {} ms",
                    command, result, exitCode, durationMs);
            return output.toString();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Выполнение команды было прервано. Command: " + command, e);
        } catch (Exception e) {
            if (failOnError) {
                throw new RuntimeException("Ошибка при выполнении команды. Command: " + command, e);
            }
            return output.toString();
        }
    }
}
