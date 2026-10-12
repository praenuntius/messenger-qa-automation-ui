package org.foxtrot.desktop.launcher;

import org.foxtrot.desktop.utils.ShellRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Запускает Electron .exe на Windows.
 * Предполагается portable / installer с /S (silent) флагом.
 */
public class WindowsExeLauncher implements DesktopAppLauncher {

    private static final Logger logger = LoggerFactory.getLogger(WindowsExeLauncher.class);

    // Путь куда устанавливается приложение после silent install
    // TODO: уточнить реальный путь после проверки на Windows
    private static final String INSTALL_DIR = System.getenv("LOCALAPPDATA") + "\\TDM";

    @Override
    public long start(Path artifactPath, int cdpPort, Path userDataDir) {
        try {
            Files.createDirectories(userDataDir);

            // 1. Silent install
            logger.info("Устанавливаю {}", artifactPath);
            ShellRunner.run("\"" + artifactPath + "\" /S /D=" + INSTALL_DIR);

            // 2. Найти исполняемый файл
            String executablePath = ShellRunner.runWithOutput(
                    "powershell -Command \"Get-ChildItem -Path '" + INSTALL_DIR
                    + "' -Filter '*.exe' -Recurse | Select-Object -First 1 -ExpandProperty FullName\"").trim();
            if (executablePath.isEmpty()) {
                throw new RuntimeException("Не найден exe после установки в: " + INSTALL_DIR);
            }
            logger.info("Найден исполняемый файл: {}", executablePath);

            ProcessBuilder pb = new ProcessBuilder(
                    executablePath,
                    "--remote-debugging-port=" + cdpPort,
                    "--user-data-dir=" + userDataDir.toString()
            );
            DesktopLocaleSetup.apply(pb);
            pb.redirectErrorStream(true);
            pb.redirectOutput(userDataDir.resolve("electron.log").toFile());

            Process process = pb.start();
            long pid = process.pid();
            logger.info("Electron запущен: pid={}, cdpPort={}", pid, cdpPort);
            return pid;

        } catch (IOException e) {
            throw new RuntimeException("Не удалось запустить EXE: " + artifactPath, e);
        }
    }

    @Override
    public void stop(long pid) {
        logger.info("Останавливаю Electron: pid={}", pid);
        ShellRunner.runIgnoreError("taskkill /PID " + pid + " /F");
    }

    @Override
    public void cleanUserData(Path userDataDir) {
        ShellRunner.runIgnoreError("rd /s /q \"" + userDataDir + "\"");
        logger.info("UserData удалён: {}", userDataDir);
    }
}
