package org.foxtrot.api.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StartRequest {

    private AuthWay authWay;
    private String hash;
    private String locale;
    private String deviceInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthWay {


        private String type;
        private String value;
    }
}
