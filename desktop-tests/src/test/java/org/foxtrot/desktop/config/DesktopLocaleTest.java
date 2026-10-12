package org.foxtrot.desktop.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DesktopLocaleTest {

    @Test
    void defaultsToRussianWhenBlank() {
        assertEquals(DesktopLocale.RU, DesktopLocale.fromString(null));
        assertEquals(DesktopLocale.RU, DesktopLocale.fromString(""));
        assertEquals(DesktopLocale.RU, DesktopLocale.fromString(" ru "));
    }

    @Test
    void chromiumAndPosixValuesForRussian() {
        DesktopLocale ru = DesktopLocale.RU;
        assertEquals("ru", ru.chromiumLang());
        assertEquals("ru_RU.UTF-8", ru.posixLocale());
        assertEquals("ru-RU,ru", ru.acceptLanguage());
        assertEquals("(ru)", ru.appleLanguagesArg());
        assertEquals("ru_RU", ru.appleLocale());
    }

    @Test
    void rejectsUnknownLocale() {
        assertThrows(IllegalArgumentException.class, () -> DesktopLocale.fromString("de"));
    }
}
