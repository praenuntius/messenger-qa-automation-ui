package org.foxtrot.api.service;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.api.BaseApiService;
import org.foxtrot.api.SmartLoggingFilter;
import org.foxtrot.api.dto.encryption.EncryptedContentDto;
import org.foxtrot.api.dto.encryption.GetPublicKeyResponse;
import org.foxtrot.api.dto.encryption.SaveSymmetricKeyRequest;
import org.foxtrot.api.dto.encryption.SaveSymmetricKeyResponse;
import org.foxtrot.api.dto.encryption.SymmetricKeyUpdateRequest;
import org.foxtrot.config.CoreConfig;
import org.foxtrot.utils.SymmetricCipherHelper;
import org.foxtrot.utils.SymmetricKeyMaterial;
import org.foxtrot.utils.Waits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static org.foxtrot.api.constants.ApiEndpoints.CONFIRM_SYMMETRIC_KEY_UPDATE;
import static org.foxtrot.api.constants.ApiEndpoints.ENCRYPTION_GET_PUBLIC_KEY;
import static org.foxtrot.api.constants.ApiEndpoints.ENCRYPTION_SAVE_SYMMETRIC_KEY;
import static org.foxtrot.api.constants.ApiEndpoints.UPDATE_SYMMETRIC_KEY;
import static org.foxtrot.api.constants.ApiEndpoints.UPDATE_SYMMETRIC_KEY_V2;

public class EncryptionApiService extends BaseApiService {

    private static final Logger log = LoggerFactory.getLogger(EncryptionApiService.class);
    private static final CoreConfig config = ConfigFactory.create(CoreConfig.class);
    private static final int PUBLIC_KEY_RETRY_ATTEMPTS = 4;

    public GetPublicKeyResponse getPublicKey() {
        Response response = null;
        for (int attempt = 1; attempt <= PUBLIC_KEY_RETRY_ATTEMPTS; attempt++) {
            response = getRequestSpecWithOutAuth()
                    .when()
                    .post(ENCRYPTION_GET_PUBLIC_KEY)
                    .then()
                    .extract().response();
            int status = response.statusCode();
            String contentType = response.getContentType();
            boolean jsonOk = status == 200 && contentType != null && contentType.contains("json");
            if (jsonOk) {
                return response.then().extract().as(GetPublicKeyResponse.class);
            }
            boolean retryable = status == 502 || status == 503 || status == 504
                    || (contentType != null && contentType.contains("text/html"));
            if (!retryable || attempt == PUBLIC_KEY_RETRY_ATTEMPTS) {
                break;
            }
            log.warn("getPublicKey attempt {}/{} status={} contentType={} — retry",
                    attempt, PUBLIC_KEY_RETRY_ATTEMPTS, status, contentType);
            Waits.sleepMillis(2_000 * attempt);
        }
        return response.then().extract().as(GetPublicKeyResponse.class);
    }

    public SaveSymmetricKeyResponse saveSymmetricKey(SaveSymmetricKeyRequest request) {
        Response response = getRequestSpecWithOutAuth()
                .body(request)
                .when()
                .post(ENCRYPTION_SAVE_SYMMETRIC_KEY)
                .then()
                .extract().response();
        return response.then().extract().as(SaveSymmetricKeyResponse.class);
    }

    /**
     * Сохранение симметричного ключа в контексте авторизованной сессии (для ротации).
     */
    public SaveSymmetricKeyResponse saveSymmetricKey(SaveSymmetricKeyRequest request,
                                                     String accessToken,
                                                     long encryptionKeyId) {
        Response response = encryptedRequestSpec(accessToken, encryptionKeyId, true)
                .header("X-Disable-Encryption", true)
                .body(request)
                .when()
                .post(ENCRYPTION_SAVE_SYMMETRIC_KEY)
                .then()
                .extract()
                .response();
        return response.then().extract().as(SaveSymmetricKeyResponse.class);
    }

    /**
     * Legacy one-step update. Prefer {@link #updateSymmetricKeyV2} + {@link #confirmSymmetricKeyUpdate}
     * for runtime rotation (DF-554).
     */
    public Response updateSymmetricKey(SymmetricKeyUpdateRequest request,
                                       String accessToken,
                                       SymmetricKeyMaterial currentKey) {
        String encryptedContent = SymmetricCipherHelper.encryptJson(
                toJson(request),
                currentKey
        );

        return encryptedPost(UPDATE_SYMMETRIC_KEY, accessToken, currentKey.getId(), encryptedContent);
    }

    /**
     * Runtime rotation step 1: body {@code { encryptionKeyId }} encrypted with current key;
     * header Encryption-Key-Id = current.
     */
    public Response updateSymmetricKeyV2(SymmetricKeyUpdateRequest request,
                                         String accessToken,
                                         SymmetricKeyMaterial currentKey) {
        String encryptedContent = SymmetricCipherHelper.encryptJson(
                toJson(request),
                currentKey
        );
        return encryptedPost(UPDATE_SYMMETRIC_KEY_V2, accessToken, currentKey.getId(), encryptedContent);
    }

    /**
     * Runtime rotation step 2: no body (skipEncrypt); header Encryption-Key-Id = new key id.
     */
    public Response confirmSymmetricKeyUpdate(String accessToken, long newEncryptionKeyId) {
        return encryptedRequestSpec(accessToken, newEncryptionKeyId)
                .post(CONFIRM_SYMMETRIC_KEY_UPDATE)
                .then()
                .extract()
                .response();
    }

    private Response encryptedPost(String endpoint,
                                 String accessToken,
                                 long encryptionKeyId,
                                 String encryptedContent) {
        return encryptedRequestSpec(accessToken, encryptionKeyId)
                .body(new EncryptedContentDto(encryptedContent))
                .post(endpoint)
                .then()
                .extract()
                .response();
    }

    static RequestSpecification encryptedRequestSpec(String accessToken, Long encryptionKeyId) {
        return encryptedRequestSpec(accessToken, encryptionKeyId, true);
    }

    static RequestSpecification encryptedAuthRequestSpec(long encryptionKeyId) {
        return encryptedRequestSpec(null, encryptionKeyId, false);
    }

    public static Response postEncryptedAuth(String endpoint, Object body, SymmetricKeyMaterial cipherKey) {
        String encryptedContent = SymmetricCipherHelper.encryptJson(toJson(body), cipherKey);
        return encryptedAuthRequestSpec(cipherKey.getId())
                .body(new EncryptedContentDto(encryptedContent))
                .post(endpoint)
                .then()
                .extract()
                .response();
    }

    static RequestSpecification encryptedRequestSpec(String accessToken, Long encryptionKeyId, boolean includeWorkspaceId) {
        RequestSpecification spec = given()
                .filter(new SmartLoggingFilter())
                .baseUri(config.baseApiUrl())
                .contentType("application/json");

        if (accessToken != null) {
            spec.header("Authorization", accessToken);
        }
        if (encryptionKeyId != null) {
            spec.header("encryption-key-id", encryptionKeyId);
        }
        if (includeWorkspaceId) {
            spec.header("workspaceId", -1);
        }
        return spec;
    }

    private static String toJson(Object body) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize request body", e);
        }
    }
}
