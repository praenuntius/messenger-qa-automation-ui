package org.foxtrot.desktop.launcher;

import org.foxtrot.desktop.config.DesktopLocale;
import org.foxtrot.desktop.config.DesktopLocaleResolver;
import org.foxtrot.desktop.config.DesktopPlatform;
import org.foxtrot.desktop.config.DesktopPlatformResolver;
import org.foxtrot.desktop.utils.ShellRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Задаёт UI-язык Electron на том же уровне, что и выбор платформы:
 * в процесс приложения, независимо от локального запуска или CI VM.
 * <p>
 * Не меняет язык всей машины — только окружение и флаги запускаемого клиента.
 */
public final class DesktopLocaleSetup {

    private static final Logger logger = LoggerFactory.getLogger(DesktopLocaleSetup.class);

    private DesktopLocaleSetup() {
    }

    /**
     * Проверяет, что POSIX-локаль доступна на Linux-хосте.
     * Вызывать один раз в {@code DesktopSuiteLifecycle.beforeSuite()}.
     */
    public static void prepareHost() {
        DesktopLocale locale = DesktopLocaleResolver.get();
        DesktopPlatform platform = DesktopPlatformResolver.getPlatform();

        if (platform != DesktopPlatform.LINUX) {
            return;
        }

        String posix = locale.posixLocale();
        try {
            String available = ShellRunner.runWithOutput("locale -a");
            if (containsPosixLocale(available, posix)) {
                logger.info("Системная локаль {} доступна", posix);
                return;
            }
            logger.warn("Системная локаль {} не сгенерирована (locale -a). "
                            + "Electron всё равно получит --lang={}, но glibc может игнорировать LANG. "
                            + "На Ubuntu: sudo locale-gen {}",
                    posix, locale.chromiumLang(), posix);
        } catch (Exception e) {
            logger.warn("Не удалось проверить locale -a: {}", e.getMessage());
        }
    }

    /**
     * Применяет локаль к уже собранному {@link ProcessBuilder} перед {@code start()}.
     */
    public static void apply(ProcessBuilder pb) {
        DesktopLocale locale = DesktopLocaleResolver.get();
        DesktopPlatform platform = DesktopPlatformResolver.getPlatform();

        applyPosixEnv(pb.environment(), locale);
        applyChromiumArgs(pb.command(), locale);

        if (platform == DesktopPlatform.MAC) {
            applyMacCocoaArgs(pb.command(), locale);
        }

        logger.info("Локаль процесса Electron: lang={}, LANG={}, platform={}",
                locale.chromiumLang(), locale.posixLocale(), platform.getValue());
    }

    private static void applyPosixEnv(Map<String, String> env, DesktopLocale locale) {
        env.put("LANG", locale.posixLocale());
        env.put("LANGUAGE", locale.languagePriority());
        env.put("LC_ALL", locale.posixLocale());
        env.put("LC_MESSAGES", locale.posixLocale());
    }

    private static void applyChromiumArgs(List<String> command, DesktopLocale locale) {
        command.add("--lang=" + locale.chromiumLang());
        command.add("--accept-lang=" + locale.acceptLanguage());
    }

    /**
     * Cocoa читает язык из argv процесса, без {@code defaults write} у пользователя.
     */
    private static void applyMacCocoaArgs(List<String> command, DesktopLocale locale) {
        if (command.isEmpty()) {
            return;
        }
        command.add(1, "-AppleLanguages");
        command.add(2, locale.appleLanguagesArg());
        command.add(3, "-AppleLocale");
        command.add(4, locale.appleLocale());
    }

    static boolean containsPosixLocale(String localeAOutput, String posixLocale) {
        String prefix = posixLocale.split("\\.")[0].toLowerCase(Locale.ROOT);
        return localeAOutput.lines()
                .map(String::trim)
                .map(line -> line.toLowerCase(Locale.ROOT))
                .anyMatch(line -> line.equals(prefix)
                        || line.equals(posixLocale.toLowerCase(Locale.ROOT))
                        || line.startsWith(prefix + "."));
    }
}
