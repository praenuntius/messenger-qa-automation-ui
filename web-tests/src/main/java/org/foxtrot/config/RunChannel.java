package org.foxtrot.config;

/**
 * Канал запуска UI-тестов из web-tests.
 * <ul>
 *   <li>{@link #WEB} — Chrome / Selenoid (по умолчанию)</li>
 *   <li>{@link #DESKTOP} — Electron + Selenide через CDP</li>
 * </ul>
 */
public enum RunChannel {

    WEB("web"),
    DESKTOP("desktop");

    private final String value;

    RunChannel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RunChannel fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            return WEB;
        }
        for (RunChannel channel : values()) {
            if (channel.value.equalsIgnoreCase(raw.trim())) {
                return channel;
            }
        }
        throw new IllegalArgumentException(
                "Неизвестный run.channel: '" + raw + "'. Допустимые значения: web, desktop");
    }
}
