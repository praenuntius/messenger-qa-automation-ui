package org.foxtrot.api.dto.workSpace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveWorkspaceRequest {

    private Long workspaceId;
}