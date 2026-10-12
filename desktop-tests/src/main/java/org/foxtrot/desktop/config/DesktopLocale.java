package org.foxtrot.desktop.config;

import java.util.Locale;

/**
 * UI-язык desktop-клиента. Сейчас всегда {@link #RU};
 * {@link #EN} зарезервирован на потом.
 * <p>
 * Передаётся через {@code -Ddesktop.locale=ru|en}.
 */
public enum DesktopLocale {

    RU("ru", "ru-RU", "ru_RU.UTF-8", "ru_RU:ru", "ru_RU"),
    EN("en", "en-US", "en_US.UTF-8", "en_US:en", "en_US");

    private final String value;
    private final String bcp47;
    private final String posixLocale;
    private final String languagePriority;
    private final String appleLocale;

    DesktopLocale(String value, String bcp47, String posixLocale,
                  String languagePriority, String appleLocale) {
        this.value = value;
        this.bcp47 = bcp47;
        this.posixLocale = posixLocale;
        this.languagePriority = languagePriority;
        this.appleLocale = appleLocale;
    }

    public String getValue() {
        return value;
    }

    /** Chromium/Electron {@code --lang}. */
    public String chromiumLang() {
        return value;
    }

    public String posixLocale() {
        return posixLocale;
    }

    public String languagePriority() {
        return languagePriority;
    }

    public String appleLocale() {
        return appleLocale;
    }

    public String bcp47() {
        return bcp47;
    }

    /** {@code --accept-lang} для HTTP и Chromium. */
    public String acceptLanguage() {
        return bcp47 + "," + value;
    }

    /** Cocoa {@code -AppleLanguages} — только этот процесс, без смены языка пользователя. */
    public String appleLanguagesArg() {
        return "(" + value + ")";
    }

    public static DesktopLocale fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            return RU;
        }
        String v = raw.trim().toLowerCase(Locale.ROOT).replace('_', '-');
        if (v.equals("ru") || v.equals("ru-ru")) {
            return RU;
        }
        if (v.equals("en") || v.equals("en-us")) {
            return EN;
        }
        throw new IllegalArgumentException(
                "Неизвестная локаль: '" + raw + "'. Допустимые значения: ru, en");
    }
}
