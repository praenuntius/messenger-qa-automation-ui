package org.foxtrot.test.send_messages;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SendMediaFilesTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();
    private GroupContext context;

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.getOwnerBuilder().clearHistory(context.getGroupId());
        }
        groupFactory.cleanUp();
        context = null;
    }

    @AllureId("52590")
    @Test
    @DisplayName("Отправить JPG через attach меню")
    public void sendJpgViaAttachMenu() {
        context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("images/tinyJPG.jpg");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyImagesSent();
    }

    @AllureId("52600")
    @Test
    @DisplayName("Отправить PNG через attach меню")
    public void sendPngViaDragNDrop() {
        context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("images/3mbPNG.png");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyImagesSent();
    }

    @Test
    @DisplayName("Отправить GIF через attach меню")
    public void sendGifViaDragNDrop() {
        context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("images/giphy.gif");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyImagesSent();
    }

    @Test
    @DisplayName("Отправить MP4 видео через attach меню")
    public void sendMp4ViaAttachMenu() {
        context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("videos/24MBMP4.MP4");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyVideoSent();
    }

    @Test
    @DisplayName("Отправить MOV видео через attach меню")
    public void sendMovViaDragNDrop() {
        context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("videos/14MBMOV.mov");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyVideoSent();
    }

    @Test
    @DisplayName("Отправить TXT документ через attach меню")
    public void sendTxtViaAttachMenu() {
        context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("documents/tinyTXT.txt");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyFileSent();
    }

    @AllureId("52610")
    @Test
    @DisplayName("Отправить DOCX документ через attach меню")
    public void sendDocxViaDragNDrop() {
        context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("documents/tinyDOCX.docx");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyFileSent();
    }

    @Test
    @DisplayName("Отправить CSV документ через attach меню")
    public void sendCsvViaAttachMenu() {
        context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("documents/tinyCSV.csv");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyFileSent();
    }

    @Disabled
    @Test
    @DisplayName("Отправить мультимедиа через attach меню")
    public void sendMultimediaViaAttachMenu() {
        context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("images/tinyJPG.jpg");
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("images/3mbPNG.png");
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("images/giphy.gif");
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("videos/24MBMP4.MP4");
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("videos/14MBMOV.mov");
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("documents/tinyTXT.txt");
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("documents/tinyDOCX.docx");
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("documents/tinyCSV.csv");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMultimediaSent();
    }

    @Test
    @Disabled
    @DisplayName("Отправить мультимедиа через Drag and Drop")
    public void sendMultimediaViaDragNDrop() {
        context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.dragAndDropMediaFile("images/tinyJPG.jpg");
        pages.chatActions.dragAndDropMediaFile("images/3mbPNG.png");
        pages.chatActions.dragAndDropMediaFile("images/giphy.gif");
        pages.chatActions.dragAndDropMediaFile("videos/24MBMP4.MP4");
        pages.chatActions.dragAndDropMediaFile("videos/14MBMOV.mov");
        pages.chatActions.dragAndDropMediaFile("documents/tinyTXT.txt");
        pages.chatActions.dragAndDropMediaFile("documents/tinyDOCX.docx");
        pages.chatActions.dragAndDropMediaFile("documents/tinyCSV.csv");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageSent();
    }
}
