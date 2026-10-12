package org.foxtrot.api.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StartAuthResponse {

    private String processId;
    private String nextStep;
    private CodeInfo code;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CodeInfo {
        private int attempts;
        private int nextAttemptIn;
        private boolean isSent;
    }
}
