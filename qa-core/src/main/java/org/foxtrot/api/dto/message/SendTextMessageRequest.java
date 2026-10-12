package org.foxtrot.api.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SendTextMessageRequest {
    private String message;
    private long clientRandomId;
    private long groupId;
    private long[] tags;
    private Long replyMsgId;
    private MessageSchedulerDto scheduler;
    private List<Entity> entities;

    public SendTextMessageRequest(String message, long clientRandomId, long groupId) {
        this.message = message;
        this.clientRandomId = clientRandomId;
        this.groupId = groupId;
    }

    public SendTextMessageRequest(String message, long clientRandomId, long groupId, long[] tags) {
        this(message, clientRandomId, groupId);
        this.tags = tags;
    }

    public static SendTextMessageRequest scheduled(String message, long groupId, long scheduledAt) {
        SendTextMessageRequest req = new SendTextMessageRequest(message, 0L, groupId);
        req.setScheduler(new MessageSchedulerDto(scheduledAt));
        return req;
    }

    public static SendTextMessageRequest scheduledWithTags(String message, long groupId, long scheduledAt, long... tags) {
        SendTextMessageRequest req = scheduled(message, groupId, scheduledAt);
        req.setTags(tags);
        return req;
    }

    public static SendTextMessageRequest scheduledWithReply(String message, long groupId, long scheduledAt, long replyMsgId) {
        SendTextMessageRequest req = new SendTextMessageRequest(message, 0L, groupId);
        req.setScheduler(new MessageSchedulerDto(scheduledAt));
        req.setReplyMsgId(replyMsgId);
        return req;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageSchedulerDto {
        private long scheduledAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entity {
        private GroupMention groupMention;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupMention {
        private int length;
        private long groupId;
        private String groupType;
        private long workspaceId;
    }
}