package org.foxtrot.test.in_chat_context_menu;

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
public class EditMessageTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @AllureId("56900")
    @Test
    @DisplayName("Изменить свое сообщение")
    public void editOwnMessage() {
        GroupContext context = groupFactory.privateGroup();
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner API SENT MESSAGE"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickEditButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage(" Edited message by OWNER");
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatVerify.verifyMessageTextById(messageId, "Owner API SENT MESSAGE Edited message by OWNER");
    }

    @Test
    @DisplayName("Проверить отсутствие кнопки Edit для сообщения от Admin")
    public void verifyEditButtonAbsentForAdminMessage() {
        GroupContext context = groupFactory.privateGroupWithAdmin();
        String messageId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(context.getGroupId(), "Admin API SENT MESSAGE"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.chatVerify.verifyContextMenuButtonAbsent(
                pages.messageBubbleContextMenu.getMessageEditButton()
        );
    }

    @Test
    @DisplayName("Проверить отсутствие кнопки Edit для сообщения от Participant")
    public void verifyEditButtonAbsentForParticipantMessage() {
        GroupContext context = groupFactory.privateGroupWithParticipant();
        String messageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), "Participant API SENT MESSAGE"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.chatVerify.verifyContextMenuButtonAbsent(
                pages.messageBubbleContextMenu.getMessageEditButton()
        );
    }
}
