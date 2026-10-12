package org.foxtrot.api.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveUserRequest {

    private long groupId;
    private long userId;
}