package org.foxtrot.api.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignInResponse {

    @JsonAlias({"access_token"})
    private String accessToken;
    @JsonAlias({"refresh_token"})
    private String refreshToken;
    private Account account;
    private List<Workspace> workspaces;
    private long encryptionKeyId;
    @JsonAlias({"access_token_valid_till"})
    private long accessTokenValidTill;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Workspace {
        private long id;
        private String name;
        private String description;
        private boolean active;
        private String userRole;
        private String notificationType;
    }
}