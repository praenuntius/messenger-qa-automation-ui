package org.foxtrot.test.chats_and_spaces.correspondence_counter;

import com.codeborne.selenide.Selenide;
import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.enums.SystemMessageType;
import org.foxtrot.pages.key_interactions.PressKeys;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ContextMenuTest extends BaseTest {

    private static final int FILLER_MESSAGES_COUNT = 21;
    private static final String FILLER_MESSAGE_PREFIX = "Filler message ";
    private static final String msgOne = "one";
    private static final String msgTwo = "two";
    private static final String msgThree = "three";

    private static final String testTxt = "Текст для вставки Ctrl+V";
    private static final String participantMessage = "Participant text";
    private static final String ownerMessage = "Owner text";

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "52699", desktop = "82056")
    @DisplayName("Вставить изображение в поле ввода сочетанием кнопок Ctrl+V")
    public void pasteImageIntoInputViaCtrlV() {
        var context = groupFactory.privateGroup();

        openGroupChatAsOwner(context);
        pages.inputBarSteps.pasteImageIntoInput("images/tinyJPG.jpg")
                .clickSendMessageButton();
        pages.chatVerify.verifyImagesSent();
    }

    @Test
    @ChannelAllureIds(web = "52700", desktop = "82057")
    @DisplayName("Вставить изображение в поле ввода через контекстное меню с помощью пункта \"Вставить\"")
    @Disabled("Забагованно поведение, баг FB-7867")
    public void pasteImageIntoInputViaContextMenu() {
        var context = groupFactory.privateGroup();

        openGroupChatAsOwner(context);
        pages.inputBarSteps.copyImageToClipboard("images/tinyJPG.jpg")
                .clickMessageInputField()
                .openInputFieldContextMenu();
        pages.contextMenuSteps.clickPasteButton();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyImagesSent();
    }

    @Test
    @ChannelAllureIds(web = "52701", desktop = "82058")
    @DisplayName("Вставить текст в поле ввода сочетанием кнопок Ctrl+V")
    public void pasteTextIntoInputViaCtrlV() {
        var context = groupFactory.privateGroup();

        openGroupChatAsOwner(context);
        Selenide.clipboard().setText(testTxt);
        pages.inputBarSteps.clickMessageInputField()
                .pasteTextIntoInput();
        pages.inputBarSteps.checkMessageFieldContainsText(testTxt);
    }

    @Test
    @ChannelAllureIds(web = "52702", desktop = "82059")
    @DisplayName("Вставить текст в поле ввода через контекстное меню с помощью пункта \"Вставить\"")
    public void pasteTextIntoInputViaContextMenu() {
        var context = groupFactory.privateGroup();

        openGroupChatAsOwner(context);
        Selenide.clipboard().setText(testTxt);
        pages.inputBarSteps.clickMessageInputField()
                .openInputFieldContextMenu();
        pages.contextMenuSteps.clickPasteButton();
        pages.inputBarSteps.checkMessageFieldContainsText(testTxt);
    }

    @Test
    @ChannelAllureIds(web = "52704", desktop = "82061")
    @DisplayName("Вырезать текст в поле ввода через контекстное меню с помощью пункта \"Вырезать\"")
    public void cutTextIntoInputViaContextMenu() {
        var context = groupFactory.privateGroup();

        openGroupChatAsOwner(context);
        pages.inputBarSteps.clickMessageInputField()
                .pasteMessage(testTxt)
                .checkMessageFieldContainsText(testTxt)
                .selectAllTextByTripleClick()
                .openInputFieldContextMenu();
        pages.contextMenuSteps.clickCutButton();
        pages.inputBarSteps.verifyMessageFieldIsEmpty();
    }

    @Test
    @ChannelAllureIds(web = "52703", desktop = "82060")
    @DisplayName("Вырезать текст из поля ввода сочетанием кнопок CTRL + X")
    public void cutTextIntoInputViaCtrlX() {
        var context = groupFactory.privateGroup();

        openGroupChatAsOwner(context);
        pages.inputBarSteps.clickMessageInputField()
                .pasteMessage(testTxt)
                .checkMessageFieldContainsText(testTxt)
                .selectAllTextByTripleClick()
                .cuteTextIntoInput();
        pages.inputBarSteps.verifyMessageFieldIsEmpty();
    }

    @Test
    @ChannelAllureIds(web = "52686", desktop = "82051")
    @DisplayName("Закрепить сообщение собеседника в чате")
    public void pinPeerMessageInChat() {
        var context = groupFactory.privateGroupWithParticipant();
        String messageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), participantMessage));

        openGroupChatAsOwner(context);
        pages.chatActions.findMessageById(messageId)
                .openMessageContextMenuById(messageId);
        pages.chatVerify.verifyContextMenuButtonVisible(pages.messageBubbleContextMenu.getMessagePinButton());
        pages.messageBubbleContextMenu.clickPinButton();
        pages.chatVerify.verifySystemMessage(SystemMessageType.PINNED, participantMessage);
        pages.topbarSteps.verifyPinButtonAvailable()
                .verifyPinnedMessagesUnreadIndicatorIsVisible();
    }

    @Test
    @ChannelAllureIds(web = "52682", desktop = "82049")
    @DisplayName("Открыть список закрепленных сообщений в чате")
    public void openPinnedMessagesListInChat() {
        var context = groupFactory.privateGroupWithParticipant();
        String messageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), participantMessage));
        context.getOwnerBuilder().pinMessage(context.getGroupId(), messageId);

        openGroupChatAsOwner(context);
        pages.topbarSteps.verifyPinButtonAvailable()
                .clickPinnedMessageButton();
        pages.pinnedMessageMenuSteps.verifyPinnedMessagesSidebarIsVisible()
                .verifyPinnedMessageIsVisible(messageId);
    }

    @Test
    @ChannelAllureIds(web = "52668", desktop = "82046")
    @DisplayName("Отображение контекстного меню в p2p-чате с контактом/неконтакт")
    public void displayContextMenuInP2pChat() {
        var context = groupFactory.p2pChat();

        String ownerMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), ownerMessage));
        String participantMessageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), participantMessage));

        openP2pChatAsOwner(context);
        pages.chatActions.openMessageContextMenuById(ownerMessageId);
        pages.messageBubbleContextMenu.verifyOwnMessageContextMenu();
        PressKeys.pressEscape();

        pages.chatActions.openMessageContextMenuById(participantMessageId);
        pages.messageBubbleContextMenu.verifyPeerMessageContextMenu();
    }

    @Test
    @ChannelAllureIds(web = "52669", desktop = "82047")
    @DisplayName("Отображение контекстного меню в групповом чате(Участник)")
    public void displayContextMenuInGroupChatByParticipant() {
        var context = groupFactory.privateGroupWithParticipant();
        String ownerMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), ownerMessage));
        String participantMessageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), participantMessage));

        openGroupChatAsParticipant(context);
        pages.chatActions.openMessageContextMenuById(participantMessageId);
        pages.messageBubbleContextMenu.verifyOwnMessageContextMenu();
        PressKeys.pressEscape();

        pages.chatActions.openMessageContextMenuById(ownerMessageId);
        pages.messageBubbleContextMenu.verifyPeerMessageContextMenu();
    }

    @Test
    @ChannelAllureIds(web = "52683", desktop = "82050")
    @DisplayName("Переход к сообщению из закрепленных сообщений")
    public void navigateToMessageFromPinnedMessages() {
        var context = groupFactory.privateGroupWithParticipant();
        String pinnedMessageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), participantMessage));
        context.getOwnerBuilder().pinMessage(context.getGroupId(), pinnedMessageId);
        pushTargetMessageOffScreen(context);

        openGroupChatAsOwner(context);
        pages.topbarSteps.verifyPinButtonAvailable()
                .clickPinnedMessageButton();
        pages.pinnedMessageMenuSteps.verifyPinnedMessagesSidebarIsVisible()
                .verifyPinnedMessageIsVisible(pinnedMessageId)
                .clickGoToMessageById(pinnedMessageId);
        pages.chatVerify.verifyMessageHighlightAnimation(pinnedMessageId);
    }

    @Test
    @ChannelAllureIds(web = "52673", desktop = "82048")
    @DisplayName("Переход к сообщению из реплая")
    public void navigateToMessageFromReply() {
        var context = groupFactory.privateGroupWithParticipant();
        String originalMessageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), participantMessage));

        pushTargetMessageOffScreen(context);

        Long replyMessageId = context.getOwnerBuilder().sendTextMessageWithReply(
                context.getGroupId(), "Reply to participant message", Long.parseLong(originalMessageId));

        openGroupChatAsOwner(context);
        pages.chatActions.clickReplyInsideMessageById(String.valueOf(replyMessageId));
        pages.chatVerify.verifyMessageHighlightAnimation(originalMessageId);
        pages.chatActions.findMessageById(originalMessageId);
    }

    @Test
    @ChannelAllureIds(web = "76654", desktop = "82075")
    @DisplayName("Переход к сообщению из реплая ( по меншену)")
    public void navigateToMessageFromMentionInReply() {
        var context = groupFactory.privateGroupWithParticipant();
        String originalMessageId = String.valueOf(
                context.getParticipantBuilder().sendGroupMentionMessage(
                        context.getGroupId(), context.getGroupName(), context.getGroupId()));

        pushTargetMessageOffScreen(context);

        Long replyMessageId = context.getOwnerBuilder().sendTextMessageWithReply(
                context.getGroupId(), "Reply to mention message", Long.parseLong(originalMessageId));

        openGroupChatAsOwner(context);
        pages.chatActions.clickMentionInReplyById(String.valueOf(replyMessageId));
        pages.chatVerify.verifyMessageHighlightAnimation(originalMessageId);
    }

    @Test
    @ChannelAllureIds(web = "52687", desktop = "82076")
    @DisplayName("Переход к сообщению по клику на системное сообщение в чате")
    public void navigateToMessageByClickingPinnedSystemMessage() {
        var context = groupFactory.privateGroupWithParticipant();
        String pinnedMessageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), participantMessage));
        context.getOwnerBuilder().pinMessage(context.getGroupId(), pinnedMessageId);
        pushTargetMessageOffScreen(context);

        openGroupChatAsOwner(context);
        pages.systemMessagesSteps.clickPinnedSystemMessage(participantMessage);
        pages.chatVerify.verifyMessageHighlightAnimation(pinnedMessageId);
    }

    @Test
    @ChannelAllureIds(web = "52692", desktop = "82052")
    @DisplayName("Удаление 3 своих сообщений в группе (роль пользователь в чате)")
    public void deleteThreeOwnMessagesViaMultiselectAsParticipant() {
        var context = groupFactory.privateGroup();
        context.getOwnerBuilder().sendTextMessages(context.getGroupId(),
                msgOne, msgTwo, msgThree);

        openGroupChatAsOwner(context);
        pages.chatActions.openMessageContextMenu(msgOne);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.clickMessagesByTextInMultiselectMode(
                        msgTwo, msgThree)
                .verifyMessagesSelectedByTextInMultiselectMode(
                        msgOne, msgTwo, msgThree)
                .clickDeleteButton();
        pages.deleteMessageModalSteps.clickDeleteButton();
        pages.chatVerify.verifyMessagesWithTextAreNotDisplayed(
                        msgOne, msgTwo, msgThree)
                .verifySystemMessage(SystemMessageType.DELETED, "Вы удалили сообщение");
    }

    @Test
    @ChannelAllureIds(web = "52693", desktop = "82053")
    @DisplayName("Удаление 3 чужих сообщений в группе (роль пользователь в чате)")
    public void deleteThreeOthersMessagesViaMultiselectAsParticipant() {
        var context = groupFactory.privateGroupWithParticipant();
        context.getOwnerBuilder().sendTextMessages(context.getGroupId(),
                msgOne, msgTwo, msgThree);

        openGroupChatAsParticipant(context);
        pages.chatActions.openMessageContextMenu(msgOne);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.clickMessagesByTextInMultiselectMode(
                        msgTwo, msgThree)
                .verifyMessagesSelectedByTextInMultiselectMode(
                        msgOne, msgTwo, msgThree)
                .verifyDeleteButtonIsDisabled();
    }

    @Test
    @ChannelAllureIds(web = "52695", desktop = "82054")
    @DisplayName("Удаление своего и чужого сообщения в чате (роль пользователь в чате)")
    public void deleteOwnAndForeignMessages() {
        var context = groupFactory.privateGroupWithParticipant();
        context.getOwnerBuilder().sendTextMessages(context.getGroupId(), ownerMessage);
        context.getParticipantBuilder().sendTextMessages(context.getGroupId(), participantMessage);

        openGroupChatAsParticipant(context);
        pages.chatActions.openMessageContextMenu(ownerMessage);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.clickMessagesByTextInMultiselectMode(participantMessage).
                verifyMessagesSelectedByTextInMultiselectMode(ownerMessage, participantMessage)
                .clickDeleteButton();
        pages.deleteMessageModalSteps.verifyCannotDeleteMessageLabelIsVisible()
                .clickDeleteButton();
        pages.chatVerify.verifyMessageText(ownerMessage)
                .verifyMessagesWithTextAreNotDisplayed(participantMessage);
    }

    @Test
    @ChannelAllureIds(web = "52697", desktop = "82055")
    @DisplayName("Удалить две публикации в канале (роль администратор в канале)")
    public void deleteTwoPublicationsInChannelAsAdmin() {
        var context = groupFactory.privateChannelWithAdmin();
        context.getOwnerBuilder().sendTextMessages(context.getGroupId(), msgOne, msgTwo);

        loginHelper.performUILogin(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.openMessageContextMenu(msgOne);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.clickMessagesByTextInMultiselectMode(msgTwo)
                .verifyMessagesSelectedByTextInMultiselectMode(msgOne, msgTwo)
                .clickDeleteButton();
        pages.deleteMessageModalSteps.clickDeleteButton();
        pages.chatVerify.verifyMessagesWithTextAreNotDisplayed(msgOne, msgTwo)
                .verifySystemMessage(SystemMessageType.DELETED, context.getGroupName() + " удалил(а) публикацию");
    }

    private void openGroupChatAsOwner(GroupContext context) {
        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
    }

    private void openGroupChatAsParticipant(GroupContext context) {
        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
    }

    private void openP2pChatAsOwner(GroupContext context) {
        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getParticipant().getName());
    }

    private void pushTargetMessageOffScreen(GroupContext context) {
        context.getOwnerBuilder().sendTextMessages(context.getGroupId(), FILLER_MESSAGES_COUNT, FILLER_MESSAGE_PREFIX);
    }
}