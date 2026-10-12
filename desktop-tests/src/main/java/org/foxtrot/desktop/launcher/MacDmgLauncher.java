package org.foxtrot.desktop.launcher;

import org.foxtrot.desktop.utils.ShellRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Запускает Electron-приложение на macOS из DMG.
 * DMG монтируется один раз на весь suite ({@link #prepareArtifact}),
 * каждый тест только стартует новый процесс Electron.
 */
public class MacDmgLauncher implements DesktopAppLauncher {

    private static final Logger logger = LoggerFactory.getLogger(MacDmgLauncher.class);

    private Path artifactPath;
    private String executablePath;
    /** Точка монтирования DMG, например /Volumes/TDM Beta 3.52.27-universal */
    private String mountPoint;

    @Override
    public void prepareArtifact(Path artifactPath) {
        this.artifactPath = artifactPath.toAbsolutePath().normalize();
        mountPoint = resolveMountPoint(this.artifactPath);
        resolveExecutable();
    }

    @Override
    public long start(Path artifactPath, int cdpPort, Path userDataDir) {
        try {
            if (executablePath == null) {
                prepareArtifact(artifactPath);
            }
            Files.createDirectories(userDataDir);

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
            throw new RuntimeException("Не удалось запустить Electron: " + artifactPath, e);
        }
    }

    @Override
    public void stop(long pid) {
        logger.info("Останавливаю Electron: pid={}", pid);
        ShellRunner.runIgnoreError("kill -TERM " + pid);
        ShellRunner.runIgnoreError("kill -9 " + pid);
    }

    @Override
    public void cleanUserData(Path userDataDir) {
        ShellRunner.runIgnoreError("rm -rf \"" + userDataDir + "\"");
        logger.info("UserData удалён: {}", userDataDir);
    }

    @Override
    public void shutdown() {
        if (mountPoint != null) {
            logger.info("Размонтирую DMG: {}", mountPoint);
            ShellRunner.runIgnoreError("hdiutil detach \"" + mountPoint + "\" -force -quiet");
            mountPoint = null;
        }
        executablePath = null;
        artifactPath = null;
    }

    private String resolveMountPoint(Path dmgPath) {
        Optional<String> existing = findExistingMountPoint(dmgPath);
        if (existing.isPresent()) {
            logger.info("DMG уже смонтирован, использую: {}", existing.get());
            return existing.get();
        }

        logger.info("Монтирую DMG: {}", dmgPath);
        String output = ShellRunner.runWithOutput("hdiutil attach \"" + dmgPath + "\" -nobrowse");
        String mount = parseMountPointFromAttachOutput(output)
                .orElseThrow(() -> new RuntimeException(
                        "Не удалось определить точку монтирования после hdiutil attach. Output: " + output));
        logger.info("DMG смонтирован: {}", mount);
        return mount;
    }

    private void resolveExecutable() {
        String appPath = ShellRunner.runWithOutput(
                "find \"" + mountPoint + "\" -name \"*.app\" -maxdepth 2 | head -1").trim();
        if (appPath.isEmpty()) {
            throw new RuntimeException("Не найдено .app внутри DMG: " + mountPoint);
        }
        logger.info("Найдено .app: {}", appPath);

        executablePath = ShellRunner.runWithOutput(
                "find \"" + appPath + "/Contents/MacOS\" -type f -maxdepth 1 | head -1").trim();
        if (executablePath.isEmpty()) {
            throw new RuntimeException("Не найден исполняемый файл в: " + appPath + "/Contents/MacOS");
        }
        logger.info("Исполняемый файл: {}", executablePath);
    }

    /**
     * Ищет уже смонтированный образ по пути к dmg (hdiutil info).
     * macOS не даёт attach дважды — «Ресурс занят».
     */
    static Optional<String> findExistingMountPoint(Path dmgPath) {
        String dmg = dmgPath.toAbsolutePath().normalize().toString();
        String info = ShellRunner.runWithOutput("hdiutil info");
        String[] lines = info.split("\n");
        boolean inBlock = false;

        for (String line : lines) {
            if (line.contains("image-path") && line.contains(dmg)) {
                inBlock = true;
                continue;
            }
            if (inBlock && line.startsWith("image-path") && !line.contains(dmg)) {
                break;
            }
            if (inBlock && line.contains("/Volumes/")) {
                String mount = parseMountPointFromAttachOutput(line).orElse(null);
                if (mount != null) {
                    return Optional.of(mount);
                }
            }
        }
        return Optional.empty();
    }

    /** Парсит строку hdiutil: последняя колонка с /Volumes/... */
    static Optional<String> parseMountPointFromAttachOutput(String output) {
        for (String line : output.split("\n")) {
            if (!line.contains("/Volumes/")) {
                continue;
            }
            String[] parts = line.trim().split("\t");
            for (int i = parts.length - 1; i >= 0; i--) {
                String part = parts[i].trim();
                if (part.startsWith("/Volumes/")) {
                    return Optional.of(part);
                }
            }
        }
        return Optional.empty();
    }
}
