package org.foxtrot.utils;

import lombok.Builder;
import lombok.Data;
import org.foxtrot.enums.UsersEnum;

@Data
@Builder
public class SymmetricKeySession {

    private final UsersEnum user;
    /** Реальный accountId с текущего стенда (из signIn), не из UsersEnum. */
    private final Long accountId;
    /** Mutable: обновляется после {@code /auth/refreshToken}. */
    private String accessToken;
    /** Refresh JWT из signIn; нужен для SSE 401 → refresh → reconnect. */
    private String refreshToken;
    private final String deviceHash;
    private final String encryptKeyJson;

    private SymmetricKeyMaterial messageKey;
    private SymmetricKeyMaterial fileKey;
}
