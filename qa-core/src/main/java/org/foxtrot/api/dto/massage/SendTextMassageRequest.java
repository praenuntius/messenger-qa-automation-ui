package org.foxtrot.api.dto.massage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendTextMassageRequest {

    private String message;
    private long clientRandomId;
    private long groupId;
    private Long replyMsgId;

    public SendTextMassageRequest(String message, long clientRandomId, long groupId) {
        this.message = message;
        this.clientRandomId = clientRandomId;
        this.groupId = groupId;
        this.replyMsgId = null; // или не задавать — и так null
    }
}
