package org.foxtrot.test.in_chat_context_menu.reactions;

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
public class InGroupTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @AllureId("52670")
    @Test
    @DisplayName("Отправить реакцию на свое сообщение")
    public void sendRecentReactionOnOwnMessage() {
        GroupContext context = groupFactory.withParticipant(groupFactory.privateGroupWithAdmin());
        String participantMessageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(
                        context.getGroupId(), "Participant text via API"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(participantMessageId);
        pages.chatActions.openMessageContextMenuById(participantMessageId);
        pages.quickReactionsUnitSteps.clickFirstAvailableReactionButton();
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(
                participantMessageId,
                pages.quickReactionsUnitSteps.getLastClickedReactionEmoji()
        );
    }

    @Test
    @DisplayName("Отправить реакцию на сообщение админа")
    public void sendRecentReactionOnAdminMessage() {
        GroupContext context = groupFactory.withParticipant(groupFactory.privateGroupWithAdmin());
        String adminMessageId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(
                        context.getGroupId(), "Admin text via API"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(adminMessageId);
        pages.chatActions.openMessageContextMenuById(adminMessageId);
        pages.quickReactionsUnitSteps.clickFirstAvailableReactionButton();
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(
                adminMessageId,
                pages.quickReactionsUnitSteps.getLastClickedReactionEmoji()
        );
    }

    @Test
    @DisplayName("Отправить реакцию на сообщение владельца")
    public void sendRecentReactionOnOwnerMessage() {
        GroupContext context = groupFactory.withParticipant(groupFactory.privateGroupWithAdmin());
        String ownerMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner text via API"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerMessageId);
        pages.chatActions.openMessageContextMenuById(ownerMessageId);
        pages.quickReactionsUnitSteps.clickFirstAvailableReactionButton();
        pages.chatVerify.verifyReactionPresentInMessageBubbleById(
                ownerMessageId,
                pages.quickReactionsUnitSteps.getLastClickedReactionEmoji()
        );
    }
}
