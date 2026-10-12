package org.foxtrot.api.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroupRequest {

    private long groupId;
    private Boolean visible;
    private String inviteLink;
    private String name;
    private String description;
    private ChannelOptions channelOptions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChannelOptions {
        private Boolean commentsEnabled;
    }
}
