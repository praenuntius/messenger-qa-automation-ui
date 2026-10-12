package org.foxtrot.utils.factories.contexts;

import lombok.Builder;
import lombok.Getter;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.utils.TestDataBuilder;

@Getter
@Builder(toBuilder = true)
public class GroupContext {
    private final UsersEnum owner;
    private final UsersEnum admin;
    private final UsersEnum participant;

    private final Long groupId;
    private final String groupName;
    private final String groupDescription;

    private final Long workspaceId;
    private final String workspaceName;

    private final Long messageId;
    private final String messageText;

    /** Id поста в канале (строка для UI findMessageById). */
    private final String postMessageId;
    /** Второй пост в том же канале (лента комментариев и т.п.). */
    private final String secondaryPostMessageId;
    /** Id группы комментариев к посту. */
    private final Long commentGroupId;

    private final UsersEnum nonParticipant;
    private final TestDataBuilder nonParticipantBuilder;

    /** Прямой web-URL чата/пространства (текст сообщения в P2P). */
    private final String directUrl;
    /** Invite web-URL (текст сообщения в P2P). */
    private final String inviteUrl;

    /** Заполняется {@link org.foxtrot.utils.factories.core.PollFactory} после создания опроса. */
    private final Long pollId;
    private final Long pollFirstOptionId;

    private final TestDataBuilder ownerBuilder;
    private final TestDataBuilder adminBuilder;
    private final TestDataBuilder participantBuilder;
}