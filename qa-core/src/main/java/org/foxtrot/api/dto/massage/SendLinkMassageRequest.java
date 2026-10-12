package org.foxtrot.api.dto.massage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendLinkMassageRequest {
    private String message;
    private long clientRandomId = 0;
    private long groupId;
    private List<Entity> entities;

    public SendLinkMassageRequest(String url, long groupId) {
        this.message = url;
        this.clientRandomId = 0;
        this.groupId = groupId;
        this.entities = List.of(
                new Entity(
                        new Entity.TextFormattedUrl(0, url.length(), url)
                )
        );
    }

    public SendLinkMassageRequest(String url, long clientRandomId, long groupId) {
        this.message = url;
        this.clientRandomId = clientRandomId;
        this.groupId = groupId;
        this.entities = List.of(
                new Entity(
                        new Entity.TextFormattedUrl(0, url.length(), url)
                )
        );
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Entity {
        private TextFormattedUrl textFormattedUrl;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class TextFormattedUrl {
            private int offset;
            private int length;
            private String url;
        }
    }
}
