package org.foxtrot.desktop.launcher;

import org.foxtrot.desktop.config.DesktopPlatform;
import org.foxtrot.desktop.config.DesktopPlatformResolver;

/**
 * Возвращает нужный launcher для текущей платформы.
 */
public class DesktopAppLauncherFactory {

    private DesktopAppLauncherFactory() {
    }

    public static DesktopAppLauncher create() {
        return create(DesktopPlatformResolver.getPlatform());
    }

    public static DesktopAppLauncher create(DesktopPlatform platform) {
        return switch (platform) {
            case MAC -> new MacDmgLauncher();
            case LINUX -> new LinuxAppImageLauncher();
            case WINDOWS -> new WindowsExeLauncher();
        };
    }
}
