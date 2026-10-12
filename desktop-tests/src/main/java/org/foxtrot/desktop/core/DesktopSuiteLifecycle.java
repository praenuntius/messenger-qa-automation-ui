package org.foxtrot.desktop.core;

import org.foxtrot.desktop.build.BuildManager;
import org.foxtrot.desktop.config.DesktopLocaleResolver;
import org.foxtrot.desktop.config.DesktopPlatformResolver;
import org.foxtrot.desktop.launcher.DesktopLocaleSetup;
import org.foxtrot.desktop.session.DesktopSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Общая подготовка Electron-артефакта для suite.
 * Используется и Playwright-тестами (desktop-tests), и Selenide+CDP (web-tests).
 */
public final class DesktopSuiteLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(DesktopSuiteLifecycle.class);

    private DesktopSuiteLifecycle() {
    }

    public static void beforeSuite() {
        logger.info("=== Desktop suite: подготовка ===");
        logger.info("Платформа: {}", DesktopPlatformResolver.getPlatform());
        logger.info("Локаль: {}", DesktopLocaleResolver.get().getValue());
        DesktopLocaleSetup.prepareHost();

        Path artifact = BuildManager.ensureBuild();
        logger.info("Артефакт готов: {}", artifact);

        DesktopSessionManager.init(artifact);

        String version = BuildManager.resolvedVersion(DesktopPlatformResolver.getPlatform());
        logger.info("Версия приложения: {}", version);
    }

    public static void afterSuite() {
        logger.info("=== Desktop suite: завершение ===");
        DesktopSessionManager.shutdown();
    }
}
