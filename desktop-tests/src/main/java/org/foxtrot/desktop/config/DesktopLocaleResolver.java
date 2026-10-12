package org.foxtrot.desktop.config;

import org.aeonbits.owner.ConfigFactory;

/**
 * Единая точка определения UI-языка desktop-клиента.
 * Аналог {@link DesktopPlatformResolver}: локаль не детектится с ОС,
 * а задаётся конфигом (сейчас всегда ru).
 */
public class DesktopLocaleResolver {

    private static final DesktopConfig config = ConfigFactory.create(DesktopConfig.class);

    private DesktopLocaleResolver() {
    }

    public static DesktopLocale get() {
        return DesktopLocale.fromString(config.locale());
    }
}
