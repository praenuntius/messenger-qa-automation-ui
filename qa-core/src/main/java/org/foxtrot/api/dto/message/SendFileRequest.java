package org.foxtrot.api.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendFileRequest {
    private String message = "";
    private List<Object> entities = Collections.emptyList();
    private long groupId;
    private long clientRandomId = System.currentTimeMillis();
    private boolean hidden = false;
    private File file;
    private Scheduler scheduler;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class File {
        private String fileName;
        private long length;
        private String mimeType;
        private ResourceRef resourceRef;
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
        private long scheduledAt;
    }
}
