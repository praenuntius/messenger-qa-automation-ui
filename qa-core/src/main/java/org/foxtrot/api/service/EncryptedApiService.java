package org.foxtrot.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.api.SmartLoggingFilter;
import org.foxtrot.api.dto.encryption.EncryptedContentDto;
import org.foxtrot.api.dto.group.CreateGroupRequest;
import org.foxtrot.api.dto.group.response.CreateGroupResponse;
import org.foxtrot.api.dto.workSpace.CreateWorkSpaceRequest;
import org.foxtrot.api.dto.workSpace.response.CreateWorkSpaceResponse;
import org.foxtrot.config.CoreConfig;
import org.foxtrot.utils.SymmetricCipherHelper;
import org.foxtrot.utils.SymmetricKeyMaterial;
import org.foxtrot.utils.SymmetricKeySession;

import static io.restassured.RestAssured.given;
import static org.foxtrot.api.constants.ApiEndpoints.CREATE_GROUP;
import static org.foxtrot.api.constants.ApiEndpoints.CREATE_WORKSPACE;
import static org.foxtrot.api.constants.ApiEndpoints.SEND_TEXT_MESSAGE;

/**
 * REST-запросы с шифрованием body (без X-Disable-Encryption).
 */
public class EncryptedApiService {

    private static final CoreConfig config = ConfigFactory.create(CoreConfig.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Response post(String endpoint, Object plainBody, SymmetricKeySession session) {
        return post(endpoint, plainBody, session.getAccessToken(), session.getMessageKey(), session.getMessageKey().getId());
    }

    public Response post(String endpoint, Object plainBody, SymmetricKeySession session, Object workspaceId) {
        return post(
                endpoint,
                plainBody,
                session.getAccessToken(),
                session.getMessageKey(),
                session.getMessageKey().getId(),
                workspaceId
        );
    }

    /**
     * Создание homespace-группы в рамках уже залогиненной encrypted-сессии
     * (без повторного plain auth через GroupApiSteps).
     */
    public CreateGroupResponse createHomespaceGroup(SymmetricKeySession session, String name) {
        Response response = post(
                CREATE_GROUP,
                new CreateGroupRequest(name, "GROUP", false),
                session
        );
        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new IllegalStateException(
                    "createGroup failed: status=" + response.statusCode()
                            + ", body=" + response.getBody().asString()
            );
        }
        CreateGroupResponse created = parse(response, session.getMessageKey(), CreateGroupResponse.class);
        if (created.getGroupId() == null) {
            throw new IllegalStateException("createGroup returned null groupId, body=" + response.getBody().asString());
        }
        return created;
    }

    public CreateWorkSpaceResponse createWorkspace(SymmetricKeySession session, String name) {
        Response response = post(
                CREATE_WORKSPACE,
                new CreateWorkSpaceRequest(new CreateWorkSpaceRequest.Workspace(name, "skr-soak")),
                session
        );
        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new IllegalStateException(
                    "createWorkspace failed: status=" + response.statusCode()
                            + ", body=" + response.getBody().asString()
            );
        }
        try {
            return parse(response, session.getMessageKey(), CreateWorkSpaceResponse.class);
        } catch (RuntimeException encryptedFailed) {
            try {
                return MAPPER.readValue(response.getBody().asString(), CreateWorkSpaceResponse.class);
            } catch (Exception plainFailed) {
                throw new RuntimeException(
                        "createWorkspace parse failed encrypted=" + encryptedFailed.getMessage()
                                + " plain=" + plainFailed.getMessage()
                                + " body=" + response.getBody().asString(),
                        plainFailed
                );
            }
        }
    }

    public <T> T parse(Response response, SymmetricKeyMaterial cipherKey, Class<T> type) {
        String body = SymmetricCipherHelper.decryptResponseBody(response.getBody().asString(), cipherKey);
        try {
            return MAPPER.readValue(body, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse encrypted response as " + type.getSimpleName() + ": " + body, e);
        }
    }

    public <T> java.util.List<T> parseList(Response response, SymmetricKeyMaterial cipherKey, Class<T> elementType) {
        String body = SymmetricCipherHelper.decryptResponseBody(response.getBody().asString(), cipherKey);
        try {
            return MAPPER.readValue(
                    body,
                    MAPPER.getTypeFactory().constructCollectionType(java.util.List.class, elementType)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse encrypted response as List<" + elementType.getSimpleName() + ">: " + body, e);
        }
    }

    public Response post(String endpoint,
                         Object plainBody,
                         String accessToken,
                         SymmetricKeyMaterial cipherKey,
                         Long encryptionKeyId) {
        return post(endpoint, plainBody, accessToken, cipherKey, encryptionKeyId, null);
    }

    public Response post(String endpoint,
                         Object plainBody,
                         String accessToken,
                         SymmetricKeyMaterial cipherKey,
                         Long encryptionKeyId,
                         Object workspaceId) {
        String encryptedContent = SymmetricCipherHelper.encryptJson(toJson(plainBody), cipherKey);

        RequestSpecification spec = EncryptionApiService.encryptedRequestSpec(accessToken, encryptionKeyId, false);
        spec.header("workspaceId", workspaceId != null ? workspaceId : -1);

        return spec.body(new EncryptedContentDto(encryptedContent))
                .post(endpoint)
                .then()
                .extract()
                .response();
    }

    public Response sendTextMessage(Object plainBody, SymmetricKeySession session) {
        return post(SEND_TEXT_MESSAGE, plainBody, session);
    }

    public Response sendTextMessage(Object plainBody, SymmetricKeySession session, Object workspaceId) {
        return post(SEND_TEXT_MESSAGE, plainBody, session, workspaceId);
    }

    public Response sendTextMessage(Object plainBody,
                                    String accessToken,
                                    SymmetricKeyMaterial cipherKey,
                                    long encryptionKeyId) {
        return post(SEND_TEXT_MESSAGE, plainBody, accessToken, cipherKey, encryptionKeyId);
    }

    public Response sendTextMessageWithoutEncryptionKeyId(Object plainBody, SymmetricKeySession session) {
        String encryptedContent = SymmetricCipherHelper.encryptJson(toJson(plainBody), session.getMessageKey());

        return EncryptionApiService.encryptedRequestSpec(session.getAccessToken(), null)
                .header("workspaceId", -1)
                .body(new EncryptedContentDto(encryptedContent))
                .post(SEND_TEXT_MESSAGE)
                .then()
                .extract()
                .response();
    }

    public Response sendTextMessageWithoutAuthorization(Object plainBody, SymmetricKeySession session) {
        String encryptedContent = SymmetricCipherHelper.encryptJson(toJson(plainBody), session.getMessageKey());

        return given()
                .filter(new SmartLoggingFilter())
                .baseUri(config.baseApiUrl())
                .contentType("application/json")
                .header("encryption-key-id", session.getMessageKey().getId())
                .header("workspaceId", -1)
                .body(new EncryptedContentDto(encryptedContent))
                .post(SEND_TEXT_MESSAGE)
                .then()
                .extract()
                .response();
    }

    private static String toJson(Object body) {
        try {
            return MAPPER.writeValueAsString(body);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize request body", e);
        }
    }
}
