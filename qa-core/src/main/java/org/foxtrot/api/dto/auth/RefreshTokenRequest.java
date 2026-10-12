package org.foxtrot.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Body for {@code POST /api/v2/auth/refreshToken}.
 * Field name on the wire is {@code jwtToken} (value = refresh token from signIn).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {

    private String jwtToken;
}
