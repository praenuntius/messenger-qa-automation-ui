package org.foxtrot.backoffice.service;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.foxtrot.api.SmartLoggingFilter;
import org.foxtrot.backoffice.BaseBackofficeApiService;
import org.foxtrot.backoffice.dto.BackofficeAuthStartRequest;
import org.foxtrot.backoffice.dto.BackofficeAuthStepResponse;
import org.foxtrot.backoffice.dto.BackofficeVerifyOtpRequest;
import org.foxtrot.backoffice.session.BackofficeSessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.foxtrot.backoffice.constants.BackofficeEndpoints.AUTH_START;
import static org.foxtrot.backoffice.constants.BackofficeEndpoints.AUTH_VERIFY_OTP;

/**
 * Логин суперадмина в backoffice: start → verifyOtp → JSESSIONID в cookie.
 */
public class BackofficeAuthApiService extends BaseBackofficeApiService {

    private static final Logger logger = LoggerFactory.getLogger(BackofficeAuthApiService.class);

    /**
     * Полный логин суперадмина по кредам из конфига.
     *
     * @return JSESSIONID — его нужно передавать в запросы users/exist, whitelist и т.д.
     */
    public String loginAsSuperAdmin() {
        return login(
                config.backofficeSuperAdminPhone(),
                config.backofficeSuperAdminOtp()
        );
    }

    /**
     * @param phone 10 цифр (9101234567) или 11 с ведущей 7 (79101234567)
     * @param otp   код из SMS (на preprod обычно 111111)
     * @return JSESSIONID
     */
    public String login(String phone, String otp) {
        String normalizedPhone = normalizePhoneForBackoffice(phone);
        RequestSpecification spec = given()
                .filter(new SmartLoggingFilter())
                .baseUri(config.backofficeApiUrl())
                .contentType("application/json");

        Response startResponse = spec
                .body(new BackofficeAuthStartRequest(
                        new BackofficeAuthStartRequest.AuthWay("PHONE", normalizedPhone)
                ))
                .when()
                .post(AUTH_START)
                .then()
                .extract()
                .response();

        if (startResponse.statusCode() != SC_OK) {
            throw new RuntimeException(
                    "Backoffice auth/start failed: status=" + startResponse.statusCode()
                            + ", body=" + startResponse.getBody().asString()
            );
        }

        BackofficeAuthStepResponse startBody = startResponse.getBody().as(BackofficeAuthStepResponse.class);
        String processId = startBody.getProcessId();
        if (processId == null || processId.isBlank()) {
            throw new RuntimeException("Backoffice auth/start: empty processId, body=" + startResponse.getBody().asString());
        }
        logger.info("Backoffice auth/start: processId={}, step={}", processId, startBody.getStep());

        Response verifyResponse = spec
                .cookies(startResponse.getCookies())
                .body(new BackofficeVerifyOtpRequest(otp, processId))
                .when()
                .post(AUTH_VERIFY_OTP)
                .then()
                .extract()
                .response();

        if (verifyResponse.statusCode() != SC_OK) {
            throw new RuntimeException(
                    "Backoffice auth/verifyOtp failed: status=" + verifyResponse.statusCode()
                            + ", body=" + verifyResponse.getBody().asString()
            );
        }

        BackofficeAuthStepResponse verifyBody = verifyResponse.getBody().as(BackofficeAuthStepResponse.class);
        if (!"FINISHED".equals(verifyBody.getStep())) {
            logger.warn("Backoffice auth/verifyOtp: unexpected step={}, processId={}",
                    verifyBody.getStep(), verifyBody.getProcessId());
        }

        String jsessionId = verifyResponse.getCookie("JSESSIONID");
        if (jsessionId == null || jsessionId.isBlank()) {
            jsessionId = startResponse.getCookie("JSESSIONID");
        }
        if (jsessionId == null || jsessionId.isBlank()) {
            throw new IllegalStateException(
                    "После verifyOtp нет cookie JSESSIONID — без него нельзя вызывать users/exist и whitelist"
            );
        }

        BackofficeSessionHolder.setSessionId(jsessionId);
        logger.info("Backoffice login OK, JSESSIONID получен (processId после логина не используется в users API)");
        return jsessionId;
    }

    private static String normalizePhoneForBackoffice(String phone) {
        String digits = phone.replaceAll("\\D", "");
        if (digits.length() == 10) {
            return "7" + digits;
        }
        if (digits.length() == 11 && digits.startsWith("7")) {
            return digits;
        }
        throw new IllegalArgumentException("Некорректный номер для backoffice: " + phone);
    }
}
