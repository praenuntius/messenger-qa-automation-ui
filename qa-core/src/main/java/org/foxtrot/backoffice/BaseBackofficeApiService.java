package org.foxtrot.backoffice;

import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.api.SmartLoggingFilter;
import org.foxtrot.backoffice.config.BackofficeConfig;
import org.foxtrot.backoffice.service.BackofficeAuthApiService;
import org.foxtrot.backoffice.session.BackofficeSessionHolder;

import static io.restassured.RestAssured.given;

public abstract class BaseBackofficeApiService {

    protected static final BackofficeConfig config = ConfigFactory.create(BackofficeConfig.class);

    private static final BackofficeAuthApiService authApiService = new BackofficeAuthApiService();

    protected RequestSpecification getBackofficeRequestSpec() {
        String sessionId = resolveSessionId();
        return given()
                .filter(new SmartLoggingFilter())
                .baseUri(config.backofficeApiUrl())
                .cookie("JSESSIONID", sessionId)
                .contentType("application/json");
    }

    /**
     * Гарантирует авторизованную сессию backoffice (JSESSIONID).
     * Приоритет: ThreadLocal после login → backofficeSessionId из конфига → автологин суперадмина.
     */
    protected static String resolveSessionId() {
        if (BackofficeSessionHolder.hasSession()) {
            return BackofficeSessionHolder.getSessionId();
        }

        String configured = config.backofficeSessionId();
        if (configured != null && !configured.isBlank()) {
            BackofficeSessionHolder.setSessionId(configured);
            return configured;
        }

        return authApiService.loginAsSuperAdmin();
    }

    /** Сбросить кэш сессии (например между тестами). */
    public static void clearBackofficeSession() {
        BackofficeSessionHolder.clear();
    }
}
