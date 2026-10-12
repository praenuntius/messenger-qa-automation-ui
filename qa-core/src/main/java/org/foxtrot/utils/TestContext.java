package org.foxtrot.utils;

import com.codeborne.selenide.WebDriverRunner;

import java.util.HashMap;
import java.util.Map;

public class TestContext {
    private static final Map<String, Object> contextData = new HashMap<>();

    public static void put(String key, Object value) {
        contextData.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) contextData.get(key);
    }

    public static Long getLong(String key) {
        Object value = contextData.get(key);
        if (value instanceof Long) {
            return (Long) value;
        }
        return null;
    }

    public static String getString(String key) {
        Object value = contextData.get(key);
        return value != null ? value.toString() : null;
    }

    public static void clear() {
        contextData.clear();
    }

    public static void saveUrlPage(String key) {
        String currentUrl = WebDriverRunner.url();
        put(key, currentUrl);
    }

    public static void saveCurrentPageId(String key) {
        String currentUrl = WebDriverRunner.url();
        Long id = extractLastPathSegment(currentUrl);
        put(key, id);
    }

    private static long extractLastPathSegment(String url) {
        java.net.URI uri = java.net.URI.create(url);
        String[] segments = uri.getPath().split("/");
        for (int i = segments.length - 1; i >= 0; i--) {
            if (!segments[i].isEmpty()) {
                return Long.parseLong(segments[i]);
            }
        }
        throw new IllegalArgumentException("URL не содержит числового id в пути: " + url);
    }
}
