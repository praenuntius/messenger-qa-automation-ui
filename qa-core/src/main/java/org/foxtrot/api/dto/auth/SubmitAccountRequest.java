package org.foxtrot.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitAccountRequest {

    private String processId;
    private String deviceInfo;
    private RegistrationUser user;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegistrationUser {
        private String firstName;
        private String lastName;
        private String middleName;
        private String password;
    }
}
