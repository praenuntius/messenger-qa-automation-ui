package org.foxtrot.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BackofficeVerifyOtpRequest {

    private String code;
    private String processId;
}
