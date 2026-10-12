package org.foxtrot.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendRecoveryEmailRequest {

    private String processId;
    private String email;
}
