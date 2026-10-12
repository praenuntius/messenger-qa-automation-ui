package org.foxtrot.api.dto.encryption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymmetricKeyUpdateRequest {

    private long encryptionKeyId;
}
