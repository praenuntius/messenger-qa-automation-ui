package org.foxtrot.utils;

import org.foxtrot.api.BaseApiService;
import org.foxtrot.api.auth.DeviceHashProvider;
import org.foxtrot.api.dto.auth.response.SignInResponse;
import org.foxtrot.api.dto.encryption.SymmetricKeyUpdateRequest;
import org.foxtrot.api.service.AuthApiService;
import org.foxtrot.api.service.EncryptionApiService;
import org.foxtrot.enums.UsersEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Создание авторизованной сессии с реальными симметричными ключами и ротация ключа.
 */
public class SymmetricKeySessionFactory {

    private static final Logger log = LoggerFactory.getLogger(SymmetricKeySessionFactory.class);

    private final EncryptionHelper encryptionHelper = new EncryptionHelper();
    private final AuthApiService authApiService = new AuthApiService();
    private final EncryptionApiService encryptionApiService = new EncryptionApiService();

    public SymmetricKeySession login(UsersEnum user) {
        BaseApiService.clearTokenCache();
        String deviceHash = DeviceHashProvider.forUserKey(user.getProcessId());

        EncryptionHelper.EncryptKeyInfo keys = encryptionHelper.setupEncryptionKeys();
        SignInResponse signIn = authApiService.performFullAuthFlow(
                user.getPhoneNumber(),
                keys.getMessageKey(),
                deviceHash
        );

        Long accountId = signIn.getAccount() != null ? signIn.getAccount().getId() : null;
        log.info("Сессия с шифрованием создана для {}, accountId={}, encryptionKeyId={}",
                user.name(), accountId, keys.getMessageKey().getId());

        return SymmetricKeySession.builder()
                .user(user)
                .accountId(accountId)
                .accessToken(signIn.getAccessToken())
                .refreshToken(signIn.getRefreshToken())
                .deviceHash(deviceHash)
                .encryptKeyJson(keys.getEncryptKeyJson())
                .messageKey(keys.getMessageKey())
                .fileKey(keys.getFileKey())
                .build();
    }

    /**
     * Обмен refresh JWT на новый access token (контракт SSE/REST 401 → refresh).
     *
     * @return обновлённая сессия (тот же объект)
     */
    public SymmetricKeySession refreshAccessToken(SymmetricKeySession session) {
        if (session.getAccessToken() == null || session.getAccessToken().isBlank()) {
            throw new IllegalStateException("Session has no accessToken — cannot refresh");
        }
        String refreshJwt = session.getRefreshToken();
        if (refreshJwt == null || refreshJwt.isBlank()) {
            // некоторые сборки кладут refresh только в cookie; jwtToken тогда = access JWT
            refreshJwt = session.getAccessToken();
        }
        SignInResponse refreshed = authApiService.refreshAccessToken(
                refreshJwt,
                session.getAccessToken(),
                session.getMessageKey()
        );
        if (refreshed.getAccessToken() == null || refreshed.getAccessToken().isBlank()) {
            throw new IllegalStateException("refreshToken response has empty accessToken");
        }
        session.setAccessToken(refreshed.getAccessToken());
        if (refreshed.getRefreshToken() != null && !refreshed.getRefreshToken().isBlank()) {
            session.setRefreshToken(refreshed.getRefreshToken());
        }
        log.info("Access token refreshed for {}", session.getUser() != null ? session.getUser().name() : "?");
        return session;
    }

    /**
     * Ротация симметричного ключа (DF-554): saveSymmetricKey → updateSymmetricKeyV2 → confirm.
     * Current на сессии меняется только после confirm 200/204.
     *
     * @return новый ключ, привязанный к сессии
     */
    public SymmetricKeyMaterial rotate(SymmetricKeySession session) {
        SymmetricKeyMaterial oldKey = session.getMessageKey();
        SymmetricKeyMaterial newKey = encryptionHelper.generateAndSaveSymmetricKey(
                session.getAccessToken(),
                oldKey.getId()
        );

        var updateResponse = encryptionApiService.updateSymmetricKeyV2(
                new SymmetricKeyUpdateRequest(newKey.getId()),
                session.getAccessToken(),
                oldKey
        );
        if (updateResponse.statusCode() != 200 && updateResponse.statusCode() != 204) {
            throw new RuntimeException("updateSymmetricKeyV2 failed: status=" + updateResponse.statusCode()
                    + ", body=" + updateResponse.getBody().asString());
        }

        var confirmResponse = encryptionApiService.confirmSymmetricKeyUpdate(
                session.getAccessToken(),
                newKey.getId()
        );
        if (confirmResponse.statusCode() != 200 && confirmResponse.statusCode() != 204) {
            throw new RuntimeException("confirmSymmetricKeyUpdate failed: status="
                    + confirmResponse.statusCode()
                    + ", body=" + confirmResponse.getBody().asString());
        }

        session.setMessageKey(newKey);
        log.info("Ключ ротирован (V2+confirm): {} → {}", oldKey.getId(), newKey.getId());
        return newKey;
    }

    /**
     * Только updateSymmetricKeyV2 (без confirm) — для негативных/pending сценариев.
     * Сессию не обновляет.
     */
    public SymmetricKeyMaterial rotateUpdateV2Only(SymmetricKeySession session) {
        SymmetricKeyMaterial oldKey = session.getMessageKey();
        SymmetricKeyMaterial newKey = encryptionHelper.generateAndSaveSymmetricKey(
                session.getAccessToken(),
                oldKey.getId()
        );
        var updateResponse = encryptionApiService.updateSymmetricKeyV2(
                new SymmetricKeyUpdateRequest(newKey.getId()),
                session.getAccessToken(),
                oldKey
        );
        if (updateResponse.statusCode() != 200 && updateResponse.statusCode() != 204) {
            throw new RuntimeException("updateSymmetricKeyV2 failed: status=" + updateResponse.statusCode()
                    + ", body=" + updateResponse.getBody().asString());
        }
        log.info("updateSymmetricKeyV2 only (no confirm): {} → pending {}", oldKey.getId(), newKey.getId());
        return newKey;
    }
}
