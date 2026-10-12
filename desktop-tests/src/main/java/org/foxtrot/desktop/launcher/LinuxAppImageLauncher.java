package org.foxtrot.desktop.launcher;

import org.foxtrot.desktop.utils.ShellRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Запускает Electron AppImage на Linux с Xvfb и remote-debugging.
 * Xvfb требуется на headless-агентах (CI), на десктоп-Linux не нужен.
 */
public class LinuxAppImageLauncher implements DesktopAppLauncher {

    private static final Logger logger = LoggerFactory.getLogger(LinuxAppImageLauncher.class);

    @Override
    public long start(Path artifactPath, int cdpPort, Path userDataDir) {
        try {
            Files.createDirectories(userDataDir);

            // Запускаем через DISPLAY=:99 (Xvfb должен быть поднят заранее через xvfb-run или отдельно)
            String display = System.getenv("DISPLAY") != null ? System.getenv("DISPLAY") : ":99";

            // --no-sandbox обязателен внутри Docker/container
            ProcessBuilder pb = new ProcessBuilder(
                    artifactPath.toString(),
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--remote-debugging-port=" + cdpPort,
                    "--user-data-dir=" + userDataDir.toString()
            );
            pb.environment().put("DISPLAY", display);
            // В Docker часто нет FUSE — AppImage должен распаковаться без mount
            pb.environment().put("APPIMAGE_EXTRACT_AND_RUN", "1");
            DesktopLocaleSetup.apply(pb);
            pb.redirectErrorStream(true);
            Path electronLog = userDataDir.resolve("electron.log");
            pb.redirectOutput(electronLog.toFile());

            Process process = pb.start();
            long pid = process.pid();
            logger.info("AppImage запущен: pid={}, cdpPort={}, display={}, log={}",
                    pid, cdpPort, display, electronLog);
            return pid;

        } catch (IOException e) {
            throw new RuntimeException("Не удалось запустить AppImage: " + artifactPath, e);
        }
    }

    @Override
    public void stop(long pid) {
        logger.info("Останавливаю AppImage: pid={}", pid);
        ShellRunner.runIgnoreError("kill -TERM " + pid);
    }

    @Override
    public void cleanUserData(Path userDataDir) {
        ShellRunner.runIgnoreError("rm -rf " + userDataDir);
        logger.info("UserData удалён: {}", userDataDir);
    }
}
