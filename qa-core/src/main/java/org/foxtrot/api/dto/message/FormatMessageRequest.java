package org.foxtrot.api.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormatMessageRequest {

    private long messageId;
    private long groupId;
    private String message;
    private List<Entity> entities;

    public static FormatMessageRequest allTextStyles(long groupId, long messageId, String message) {
        int length = message.length();
        return new FormatMessageRequest(messageId, groupId, message, List.of(
                Entity.bold(length),
                Entity.italic(length),
                Entity.underline(length),
                Entity.strike(length),
                Entity.monospace(length)
        ));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Entity {
        private TextStyle bold;
        private TextStyle italic;
        private TextStyle underline;
        private TextStyle strike;
        private TextStyle monospace;

        public static Entity bold(int length) {
            return new Entity(style(length), null, null, null, null);
        }

        public static Entity italic(int length) {
            return new Entity(null, style(length), null, null, null);
        }

        public static Entity underline(int length) {
            return new Entity(null, null, style(length), null, null);
        }

        public static Entity strike(int length) {
            return new Entity(null, null, null, style(length), null);
        }

        public static Entity monospace(int length) {
            return new Entity(null, null, null, null, style(length));
        }

        private static TextStyle style(int length) {
            return new TextStyle(0, length);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextStyle {
        private int offset;
        private int length;
    }
}
