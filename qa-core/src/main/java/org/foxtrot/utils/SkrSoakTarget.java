package org.foxtrot.utils;

/**
 * Цель soak-тика: UI-чат в homespace или в пространстве.
 * {@code spaceName == null} — homespace. Ключ шифрования на сессии, не на чате.
 */
public record SkrSoakTarget(String chatName, String spaceName, SkrSharedChat chat) {

    public boolean isHomespace() {
        return spaceName == null || spaceName.isBlank();
    }

    /**
     * Префикс имён чатов этого клиента, чтобы параллельный web/android/ios
     * не чистил чужой пул. {@code -Dencryption.soak.name.prefix=skr-web-}
     */
    public static String namePrefix() {
        String explicit = System.getProperty("encryption.soak.name.prefix", "").trim();
        if (!explicit.isEmpty()) {
            return explicit.endsWith("-") ? explicit : explicit + "-";
        }
        String platform = System.getProperty("platform.name", "web").trim().toLowerCase();
        if (platform.isEmpty()) {
            platform = "web";
        }
        return "skr-" + platform + "-";
    }
}

