package org.foxtrot.api.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClearHistoryRequest {

    private long groupId;
}
