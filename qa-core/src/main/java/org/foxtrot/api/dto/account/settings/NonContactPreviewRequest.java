package org.foxtrot.api.dto.account.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NonContactPreviewRequest {
    private String nonContactPreviewType;
}