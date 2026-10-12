package org.foxtrot.api.service;

import io.restassured.response.Response;
import org.foxtrot.api.BaseApiService;
import org.foxtrot.api.dto.auth.SendRecoveryEmailRequest;
import org.foxtrot.api.dto.auth.SignUpRequest;
import org.foxtrot.api.dto.auth.StartRequest;
import org.foxtrot.api.dto.auth.SubmitAccountRequest;
import org.foxtrot.api.dto.auth.VerifyCodeRequest;
import org.foxtrot.api.dto.auth.response.SignInResponse;
import org.foxtrot.api.dto.auth.response.StartAuthResponse;
import org.foxtrot.utils.EncryptionHelper;
import org.foxtrot.utils.SymmetricKeyMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.http.HttpStatus.SC_OK;
import static org.foxtrot.api.constants.ApiEndpoints.ASSETS;
import static org.foxtrot.api.constants.ApiEndpoints.SEND_RECOVERY_EMAIL;
import static org.foxtrot.api.constants.ApiEndpoints.SIGN_UP;
import static org.foxtrot.api.constants.ApiEndpoints.SUBMIT_ACCOUNT;
import static org.foxtrot.api.constants.ApiEndpoints.VERIFY_CODE;

/**
 * API-флоу регистрации пользователя в мессенджере:
 * start → verifyCode → [assets] → submitAccount → sendRecoveryEmail → signUp.
 */
public class RegistrationApiService extends BaseApiService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationApiService.class);

    public static final String DEFAULT_SMS_CODE = "111111";
    public static final String DEFAULT_PASSWORD = "111111";
    /** Совпадает с AuthApiService.buildDeviceInfo("hash") — иначе process ломается между шагами. */
    public static final String DEFAULT_DEVICE_INFO = "deviceInfo:hash";
    public static final String DEFAULT_DEVICE_HASH = "hash";
    public static final long DEFAULT_ENCRYPTION_KEY_ID = 1111L;

    private final EncryptionHelper encryptionHelper = new EncryptionHelper();
    private final AuthApiService authApiService = new AuthApiService();

    public Response startRegistration(String phone, SymmetricKeyMaterial cipherKey) {
        return authApiService.startAuth(new StartRequest(
                new StartRequest.AuthWay("PHONE", normalizePhone(phone)),
                DEFAULT_DEVICE_HASH,
                "ru",
                DEFAULT_DEVICE_INFO
        ), cipherKey);
    }

    public Response verifyCode(String processId, String code) {
        return getRequestSpecWithOutAuth()
                .body(new VerifyCodeRequest(processId, code))
                .when()
                .post(VERIFY_CODE)
                .then()
                .extract()
                .response();
    }

    public Response submitAccount(SubmitAccountRequest request, long encryptionKeyId) {
        return submitAccount(request, encryptionKeyId, null);
    }

    public Response submitAccount(SubmitAccountRequest request, SymmetricKeyMaterial cipherKey) {
        return submitAccount(request, cipherKey.getId(), cipherKey);
    }

    public Response submitAccount(SubmitAccountRequest request, long encryptionKeyId, SymmetricKeyMaterial cipherKey) {
        if (cipherKey == null) {
            return getRequestSpecWithOutAuth()
                    .header("encryption-key-id", encryptionKeyId)
                    .body(request)
                    .when()
                    .post(SUBMIT_ACCOUNT)
                    .then()
                    .extract()
                    .response();
        }
        return EncryptionApiService.postEncryptedAuth(SUBMIT_ACCOUNT, request, cipherKey);
    }

    public Response sendRecoveryEmail(SendRecoveryEmailRequest request, long encryptionKeyId) {
        return sendRecoveryEmail(request, encryptionKeyId, null);
    }

    public Response sendRecoveryEmail(SendRecoveryEmailRequest request, SymmetricKeyMaterial cipherKey) {
        return sendRecoveryEmail(request, cipherKey.getId(), cipherKey);
    }

    public Response sendRecoveryEmail(SendRecoveryEmailRequest request, long encryptionKeyId, SymmetricKeyMaterial cipherKey) {
        if (cipherKey == null) {
            return getRequestSpecWithOutAuth()
                    .header("encryption-key-id", encryptionKeyId)
                    .body(request)
                    .when()
                    .post(SEND_RECOVERY_EMAIL)
                    .then()
                    .extract()
                    .response();
        }
        return EncryptionApiService.postEncryptedAuth(SEND_RECOVERY_EMAIL, request, cipherKey);
    }

    public Response signUp(SignUpRequest request, long encryptionKeyId) {
        return signUp(request, encryptionKeyId, null);
    }

    public Response signUp(SignUpRequest request, SymmetricKeyMaterial cipherKey) {
        return signUp(request, cipherKey.getId(), cipherKey);
    }

    public Response signUp(SignUpRequest request, long encryptionKeyId, SymmetricKeyMaterial cipherKey) {
        if (cipherKey == null) {
            return getRequestSpecWithOutAuth()
                    .header("encryption-key-id", encryptionKeyId)
                    .body(request)
                    .when()
                    .post(SIGN_UP)
                    .then()
                    .extract()
                    .response();
        }
        return EncryptionApiService.postEncryptedAuth(SIGN_UP, request, cipherKey);
    }

    /**
     * Загрузка симметричного ключа как asset (используется фронтом при полном шифровании).
     * На preprod endpoint может быть недоступен (404) — ошибка игнорируется.
     */
    public void uploadAssetIfSupported(String symmetricKeyBase64, long encryptionKeyId) {
        Response response = getRequestSpecWithOutAuth()
                .header("encryption-key-id", encryptionKeyId)
                .body(new org.foxtrot.api.dto.auth.AssetRequest(symmetricKeyBase64))
                .when()
                .post(ASSETS)
                .then()
                .extract()
                .response();
        if (response.statusCode() == 404 || response.statusCode() == 400) {
            return;
        }
        if (response.statusCode() != SC_OK) {
            throw new RuntimeException(
                    "assets upload failed: status=" + response.statusCode() + ", body=" + response.getBody().asString()
            );
        }
    }

    /**
     * Полный флоу регистрации нового пользователя по номеру (уже добавленному в whitelist backoffice).
     */
    public SignInResponse performRegistration(
            String phone,
            String firstName,
            String lastName,
            String recoveryEmail,
            String password,
            String smsCode,
            String emailVerificationCode,
            long encryptionKeyId,
            boolean setupRealEncryptionKeys
    ) {
        SymmetricKeyMaterial startCipherKey;
        if (setupRealEncryptionKeys) {
            EncryptionHelper.EncryptKeyInfo keys = encryptionHelper.setupEncryptionKeys();
            encryptionKeyId = keys.getEncryptionKeyId();
            startCipherKey = keys.getMessageKey();
            uploadAssetIfSupported(extractKeyFromJson(keys.getEncryptKeyJson(), "encryptKey"), encryptionKeyId);
            uploadAssetIfSupported(extractKeyFromJson(keys.getEncryptKeyJson(), "encryptFileKey"), encryptionKeyId);
        } else {
            startCipherKey = encryptionHelper.generateAndSaveSymmetricKey();
            encryptionKeyId = startCipherKey.getId();
        }

        Response startResponse = startRegistration(phone, startCipherKey);
        assertOk(startResponse, "auth/start");
        StartAuthResponse startBody = authApiService.parseStartAuthResponse(startResponse, startCipherKey);
        String processId = startBody.getProcessId();

        Response verifyResponse = authApiService.verifyCode(new VerifyCodeRequest(processId, smsCode), startCipherKey);
        assertOk(verifyResponse, "auth/verifyCode");

        String nextStep = AuthApiService.extractNextStepPublic(verifyResponse, startCipherKey);
        if (nextStep == null || nextStep.isBlank()) {
            nextStep = startBody.getNextStep();
        }
        logger.info("После verifyCode phone={} processId={} nextStep={}", phone, processId, nextStep);

        if (AuthApiService.isLoginStepPublic(nextStep)) {
            throw new UserAlreadyRegisteredException(phone);
        }

        SubmitAccountRequest submitRequest = new SubmitAccountRequest(
                processId,
                DEFAULT_DEVICE_INFO,
                new SubmitAccountRequest.RegistrationUser(firstName, lastName, "", password)
        );
        Response submitResponse = submitAccount(submitRequest, startCipherKey);
        assertOk(submitResponse, "auth/submitAccount");

        SendRecoveryEmailRequest emailRequest = new SendRecoveryEmailRequest(processId, recoveryEmail);
        Response emailResponse = sendRecoveryEmail(emailRequest, startCipherKey);
        assertOk(emailResponse, "auth/sendRecoveryEmail");

        SignUpRequest signUpRequest = new SignUpRequest(
                processId,
                DEFAULT_DEVICE_INFO,
                recoveryEmail,
                emailVerificationCode,
                emailVerificationCode
        );
        Response signUpResponse = signUp(signUpRequest, startCipherKey);
        assertOk(signUpResponse, "auth/signUp");
        return authApiService.parseSignInResponse(signUpResponse, startCipherKey);
    }

    public SignInResponse performRegistration(String phone, String firstName, String lastName, String recoveryEmail) {
        return performRegistration(
                phone,
                firstName,
                lastName,
                recoveryEmail,
                DEFAULT_PASSWORD,
                DEFAULT_SMS_CODE,
                DEFAULT_SMS_CODE,
                DEFAULT_ENCRYPTION_KEY_ID,
                false
        );
    }

    private static void assertOk(Response response, String step) {
        if (response.statusCode() != SC_OK) {
            throw new RuntimeException(
                    "Registration step " + step + " failed: status=" + response.statusCode()
                            + ", body=" + response.getBody().asString()
            );
        }
    }

    private static String normalizePhone(String phone) {
        String digits = phone.replaceAll("\\D", "");
        if (digits.length() == 10 && digits.startsWith("9")) {
            return "7" + digits;
        }
        if (digits.length() == 11 && digits.startsWith("7")) {
            return digits;
        }
        return digits;
    }

    private static String extractKeyFromJson(String encryptKeyJson, String keyName) {
        String marker = "\"" + keyName + "\":{\"id\":";
        int start = encryptKeyJson.indexOf(marker);
        if (start < 0) {
            throw new IllegalArgumentException("Key " + keyName + " not found in encryptKeyJson");
        }
        int keyField = encryptKeyJson.indexOf("\"key\":\"", start);
        if (keyField < 0) {
            throw new IllegalArgumentException("key field not found for " + keyName);
        }
        int valueStart = keyField + "\"key\":\"".length();
        int valueEnd = encryptKeyJson.indexOf('"', valueStart);
        return encryptKeyJson.substring(valueStart, valueEnd);
    }
}
