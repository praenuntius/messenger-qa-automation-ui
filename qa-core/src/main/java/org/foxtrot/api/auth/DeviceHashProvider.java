package org.foxtrot.api.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Генерация стабильного deviceHash на поток/пользователя.
 * Важно: deviceHash должен быть уникальным между пользователями в параллели,
 * но стабильным в рамках одной API-сессии (потока).
 */
public final class DeviceHashProvider {

    private static final String RUN_ID = firstNonBlank(
            System.getenv("ALLURE_JOB_RUN_ID"),
            System.getenv("BUILD_TAG"),
            System.getenv("BUILD_NUMBER"),
            System.getenv("CI"),
            String.valueOf(System.currentTimeMillis())
    );

    private DeviceHashProvider() {}

    public static String forUserKey(String userKey) {
        String seed = RUN_ID + "|" + Thread.currentThread().getName() + "|" + System.identityHashCode(Thread.currentThread()) + "|" + userKey;
        return "qa-" + sha256Hex(seed).substring(0, 24);
    }

    private static String firstNonBlank(String... values) {
        if (values == null) return "";
        for (String v : values) {
            if (v != null && !v.trim().isEmpty() && !"true".equalsIgnoreCase(v.trim())) {
                return v.trim();
            }
        }
        // CI может быть просто "true" — тогда используем fallback
        return String.valueOf(System.currentTimeMillis());
    }

    private static String sha256Hex(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            // fallback: не криптостойко, но уникально достаточно для тестов
            return Integer.toHexString(value.hashCode()) + Long.toHexString(System.nanoTime());
        }
    }
}

