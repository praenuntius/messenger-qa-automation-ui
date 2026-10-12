package org.foxtrot.desktop.session;

import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.desktop.config.DesktopConfig;
import org.foxtrot.desktop.launcher.DesktopAppLauncher;
import org.foxtrot.desktop.launcher.DesktopAppLauncherFactory;
import org.foxtrot.desktop.utils.CdpBrowserVersionResolver;
import org.foxtrot.utils.Waits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Управляет пулом сессий Electron для параллельных потоков.
 * Каждый JUnit-поток получает свою сессию с уникальным CDP-портом и userDataDir.
 */
public class DesktopSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(DesktopSessionManager.class);
    private static final DesktopConfig config = ConfigFactory.create(DesktopConfig.class);

    /** Текущая сессия потока */
    private static final ThreadLocal<DesktopSession> THREAD_SESSION = new ThreadLocal<>();

    /** Счётчик для генерации уникальных портов */
    private static final AtomicInteger portCounter = new AtomicInteger(config.cdpPortBase());

    /** Активные сессии: sessionId → session */
    private static final ConcurrentHashMap<String, DesktopSession> activeSessions = new ConcurrentHashMap<>();

    private static DesktopAppLauncher launcher;
    private static Path artifactPath;

    private DesktopSessionManager() {
    }

    /**
     * Вызывается один раз перед всеми тестами (из DesktopTestLifecycleExtension.beforeSuite).
     * Не создаёт сессии — только инициализирует общие ресурсы.
     */
    public static synchronized void init(Path artifact) {
        artifactPath = artifact;
        launcher = DesktopAppLauncherFactory.create();
        launcher.prepareArtifact(artifact);
        portCounter.set(config.cdpPortBase());
        activeSessions.clear();
        logger.info("DesktopSessionManager инициализирован: artifact={}", artifact);
    }

    /**
     * Создаёт и запускает сессию для текущего потока (вызывается в beforeEach).
     */
    public static DesktopSession acquireForCurrentThread() {
        String sessionId = "session-" + Thread.currentThread().getId();
        int cdpPort = portCounter.getAndIncrement();
        Path userDataDir = Paths.get(System.getProperty("java.io.tmpdir"), "tdm-" + sessionId);

        logger.info("Запускаю сессию: id={}, cdpPort={}", sessionId, cdpPort);
        long pid = launcher.start(artifactPath, cdpPort, userDataDir);

        String chromiumVersion;
        try {
            // Ждём CDP HTTP (не только TCP) и узнаём версию Chromium
            chromiumVersion = CdpBrowserVersionResolver.waitForReady(cdpPort, config.appStartupTimeoutSeconds());
        } catch (RuntimeException e) {
            logger.error("CDP не поднялся для {}: pid={}, log=\n{}",
                    sessionId, pid, readElectronLog(userDataDir));
            try {
                launcher.stop(pid);
            } catch (Exception stopError) {
                logger.warn("Не удалось остановить pid={} после CDP timeout: {}", pid, stopError.getMessage());
            }
            throw e;
        }

        DesktopSession session = DesktopSession.builder()
                .sessionId(sessionId)
                .cdpPort(cdpPort)
                .cdpUrl("http://127.0.0.1:" + cdpPort)
                .pid(pid)
                .userDataDir(userDataDir)
                .artifactPath(artifactPath)
                .chromiumVersion(chromiumVersion)
                .build();

        THREAD_SESSION.set(session);
        activeSessions.put(sessionId, session);
        logger.info("Сессия запущена: {}", session);
        return session;
    }

    private static String readElectronLog(Path userDataDir) {
        Path log = userDataDir.resolve("electron.log");
        try {
            if (!Files.isRegularFile(log)) {
                return "(electron.log отсутствует: " + log + ")";
            }
            String body = Files.readString(log);
            if (body.isBlank()) {
                return "(electron.log пуст: " + log + ")";
            }
            int max = 4000;
            return body.length() <= max ? body : body.substring(0, max) + "\n...[truncated]";
        } catch (Exception e) {
            return "(не удалось прочитать electron.log: " + e.getMessage() + ")";
        }
    }

    /**
     * Останавливает сессию текущего потока и очищает userData (вызывается в afterEach).
     */
    public static void releaseCurrentThread() {
        DesktopSession session = THREAD_SESSION.get();
        if (session == null) {
            logger.warn("Нет активной сессии для потока {}", Thread.currentThread().getId());
            return;
        }
        try {
            launcher.stop(session.getPid());
            Waits.sleepSeconds(1);
            launcher.cleanUserData(session.getUserDataDir());
        } catch (Exception e) {
            logger.error("Ошибка при освобождении сессии {}: {}", session.getSessionId(), e.getMessage(), e);
        } finally {
            activeSessions.remove(session.getSessionId());
            THREAD_SESSION.remove();
        }
        logger.info("Сессия завершена: {}", session.getSessionId());
    }

    /**
     * Возвращает сессию текущего потока. Бросает исключение если сессии нет.
     */
    public static DesktopSession currentSession() {
        DesktopSession session = THREAD_SESSION.get();
        if (session == null) {
            throw new IllegalStateException(
                    "DesktopSession не создана для потока " + Thread.currentThread().getId() +
                    ". Убедитесь, что тест использует @ExtendWith(DesktopTestLifecycleExtension.class)");
        }
        return session;
    }

    /**
     * Останавливает все активные сессии. Вызывается из afterSuite.
     */
    public static void shutdown() {
        logger.info("Завершаю все активные сессии: {}", activeSessions.size());
        activeSessions.forEach((id, session) -> {
            try {
                launcher.stop(session.getPid());
                launcher.cleanUserData(session.getUserDataDir());
            } catch (Exception e) {
                logger.warn("Ошибка при shutdown сессии {}: {}", id, e.getMessage());
            }
        });
        activeSessions.clear();
        if (launcher != null) {
            launcher.shutdown();
        }
    }
}
