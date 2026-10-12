package org.foxtrot.desktop.config;

/**
 * Поддерживаемые десктоп-платформы.
 * Передаётся через -Ddesktop.platform=mac|linux|windows
 */
public enum DesktopPlatform {

    MAC("mac", "dmg"),
    LINUX("linux", "AppImage"),
    WINDOWS("windows", "exe");

    private final String value;
    /** Расширение файла-артефакта для данной платформы */
    private final String ext;

    DesktopPlatform(String value, String ext) {
        this.value = value;
        this.ext = ext;
    }

    public String getValue() {
        return value;
    }

    public String getExt() {
        return ext;
    }

    public static DesktopPlatform fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            return detectFromOs();
        }
        for (DesktopPlatform p : values()) {
            if (p.value.equalsIgnoreCase(raw.trim())) {
                return p;
            }
        }
        throw new IllegalArgumentException(
                "Неизвестная платформа: '" + raw + "'. Допустимые значения: mac, linux, windows");
    }

    /** Автоматически определяет платформу по OS, если не задана явно */
    public static DesktopPlatform detectFromOs() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("mac")) return MAC;
        if (os.contains("win")) return WINDOWS;
        return LINUX;
    }
}
