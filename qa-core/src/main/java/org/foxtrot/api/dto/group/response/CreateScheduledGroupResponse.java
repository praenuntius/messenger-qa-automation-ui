package org.foxtrot.api.dto.group.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateScheduledGroupResponse {
    private long parentGroupId;
    private long groupId;
}
