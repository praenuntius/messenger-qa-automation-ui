package org.foxtrot.api.dto.encryption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveSymmetricKeyResponse {

    private long id;
    private String transformation;
}

