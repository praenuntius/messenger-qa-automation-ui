package org.foxtrot.api.auth;

public final class ApiAuthContextHolder {

    private static final ThreadLocal<ApiAuthContext> CTX = new ThreadLocal<>();

    private ApiAuthContextHolder() {}

    public static ApiAuthContext get() {
        return CTX.get();
    }

    public static void set(ApiAuthContext ctx) {
        CTX.set(ctx);
    }

    public static void clear() {
        CTX.remove();
    }
}

