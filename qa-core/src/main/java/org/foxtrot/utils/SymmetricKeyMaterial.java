package org.foxtrot.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymmetricKeyMaterial {

    private long id;
    private String keyBase64;
    private String transformation;
}
