package org.foxtrot.test.in_chat_context_menu;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.enums.SystemMessageType;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.foxtrot.utils.*;
import org.junit.jupiter.api.*;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeleteMessagesTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @AllureId("52694")
    @Test
    @DisplayName("Удалить собственное сообщение (владелец)")
    public void deleteOwnMessage() {
        GroupContext context = groupFactory.privateGroup();
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickDeleteButton();
        pages.deleteMessageModalSteps.clickDeleteButton();
        pages.chatVerify.verifySystemMessage(SystemMessageType.DELETED, "Вы удалили сообщение");
    }

    @AllureId("52696")
    @Test
    @DisplayName("Удалить сообщение админа")
    public void deleteAdminPeerMessage() {
        GroupContext context = groupFactory.privateGroupWithAdmin();
        String messageId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(context.getGroupId(), "Admin text via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickDeleteButton();
        pages.deleteMessageModalSteps.clickDeleteButton();
        pages.chatVerify.verifySystemMessage(SystemMessageType.DELETED, "Вы удалили сообщение");
    }

    @Test
    @DisplayName("Удалить сообщение участника")
    public void deleteParticipantPeerMessage() {
        GroupContext context = groupFactory.privateGroupWithParticipant();
        String messageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), "Participant text via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickDeleteButton();
        pages.deleteMessageModalSteps.clickDeleteButton();
        pages.chatVerify.verifySystemMessage(SystemMessageType.DELETED, "Вы удалили сообщение");
    }
}
