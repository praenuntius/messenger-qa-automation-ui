package org.foxtrot.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrganizationRequest {

    private String name;
    private String type;
    private List<String> representatives;
    private String inn;
    private String externalId;
}
