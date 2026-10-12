package org.foxtrot.desktop.launcher;

import java.nio.file.Path;

/**
 * Контракт для запуска и остановки Electron-приложения.
 * Каждая реализация — под конкретную платформу.
 */
public interface DesktopAppLauncher {

    /**
     * Подготовка артефакта перед suite (mount DMG, install exe и т.д.).
     * Вызывается один раз в {@code DesktopSessionManager.init()}.
     */
    default void prepareArtifact(Path artifactPath) {
    }

    /**
     * Запускает приложение с заданными параметрами.
     *
     * @param artifactPath  путь к артефакту (AppImage / .app / .exe)
     * @param cdpPort       CDP remote debugging port для Playwright
     * @param userDataDir   изолированная директория данных пользователя
     * @return PID запущенного процесса
     */
    long start(Path artifactPath, int cdpPort, Path userDataDir);

    /**
     * Останавливает ранее запущенный процесс.
     *
     * @param pid PID процесса, полученный из {@link #start}
     */
    void stop(long pid);

    /**
     * Удаляет данные пользователя (userData), оставляя артефакт в кеше нетронутым.
     */
    void cleanUserData(Path userDataDir);

    /**
     * Завершает работу launcher'а после suite (unmount DMG и т.д.).
     */
    default void shutdown() {
    }
}
