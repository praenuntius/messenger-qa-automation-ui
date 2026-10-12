package org.foxtrot.api.dto.workSpace.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateWorkSpaceResponse {

    public long id;
    public String name;
    public String description;
    public String inviteLink;
    public boolean active;
    public String userRole;
    public String notificationType;
    public String inviteCode;
}
