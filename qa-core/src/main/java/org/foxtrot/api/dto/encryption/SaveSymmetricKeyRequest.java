package org.foxtrot.api.dto.encryption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveSymmetricKeyRequest {

    private String encryptedKey;
    private String transformation;
    private long asymmetricKeyId;
}

