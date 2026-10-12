package org.foxtrot.utils.user;

import lombok.Builder;
import lombok.Value;
import org.foxtrot.api.dto.auth.response.SignInResponse;

@Value
@Builder
public class CreatedUser {

    String phone;
    String firstName;
    String lastName;
    String recoveryEmail;
    String password;
    Long accountId;
    String accessToken;
    SignInResponse signInResponse;
}
