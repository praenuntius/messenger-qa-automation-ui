package org.foxtrot.api.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendImageRequest {

    private String message = "";
    private long groupId;
    private Image image;
    private Scheduler scheduler;

    public SendImageRequest(long groupId, Image image) {
        this.groupId = groupId;
        this.image = image;
        this.scheduler = null;
    }

    public SendImageRequest(long groupId, Image image, Scheduler scheduler) {
        this.groupId = groupId;
        this.image = image;
        this.scheduler = scheduler;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Image {
        private String fileName;
        private long length;
        private String mimeType;
        private ResourceRef resourceRef;
        private int width;
        private int height;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResourceRef {
        private String id;
        private String url;
        private String key;
        private String transformation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Scheduler {
        private Long scheduledAt;
    }
}