package org.foxtrot.api.dto.massage.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendTextMassageResponse {
    private Long messageId;
}