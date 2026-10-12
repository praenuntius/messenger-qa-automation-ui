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
public class CopyTextMessageTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @AllureId("52674")
    @Test
    @DisplayName("Скопировать собственное текстовое сообщение, отправить его в чат при помощи CMD+V")
    public void copyOwnTextMessage() {
        String messageText = "Owner text via API";
        GroupContext context = groupFactory.privateGroup();
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), messageText));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCopyTextButton();
        pages.snackbarsSteps.verifyTextCopiedSnackbarVisible();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.pasteTextIntoInput();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageSent(messageText);
    }

    @AllureId("58385")
    @Test
    @DisplayName("Скопировать чужое текстовое сообщение, отправить его в чат при помощи контекстного меню в поле ввода")
    public void copyPeerParticipantTextMessage() {
        String messageText = "Participant text via API";
        GroupContext context = groupFactory.privateGroupWithParticipant();
        String messageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), messageText));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCopyTextButton();
        pages.snackbarsSteps.verifyTextCopiedSnackbarVisible();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.openInputFieldContextMenu();
        pages.contextMenuSteps.clickPasteButton(); //починить здесь навигацию в кнопку (не кликается)
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatVerify.verifyMessageSent(messageText);
    }

    //TODO: добавить проверку отсутствия кнопки copy text в баббле где нет текста.
}
