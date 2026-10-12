package org.foxtrot.api.dto.group.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetUserGroupStatesResponse {
    private long groupId;
    private long lastMessageId; // Добавляем сюда
    private GroupShort group;
    private String role;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GroupShort {
        private Long workspaceId;
        private long id;
        private String type;
        private String name;
    }
}

