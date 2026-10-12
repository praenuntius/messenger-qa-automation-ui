package org.foxtrot.api.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroupUnreadRequest {

    private long groupId;
    private Boolean unread;
}