package org.foxtrot.api.dto.poll;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VotePollRequest {

    private long groupId;
    private long messageId;
    private long pollId;
    private List<Long> options;
}

