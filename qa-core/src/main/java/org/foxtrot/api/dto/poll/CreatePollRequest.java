package org.foxtrot.api.dto.poll;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreatePollRequest {
    private long clientRandomId;
    private long groupId;
    private String message;
    private long peerId;
    private Poll poll;

    @Data
    @NoArgsConstructor
    public static class Poll {
        private String title;
        private long countDownMillis;
        private List<Option> options;
        private Settings settings;
    }

    @Data
    @NoArgsConstructor
    public static class Option {
        private String title;
    }

    @Data
    @NoArgsConstructor
    public static class Settings {
        private boolean anonymous;
        private boolean multiChoice;
    }
}
