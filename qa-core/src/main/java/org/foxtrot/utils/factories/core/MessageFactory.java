package org.foxtrot.utils.factories.core;

import org.foxtrot.utils.factories.contexts.GroupContext;

public class MessageFactory {

    public GroupContext sendMessage(GroupContext ctx, String text) {
        long msgId = ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), text);

        return ctx.toBuilder()
                .messageId(msgId)
                .messageText(text)
                .build();
    }

    public GroupContext sendMessage(GroupContext ctx) {
        return sendMessage(ctx, "test" + System.currentTimeMillis());
    }
}
