package org.foxtrot.backoffice.constants;

public final class BackofficeEndpoints {

    public static final String AUTH_START = "/api/v1/auth/start";
    public static final String AUTH_VERIFY_OTP = "/api/v1/auth/verifyOtp";

    public static final String USERS_EXIST = "/api/v1/users/exist";
    public static final String USERS_WHITELIST_CELLPHONES = "/api/v1/users/whitelist/cellphones";

    public static final String ORGANIZATIONS = "/api/v1/organizations";

    private BackofficeEndpoints() {
    }
}
