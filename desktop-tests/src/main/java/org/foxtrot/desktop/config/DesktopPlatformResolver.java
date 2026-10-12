package org.foxtrot.desktop.config;

import org.aeonbits.owner.ConfigFactory;

/**
 * Единая точка определения платформы для текущего запуска.
 * Аналог MobilePlatformResolver.
 */
public class DesktopPlatformResolver {

    private static final DesktopConfig config = ConfigFactory.create(DesktopConfig.class);

    private DesktopPlatformResolver() {
    }

    public static DesktopPlatform getPlatform() {
        return DesktopPlatform.fromString(config.platform());
    }

    public static boolean isMac() {
        return getPlatform() == DesktopPlatform.MAC;
    }

    public static boolean isLinux() {
        return getPlatform() == DesktopPlatform.LINUX;
    }

    public static boolean isWindows() {
        return getPlatform() == DesktopPlatform.WINDOWS;
    }
}
