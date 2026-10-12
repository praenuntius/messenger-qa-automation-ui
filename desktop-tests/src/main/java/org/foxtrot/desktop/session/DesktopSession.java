package org.foxtrot.desktop.session;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.nio.file.Path;

/**
 * Дескриптор одной запущенной сессии Electron.
 * Создаётся DesktopSessionManager при старте теста, передаётся в тест через ThreadLocal.
 */
@Getter
@Builder
@ToString
public class DesktopSession {

    /** Уникальный идентификатор сессии (например, threadId) */
    private final String sessionId;

    /** CDP-порт, на котором слушает Electron */
    private final int cdpPort;

    /** Адрес для Playwright connectOverCDP */
    private final String cdpUrl;

    /** PID запущенного процесса Electron */
    private final long pid;

    /** Изолированная директория данных пользователя */
    private final Path userDataDir;

    /** Путь к артефакту, которым была запущена сессия */
    private final Path artifactPath;

    /** Версия Chromium в Electron (из CDP /json/version) */
    private final String chromiumVersion;
}
