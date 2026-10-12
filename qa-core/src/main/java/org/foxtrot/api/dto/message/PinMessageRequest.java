package org.foxtrot.api.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PinMessageRequest {
    private String groupId;
    private List<String> messageIds;
}

