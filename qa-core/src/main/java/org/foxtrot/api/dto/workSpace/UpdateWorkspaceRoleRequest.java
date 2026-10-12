package org.foxtrot.api.dto.workSpace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWorkspaceRoleRequest {

    private long userId;
    private String workspaceRole;
}
