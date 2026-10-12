package org.foxtrot.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    private String processId;
    private String deviceInfo;
    private String recoveryEmail;
    private String verificationCode;
    private String tfaCode;
}
