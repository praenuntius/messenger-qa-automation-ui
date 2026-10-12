package org.foxtrot.api.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmMessageRequest {
    private Long lastMessageId;
    private Long groupId;
}
