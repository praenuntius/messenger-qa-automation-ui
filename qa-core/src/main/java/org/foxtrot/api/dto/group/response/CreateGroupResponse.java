package org.foxtrot.api.dto.group.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateGroupResponse {

    @JsonAlias({"id", "group_id", "groupID"})
    private Long groupId;
    private Long ownerId;
    private NestedGroup group;

    public Long getGroupId() {
        if (groupId != null) {
            return groupId;
        }
        if (group == null) {
            return null;
        }
        if (group.getGroupId() != null) {
            return group.getGroupId();
        }
        return group.getId();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NestedGroup {
        @JsonAlias({"groupId", "group_id"})
        private Long id;
        private Long groupId;
    }
}
