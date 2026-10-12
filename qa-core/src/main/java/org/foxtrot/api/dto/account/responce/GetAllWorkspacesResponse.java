package org.foxtrot.api.dto.account.responce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetAllWorkspacesResponse {

    private List<Workspace> availableWorkspaces;

    public List<Workspace> getAvailableWorkspaces() {
        return availableWorkspaces != null ? availableWorkspaces : Collections.emptyList();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Workspace {
        private long id;
        private String name;
        private String description;
        private boolean active;
        private String userRole;
        private String notificationType;
    }
}
