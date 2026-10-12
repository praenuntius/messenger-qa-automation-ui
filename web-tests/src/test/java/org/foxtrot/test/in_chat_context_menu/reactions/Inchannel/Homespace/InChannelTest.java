package org.foxtrot.test.in_chat_context_menu.reactions.Inchannel.Homespace;

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

    //TODO:добавить два теста от лица неучастника: 60195, 60196
    @AllureId("60182")
    @Test
    @DisplayName("Отправить быструю реакцию на свою публикацию")
    public void sendQuickReactionOnOwnPost() {
        GroupContext context = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String ownerMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerMessageId);
        pages.chatActions.openMessageContextMenuById(ownerMessageId);
        pages.quickReactionsUnitSteps.clickFirstAvailableReactionButton();
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(
                ownerMessageId,
                pages.quickReactionsUnitSteps.getLastClickedReactionEmoji()
        );
    }

    @AllureId("60183")
    @Test
    @DisplayName("Отправить реакцию на публикацию админа")
    public void sendReactionOnAdminPost() {
        GroupContext context = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String adminMessageId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(context.getGroupId(), "Admin text via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(adminMessageId);
        pages.chatActions.openMessageContextMenuById(adminMessageId);
        pages.quickReactionsUnitSteps.clickQuickReactionAddButton();
        pages.fullReactionsListSteps
                .verifyReactionsListVisible()
                .selectReactionByEmoji("🙂");
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(adminMessageId, "🙂");
    }

    @AllureId("60184")
    @Test
    @DisplayName("Отправить реакцию на свою публикацию с уже проставленной реакцией")
    public void sendReactionOnOwnPostWithReactions() {
        GroupContext context = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String ownerMessageWithReactionId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner message with reaction via API"));
        context.getAdminBuilder().addReaction(
                Long.parseLong(ownerMessageWithReactionId), context.getGroupId(), "🫠");
        context.getOwnerBuilder().addReaction(
                Long.parseLong(ownerMessageWithReactionId), context.getGroupId(), "🫠");

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(ownerMessageWithReactionId, "🫠");
        pages.chatActions.openMessageContextMenuById(ownerMessageWithReactionId);
        pages.quickReactionsUnitSteps.clickQuickReactionAddButton();
        pages.fullReactionsListSteps
                .verifyReactionsListVisible()
                .selectReactionByEmoji("🫠");
        pages.chatVerify.verifyReactionCountInMessageBubbleById(ownerMessageWithReactionId, "🫠", 2);
    }

    @AllureId("60185")
    @Test
    @DisplayName("Снять реакцию с публикации с реакциями других пользователей")
    public void removeReactionFromAdminPostWithReactions() {
        GroupContext context = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String adminMessageWithReactionId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(
                        context.getGroupId(), "Admin message with reaction via API"));
        context.getAdminBuilder().addReaction(
                Long.parseLong(adminMessageWithReactionId), context.getGroupId(), "🔥");
        context.getOwnerBuilder().addReaction(
                Long.parseLong(adminMessageWithReactionId), context.getGroupId(), "🔥");

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(adminMessageWithReactionId);
        pages.chatVerify.verifyReactionCountInMessageBubbleById(adminMessageWithReactionId, "🔥", 2);
        pages.chatActions.clickReactionInMessageBubbleById(adminMessageWithReactionId, "🔥");
        pages.chatVerify.verifyReactionCountInMessageBubbleById(adminMessageWithReactionId, "🔥", 1);
    }

    @AllureId("60187")
    @Test
    @DisplayName("Отправить реакцию на публикацию Администратором канала")
    public void sendReactionOnPostByAdmin() {
        GroupContext context = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String ownerMessageForAdminReactionId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner message for admin reaction via API"));

        loginHelper.performUILogin(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.openMessageContextMenuById(ownerMessageForAdminReactionId);
        pages.quickReactionsUnitSteps.clickQuickReactionAddButton();
        pages.fullReactionsListSteps
                .verifyReactionsListVisible()
                .selectReactionByEmoji("🙂");
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(ownerMessageForAdminReactionId, "🙂");
    }

    @AllureId("60188")
    @Test
    @DisplayName("Отправить реакцию на свою публикацию с уже проставленной реакцией администратором")
    public void sendReactionOnOwnPostWithReactionsByAdmin() {
        GroupContext context = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String adminMessageWithReactionId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(
                        context.getGroupId(), "Admin message with reaction via API"));
        context.getAdminBuilder().addReaction(
                Long.parseLong(adminMessageWithReactionId), context.getGroupId(), "🔥");
        context.getOwnerBuilder().addReaction(
                Long.parseLong(adminMessageWithReactionId), context.getGroupId(), "🔥");

        loginHelper.performUILogin(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(adminMessageWithReactionId, "🔥");
        pages.chatActions.findMessageById(adminMessageWithReactionId);
        pages.chatActions.openMessageContextMenuById(adminMessageWithReactionId);
        pages.quickReactionsUnitSteps.clickQuickReactionAddButton();
        pages.fullReactionsListSteps
                .verifyReactionsListVisible()
                .selectReactionByEmoji("🔥");
        pages.chatVerify.verifyReactionCountInMessageBubbleById(adminMessageWithReactionId, "🔥", 2);
    }

    @AllureId("60189")
    @Test
    @DisplayName("Снять реакцию с публикации с реакциями других пользователей")
    public void removeReactionFromPostWithReactionsByAdmin() {
        GroupContext context = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String ownerMessageWithReactionId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner message with reaction via API"));
        context.getAdminBuilder().addReaction(
                Long.parseLong(ownerMessageWithReactionId), context.getGroupId(), "🫠");
        context.getOwnerBuilder().addReaction(
                Long.parseLong(ownerMessageWithReactionId), context.getGroupId(), "🫠");

        loginHelper.performUILogin(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyReactionCountInMessageBubbleById(ownerMessageWithReactionId, "🫠", 2);
        pages.chatActions.clickReactionInMessageBubbleById(ownerMessageWithReactionId, "🫠");
        pages.chatVerify.verifyReactionCountInMessageBubbleById(ownerMessageWithReactionId, "🫠", 1);
    }

    @AllureId("60191")
    @Test
    @DisplayName("Отправить быструю реакцию на свою публикацию")
    public void sendQuickReactionOnOwnPostByParticipant() {
        GroupContext context = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String adminMessageId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(context.getGroupId(), "Admin text via API"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.openMessageContextMenuById(adminMessageId);
        pages.quickReactionsUnitSteps.clickFirstAvailableReactionButton();
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(
                adminMessageId,
                pages.quickReactionsUnitSteps.getLastClickedReactionEmoji()
        );
    }

    @AllureId("60193")
    @Test
    @DisplayName("Отправить реакцию на публикацию владельца участником")
    public void sendReactionOnOwnerPostByParticipant() {
        GroupContext context = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String ownerMessageForParticipantReactionId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner message for participant reaction via API"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerMessageForParticipantReactionId);
        pages.chatActions.openMessageContextMenuById(ownerMessageForParticipantReactionId);
        pages.quickReactionsUnitSteps.clickQuickReactionAddButton();
        pages.fullReactionsListSteps
                .verifyReactionsListVisible()
                .selectReactionByEmoji("🙂");
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(ownerMessageForParticipantReactionId, "🙂");
    }

    @AllureId("60192")
    @Test
    @DisplayName("Отправить реакцию на свою публикацию с уже проставленной реакцией участником")
    public void sendReactionOnOwnPostWithReactionsByParticipant() {
        GroupContext context = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String ownerMessageWithOwnerAndAdminReactionId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner message with owner and admin reaction via API"));
        context.getOwnerBuilder().addReaction(
                Long.parseLong(ownerMessageWithOwnerAndAdminReactionId), context.getGroupId(), "😎");
        context.getAdminBuilder().addReaction(
                Long.parseLong(ownerMessageWithOwnerAndAdminReactionId), context.getGroupId(), "😎");

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(ownerMessageWithOwnerAndAdminReactionId, "😎");
        pages.chatActions.findMessageById(ownerMessageWithOwnerAndAdminReactionId);
        pages.chatActions.openMessageContextMenuById(ownerMessageWithOwnerAndAdminReactionId);
        pages.quickReactionsUnitSteps.clickQuickReactionAddButton();
        pages.fullReactionsListSteps
                .verifyReactionsListVisible()
                .selectReactionByEmoji("😎");
        pages.chatVerify.verifyReactionCountInMessageBubbleById(ownerMessageWithOwnerAndAdminReactionId, "😎", 3);
    }

    @AllureId("60194")
    @Test
    @DisplayName("Снять реакцию с публикации участником")
    public void removeReactionFromOwnerPostWithReactionsByParticipant() {
        GroupContext context = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String ownerMessageWithParticipantReactionId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner message with participant reaction via API"));
        context.getOwnerBuilder().addReaction(
                Long.parseLong(ownerMessageWithParticipantReactionId), context.getGroupId(), "🙂");
        context.getAdminBuilder().addReaction(
                Long.parseLong(ownerMessageWithParticipantReactionId), context.getGroupId(), "🙂");
        context.getParticipantBuilder().addReaction(
                Long.parseLong(ownerMessageWithParticipantReactionId), context.getGroupId(), "🙂");

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerMessageWithParticipantReactionId);
        pages.chatVerify.verifyReactionCountInMessageBubbleById(ownerMessageWithParticipantReactionId, "🙂", 3);
        pages.chatActions.clickReactionInMessageBubbleById(ownerMessageWithParticipantReactionId, "🙂");
        pages.chatVerify.verifyReactionCountInMessageBubbleById(ownerMessageWithParticipantReactionId, "🙂", 2);
    }

    @AllureId("60197")
    @Test
    @DisplayName("Реакции от админа и владельца отображаются отдельно")
    public void reactionsFromAdminAndOwnerAreSeparate() {
        GroupContext context = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String ownerMessageWithOwnerAndAdminDiffReactionId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner message with owner and admin peace reaction via API"));
        context.getOwnerBuilder().addReaction(
                Long.parseLong(ownerMessageWithOwnerAndAdminDiffReactionId), context.getGroupId(), "☮️");
        context.getAdminBuilder().addReaction(
                Long.parseLong(ownerMessageWithOwnerAndAdminDiffReactionId), context.getGroupId(), "☮️");

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerMessageWithOwnerAndAdminDiffReactionId);
        pages.chatVerify.verifyReactionCountInMessageBubbleById(ownerMessageWithOwnerAndAdminDiffReactionId, "☮️", 2);
        pages.chatActions.hoverReactionInMessageBubbleById(ownerMessageWithOwnerAndAdminDiffReactionId, "☮️");
        pages.chatVerify.verifyReactionTooltipContainsUserNames(
                ownerMessageWithOwnerAndAdminDiffReactionId,
                "☮️",
                context.getOwner().getName(),
                context.getAdmin().getName()
        );
    }
}
