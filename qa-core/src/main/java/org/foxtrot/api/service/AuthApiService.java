package org.foxtrot.api.service;

import io.restassured.response.Response;
import org.foxtrot.api.BaseApiService;
import org.foxtrot.api.dto.auth.RefreshTokenRequest;
import org.foxtrot.api.dto.auth.SignInRequest;
import org.foxtrot.api.dto.auth.StartRequest;
import org.foxtrot.api.dto.auth.VerifyCodeRequest;
import org.foxtrot.api.dto.auth.VerifyPasswordRequest;
import org.foxtrot.api.dto.auth.response.SignInResponse;
import org.foxtrot.api.dto.auth.response.StartAuthResponse;
import org.foxtrot.api.dto.encryption.EncryptedContentDto;
import org.foxtrot.utils.EncryptionHelper;
import org.foxtrot.utils.SymmetricCipherHelper;
import org.foxtrot.utils.SymmetricKeyMaterial;

import static org.apache.http.HttpStatus.SC_OK;
import static org.foxtrot.api.constants.ApiEndpoints.AUTH_START;
import static org.foxtrot.api.constants.ApiEndpoints.REFRESH_TOKEN;
import static org.foxtrot.api.constants.ApiEndpoints.SIGN_IN;
import static org.foxtrot.api.constants.ApiEndpoints.VERIFY_CODE;
import static org.foxtrot.api.constants.ApiEndpoints.VERIFY_PASSWORD;

public class AuthApiService extends BaseApiService {

    private static final Object LOCK = new Object();
    private static final String DEVICE_INFO_PREFIX = "deviceInfo:";

    public Response startAuth(StartRequest startRequest) {
        return startAuth(startRequest, (SymmetricKeyMaterial) null);
    }

    public Response startAuth(StartRequest startRequest, SymmetricKeyMaterial cipherKey) {
        if (cipherKey == null) {
            return getRequestSpecWithOutAuth()
                    .body(startRequest)
                    .when()
                    .post(AUTH_START)
                    .then()
                    .extract()
                    .response();
        }

        return EncryptionApiService.postEncryptedAuth(AUTH_START, startRequest, cipherKey);
    }

    public StartAuthResponse parseStartAuthResponse(Response response, SymmetricKeyMaterial cipherKey) {
        return parseEncryptedResponse(response, cipherKey, StartAuthResponse.class);
    }

    public SignInResponse parseSignInResponse(Response response, SymmetricKeyMaterial cipherKey) {
        return parseEncryptedResponse(response, cipherKey, SignInResponse.class);
    }

    public <T> T parseEncryptedResponse(Response response, SymmetricKeyMaterial cipherKey, Class<T> type) {
        String body = response.getBody().asString();
        if (cipherKey != null) {
            body = SymmetricCipherHelper.decryptResponseBody(body, cipherKey);
        }
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(body, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse encrypted response as " + type.getSimpleName() + ": " + body, e);
        }
    }

    public Response verifyCode(VerifyCodeRequest verifyCodeRequestDto) {
        return verifyCode(verifyCodeRequestDto, null);
    }

    public Response verifyCode(VerifyCodeRequest verifyCodeRequestDto, SymmetricKeyMaterial cipherKey) {
        if (cipherKey == null) {
            return getRequestSpecWithOutAuth()
                    .body(verifyCodeRequestDto)
                    .when()
                    .post(VERIFY_CODE)
                    .then()
                    .extract().response();
        }
        return postEncryptedAuth(VERIFY_CODE, verifyCodeRequestDto, cipherKey);
    }

    public Response verifyPassword(VerifyPasswordRequest verifyPasswordRequestDto) {
        return verifyPassword(verifyPasswordRequestDto, null);
    }

    public Response verifyPassword(VerifyPasswordRequest verifyPasswordRequestDto, SymmetricKeyMaterial cipherKey) {
        if (cipherKey == null) {
            return getRequestSpecWithOutAuth()
                    .body(verifyPasswordRequestDto)
                    .when()
                    .post(VERIFY_PASSWORD)
                    .then()
                    .extract().response();
        }
        return postEncryptedAuth(VERIFY_PASSWORD, verifyPasswordRequestDto, cipherKey);
    }

    /**
     * {@code POST /api/v2/auth/refreshToken} — plaintext (X-Disable-Encryption).
     * Body: {@code { "jwtToken": "..." }}. Prefer {@link #refreshTokenEncrypted} on stage14.
     */
    public Response refreshToken(String jwtToken) {
        return refreshToken(jwtToken, null);
    }

    public Response refreshToken(String jwtToken, String accessToken) {
        var spec = getRequestSpecWithOutAuth().body(new RefreshTokenRequest(jwtToken));
        if (accessToken != null && !accessToken.isBlank()) {
            spec.header("Authorization", accessToken);
        }
        return spec.when()
                .post(REFRESH_TOKEN)
                .then()
                .extract()
                .response();
    }

    /**
     * Encrypted refresh (как signIn/verify*): Authorization + encryption-key-id, body {@code {content}}.
     */
    public Response refreshTokenEncrypted(String jwtToken,
                                          String accessToken,
                                          SymmetricKeyMaterial cipherKey) {
        String encryptedContent = SymmetricCipherHelper.encryptJson(
                toJson(new RefreshTokenRequest(jwtToken)),
                cipherKey
        );
        return EncryptionApiService.encryptedRequestSpec(accessToken, cipherKey.getId(), false)
                .body(new EncryptedContentDto(encryptedContent))
                .when()
                .post(REFRESH_TOKEN)
                .then()
                .extract()
                .response();
    }

    /**
     * Refresh access token. На stage14 сначала encrypted; при OAUTH_VALIDATION_FAILED
     * пробуем {@code jwtToken=accessToken} (поле названо jwtToken, не refreshToken).
     */
    public SignInResponse refreshAccessToken(String refreshJwt,
                                             String accessToken,
                                             SymmetricKeyMaterial cipherKey) {
        Response response = refreshTokenEncrypted(refreshJwt, accessToken, cipherKey);
        if (response.statusCode() != SC_OK) {
            response = refreshTokenEncrypted(accessToken, accessToken, cipherKey);
        }
        if (response.statusCode() != SC_OK) {
            // last resort: plaintext + Authorization
            response = refreshToken(refreshJwt, accessToken);
        }
        if (response.statusCode() != SC_OK) {
            response = refreshToken(accessToken, accessToken);
        }
        if (response.statusCode() != SC_OK) {
            throw new RuntimeException("refreshToken failed: status=" + response.statusCode()
                    + ", body=" + response.getBody().asString());
        }
        // encrypted response if cipher present and body looks encrypted
        try {
            return parseSignInResponse(response, cipherKey);
        } catch (RuntimeException decryptFailed) {
            return parseSignInResponse(response, null);
        }
    }

    private static String toJson(Object body) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize request body", e);
        }
    }

    public Response signIn(SignInRequest signInRequestDto) {
        return signIn(signInRequestDto, 1111L);
    }

    /**
     * signIn с указанием id ключа шифрования (должен совпадать с ключом, сохранённым через saveSymmetricKey).
     */
    public Response signIn(SignInRequest signInRequestDto, long encryptionKeyId) {
        return signIn(signInRequestDto, encryptionKeyId, null);
    }

    public Response signIn(SignInRequest signInRequestDto, SymmetricKeyMaterial cipherKey) {
        return signIn(signInRequestDto, cipherKey.getId(), cipherKey);
    }

    public Response signIn(SignInRequest signInRequestDto, long encryptionKeyId, SymmetricKeyMaterial cipherKey) {
        if (cipherKey == null) {
            return getRequestSpecWithOutAuth()
                    .header("encryption-key-id", encryptionKeyId)
                    .body(signInRequestDto)
                    .when()
                    .post(SIGN_IN)
                    .then()
                    .extract().response();
        }
        return postEncryptedAuth(SIGN_IN, signInRequestDto, cipherKey);
    }

    public String fetchNewTokenProcessId(String processId) {
        return fetchNewTokenProcessId(processId, "deviceInfo");
    }

    public String fetchNewTokenProcessId(String processId, String deviceHash) {
        synchronized (LOCK) {
            Response response = signIn(new SignInRequest(processId, buildDeviceInfo(deviceHash)));
            return response.jsonPath().getString("accessToken");
        }
    }

    public String fetchNewTokenMultiStep(String number) {
        return fetchNewTokenMultiStep(number, "hash", "deviceInfo");
    }

    public String fetchNewTokenMultiStep(String number, String deviceHash, String deviceInfo) {

        Response startResponse = startAuth(new StartRequest(new StartRequest.AuthWay("PHONE", number), deviceHash, "ru", deviceInfo));
        if (startResponse.statusCode() != SC_OK) {
            throw new RuntimeException("Failed at start auth step");
        }
        String processId = startResponse.jsonPath().getString("processId");

        verifyCode(new VerifyCodeRequest(processId, "111111"));
        verifyPassword(new VerifyPasswordRequest(processId, "111111"));

        Response response = signIn(new SignInRequest(processId, buildDeviceInfo(deviceHash)));
        return response.jsonPath().getString("accessToken");
    }

    /**
     * Определяет, нужна ли регистрация в мессенджере, по nextStep после verifyCode.
     * При UNVERIFIED (rate limit / неверный SMS) повторная регистрация не запускается.
     */
    private final EncryptionHelper encryptionHelper = new EncryptionHelper();

    public MessengerRegistrationStatus checkRegistrationStatus(String phone) {
        String normalizedPhone = normalizePhoneForAuth(phone);
        StartRequest startRequest = new StartRequest(
                new StartRequest.AuthWay("PHONE", normalizedPhone),
                "hash",
                "ru",
                buildDeviceInfo("hash")
        );

        Response startResponse = startAuth(startRequest);
        if (startResponse.statusCode() != SC_OK) {
            String body = startResponse.getBody().asString();
            if (body != null && body.contains("Cannot get key id from header")) {
                return checkRegistrationStatusWithEncryption(phone);
            }
            if (body != null && body.contains("ENCRYPTION_KEY")) {
                return MessengerRegistrationStatus.NOT_REGISTERED;
            }
            return MessengerRegistrationStatus.UNVERIFIED;
        }

        return resolveRegistrationStatus(startResponse, null, startRequest);
    }

    private MessengerRegistrationStatus resolveRegistrationStatus(Response startResponse,
                                                                  SymmetricKeyMaterial cipherKey,
                                                                  StartRequest startRequest) {
        StartAuthResponse startBody = cipherKey != null
                ? parseStartAuthResponse(startResponse, cipherKey)
                : startResponse.getBody().as(StartAuthResponse.class);
        String processId = startBody.getProcessId();
        if (processId == null || processId.isBlank()) {
            throw new RuntimeException("auth/start response missing processId");
        }

        Response verifyCodeResponse = verifyCode(new VerifyCodeRequest(processId, "111111"), cipherKey);
        if (verifyCodeResponse.statusCode() != SC_OK) {
            return MessengerRegistrationStatus.UNVERIFIED;
        }

        String nextStep = extractNextStep(verifyCodeResponse, cipherKey);
        if (nextStep == null || nextStep.isBlank()) {
            nextStep = startBody.getNextStep();
        }

        if (isRegistrationStep(nextStep)) {
            return MessengerRegistrationStatus.NOT_REGISTERED;
        }
        if (isLoginStep(nextStep)) {
            return MessengerRegistrationStatus.REGISTERED;
        }

        // Не режем сюда регистрацию снаружи: ensureUser больше не опирается на UNVERIFIED→register
        // после отдельного probe (это ломало process). Логируем nextStep для диагностики.
        org.slf4j.LoggerFactory.getLogger(AuthApiService.class)
                .warn("checkRegistrationStatus: неизвестный nextStep='{}' processId={}", nextStep, processId);
        return MessengerRegistrationStatus.UNVERIFIED;
    }

    public MessengerRegistrationStatus checkRegistrationStatusWithEncryption(String phone) {
        String normalizedPhone = normalizePhoneForAuth(phone);
        EncryptionHelper.EncryptKeyInfo keys = encryptionHelper.setupEncryptionKeys();
        SymmetricKeyMaterial cipherKey = keys.getMessageKey();
        StartRequest startRequest = new StartRequest(
                new StartRequest.AuthWay("PHONE", normalizedPhone),
                "hash",
                "ru",
                buildDeviceInfo("hash")
        );

        Response startResponse = startAuth(startRequest, cipherKey);
        if (startResponse.statusCode() != SC_OK) {
            return MessengerRegistrationStatus.UNVERIFIED;
        }
        return resolveRegistrationStatus(startResponse, cipherKey, startRequest);
    }

    private static String extractNextStep(Response response, SymmetricKeyMaterial cipherKey) {
        try {
            String body = response.getBody().asString();
            if (cipherKey != null) {
                body = SymmetricCipherHelper.decryptResponseBody(body, cipherKey);
            }
            var tree = new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);
            String nextStep = textOrNull(tree, "nextStep");
            if (nextStep == null || nextStep.isBlank()) {
                nextStep = textOrNull(tree, "step");
            }
            return nextStep;
        } catch (Exception e) {
            String nextStep = response.jsonPath().getString("nextStep");
            if (nextStep == null || nextStep.isBlank()) {
                nextStep = response.jsonPath().getString("step");
            }
            return nextStep;
        }
    }

    private static String textOrNull(com.fasterxml.jackson.databind.JsonNode tree, String field) {
        com.fasterxml.jackson.databind.JsonNode node = tree.path(field);
        if (node.isMissingNode() || node.isNull()) {
            return null;
        }
        String value = node.asText(null);
        return value != null && !value.isBlank() ? value : null;
    }

    /** Для RegistrationApiService / провижна. */
    public static String extractNextStepPublic(Response response, SymmetricKeyMaterial cipherKey) {
        return extractNextStep(response, cipherKey);
    }

    public static boolean isLoginStepPublic(String nextStep) {
        return isLoginStep(nextStep);
    }

    public static boolean isRegistrationStepPublic(String nextStep) {
        return isRegistrationStep(nextStep);
    }

    private static boolean isLoginStep(String nextStep) {
        if (nextStep == null) {
            return false;
        }
        String step = nextStep.toUpperCase();
        // PASSWORD — обычный логин; FA2/TFA/2FA — уже зарегистрированный аккаунт с 2FA
        return step.contains("PASSWORD")
                || step.contains("FA2")
                || step.contains("TFA")
                || step.contains("2FA");
    }

    private static boolean isRegistrationStep(String nextStep) {
        if (nextStep == null) {
            return false;
        }
        String step = nextStep.toUpperCase();
        return step.contains("SUBMIT") || step.contains("ACCOUNT") || step.contains("REGISTER");
    }

    /**
     * Проверяет, завершена ли регистрация в мессенджере (можно пройти verifyPassword).
     */
    public boolean canLoginWithPassword(String phone) {
        try {
            EncryptionHelper.EncryptKeyInfo keys = encryptionHelper.setupEncryptionKeys();
            SymmetricKeyMaterial cipherKey = keys.getMessageKey();
            String normalizedPhone = normalizePhoneForAuth(phone);

            Response startResponse = startAuth(new StartRequest(
                    new StartRequest.AuthWay("PHONE", normalizedPhone),
                    "hash",
                    "ru",
                    buildDeviceInfo("hash")
            ), cipherKey);
            if (startResponse.statusCode() != SC_OK) {
                return false;
            }
            String processId = parseStartAuthResponse(startResponse, cipherKey).getProcessId();
            if (processId == null || processId.isBlank()) {
                return false;
            }

            Response verifyCodeResponse = verifyCode(new VerifyCodeRequest(processId, "111111"), cipherKey);
            if (verifyCodeResponse.statusCode() != SC_OK) {
                return false;
            }

            String nextStep = extractNextStep(verifyCodeResponse, cipherKey);
            // Уже зарегистрирован: пароль или 2FA — регистрация не нужна
            if (isLoginStep(nextStep)) {
                return true;
            }

            Response verifyPasswordResponse = verifyPassword(new VerifyPasswordRequest(processId, "111111"), cipherKey);
            return verifyPasswordResponse.statusCode() == SC_OK;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Полная цепочка авторизации по номеру телефона (с дефолтным encryptionKeyId=1111).
     */
    public SignInResponse performFullAuthFlow(String phone) {
        throw new IllegalArgumentException("Use performFullAuthFlow(phone, cipherKey)");
    }

    /**
     * @deprecated используйте {@link #performFullAuthFlow(String, SymmetricKeyMaterial)}
     */
    @Deprecated
    public SignInResponse performFullAuthFlow(String phone, long encryptionKeyId) {
        throw new IllegalArgumentException("Use performFullAuthFlow(phone, cipherKey)");
    }

    public SignInResponse performFullAuthFlow(String phone, SymmetricKeyMaterial cipherKey) {
        return performFullAuthFlow(phone, cipherKey, "hash");
    }

    public SignInResponse performFullAuthFlow(String phone, SymmetricKeyMaterial cipherKey, String deviceHash) {
        String normalizedPhone = normalizePhoneForAuth(phone);

        Response startResponse = startAuth(new StartRequest(
                new StartRequest.AuthWay("PHONE", normalizedPhone),
                deviceHash,
                "ru",
                buildDeviceInfo(deviceHash)
        ), cipherKey);
        if (startResponse.statusCode() != SC_OK) {
            String body = startResponse.getBody().asString();
            throw new RuntimeException(
                    "Failed at start auth step: status=" + startResponse.statusCode() + ", body=" + (body != null ? body : "empty")
            );
        }
        StartAuthResponse startBody = parseStartAuthResponse(startResponse, cipherKey);
        String processId = startBody.getProcessId();
        if (processId == null || processId.isEmpty()) {
            throw new RuntimeException("Start auth response missing processId");
        }

        Response verifyCodeResponse = verifyCode(new VerifyCodeRequest(processId, "111111"), cipherKey);
        if (verifyCodeResponse.statusCode() != SC_OK) {
            throw new RuntimeException("Failed at verifyCode: status=" + verifyCodeResponse.statusCode() + ", body=" + verifyCodeResponse.getBody().asString());
        }

        Response verifyPasswordResponse = verifyPassword(new VerifyPasswordRequest(processId, "111111"), cipherKey);
        if (verifyPasswordResponse.statusCode() != SC_OK) {
            throw new RuntimeException("Failed at verifyPassword: status=" + verifyPasswordResponse.statusCode() + ", body=" + verifyPasswordResponse.getBody().asString());
        }

        Response signInResponse = signIn(new SignInRequest(processId, buildDeviceInfo(deviceHash)), cipherKey);
        if (signInResponse.statusCode() != SC_OK) {
            throw new RuntimeException("Failed at signIn: status=" + signInResponse.statusCode() + ", body=" + signInResponse.getBody().asString());
        }
        return parseSignInResponse(signInResponse, cipherKey);
    }

    /**
     * То же, что performFullAuthFlow, но возвращает сырой Response последнего signIn (для извлечения cookies).
     */
    public Response performFullAuthFlowResponse(String phone, SymmetricKeyMaterial cipherKey) {
        return performFullAuthFlowResponse(phone, cipherKey, "hash");
    }

    public Response performFullAuthFlowResponse(String phone, SymmetricKeyMaterial cipherKey, String deviceHash) {
        String normalizedPhone = normalizePhoneForAuth(phone);

        Response startResponse = startAuth(new StartRequest(
                new StartRequest.AuthWay("PHONE", normalizedPhone),
                deviceHash,
                "ru",
                buildDeviceInfo(deviceHash)
        ), cipherKey);
        if (startResponse.statusCode() != SC_OK) {
            throw new RuntimeException("Failed at start auth step: status=" + startResponse.statusCode() + ", body=" + startResponse.getBody().asString());
        }
        String processId = parseStartAuthResponse(startResponse, cipherKey).getProcessId();
        if (processId == null || processId.isEmpty()) {
            throw new RuntimeException("Start auth response missing processId");
        }

        Response verifyCodeResponse = verifyCode(new VerifyCodeRequest(processId, "111111"), cipherKey);
        if (verifyCodeResponse.statusCode() != SC_OK) {
            throw new RuntimeException("Failed at verifyCode: status=" + verifyCodeResponse.statusCode());
        }

        Response verifyPasswordResponse = verifyPassword(new VerifyPasswordRequest(processId, "111111"), cipherKey);
        if (verifyPasswordResponse.statusCode() != SC_OK) {
            throw new RuntimeException("Failed at verifyPassword: status=" + verifyPasswordResponse.statusCode());
        }

        Response signInResponse = signIn(new SignInRequest(processId, buildDeviceInfo(deviceHash)), cipherKey);
        if (signInResponse.statusCode() != SC_OK) {
            throw new RuntimeException("Failed at signIn: status=" + signInResponse.statusCode() + ", body=" + signInResponse.getBody().asString());
        }
        return signInResponse;
    }

    private static String buildDeviceInfo(String deviceHash) {
        if (deviceHash == null || deviceHash.isBlank()) {
            return "deviceInfo";
        }
        return DEVICE_INFO_PREFIX + deviceHash.trim();
    }

    /** Номер для API: 7XXXXXXXXXX (11 цифр). */
    private static String normalizePhoneForAuth(String phone) {
        if (phone == null || phone.isEmpty()) {
            throw new IllegalArgumentException("phone is empty");
        }
        String digits = phone.replaceAll("\\D", "");
        if (digits.length() == 10 && digits.startsWith("9")) {
            return "7" + digits;
        }
        if (digits.length() == 11 && digits.startsWith("7")) {
            return digits;
        }
        return phone;
    }

    private static Response postEncryptedAuth(String endpoint, Object body, SymmetricKeyMaterial cipherKey) {
        return EncryptionApiService.postEncryptedAuth(endpoint, body, cipherKey);
    }
}

