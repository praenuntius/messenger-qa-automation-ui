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
public class ReplyTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @AllureId("52672")
    @Test
    @DisplayName("Реплай на свое сообщение")
    public void replyOnOwnMessage() {
        GroupContext context = groupFactory.privateGroup();
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner API SENT MESSAGE"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickReplyButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Reply to own message in group");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyReplyMessageSent("Reply to own message in group");
    }

    @Test
    @DisplayName("Реплай на чужое сообщение (от Админа)")
    public void replyOnAdminMessage() {
        GroupContext context = groupFactory.privateGroupWithAdmin();
        String messageId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(context.getGroupId(), "Admin API SENT MESSAGE"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickReplyButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Reply to Admin message in group");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyReplyMessageSent("Reply to Admin message in group");
    }

    @Test
    @DisplayName("Реплай на чужое сообщение (от Участника)")
    public void replyOnParticipantMessage() {
        GroupContext context = groupFactory.privateGroupWithParticipant();
        String messageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), "Participant API SENT MESSAGE"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickReplyButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Reply to Participant message in group");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyReplyMessageSent("Reply to Participant message in group");
    }
}
