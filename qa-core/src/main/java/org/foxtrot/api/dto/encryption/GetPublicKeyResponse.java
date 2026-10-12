package org.foxtrot.api.dto.encryption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPublicKeyResponse {

    private long id;
    private String key;
    private int keySize;
    private String transformation;
}

