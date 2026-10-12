package org.foxtrot.api.dto.workSpace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateWorkSpaceRequest {

    private Workspace workspace;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Workspace {
        private String name;
        private String description = "Description";
    }
}

