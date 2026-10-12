package org.foxtrot.test.send_messages;

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
public class SendMessageTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();
    private final String testMessage = "TestMessage";

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @AllureId("52589")
    @Test
    @DisplayName("Отправить текстовое сообщение")
    public void sendTextMessage() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Group Text Message By Owner");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageSent("Group Text Message By Owner");
    }

    @AllureId("56730")
    @Test
    @DisplayName("Отправить emoji сообщение")
    public void sendEmojiMessage() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("😀");
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatVerify.verifyEmojiSent();
    }

    @AllureId("56745")
    @Test
    @DisplayName("Отправить отформатированное в чате сообщение (CMD+B)")
    public void shouldSendBoldMessageWithHotkeys() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage(testMessage);
        pages.inputBarSteps.selectAllTextByTripleClick();
        pages.inputBarSteps.pressCmdB();
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatVerify.verifyBoldInMessage(testMessage);
    }

    @Test
    @DisplayName("Отправить отформатированное в чате сообщение (CMD+U)")
    public void SendUnderlinedMessageWithHotkeys() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage(testMessage);
        pages.inputBarSteps.selectAllTextByTripleClick();
        pages.inputBarSteps.pressCmdU();
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatVerify.verifyUnderlinedInMessage(testMessage);
    }

    @Test
    @DisplayName("Отправить отформатированное в чате сообщение (CMD+I)")
    public void shouldSendItalicMessageWithHotkeys() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage(testMessage);
        pages.inputBarSteps.selectAllTextByTripleClick();
        pages.inputBarSteps.pressCmdI();
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatVerify.verifyItalicInMessage(testMessage);
    }

    @Test
    @DisplayName("Отправить отформатированное в чате сообщение (CMD+S)")
    public void shouldSendStrikethroughMessageWithHotkeys() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage(testMessage);
        pages.inputBarSteps.selectAllTextByTripleClick();
        pages.inputBarSteps.pressCmdS();
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatVerify.verifyStrikethroughInMessage(testMessage);
    }

    @Test
    @DisplayName("Отправить отформатированное в чате сообщение (Cmd+Shift+M)")
    public void shouldSendMonospaceMessageWithHotkeys() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage(testMessage);
        pages.inputBarSteps.selectAllTextByTripleClick();
        pages.inputBarSteps.pressCmdShiftM();
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatVerify.verifyMonospaceInMessage(testMessage);
    }

    @Test
    @DisplayName("Отправить отформатированное через контекстное меню сообщение (Жирный)")
    public void SendBoldMessageInChatWithContextMenu() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage(testMessage);
        pages.inputBarSteps.selectAllTextByTripleClick();
        pages.inputBarSteps.openInputFieldContextMenu();
        pages.contextMenuSteps.clickFormattingButton();
        pages.contextMenuSteps.clickBoldButton();
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatVerify.verifyBoldInMessage(testMessage);
    }

    @Test
    @DisplayName("Отправить отформатированное через контекстное меню сообщение (Курсив)")
    public void SendItalicMessageInChatWithContextMenu() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage(testMessage);
        pages.inputBarSteps.selectAllTextByTripleClick();
        pages.inputBarSteps.openInputFieldContextMenu();
        pages.contextMenuSteps.clickFormattingButton();
        pages.contextMenuSteps.clickItalicButton();
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatVerify.verifyItalicInMessage(testMessage);
    }

    @Test
    @DisplayName("Отправить отформатированное через контекстное меню сообщение (Подчеркнутый)")
    public void SendUnderlinedMessageInChatWithContextMenu() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage(testMessage);
        pages.inputBarSteps.selectAllTextByTripleClick();
        pages.inputBarSteps.openInputFieldContextMenu();
        pages.contextMenuSteps.clickFormattingButton();
        pages.contextMenuSteps.clickUnderlineButton();
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatVerify.verifyUnderlinedInMessage(testMessage);
    }

    @Test
    @DisplayName("Отправить отформатированное через контекстное меню сообщение (Зачеркнутый)")
    public void SendStrikethroughMessageInChatWithContextMenu() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage(testMessage);
        pages.inputBarSteps.selectAllTextByTripleClick();
        pages.inputBarSteps.openInputFieldContextMenu();
        pages.contextMenuSteps.clickFormattingButton();
        pages.contextMenuSteps.clickStrikethroughButton();
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatVerify.verifyStrikethroughInMessage(testMessage);
    }

    @Test
    @DisplayName("Отправить отформатированное через контекстное меню сообщение (Моноширинный)")
    public void SendMonospaceMessageInChatWithContextMenu() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage(testMessage);
        pages.inputBarSteps.selectAllTextByTripleClick();
        pages.inputBarSteps.openInputFieldContextMenu();
        pages.contextMenuSteps.clickFormattingButton();
        pages.contextMenuSteps.clickMonospaceButton();
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatVerify.verifyMonospaceInMessage(testMessage);
    }

    @AllureId("68153")
    @Test
    @DisplayName("Отправить отформатированное сообщение со ссылкой, проверить работу ссылки")
    public void sendFormattedWithLinkMessage() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("TestMessageForManualFormatting");
        pages.inputBarSteps.selectAllTextByTripleClick();
        pages.inputBarSteps.pressCmdК();
        pages.formatTextWithLinkModalSteps.enterUrl("https://google.com/");
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatActions.findAndClickLinkMessageByText("TestMessageForManualFormatting");
        pages.openTheLinkModalSteps.clickOpenButton();
        pages.chatVerify.verifyLinkOpensInNewTab("https://www.google.com/");
    }

    @AllureId("57838")
    @Test
    @DisplayName("Отправить карточку контакта")
    public void sendContactCard() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickOpenAttachMenuButton();
        pages.attachContextMenuSteps.clickShareContactsButton();
        pages.shareContactListSteps.sendContact(context.getAdmin().getName());
        pages.chatVerify.verifyContactSent(context.getAdmin().getName());
    }

    @Test
    @DisplayName("Отправить упоминание участника группы ")
    public void sendContactMention() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("@" + context.getAdmin().getName());
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatActions.sendMessageWithEnterKey();
        pages.chatVerify.verifyMentionSent(context.getAdmin().getName());
    }

    @AllureId("52612")
    @Test
    @DisplayName("Отправить несколько упоминаний: себя и общий ")
    public void sendSeveralMentions() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("@Channel");
        pages.chatActions.sendMessageWithEnterKey();
        pages.inputBarSteps.enterMessage(" ");
        pages.inputBarSteps.enterMessage("@" + context.getAdmin().getName());
        pages.chatActions.sendMessageWithEnterKey();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMentionSent(context.getAdmin().getName(), "Channel");
    }
}
