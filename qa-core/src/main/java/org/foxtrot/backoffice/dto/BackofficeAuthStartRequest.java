package org.foxtrot.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BackofficeAuthStartRequest {

    private AuthWay authWay;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthWay {
        private String type;
        private String value;
    }
}
