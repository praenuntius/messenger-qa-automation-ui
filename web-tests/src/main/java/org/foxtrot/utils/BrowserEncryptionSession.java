package org.foxtrot.utils;

import com.codeborne.selenide.Selenide;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.foxtrot.enums.UsersEnum;

import java.time.Duration;

/**
 * Читает auth + encryptKey из localStorage браузера после UI-логина,
 * чтобы инициировать серверную ротацию ключа той же клиентской сессии.
 */
public final class BrowserEncryptionSession {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private BrowserEncryptionSession() {
    }

    public static SymmetricKeySession fromLocalStorage(UsersEnum user) {
        try {
            String authJson = Selenide.executeJavaScript("return localStorage.getItem('auth');");
            String encryptKeyJson = readEncryptKeyJson();
            if (authJson == null || authJson.isBlank()) {
                throw new IllegalStateException("localStorage.auth is empty — UI login did not finish");
            }
            if (encryptKeyJson == null || encryptKeyJson.isBlank()) {
                throw new IllegalStateException("localStorage.encryptKey is empty — encryption not set up in browser");
            }

            JsonNode auth = MAPPER.readTree(authJson);
            String accessToken = auth.path("tokens").path("accessToken").asText(null);
            if (accessToken == null || accessToken.isBlank()) {
                throw new IllegalStateException("auth.tokens.accessToken is empty");
            }

            JsonNode encryptKey = MAPPER.readTree(encryptKeyJson).path("encryptKey");
            JsonNode fileKeyNode = MAPPER.readTree(encryptKeyJson).path("encryptFileKey");

            SymmetricKeyMaterial messageKey = new SymmetricKeyMaterial(
                    encryptKey.path("id").asLong(),
                    encryptKey.path("key").asText(),
                    encryptKey.path("transformation").asText(null)
            );
            SymmetricKeyMaterial fileKey = fileKeyNode.isMissingNode() || fileKeyNode.isNull()
                    ? messageKey
                    : new SymmetricKeyMaterial(
                            fileKeyNode.path("id").asLong(),
                            fileKeyNode.path("key").asText(),
                            fileKeyNode.path("transformation").asText(null)
                    );

            return SymmetricKeySession.builder()
                    .user(user)
                    .accountId(null)
                    .accessToken(accessToken)
                    .deviceHash("browser")
                    .encryptKeyJson(encryptKeyJson)
                    .messageKey(messageKey)
                    .fileKey(fileKey)
                    .build();
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to build SymmetricKeySession from browser localStorage", e);
        }
    }

    /** Текущий {@code encryptKey.id} из localStorage, или {@code null} если нет. */
    public static Long readMessageKeyId() {
        try {
            String encryptKeyJson = readEncryptKeyJson();
            if (encryptKeyJson == null || encryptKeyJson.isBlank()) {
                return null;
            }
            long id = MAPPER.readTree(encryptKeyJson).path("encryptKey").path("id").asLong(0L);
            return id == 0L ? null : id;
        } catch (Exception e) {
            return null;
        }
    }

    /** Текущий {@code encryptFileKey.id} из localStorage, или {@code null} если нет. */
    public static Long readFileKeyId() {
        try {
            String encryptKeyJson = readEncryptKeyJson();
            if (encryptKeyJson == null || encryptKeyJson.isBlank()) {
                return null;
            }
            long id = MAPPER.readTree(encryptKeyJson).path("encryptFileKey").path("id").asLong(0L);
            return id == 0L ? null : id;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Ждёт, пока клиент сам обновит {@code encryptKey.id} в localStorage
     * (после 493 → updateSymmetricKeyV2 → confirm; current меняется только после confirm 200).
     *
     * @return новый key id
     */
    public static long awaitMessageKeyIdChange(long previousKeyId, Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            Long current = readMessageKeyId();
            if (current != null && current != previousKeyId) {
                return current;
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        throw new AssertionError(
                "Browser encryptKey.id did not change from " + previousKeyId
                        + " within " + timeout
                        + " (last=" + readMessageKeyId() + ", pending=" + readPendingEncryptKeyId() + ")"
        );
    }

    /**
     * Id pending-ключа из persist (encryptKeyStore.pendingEncryptKey / localStorage variants).
     * {@code null} если pending нет.
     */
    public static Long readPendingEncryptKeyId() {
        try {
            Object raw = Selenide.executeJavaScript("""
                    const tryParse = (s) => {
                      if (!s) return null;
                      try { return JSON.parse(s); } catch (e) { return null; }
                    };
                    const fromNode = (n) => {
                      if (!n) return null;
                      if (typeof n.id === 'number') return n.id;
                      if (n.encryptKey && typeof n.encryptKey.id === 'number') return n.encryptKey.id;
                      if (typeof n.encryptionKeyId === 'number') return n.encryptionKeyId;
                      return null;
                    };
                    const keys = ['pendingEncryptKey', 'encryptKeyStore', 'encryptKey'];
                    for (const k of keys) {
                      const parsed = tryParse(localStorage.getItem(k));
                      if (!parsed) continue;
                      if (k === 'pendingEncryptKey') {
                        const id = fromNode(parsed);
                        if (id != null) return id;
                      }
                      if (parsed.pendingEncryptKey) {
                        const id = fromNode(parsed.pendingEncryptKey);
                        if (id != null) return id;
                      }
                      if (parsed.state && parsed.state.pendingEncryptKey) {
                        const id = fromNode(parsed.state.pendingEncryptKey);
                        if (id != null) return id;
                      }
                    }
                    return null;
                    """);
            if (raw instanceof Number n) {
                long id = n.longValue();
                return id == 0L ? null : id;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static long awaitPendingEncryptKey(Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            Long pending = readPendingEncryptKeyId();
            if (pending != null) {
                return pending;
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        throw new AssertionError("pendingEncryptKey did not appear within " + timeout);
    }

    public static void awaitPendingEncryptKeyCleared(Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            if (readPendingEncryptKeyId() == null) {
                return;
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        throw new AssertionError(
                "pendingEncryptKey still present after " + timeout
                        + " (pending=" + readPendingEncryptKeyId() + ")"
        );
    }

    /** Текущий {@code tokens.accessToken} из localStorage.auth, или {@code null}. */
    public static String readAccessToken() {
        try {
            String authJson = Selenide.executeJavaScript("return localStorage.getItem('auth');");
            if (authJson == null || authJson.isBlank()) {
                return null;
            }
            String token = MAPPER.readTree(authJson).path("tokens").path("accessToken").asText(null);
            return (token == null || token.isBlank()) ? null : token;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Подменяет accessToken на заведомо невалидный, refreshToken не трогает.
     * Не обнуляем accessTokenValidTill — иначе клиент может сразу уйти на login
     * до SSE reconnect / refresh.
     *
     * @return предыдущий accessToken
     */
    public static String corruptAccessToken() {
        String previous = readAccessToken();
        if (previous == null) {
            throw new IllegalStateException("localStorage.auth.tokens.accessToken is empty");
        }
        Boolean ok = Selenide.executeJavaScript("""
                const raw = localStorage.getItem('auth');
                if (!raw) return false;
                const auth = JSON.parse(raw);
                if (!auth.tokens) auth.tokens = {};
                auth.tokens.accessToken = 'skr-invalid-access-token-' + Date.now();
                localStorage.setItem('auth', JSON.stringify(auth));
                return true;
                """);
        if (!Boolean.TRUE.equals(ok)) {
            throw new IllegalStateException("Failed to corrupt accessToken in localStorage.auth");
        }
        return previous;
    }

    /** Записывает accessToken в localStorage.auth (имитация успешного refresh). */
    public static void writeAccessToken(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("accessToken must not be blank");
        }
        Boolean ok = Selenide.executeJavaScript("""
                const raw = localStorage.getItem('auth');
                if (!raw) return false;
                const auth = JSON.parse(raw);
                if (!auth.tokens) auth.tokens = {};
                auth.tokens.accessToken = arguments[0];
                localStorage.setItem('auth', JSON.stringify(auth));
                return true;
                """, accessToken);
        if (!Boolean.TRUE.equals(ok)) {
            throw new IllegalStateException("Failed to write accessToken to localStorage.auth");
        }
    }

    /**
     * Ждёт, пока клиент обновит {@code tokens.accessToken} (после refresh).
     *
     * @return новый accessToken
     */
    public static String awaitAccessTokenChange(String previousAccessToken, Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            String current = readAccessToken();
            if (current != null
                    && !current.equals(previousAccessToken)
                    && !current.startsWith("skr-invalid-access-token-")) {
                return current;
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        throw new AssertionError(
                "Browser accessToken did not refresh within " + timeout
                        + " (last=" + readAccessToken() + ")"
        );
    }

    /**
     * Ждёт смены {@code encryptFileKey.id} (мягко: возвращает null по таймауту).
     */
    public static Long awaitFileKeyIdChangeSoft(Long previousFileKeyId, Duration timeout) {
        if (previousFileKeyId == null) {
            return readFileKeyId();
        }
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            Long current = readFileKeyId();
            if (current != null && !current.equals(previousFileKeyId)) {
                return current;
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        return readFileKeyId();
    }

    private static String readEncryptKeyJson() {
        return Selenide.executeJavaScript("return localStorage.getItem('encryptKey');");
    }
}
