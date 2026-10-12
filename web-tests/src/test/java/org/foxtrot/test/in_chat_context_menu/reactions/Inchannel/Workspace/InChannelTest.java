package org.foxtrot.test.in_chat_context_menu.reactions.Inchannel.Workspace;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InChannelTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    //TODO:добавить два теста от лица неучастника: 60281, 60282
    @AllureId("60273")
    @Test
    @DisplayName("Отправить реакцию на свою публикацию")
    public void sendReactionOnOwnPost() {
        GroupContext context = groupFactory.withParticipant(groupFactory.withChatAdmin(groupFactory.privateChannelInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String ownerMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner text via API", ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.openMessageContextMenuById(ownerMessageId);
        pages.quickReactionsUnitSteps.clickQuickReactionAddButton();
        pages.fullReactionsListSteps
                .verifyReactionsListVisible()
                .selectReactionByEmoji("🙂");
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(ownerMessageId, "🙂");
    }

    @AllureId("60275")
    @Test
    @DisplayName("Отправить реакцию на свою публикацию с уже проставленной реакцией")
    public void sendReactionOnOwnPostWithReactions() {
        GroupContext context = groupFactory.withParticipant(groupFactory.withChatAdmin(groupFactory.privateChannelInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String ownerMessageWithReactionId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner message with reaction via API", ws));
        context.getAdminBuilder().addReaction(
                Long.parseLong(ownerMessageWithReactionId), context.getGroupId(), "🫠", context.getWorkspaceId());
        context.getOwnerBuilder().addReaction(
                Long.parseLong(ownerMessageWithReactionId), context.getGroupId(), "🫠", context.getWorkspaceId());

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(ownerMessageWithReactionId, "🫠");
        pages.chatActions.openMessageContextMenuById(ownerMessageWithReactionId);
        pages.quickReactionsUnitSteps.clickQuickReactionAddButton();
        pages.fullReactionsListSteps
                .verifyReactionsListVisible()
                .selectReactionByEmoji("🫠");
        pages.chatVerify.verifyReactionCountInMessageBubbleById(ownerMessageWithReactionId, "🫠", 2);
    }

    @AllureId("60283")
    @Test
    @DisplayName("Снять реакцию с публикации с реакциями других пользователей")
    public void removeReactionFromAdminPostWithReactions() {
        GroupContext context = groupFactory.withParticipant(groupFactory.withChatAdmin(groupFactory.privateChannelInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String adminMessageWithReactionId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(
                        context.getGroupId(), "Admin message with reaction via API", ws));
        context.getAdminBuilder().addReaction(
                Long.parseLong(adminMessageWithReactionId), context.getGroupId(), "👌", context.getWorkspaceId());
        context.getOwnerBuilder().addReaction(
                Long.parseLong(adminMessageWithReactionId), context.getGroupId(), "👌", context.getWorkspaceId());

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(adminMessageWithReactionId);
        pages.chatVerify.verifyReactionCountInMessageBubbleById(adminMessageWithReactionId, "👌", 2);
        pages.chatActions.clickReactionInMessageBubbleById(adminMessageWithReactionId, "👌");
        pages.chatVerify.verifyReactionCountInMessageBubbleById(adminMessageWithReactionId, "👌", 1);
    }

    @AllureId("60277")
    @Test
    @DisplayName("Отправить реакцию на публикацию Администратором канала")
    public void sendReactionOnPostByAdmin() {
        GroupContext context = groupFactory.withParticipant(groupFactory.withChatAdmin(groupFactory.privateChannelInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String ownerMessageForAdminReactionId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner message for admin reaction via API", ws));

        loginHelper.performUILogin(context.getAdmin());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.openMessageContextMenuById(ownerMessageForAdminReactionId);
        pages.quickReactionsUnitSteps.clickQuickReactionAddButton();
        pages.fullReactionsListSteps
                .verifyReactionsListVisible()
                .selectReactionByEmoji("🙂");
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(ownerMessageForAdminReactionId, "🙂");
    }

    @AllureId("60278")
    @Test
    @DisplayName("Снять реакцию с публикации с реакциями других пользователей")
    public void removeReactionFromPostWithReactionsByAdmin() {
        GroupContext context = groupFactory.withParticipant(groupFactory.withChatAdmin(groupFactory.privateChannelInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String ownerMessageWithReactionId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner message with reaction via API", ws));
        context.getAdminBuilder().addReaction(
                Long.parseLong(ownerMessageWithReactionId), context.getGroupId(), "🫠", context.getWorkspaceId());
        context.getOwnerBuilder().addReaction(
                Long.parseLong(ownerMessageWithReactionId), context.getGroupId(), "🫠", context.getWorkspaceId());

        loginHelper.performUILogin(context.getAdmin());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyReactionCountInMessageBubbleById(ownerMessageWithReactionId, "🫠", 2);
        pages.chatActions.clickReactionInMessageBubbleById(ownerMessageWithReactionId, "🫠");
        pages.chatVerify.verifyReactionCountInMessageBubbleById(ownerMessageWithReactionId, "🫠", 1);
    }

    @AllureId("60279")
    @Test
    @DisplayName("Отправить реакцию на публикацию владельца участником")
    public void sendReactionOnOwnerPostByParticipant() {
        GroupContext context = groupFactory.withParticipant(groupFactory.withChatAdmin(groupFactory.privateChannelInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String ownerMessageForParticipantReactionId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner message for participant reaction via API", ws));

        loginHelper.performUILogin(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerMessageForParticipantReactionId);
        pages.chatActions.openMessageContextMenuById(ownerMessageForParticipantReactionId);
        pages.quickReactionsUnitSteps.clickQuickReactionAddButton();
        pages.fullReactionsListSteps
                .verifyReactionsListVisible()
                .selectReactionByEmoji("🙂");
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(ownerMessageForParticipantReactionId, "🙂");
    }

    @AllureId("60280")
    @Test
    @DisplayName("Снять реакцию с публикации участником")
    public void removeReactionFromOwnerPostWithReactionsByParticipant() {
        GroupContext context = groupFactory.withParticipant(groupFactory.withChatAdmin(groupFactory.privateChannelInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String ownerMessageWithParticipantReactionId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner message with participant reaction via API", ws));
        context.getOwnerBuilder().addReaction(
                Long.parseLong(ownerMessageWithParticipantReactionId), context.getGroupId(), "🙂", context.getWorkspaceId());
        context.getAdminBuilder().addReaction(
                Long.parseLong(ownerMessageWithParticipantReactionId), context.getGroupId(), "🙂", context.getWorkspaceId());
        context.getParticipantBuilder().addReaction(
                Long.parseLong(ownerMessageWithParticipantReactionId), context.getGroupId(), "🙂", context.getWorkspaceId());

        loginHelper.performUILogin(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerMessageWithParticipantReactionId);
        pages.chatVerify.verifyReactionCountInMessageBubbleById(ownerMessageWithParticipantReactionId, "🙂", 3);
        pages.chatActions.clickReactionInMessageBubbleById(ownerMessageWithParticipantReactionId, "🙂");
        pages.chatVerify.verifyReactionCountInMessageBubbleById(ownerMessageWithParticipantReactionId, "🙂", 2);
    }
}
