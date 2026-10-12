package org.foxtrot.api.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiAuthContext {
    private final String userKey; //processId
    private final String deviceHash;
    private String token; //accessToken (апдейтим его же лечилкой из BaseApiService при получении 401)

    public ApiAuthContext(String userKey, String deviceHash, String token) {
        this.userKey = userKey;
        this.deviceHash = deviceHash;
        this.token = token;
    }
}

