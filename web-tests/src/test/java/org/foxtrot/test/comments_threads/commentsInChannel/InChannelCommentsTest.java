package org.foxtrot.test.comments_threads.commentsInChannel;

import com.codeborne.selenide.Selenide;
import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.UrlConstants;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Objects;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InChannelCommentsTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    //раздел "Настройки":
    @ChannelAllureIds(web = "54533", desktop = "54561")
    @Test
    @DisplayName("Меню настроек канала, роль 'Админ'")
    public void commentSettingsInChannelForAdmin() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());

        loginHelper.performUILogin(ctx.getAdmin());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.openChannelSettings();
        pages.groupAndChannelEditModalSteps.verifyAllowCommentsOptionPresentInChannel();
    }

    @ChannelAllureIds(web = "54532", desktop = "54560")
    @Test
    @DisplayName("Меню настроек канала, роль 'Владелец'")
    public void commentSettingsInChannelForOwner() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());

        loginHelper.performUILogin(ctx.getOwner());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.openChannelSettings();
        pages.groupAndChannelEditModalSteps.verifyAllowCommentsOptionPresentInChannel();
    }

    @ChannelAllureIds(web = "54534", desktop = "54562")
    @Test
    @DisplayName("Меню настроек канала, роль 'Подписчик'")
    public void commentSettingsInChannelForParticipant() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());

        loginHelper.performUILogin(ctx.getParticipant());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuVerifier.verifySettingsButtonAbsent();
    }

    //раздел "Счетчик Комментариев"
    @ChannelAllureIds(web = "54632", desktop = "54637")
    @Test
    @DisplayName("Отображение количества комментариев под публикацией")
    public void commentCounterBeneathThePost() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Owner text via API"));

        loginHelper.performUILogin(ctx.getOwner());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Owner comment on Post");
        pages.commentsSteps.sendCommentWithEnterKey();
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentCounterValue(postId, 1);

        loginHelper.performUILoginForTest(ctx.getParticipant());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Admin comment 1 on Post");
        pages.commentsSteps.sendCommentWithEnterKey();
        pages.inputBarSteps.enterMessage("Admin comment 2 on Post");
        pages.commentsSteps.sendCommentWithEnterKey();
        pages.inputBarSteps.enterMessage("Admin comment 3 on Post");
        pages.commentsSteps.sendCommentWithEnterKey();
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentCounterValue(postId, 4);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("Admin comment 3 on Post");
        pages.commentsSteps.clickDeleteButtonInCommentContextMenu();
        pages.deleteMessageModalSteps.clickDeleteButton();
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentCounterValue(postId, 3);

        loginHelper.performUILoginForTest(ctx.getAdmin());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("Admin comment 1 on Post");
        pages.commentsSteps.clickSelectButtonInCommentContextMenu();
        pages.multiselectMessagesSteps.clickCommentByTextInMultiselectMode("Admin comment 2 on Post");
        pages.multiselectMessagesSteps.clickCommentsByTextInMultiselectMode("Owner comment on Post");
        pages.multiselectMessagesSteps.clickDeleteButton();
        pages.deleteMessageModalSteps.clickDeleteButton();
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
    }

    //раздел "Удаление Комментариев":
    @ChannelAllureIds(web = "54640", desktop = "54643")
    @Test
    @DisplayName("Удаление через контекстное меню. Владелец/Админ")
    public void removeCommentViaContextMenuOwnerAndAdmin() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Deleting via context menu Post"));
        long commentGroupId = ctx.getOwnerBuilder().createCommentGroup(ctx.getGroupId(), Long.parseLong(postId));
        ctx.getOwnerBuilder().sendTextMessage(commentGroupId, "Owner comment on Deleting via context menu Post");
        ctx.getAdminBuilder().sendTextMessage(commentGroupId, "Admin comment on Deleting via context menu Post");
        ctx.getParticipantBuilder().sendTextMessage(commentGroupId, "Participant comment on Deleting via context menu Post");

        loginHelper.performUILogin(ctx.getOwner());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("Owner comment on Deleting via context menu Post");
        pages.commentsSteps.clickDeleteButtonInCommentContextMenu();
        pages.deleteMessageModalSteps.clickDeleteButton();
        pages.commentsSteps.verifySystemMessage("Комментарий удален");

        loginHelper.performUILoginForTest(ctx.getAdmin());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("Admin comment on Deleting via context menu Post");
        pages.commentsSteps.clickDeleteButtonInCommentContextMenu();
        pages.deleteMessageModalSteps.clickDeleteButton();
        pages.commentsSteps.verifySystemMessage("Комментарий удален");
    }

    @ChannelAllureIds(web = "54641", desktop = "54644")
    @Test
    @DisplayName("Удаление через контекстное меню. Подписчик")
    public void removeCommentViaContextMenuAsParticipant() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Deleting via context menu Post"));
        long commentGroupId = ctx.getOwnerBuilder().createCommentGroup(ctx.getGroupId(), Long.parseLong(postId));
        ctx.getOwnerBuilder().sendTextMessage(commentGroupId, "Owner comment on Deleting via context menu Post");
        ctx.getAdminBuilder().sendTextMessage(commentGroupId, "Admin comment on Deleting via context menu Post");
        ctx.getParticipantBuilder().sendTextMessage(commentGroupId, "Participant comment on Deleting via context menu Post");

        loginHelper.performUILogin(ctx.getParticipant());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("Participant comment on Deleting via context menu Post");
        pages.commentsSteps.clickDeleteButtonInCommentContextMenu();
        pages.deleteMessageModalSteps.clickDeleteButton();
        pages.commentsSteps.verifySystemMessage("Комментарий удален");
    }

    // раздел "Управление комментариями":
    @ChannelAllureIds(web = "54648", desktop = "54659")
    @Test
    @DisplayName("Включение комментариев Владельцем (отображение у других) > Под публикациями появилась плашка 'Прокомментировать'")
    public void enablingCommentsInChannelByOwnerInHomespace() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.withChatAdmin(groupFactory.privateChannel(false)));
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Owner post in comments disabled channel"));

        loginHelper.performUILogin(ctx.getOwner());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.verifyCommentButtonAbsentUnderBubbleById(postId);
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.openChannelSettings();
        pages.groupAndChannelEditModalSteps.clickAllowCommentsRadioButton();
        pages.groupAndChannelEditModalSteps.clickSaveButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(postId);

        loginHelper.performUILoginForTest(ctx.getAdmin());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(postId);

        loginHelper.performUILoginForTest(ctx.getParticipant());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(postId);
    }

    @ChannelAllureIds(web = "54646", desktop = "54657")
    @Test
    @DisplayName("Включение комментариев (отображение в ленте у себя) > Под публикациями появилась плашка 'Прокомментировать'")
    public void enablingCommentsInChannelByOwnerAndAdminInWorkspace() {
        GroupContext ctx = groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateChannelInWs(false));
        String ws = String.valueOf(ctx.getWorkspaceId());
        String postId = String.valueOf(
                ctx.getAdminBuilder().sendTextMessage(ctx.getGroupId(), "Admin text via API", ws));

        loginHelper.performUILogin(ctx.getAdmin());
        pages.leftSidebarSteps.clickSpaceByName(ctx.getWorkspaceName());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.verifyCommentButtonAbsentUnderBubbleById(postId);
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.openChannelSettings();
        pages.groupAndChannelEditModalSteps.clickAllowCommentsRadioButton();
        pages.groupAndChannelEditModalSteps.clickSaveButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(postId);
    }

    @ChannelAllureIds(web = "54649", desktop = "54660")
    @Test
    @DisplayName("Отключение комментариев Админом (отображение у других) > Под публикациями отсутствует плашка 'Прокомментировать'")
    public void disablingCommentsInChannelByAdminInHomespace() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Admin post for comments disabling channel"));

        loginHelper.performUILogin(ctx.getAdmin());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(postId);
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.openChannelSettings();
        pages.groupAndChannelEditModalSteps.clickAllowCommentsRadioButton();
        pages.groupAndChannelEditModalSteps.clickSaveButton();
        pages.commentsSteps.verifyCommentButtonAbsentUnderBubbleById(postId);

        loginHelper.performUILoginForTest(ctx.getAdmin());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.verifyCommentButtonAbsentUnderBubbleById(postId);

        loginHelper.performUILoginForTest(ctx.getParticipant());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.verifyCommentButtonAbsentUnderBubbleById(postId);
    }

    @ChannelAllureIds(web = "54647", desktop = "54658")
    @Test
    @DisplayName("Отключение комментариев (отображение в ленте у себя) > Под публикациями отсутствует плашка 'Прокомментировать'")
    public void disablingCommentsInChannelByOwnerAndAdminInWorkspace() {
        GroupContext ctx = groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateChannelInWs());
        String ws = String.valueOf(ctx.getWorkspaceId());
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Owner post in workspace comments enabled channel", ws));

        loginHelper.performUILogin(ctx.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(ctx.getWorkspaceName());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(postId);
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.openChannelSettings();
        pages.groupAndChannelEditModalSteps.clickAllowCommentsRadioButton();
        pages.groupAndChannelEditModalSteps.clickSaveButton();
        pages.commentsSteps.verifyCommentButtonAbsentUnderBubbleById(postId);

        loginHelper.performUILoginForTest(ctx.getAdmin());
        pages.leftSidebarSteps.clickSpaceByName(ctx.getWorkspaceName());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.verifyCommentButtonAbsentUnderBubbleById(postId);
    }

    @ChannelAllureIds(web = "54650", desktop = "54661")
    @Test
    @DisplayName("Управление комментариями после того, как появились комменты к публикациям 1.1")
    public void commentsManagementWithCommentedPost() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getAdminBuilder().sendTextMessage(ctx.getGroupId(), "Admin post for comments management (homespace)"));
        long commentGroupId = ctx.getOwnerBuilder().createCommentGroup(ctx.getGroupId(), Long.parseLong(postId));
        ctx.getOwnerBuilder().sendTextMessage(commentGroupId, "First comment for comments management (homespace)");

        loginHelper.performUILogin(ctx.getOwner());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(postId);
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.openChannelSettings();
        pages.groupAndChannelEditModalSteps.clickAllowCommentsRadioButton();
        pages.groupAndChannelEditModalSteps.clickSaveButton();
        pages.commentsSteps.verifyCommentCounterValue(postId, 1);

        loginHelper.performUILoginForTest(ctx.getAdmin());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.verifyCommentCounterValue(postId, 1);
    }

    @ChannelAllureIds(web = "54651", desktop = "54662")
    @Test
    @DisplayName("Управление комментариями после того, как появились комменты к публикациям 1.4")
    public void commentsManagementWithBothPostType() {
        GroupContext ctx = groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateChannelInWs(false));
        String ws = String.valueOf(ctx.getWorkspaceId());
        String postId = String.valueOf(
                ctx.getAdminBuilder().sendTextMessage(ctx.getGroupId(), "Admin post for comments management (workspace)", ws));

        loginHelper.performUILogin(ctx.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(ctx.getWorkspaceName());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.verifyCommentButtonAbsentUnderBubbleById(postId);
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.openChannelSettings();
        pages.groupAndChannelEditModalSteps.clickAllowCommentsRadioButton();
        pages.groupAndChannelEditModalSteps.clickSaveButton();
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
    }

    //раздел "Написание Комментария":
    @ChannelAllureIds(web = "54617", desktop = "54625")
    @Test
    @DisplayName("Неучастник канала, прокомментировавший пост, не становится подписчиком")
    public void commentByNoParticipantStill() {
        GroupContext ctx = groupFactory.withNonParticipant(groupFactory.publicChannel());
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Post for non-participant flow (homespace open channel)"));

        loginHelper.performUILogin(ctx.getNonParticipant());
        Selenide.open(Objects.requireNonNull(UrlConstants.getHomespaceChannelUrl(String.valueOf(ctx.getGroupId()))));
        pages.topbarSteps.verifyChatName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("No-participant comment text");
        pages.chatVerify.saveMessageInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(postId);
        pages.inputBarSteps.verifySubscribeToChannelButtonVisible();
    }

    @ChannelAllureIds(web = "54619", desktop = "54627")
    @Test
    @DisplayName("Отображение имени автора")
    public void commentAuthorNameDisplay() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Owner text via API"));

        loginHelper.performUILogin(ctx.getOwner());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Owner comment for author name display");
        pages.commentsSteps.saveCommentInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent();
        pages.commentsSteps.verifyCommentAuthorName(ctx.getGroupName());
        pages.commentsSteps.verifyCommentAuthorAvatarVisible();

        loginHelper.performUILoginForTest(ctx.getAdmin());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Admin comment for author name display");
        pages.commentsSteps.saveCommentInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent();
        pages.commentsSteps.verifyCommentAuthorAvatarVisible();

        loginHelper.performUILoginForTest(ctx.getParticipant());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Participant comment for author name display");
        pages.commentsSteps.saveCommentInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent();
        pages.commentsSteps.verifyCommentAuthorName(ctx.getParticipant().getName());
        pages.commentsSteps.verifyCommentAuthorAvatarVisible();
        pages.commentsSteps.clickBackFromCommentsButton();
    }

    @ChannelAllureIds(web = "54616", desktop = "54624")
    @Test
    @DisplayName("Отправка комментария неучастником канала")
    public void noParticipantCommentSent() {
        GroupContext ctx = groupFactory.withNonParticipant(groupFactory.publicChannel());
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Post for non-participant flow (homespace open channel)"));

        loginHelper.performUILogin(ctx.getNonParticipant());
        Selenide.open(Objects.requireNonNull(UrlConstants.getHomespaceChannelUrl(String.valueOf(ctx.getGroupId()))));
        pages.topbarSteps.verifyChatName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("No-participant comment text");
        pages.chatVerify.saveMessageInputText();
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(postId);
    }

    @ChannelAllureIds(web = "54618", desktop = "54626")
    @Test
    @DisplayName("Отправка нетекстового комментария")
    public void sendNoTextCommentMessage() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Owner post in comments enabled channel"));

        loginHelper.performUILogin(ctx.getOwner());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("documents/tinyTXT.txt");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.waitForMediaUploadToComplete().verifyFileSent();
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("images/tinyJPG.jpg");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.waitForMediaUploadToComplete().verifyImagesSent();
        pages.inputBarSteps.uploadMediaFileViaAttachMenu("videos/14MBMOV.mov");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.waitForMediaUploadToComplete().verifyVideoSent();
        pages.inputBarSteps.enterMessage("https://google.com/");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageSent("https://google.com/");
    }

    @ChannelAllureIds(web = "54620", desktop = "54628")
    @Test
    @DisplayName("Упоминание в комментариях")
    public void sendMentionInCommentMessage() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Owner post in comments enabled channel"));

        loginHelper.performUILogin(ctx.getOwner());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("@");
        pages.usersToMentionListSteps.verifyMentionListNotVisible();

        loginHelper.performUILoginForTest(ctx.getAdmin());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("@");
        pages.usersToMentionListSteps.verifyMentionListNotVisible();

        loginHelper.performUILoginForTest(ctx.getParticipant());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("@");
        pages.usersToMentionListSteps.verifyMentionListNotVisible();
    }

    //раздел "Мультиселект в Комментариях":
    @ChannelAllureIds(web = "54593", desktop = "54600")
    @Test
    @DisplayName("Мультиселект. Владелец/Админ. Неактивность кнопки 'Закрепить'")
    public void pinCommentUnavailableForOwnerAndAdmin() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getAdminBuilder().sendTextMessage(ctx.getGroupId(), "Admin post for multiselect (homespace)"));
        long commentGroupId = ctx.getOwnerBuilder().createCommentGroup(ctx.getGroupId(), Long.parseLong(postId));
        ctx.getOwnerBuilder().sendTextMessage(commentGroupId, "First comment for comments management (homespace)");
        ctx.getAdminBuilder().sendTextMessage(commentGroupId, "Admin comment for comments management (homespace)");
        ctx.getParticipantBuilder().sendTextMessage(commentGroupId, "Participant comment for comments management (homespace)");

        loginHelper.performUILogin(ctx.getOwner());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("First comment for comments management (homespace)");
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.verifyPinButtonIsDisabled();

        loginHelper.performUILoginForTest(ctx.getAdmin());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("First comment for comments management (homespace)");
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.verifyPinButtonIsDisabled();
    }

    @ChannelAllureIds(web = "54592", desktop = "54599")
    @Test
    @DisplayName("Мультиселект. Владелец/Админ. Свои и чужие комментарии")
    public void multiselectOptionsAvailabilityPeerAndOwnComments() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getAdminBuilder().sendTextMessage(ctx.getGroupId(), "Admin post for multiselect (homespace)"));
        long commentGroupId = ctx.getOwnerBuilder().createCommentGroup(ctx.getGroupId(), Long.parseLong(postId));
        ctx.getOwnerBuilder().sendTextMessage(commentGroupId, "First comment for comments management (homespace)");
        ctx.getAdminBuilder().sendTextMessage(commentGroupId, "Admin comment for comments management (homespace)");
        ctx.getParticipantBuilder().sendTextMessage(commentGroupId, "Participant comment for comments management (homespace)");

        loginHelper.performUILogin(ctx.getOwner());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("First comment for comments management (homespace)");
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.clickCommentByTextInMultiselectMode("Admin comment for comments management (homespace)");
        pages.multiselectMessagesSteps.clickCommentByTextInMultiselectMode("Participant comment for comments management (homespace)");
        pages.multiselectMessagesSteps.verifyPinButtonIsDisabled();
        pages.multiselectMessagesSteps.verifyCancelButtonIsEnabled();
        pages.multiselectMessagesSteps.verifyDeleteButtonIsEnabled();
        pages.multiselectMessagesSteps.verifyForwardButtonIsEnabled();

        loginHelper.performUILoginForTest(ctx.getAdmin());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("First comment for comments management (homespace)");
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.clickCommentByTextInMultiselectMode("Admin comment for comments management (homespace)");
        pages.multiselectMessagesSteps.clickCommentByTextInMultiselectMode("Participant comment for comments management (homespace)");
        pages.multiselectMessagesSteps.verifyPinButtonIsDisabled();
        pages.multiselectMessagesSteps.verifyCancelButtonIsEnabled();
        pages.multiselectMessagesSteps.verifyDeleteButtonIsEnabled();
        pages.multiselectMessagesSteps.verifyForwardButtonIsEnabled();
    }

    @ChannelAllureIds(web = "54594", desktop = "54601")
    @Test
    @DisplayName("Мультиселект. Подписчик. Только свои комментарии")
    public void multiselectOptionsAvailabilityOwnComments() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getAdminBuilder().sendTextMessage(ctx.getGroupId(), "Admin post for multiselect (homespace)"));
        long commentGroupId = ctx.getOwnerBuilder().createCommentGroup(ctx.getGroupId(), Long.parseLong(postId));
        ctx.getOwnerBuilder().sendTextMessage(commentGroupId, "First comment for comments management (homespace)");
        ctx.getAdminBuilder().sendTextMessage(commentGroupId, "Admin comment for comments management (homespace)");
        ctx.getParticipantBuilder().sendTextMessage(commentGroupId, "Participant comment for comments management (homespace)");

        loginHelper.performUILogin(ctx.getParticipant());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("Participant comment for comments management (homespace)");
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.verifyPinButtonIsDisabled();
        pages.multiselectMessagesSteps.verifyCancelButtonIsEnabled();
        pages.multiselectMessagesSteps.verifyDeleteButtonIsEnabled();
        pages.multiselectMessagesSteps.verifyForwardButtonIsEnabled();
    }

    @ChannelAllureIds(web = "54595", desktop = "54602")
    @Test
    @DisplayName("Мультиселект. Подписчик. Только чужие комментарии. Проверка задизэйбленных кнопок.")
    public void multiselectOptionsAvailabilityPeerComments() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getAdminBuilder().sendTextMessage(ctx.getGroupId(), "Admin post for multiselect (homespace)"));
        long commentGroupId = ctx.getOwnerBuilder().createCommentGroup(ctx.getGroupId(), Long.parseLong(postId));
        ctx.getOwnerBuilder().sendTextMessage(commentGroupId, "First comment for comments management (homespace)");
        ctx.getAdminBuilder().sendTextMessage(commentGroupId, "Admin comment for comments management (homespace)");
        ctx.getParticipantBuilder().sendTextMessage(commentGroupId, "Participant comment for comments management (homespace)");

        loginHelper.performUILogin(ctx.getParticipant());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("First comment for comments management (homespace)");
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.clickCommentByTextInMultiselectMode("Admin comment for comments management (homespace)");
        pages.multiselectMessagesSteps.verifyDeleteButtonIsDisabled();
        pages.multiselectMessagesSteps.verifyPinButtonIsDisabled();
    }

    //раздел "Лента Комментариев":
    @ChannelAllureIds(web = "54584", desktop = "54598")
    @Test
    @DisplayName("В ленте много комментариев. Тап/клик на закрепленный пост.")
    public void clickPinnedPostFeedWithMultipleComments() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Owner post for comments feed (homespace)"));
        ctx.getAdminBuilder().sendTextMessage(ctx.getGroupId(), "Admin post for comments feed (homespace)");

        loginHelper.performUILogin(ctx.getOwner());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.sendTextMessagesUntilCommentedPostHeaderAppears(30, true);
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("1");
        pages.inputBarSteps.clickSendMessageButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("1");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatActions.clickCommentedPostHeader();
        pages.chatActions.findMessageByText("Owner post for comments feed (homespace)");
    }

    @ChannelAllureIds(web = "54579", desktop = "54596")
    @Test
    @DisplayName("Проверка отображения ленты комментариев публикации без комментариев")
    public void commentFeedWithoutComments() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Owner post for comments feed (homespace)");
        String secondaryPostId = String.valueOf(
                ctx.getAdminBuilder().sendTextMessage(ctx.getGroupId(), "Admin post for comments feed (homespace)"));

        loginHelper.performUILogin(ctx.getOwner());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(secondaryPostId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(secondaryPostId);
        pages.commentsSteps.verifyNoCommentsYetSystemMessage();
    }

    @ChannelAllureIds(web = "54580", desktop = "54597")
    @Test
    @DisplayName("Проверка отображения ленты комментариев публикации, прокомментированной в день публикации")
    public void commentFeedWithCommentsMadeToday() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Owner post for comments feed (homespace)");
        String secondaryPostId = String.valueOf(
                ctx.getAdminBuilder().sendTextMessage(ctx.getGroupId(), "Admin post for comments feed (homespace)"));

        loginHelper.performUILogin(ctx.getOwner());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(secondaryPostId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(secondaryPostId);
        pages.commentsSteps.verifyNoCommentsYetSystemMessage();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("1");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyNoCommentsYetSystemMessageNotVisible();
        pages.commentsSteps.verifyThreadStartSystemMessageVisible();
        pages.commentsSteps.verifyTodayDateSystemMessageVisible();
    }

    //раздел "Контекстное меню":
    @ChannelAllureIds(web = "54564", desktop = "54574")
    @Test
    @DisplayName("Собственный коммент. Владелец/Админ")
    public void ownCommentContextMenuByAdminAndOwner() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Owner post for comments context menu (homespace)"));
        long commentGroupId = ctx.getOwnerBuilder().createCommentGroup(ctx.getGroupId(), Long.parseLong(postId));
        ctx.getOwnerBuilder().sendTextMessage(commentGroupId, "Owner comment for context menu (homespace)");
        ctx.getAdminBuilder().sendTextMessage(commentGroupId, "Admin comment for context menu (homespace)");
        ctx.getParticipantBuilder().sendTextMessage(commentGroupId, "Participant comment for context menu (homespace)");

        loginHelper.performUILogin(ctx.getOwner());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("Owner comment for context menu (homespace)");
        pages.messageBubbleContextMenu.verifyOwnCommentContextMenuByOwnerOrAdmin();

        loginHelper.performUILoginForTest(ctx.getAdmin());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("Admin comment for context menu (homespace)");
        pages.messageBubbleContextMenu.verifyOwnCommentContextMenuByOwnerOrAdmin();
    }

    @ChannelAllureIds(web = "54565", desktop = "54575")
    @Test
    @DisplayName("Собственный коммент. Подписчик")
    public void ownCommentContextMenuBySubscriber() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Owner post for comments context menu (homespace)"));
        long commentGroupId = ctx.getOwnerBuilder().createCommentGroup(ctx.getGroupId(), Long.parseLong(postId));
        ctx.getOwnerBuilder().sendTextMessage(commentGroupId, "Owner comment for context menu (homespace)");
        ctx.getAdminBuilder().sendTextMessage(commentGroupId, "Admin comment for context menu (homespace)");
        ctx.getParticipantBuilder().sendTextMessage(commentGroupId, "Participant comment for context menu (homespace)");

        loginHelper.performUILogin(ctx.getParticipant());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("Participant comment for context menu (homespace)");
        pages.messageBubbleContextMenu.verifyOwnCommentContextMenuBySubscriber();
    }

    @ChannelAllureIds(web = "54566", desktop = "54576")
    @Test
    @DisplayName("Чужой коммент. Владелец/Админ")
    public void peerCommentByOwnerAndAdmin() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Owner post for comments context menu (homespace)"));
        long commentGroupId = ctx.getOwnerBuilder().createCommentGroup(ctx.getGroupId(), Long.parseLong(postId));
        ctx.getOwnerBuilder().sendTextMessage(commentGroupId, "Owner comment for context menu (homespace)");
        ctx.getAdminBuilder().sendTextMessage(commentGroupId, "Admin comment for context menu (homespace)");
        ctx.getParticipantBuilder().sendTextMessage(commentGroupId, "Participant comment for context menu (homespace)");

        loginHelper.performUILogin(ctx.getOwner());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("Participant comment for context menu (homespace)");
        pages.messageBubbleContextMenu.verifyPeerCommentContextMenuByOwnerOrAdmin();

        loginHelper.performUILoginForTest(ctx.getAdmin());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("Participant comment for context menu (homespace)");
        pages.messageBubbleContextMenu.verifyPeerCommentContextMenuByOwnerOrAdmin();
    }

    @ChannelAllureIds(web = "54567", desktop = "54577")
    @Test
    @DisplayName("Чужой коммент. Подписчик")
    public void peerCommentBySubscriber() {
        GroupContext ctx = groupFactory.withParticipant(groupFactory.privateChannelWithAdmin());
        String postId = String.valueOf(
                ctx.getOwnerBuilder().sendTextMessage(ctx.getGroupId(), "Owner post for comments context menu (homespace)"));
        long commentGroupId = ctx.getOwnerBuilder().createCommentGroup(ctx.getGroupId(), Long.parseLong(postId));
        ctx.getOwnerBuilder().sendTextMessage(commentGroupId, "Owner comment for context menu (homespace)");
        ctx.getAdminBuilder().sendTextMessage(commentGroupId, "Admin comment for context menu (homespace)");
        ctx.getParticipantBuilder().sendTextMessage(commentGroupId, "Participant comment for context menu (homespace)");

        loginHelper.performUILogin(ctx.getParticipant());
        pages.chatListSteps.clickChatByName(ctx.getGroupName());
        pages.chatActions.findMessageById(postId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(postId);
        pages.chatActions.openMessageContextMenu("Owner comment for context menu (homespace)");
        pages.messageBubbleContextMenu.verifyPeerCommentContextMenuBySubscriber();
    }
}
