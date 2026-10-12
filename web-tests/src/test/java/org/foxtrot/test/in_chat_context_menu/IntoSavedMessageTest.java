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
public class IntoSavedMessageTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @AllureId("52698")
    @Test
    @DisplayName("Отправить в сохраненные собственное сообщение")
    public void intoSavedOwnMessage() {
        String messageText = "Owner text via API";
        GroupContext context = groupFactory.privateGroup();
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), messageText));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickIntoSavedButton();
        pages.snackbarsSteps.verifySentIntoSaveSnackbarVisible();
        pages.chatListSteps.clickChatSaved();
        pages.chatVerify.verifyMessageText(messageText);
    }

    @Test
    @DisplayName("Отправить в сохраненные чужое сообщение участника")
    public void intoSavedPeerMessage() {
        String messageText = "Participant text via API";
        GroupContext context = groupFactory.privateGroupWithParticipant();
        String messageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), messageText));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickIntoSavedButton();
        pages.snackbarsSteps.verifySentIntoSaveSnackbarVisible();
        pages.chatListSteps.clickChatSaved();
        pages.chatVerify.verifyMessageText(messageText);
    }

    //TODO: Расширить список проверок разными типами сообщений, сверится с ALLURE
}
