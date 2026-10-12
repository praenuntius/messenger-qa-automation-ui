package org.foxtrot.backoffice.session;

/**
 * JSESSIONID авторизованной сессии backoffice (после verifyOtp).
 * processId нужен только на этапе логина; для users/* API используется cookie.
 */
public final class BackofficeSessionHolder {

    private static final ThreadLocal<String> sessionId = new ThreadLocal<>();

    private BackofficeSessionHolder() {
    }

    public static void setSessionId(String jsessionId) {
        sessionId.set(jsessionId);
    }

    public static String getSessionId() {
        return sessionId.get();
    }

    public static boolean hasSession() {
        String id = sessionId.get();
        return id != null && !id.isBlank();
    }

    public static void clear() {
        sessionId.remove();
    }
}
