package org.foxtrot.test.chats_and_spaces.correspondence_counter;

import io.qameta.allure.AllureId;
import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SendMessagesTest extends BaseTest {

    private static final String testTxt = "Test Message";

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "82086", desktop = "82102")
    @DisplayName("Ответить на сообщение в чате")
    public void replyToMessageInChat() {
        var context = groupFactory.privateGroupWithParticipant();
        context.getOwnerBuilder().sendTextMessages(context.getGroupId(), testTxt);

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.openMessageContextMenu(testTxt);
        pages.messageBubbleContextMenu.clickReplyButton();
        pages.inputBarSteps.verifyReplyFieldIsVisible()
                .verifyMessageFieldIsEmpty()
                .pasteMessage(testTxt)
                .clickSendMessageButton();
        pages.chatVerify.verifyReplyMessageSent(testTxt);
    }

    @Test
    @ChannelAllureIds(web = "82091", desktop = "82101")
    @DisplayName("Отправить ссылку в чат")
    public void sendLinkInChat() {
        var context = groupFactory.privateGroup();
        String testUrl = "https://www.google.com";

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.pasteMessage(testUrl)
                .verifyAttachedLinkPreviewIsVisible()
                .clickSendMessageButton();
        pages.chatVerify.verifyMessageLinkText(testUrl);
    }

    @Test
    @ChannelAllureIds(web = "82092", desktop = "82103")
    @DisplayName("Отправить ссылку с текстом в чат")
    public void sendLinkAndTextInChat() {
        var context = groupFactory.privateGroup();
        String testUrl = "https://www.google.com";
        String linkText = "One";
        String plainText = "Two";

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField()
                .pasteMessage(linkText)
                .pressShiftEnter()
                .pasteMessage(plainText)
                .selectLineByTextWithTripleClick(linkText)
                .pressCmdК();
        pages.formatTextWithLinkModalSteps.enterUrl(testUrl);
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageLinkText(linkText)
                .verifyMessageTextIsNotLink(plainText);
    }

    @Test
    @ChannelAllureIds(web = "82088", desktop = "82104")
    @DisplayName("Отправить текстовое сообщение")
    public void sendTextMessage() {
        var context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField()
                .enterMessage(testTxt)
                .clickSendMessageButton();
        pages.chatVerify.verifyMessageSent(testTxt);
    }

    @Test
    @ChannelAllureIds(web = "82093", desktop = "82105")
    @DisplayName("Отправить текст + файл в чат")
    public void sendTextAndFileInChat() {
        var context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickMessageInputField()
                .enterMessage(testTxt)
                .uploadMediaFileViaAttachMenu("documents/tinyTXT.txt")
                .clickSendMessageButton();
        pages.chatVerify.verifyMessageSent(testTxt)
                .waitForMediaUploadToComplete().verifyFileSent();
    }

    @Test
    @ChannelAllureIds(web = "82094", desktop = "82106")
    @DisplayName("Отправить файл в чат")
    public void sendFileInChat() {
        var context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("documents/tinyTXT.txt")
                .clickSendMessageButton();
        pages.chatVerify.waitForMediaUploadToComplete().verifyFileSent();
    }

    @Test
    @ChannelAllureIds(web = "82083", desktop = "82097")
    @DisplayName("Отправка нескольких медиафайлов (альбом)")
    public void sendMultipleMediaFiles() {
        var context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.verifyInputBarIsVisible()
                .uploadMediaFileViaAttachMenu("images/tinyJPG.jpg")
                .uploadMediaFileViaAttachMenu("images/3mbPNG.png")
                .uploadMediaFileViaAttachMenu("videos/14MBMOV.mov")
                .uploadMediaFileViaAttachMenu("videos/24MBMP4.MP4")
                .verifyAttachedMediaPreviews(2, 2)
                .clickSendMessageButton();
        pages.chatVerify.waitForMediaUploadToComplete().verifyAlbumSent(2, 2);
    }

    @Test
    @ChannelAllureIds(web = "82082", desktop = "82096")
    @DisplayName("Отправка одного видео")
    public void sendSingleVideo() {
        var context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.verifyInputBarIsVisible()
                .uploadMediaFileViaAttachMenu("videos/24MBMP4.MP4")
                .verifyAttachedMediaPreviews(0, 1)
                .clickSendMessageButton();
        pages.chatVerify.waitForMediaUploadToComplete().verifyVideoSent();
    }

    @Test
    @ChannelAllureIds(web = "82081", desktop = "82095")
    @DisplayName("Отправка одного изображения")
    public void sendSingleImage() {
        var context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.verifyInputBarIsVisible()
                .uploadMediaFileViaAttachMenu("images/tinyJPG.jpg")
                .verifyAttachedMediaPreviews(1, 0)
                .clickSendMessageButton();
        pages.chatVerify.waitForMediaUploadToComplete().verifyImagesSent();
    }

    @Test
    @ChannelAllureIds(web = "68150", desktop = "82074")
    @DisplayName("Проверить открытие чата при получении гиперссылки")
    public void openChatOnReceivingHyperlink() {
        var context = groupFactory.privateGroupWithParticipant();
        String testUrl = "https://www.google.com";
        String linkText = "One";
        String plainText = "Two";

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.verifyInputBarIsVisible()
                .clickMessageInputField()
                .pasteMessage(testTxt)
                .pressShiftEnter()
                .pasteMessage(linkText)
                .pressShiftEnter()
                .pasteMessage(plainText)
                .selectLineByTextWithTripleClick(linkText)
                .pressCmdК();
        pages.formatTextWithLinkModalSteps.enterUrl(testUrl);
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageLinkText(linkText)
                .verifyMessageTextIsNotLink(plainText);

        loginHelper.performUILoginForTest(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.verifyInputBarIsVisible();
        pages.chatVerify.verifyMessageLinkText(linkText)
                .verifyMessageSent(testTxt)
                .verifyMessageTextIsNotLink(plainText);
    }

    @Test
    @ChannelAllureIds(web = "82084", desktop = "82098")
    @DisplayName("Редактирование недавно отправленного сообщения")
    public void editMessage() {
        var context = groupFactory.privateGroup();
        String editMessage = "edit Message";
        context.getOwnerBuilder().sendTextMessages(context.getGroupId(), testTxt);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.openMessageContextMenu(testTxt);
        pages.messageBubbleContextMenu.clickEditButton();
        pages.inputBarSteps.checkMessageFieldContainsText(testTxt)
                .selectAllTextByTripleClick()
                .pasteMessage(editMessage)
                .clickSendMessageButton();
        pages.chatVerify.verifyMessageText(editMessage);
    }

    @Test
    @ChannelAllureIds(web = "82087", desktop = "82100")
    @DisplayName("Упоминание пользователя в чате")
    public void mentionUserInChat() {
        var context = groupFactory.privateGroupWithParticipant();
        String mentionedUser = context.getParticipant().getName();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.verifyInputBarIsVisible()
                .clickMessageInputField()
                .enterMessage("@");
        pages.usersToMentionListSteps.verifyMentionListVisible()
                .clickMentionListItemByName(mentionedUser);
        pages.inputBarSteps.verifyMentionInInputField(mentionedUser)
                .clickSendMessageButton();
        pages.chatVerify.verifyMentionSent(mentionedUser);
    }

    @Test
    @AllureId("52607")
    @DisplayName("Отправить одновременно три файла формата .xls в группу пространства")
    public void sendThreeXlsFilesToWorkspaceGroup() {
        var context = groupFactory.privateGroupInWsWithParticipant();
        int xlsFilesCount = 3;

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.verifyInputBarIsVisible()
                .uploadMediaFileViaAttachMenu("documents/simple1.xls")
                .uploadMediaFileViaAttachMenu("documents/simple2.xls")
                .uploadMediaFileViaAttachMenu("documents/empty.xls")
                .clickSendMessageButton();
        pages.chatVerify.waitForMediaUploadToComplete()
                .verifyMessagesCount(xlsFilesCount)
                .verifyLastMessagesAreFiles(xlsFilesCount);

        loginHelper.performUILoginForTest(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessagesCount(xlsFilesCount)
                .verifyLastMessagesAreFiles(xlsFilesCount);
    }
}
