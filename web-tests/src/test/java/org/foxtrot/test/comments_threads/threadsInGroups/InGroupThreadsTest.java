package org.foxtrot.test.comments_threads.threadsInGroups;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.UrlConstants;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.contexts.WorkspaceContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.foxtrot.utils.factories.core.PollFactory;
import org.foxtrot.utils.factories.core.PollFactory.PollConfig;
import org.foxtrot.utils.factories.core.WorkspaceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InGroupThreadsTest extends BaseTest {

    //TODO: починить работу отключенных тестов

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();
    private final PollFactory pollFactory = new PollFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
        workspaceFactory.cleanUp();
    }

    //Контекстное меню сообщения в треде
    @ChannelAllureIds(web = "57195", desktop = "57821")
    @Test
    @DisplayName("Проверка корректности работы контекстного меню")
    public void threadContextMenuCheck() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));
        long threadGroupId = context.getOwnerBuilder().createCommentGroup(
                context.getGroupId(), Long.parseLong(ownerPostMessageId));
        String ownerCommentId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(threadGroupId, "Owner comment via API"));
        String adminCommentId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(threadGroupId, "Admin comment via API"));
        String participantCommentId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(threadGroupId, "Participant comment via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(ownerCommentId);
        pages.chatActions.openMessageContextMenuById(ownerCommentId);
        pages.messageBubbleContextMenu.verifyOwnCommentContextMenuByOwnerOrAdmin();
        pages.chatActions.findMessageById(adminCommentId);
        pages.chatActions.openMessageContextMenuById(adminCommentId);
        pages.messageBubbleContextMenu.verifyPeerCommentContextMenuByOwnerOrAdmin();
        pages.chatActions.findMessageById(participantCommentId);
        pages.chatActions.openMessageContextMenuById(participantCommentId);
        pages.messageBubbleContextMenu.verifyPeerCommentContextMenuByOwnerOrAdmin();

        loginHelper.performUILoginForTest(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(ownerCommentId);
        pages.chatActions.openMessageContextMenuById(ownerCommentId);
        pages.messageBubbleContextMenu.verifyPeerCommentContextMenuByOwnerOrAdmin();
        pages.chatActions.findMessageById(adminCommentId);
        pages.chatActions.openMessageContextMenuById(adminCommentId);
        pages.messageBubbleContextMenu.verifyOwnCommentContextMenuByOwnerOrAdmin();
        pages.chatActions.findMessageById(participantCommentId);
        pages.chatActions.openMessageContextMenuById(participantCommentId);
        pages.messageBubbleContextMenu.verifyPeerCommentContextMenuByOwnerOrAdmin();

        loginHelper.performUILoginForTest(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(ownerCommentId);
        pages.chatActions.openMessageContextMenuById(ownerCommentId);
        pages.messageBubbleContextMenu.verifyPeerCommentContextMenuBySubscriber();
        pages.chatActions.findMessageById(adminCommentId);
        pages.chatActions.openMessageContextMenuById(adminCommentId);
        pages.messageBubbleContextMenu.verifyPeerCommentContextMenuBySubscriber();
        pages.chatActions.findMessageById(participantCommentId);
        pages.chatActions.openMessageContextMenuById(participantCommentId);
        pages.messageBubbleContextMenu.verifyOwnCommentContextMenuBySubscriber();
    }

    @ChannelAllureIds(web = "57191", desktop = "57817")
    @Test
    @DisplayName("Собственное сообщение в Комментарии. Владелец/Админ")
    public void ownCommentInThreadForOwnerAndAdmin() {
        GroupContext context = groupFactory.privateGroupWithAdmin();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));
        long threadGroupId = context.getOwnerBuilder().createCommentGroup(
                context.getGroupId(), Long.parseLong(ownerPostMessageId));
        String ownerCommentId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(threadGroupId, "Owner comment via API"));
        String adminCommentId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(threadGroupId, "Admin comment via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(ownerCommentId);
        pages.chatActions.openMessageContextMenuById(ownerCommentId);
        pages.messageBubbleContextMenu.verifyOwnCommentContextMenuByOwnerOrAdmin();

        loginHelper.performUILoginForTest(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(adminCommentId);
        pages.chatActions.openMessageContextMenuById(adminCommentId);
        pages.messageBubbleContextMenu.verifyOwnCommentContextMenuByOwnerOrAdmin();
    }

    @ChannelAllureIds(web = "57192", desktop = "57818")
    @Test
    @DisplayName("Собственное сообщение в Комментариях. Участник")
    public void ownCommentInThreadForParticipant() {
        GroupContext context = groupFactory.privateGroupWithParticipant();
        String participantPostMessageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), "Participant text via API"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(participantPostMessageId);
        pages.chatActions.openMessageContextMenuById(participantPostMessageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("Participant thread-comment on OWN Post");
        pages.commentsSteps.sendCommentWithEnterKey();
        pages.chatActions.findMessageByText("Participant thread-comment on OWN Post");
        pages.chatActions.openMessageContextMenu("Participant thread-comment on OWN Post");
        pages.messageBubbleContextMenu.verifyOwnCommentContextMenuBySubscriber();
    }

    @ChannelAllureIds(web = "57194", desktop = "57820")
    @Test
    @DisplayName("Чужое сообщение в Комментариях. Владелец/Админ")
    public void peerCommentInThreadForOwnerAndAdmin() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));
        long threadGroupId = context.getOwnerBuilder().createCommentGroup(
                context.getGroupId(), Long.parseLong(ownerPostMessageId));
        String adminCommentId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(threadGroupId, "Admin comment via API"));
        String participantCommentId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(threadGroupId, "Participant comment via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(adminCommentId);
        pages.chatActions.openMessageContextMenuById(adminCommentId);
        pages.messageBubbleContextMenu.verifyPeerCommentContextMenuByOwnerOrAdmin();

        loginHelper.performUILoginForTest(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(participantCommentId);
        pages.chatActions.openMessageContextMenuById(participantCommentId);
        pages.messageBubbleContextMenu.verifyPeerCommentContextMenuByOwnerOrAdmin();
    }

    @ChannelAllureIds(web = "57193", desktop = "57819")
    @Test
    @DisplayName("Чужое сообщение в Комментариях. Участник")
    public void peerCommentInThreadForParticipant() {
        GroupContext context = groupFactory.privateGroupWithParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));
        long threadGroupId = context.getOwnerBuilder().createCommentGroup(
                context.getGroupId(), Long.parseLong(ownerPostMessageId));
        String ownerCommentId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(threadGroupId, "Owner comment via API"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(ownerCommentId);
        pages.chatActions.openMessageContextMenuById(ownerCommentId);
        pages.messageBubbleContextMenu.verifyPeerCommentContextMenuBySubscriber();
    }

    //Лента сообщений в треде
    @ChannelAllureIds(web = "57154", desktop = "57810")
    @Test
    @DisplayName("В ленте Комментариев много сообщений. Кнопка скролла")
    public void commentFeedScrollButton() {
        GroupContext homespaceContext = groupFactory.privateGroup();
        String homespaceOwnerPostMessageId = String.valueOf(
                homespaceContext.getOwnerBuilder().sendTextMessage(
                        homespaceContext.getGroupId(), "Owner post for thread feed (via API)"));

        GroupContext workspaceContext = groupFactory.privateGroupInWs();
        String ws = String.valueOf(workspaceContext.getWorkspaceId());
        String workspaceOwnerPostMessageId = String.valueOf(
                workspaceContext.getOwnerBuilder().sendTextMessage(
                        workspaceContext.getGroupId(), "Owner post for thread feed (via API)", ws));
        long threadGroupId = workspaceContext.getOwnerBuilder().createCommentGroup(
                workspaceContext.getGroupId(), Long.parseLong(workspaceOwnerPostMessageId));
        for (int i = 1; i <= 25; i++) {
            workspaceContext.getOwnerBuilder().sendTextMessage(threadGroupId, String.valueOf(i), ws);
        }

        loginHelper.performUILogin(homespaceContext.getOwner());
        pages.chatListSteps.clickChatByName(homespaceContext.getGroupName());
        pages.chatActions.findMessageById(homespaceOwnerPostMessageId);
        pages.chatActions.openMessageContextMenuById(homespaceOwnerPostMessageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.chatActions.textMessageSpam(25);
        pages.chatActions.scrollChatUp(1000);
        pages.chatActions.clickScrollDownButtonAndVerifyScrolledDown();

        loginHelper.performUILoginForTest(workspaceContext.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(workspaceContext.getWorkspaceName());
        pages.chatListSteps.clickChatByName(workspaceContext.getGroupName());
        pages.chatActions.findMessageById(workspaceOwnerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(workspaceOwnerPostMessageId);
        pages.chatActions.textMessageSpam(25);
        pages.chatActions.scrollChatUp(1000);
        pages.chatActions.clickScrollDownButtonAndVerifyScrolledDown();
    }

    @ChannelAllureIds(web = "57151", desktop = "57807")
    @Test
    @DisplayName("В ленте Комментариев много сообщений. Нажать на закрепленный пост")
    public void commentFeedClickPinnedThreadMessage() {
        String postMessageText = "Owner post for thread feed pinned navigation (via API)";

        GroupContext homespaceContext = groupFactory.privateGroup();
        String homespaceOwnerPostMessageId = String.valueOf(
                homespaceContext.getOwnerBuilder().sendTextMessage(
                        homespaceContext.getGroupId(), postMessageText));

        GroupContext workspaceContext = groupFactory.privateGroupInWs();
        String ws = String.valueOf(workspaceContext.getWorkspaceId());
        String workspaceOwnerPostMessageId = String.valueOf(
                workspaceContext.getOwnerBuilder().sendTextMessage(
                        workspaceContext.getGroupId(), postMessageText, ws));
        long threadGroupId = workspaceContext.getOwnerBuilder().createCommentGroup(
                workspaceContext.getGroupId(), Long.parseLong(workspaceOwnerPostMessageId));
        for (int i = 1; i <= 25; i++) {
            workspaceContext.getOwnerBuilder().sendTextMessage(threadGroupId, String.valueOf(i), ws);
        }

        loginHelper.performUILogin(homespaceContext.getOwner());
        pages.chatListSteps.clickChatByName(homespaceContext.getGroupName());
        pages.chatActions.findMessageById(homespaceOwnerPostMessageId);
        pages.chatActions.openMessageContextMenuById(homespaceOwnerPostMessageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.chatActions.sendTextMessagesUntilCommentedPostHeaderAppears(30, postMessageText);
        pages.chatActions.clickCommentedPostHeaderAndVerifyNavigatedToOriginalPost(
                workspaceOwnerPostMessageId, postMessageText);
        pages.chatActions.sendTextMessagesUntilCommentedPostHeaderAppears(30, postMessageText);
        pages.chatActions.clickCommentedPostHeaderAndVerifyNavigatedToOriginalPost(homespaceOwnerPostMessageId);

        loginHelper.performUILoginForTest(workspaceContext.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(workspaceContext.getWorkspaceName());
        pages.chatListSteps.clickChatByName(workspaceContext.getGroupName());
        pages.chatActions.findMessageById(workspaceOwnerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(workspaceOwnerPostMessageId);
        pages.chatActions.clickCommentedPostHeaderAndVerifyNavigatedToOriginalPost(workspaceOwnerPostMessageId);
    }

    @ChannelAllureIds(web = "57152", desktop = "57808")
    @Test
    @DisplayName("Первое сообщение в Комментариях написано в тот же день")
    public void firstThreadMessageSentToday() {
        GroupContext context = groupFactory.privateGroup();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Thread feed: first message sent today (via API)"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.chatActions.openMessageContextMenuById(ownerPostMessageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.commentsSteps.verifyNoCommentsYetSystemMessage();

        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("1");
        pages.inputBarSteps.clickSendMessageButton();

        pages.commentsSteps.verifyNoCommentsYetSystemMessageNotVisible();
        pages.commentsSteps.verifyThreadStartSystemMessageVisible();
        pages.commentsSteps.verifyTodayDateSystemMessageVisible();
    }

    @ChannelAllureIds(web = "57150", desktop = "57806")
    @Test
    @DisplayName("У сообщения нет Комментариев")
    public void threadHasNoMessages() {
        GroupContext context = groupFactory.privateGroup();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Thread feed: message with no comments (via API)"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.chatActions.openMessageContextMenuById(ownerPostMessageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.commentsSteps.verifyNoCommentsYetSystemMessage();
    }

    //Мультиселект в тредах
    @ChannelAllureIds(web = "57207", desktop = "57829")
    @Test
    @DisplayName("Мультиселект в Комментариях. Выход из режима мультиселекта")
    public void exitFromThreadMultiselectMode() {
        GroupContext context = groupFactory.privateGroup();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));
        long threadGroupId = context.getOwnerBuilder().createCommentGroup(
                context.getGroupId(), Long.parseLong(ownerPostMessageId));
        String ownerCommentId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(threadGroupId, "Owner comment via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(ownerCommentId);
        pages.chatActions.openMessageContextMenuById(ownerCommentId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.clickCancelButton();
    }

    @ChannelAllureIds(web = "57206", desktop = "57828") //todo: остановился в четверг здесь
    @Test
    @DisplayName("Мультиселект. Владелец/Админ. Свои и чужие комментарии")
    public void threadMultiselect_OwnerAdmin_OwnAndPeerMessages() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));
        long threadGroupId = context.getOwnerBuilder().createCommentGroup(
                context.getGroupId(), Long.parseLong(ownerPostMessageId));
        String ownerCommentId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(threadGroupId, "Owner comment via API"));
        String adminCommentId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(threadGroupId, "Admin comment via API"));
        String participantCommentId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(threadGroupId, "Participant comment via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(ownerCommentId);
        pages.chatActions.openMessageContextMenuById(ownerCommentId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.clickCommentByIdInMultiselectMode(adminCommentId);
        pages.multiselectMessagesSteps.clickCommentByIdInMultiselectMode(participantCommentId);
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailableWithDisabledPin();

        loginHelper.performUILoginForTest(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(ownerCommentId);
        pages.chatActions.openMessageContextMenuById(ownerCommentId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.clickCommentByIdInMultiselectMode(adminCommentId);
        pages.multiselectMessagesSteps.clickCommentByIdInMultiselectMode(participantCommentId);
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailableWithDisabledPin();
    }

    @ChannelAllureIds(web = "57200", desktop = "57823")
    @Test
    @DisplayName("Мультиселект. Владелец/Админ. Только свои комментарии")
    public void threadMultiselect_OwnerAdmin_OwnMessages() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));
        long threadGroupId = context.getOwnerBuilder().createCommentGroup(
                context.getGroupId(), Long.parseLong(ownerPostMessageId));
        String ownerCommentId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(threadGroupId, "Owner comment via API"));
        String adminCommentId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(threadGroupId, "Admin comment via API"));
        context.getParticipantBuilder().sendTextMessage(threadGroupId, "Participant comment via API");

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(ownerCommentId);
        pages.chatActions.openMessageContextMenuById(ownerCommentId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailableWithDisabledPin();

        loginHelper.performUILoginForTest(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(adminCommentId);
        pages.chatActions.openMessageContextMenuById(adminCommentId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailableWithDisabledPin();
    }
    @ChannelAllureIds(web = "57198", desktop = "57822")
    @Test
    @DisplayName("Мультиселект. Владелец/Админ. Только чужие комментарии")
    public void threadMultiselect_OwnerAdmin_PeerMessages() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));
        long threadGroupId = context.getOwnerBuilder().createCommentGroup(
                context.getGroupId(), Long.parseLong(ownerPostMessageId));
        context.getOwnerBuilder().sendTextMessage(threadGroupId, "Owner comment via API");
        context.getAdminBuilder().sendTextMessage(threadGroupId, "Admin comment via API");
        String participantCommentId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(threadGroupId, "Participant comment via API"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(participantCommentId);
        pages.chatActions.openMessageContextMenuById(participantCommentId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailableWithDisabledPin();

        loginHelper.performUILoginForTest(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(participantCommentId);
        pages.chatActions.openMessageContextMenuById(participantCommentId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailableWithDisabledPin();
    }

    @ChannelAllureIds(web = "57203", desktop = "57826")
    @Test
    @DisplayName("Мультиселект. Участник. Свои и чужие комментарии")
    public void threadMultiselect_Participant_OwnAndPeerMessages() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));
        long threadGroupId = context.getOwnerBuilder().createCommentGroup(
                context.getGroupId(), Long.parseLong(ownerPostMessageId));
        String ownerCommentId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(threadGroupId, "Owner comment via API"));
        String adminCommentId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(threadGroupId, "Admin comment via API"));
        String participantCommentId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(threadGroupId, "Participant comment via API"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(ownerCommentId);
        pages.chatActions.openMessageContextMenuById(ownerCommentId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.clickCommentByIdInMultiselectMode(adminCommentId);
        pages.multiselectMessagesSteps.clickCommentByIdInMultiselectMode(participantCommentId);
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailableWithDisabledPin();
    }

    @ChannelAllureIds(web = "57201", desktop = "57824")
    @Test
    @DisplayName("Мультиселект. Участник. Только свои комментарии")
    public void threadMultiselect_Participant_OwnMessages() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));
        long threadGroupId = context.getOwnerBuilder().createCommentGroup(
                context.getGroupId(), Long.parseLong(ownerPostMessageId));
        context.getOwnerBuilder().sendTextMessage(threadGroupId, "Owner comment via API");
        context.getAdminBuilder().sendTextMessage(threadGroupId, "Admin comment via API");
        String participantCommentId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(threadGroupId, "Participant comment via API"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(participantCommentId);
        pages.chatActions.openMessageContextMenuById(participantCommentId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailableWithDisabledPin();
    }

    @ChannelAllureIds(web = "57204", desktop = "57827")
    @Test
    @DisplayName("Мультиселект. Участник. Только чужие комментарии")
    public void threadMultiselect_Participant_PeerMessages() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner text via API"));
        long threadGroupId = context.getOwnerBuilder().createCommentGroup(
                context.getGroupId(), Long.parseLong(ownerPostMessageId));
        String ownerCommentId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(threadGroupId, "Owner comment via API"));
        String adminCommentId = String.valueOf(
                context.getAdminBuilder().sendTextMessage(threadGroupId, "Admin comment via API"));
        context.getParticipantBuilder().sendTextMessage(threadGroupId, "Participant comment via API");

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(ownerPostMessageId);
        pages.chatActions.findMessageById(ownerCommentId);
        pages.chatActions.openMessageContextMenuById(ownerCommentId);
        pages.messageBubbleContextMenu.clickSelectButton();
        pages.multiselectMessagesSteps.clickCommentByIdInMultiselectMode(adminCommentId);
        pages.multiselectMessagesSteps.FullMultiselectMenuAvailableWithDisabledPinAndDelete();
    }

    //Настройки
    @ChannelAllureIds(web = "57141", desktop = "57797")
    @Test
    @DisplayName("(Владелец/Админ) Включенный фича флаг - отображается элемент 'Разрешить комментарии' на экране редактирования группы")
    public void threadSettings_EditGroup() {
        GroupContext context = groupFactory.privateGroup();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread settings (via API)"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickSettingsButton();
        pages.groupAndChannelEditModalSteps.verifyAllowCommentsToggleOnInGroup();
    }

    @ChannelAllureIds(web = "57142", desktop = "57798")
    @Test
    @DisplayName("Создание группы с запрещенными комментариями")
    public void createGroupWithThreadsDisabled() {
        GroupContext context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.homespaceSteps.clickCreateNewChatButton();
        pages.newMessageModalSteps.clickCreateGroupButton();
        pages.newGroupModalSteps.verifyAllowCommentsToggleEnabled();
        pages.newGroupModalSteps.clickAllowCommentsToggle();
        pages.newGroupModalSteps.createHomespaceGroup("ManuallyCreatedGroupForThreadsTesting");
        pages.addPeopleListSteps.clickAddUsersButton();
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickSettingsButton();
        pages.groupAndChannelEditModalSteps.verifyAllowCommentsToggleOffInGroup();
    }

    //Создание треда
    @ChannelAllureIds(web = "57758", desktop = "57784")
    @Test
    @DisplayName("В чате Главный Комментарии по умолчанию включены")
    public void mainChatThreadsAvailableByDefault() {
        WorkspaceContext workspace = workspaceFactory.createWorkspace();

        loginHelper.performUILogin(workspace.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(workspace.getWsName());
        pages.chatListSteps.clickMainChat();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("TextMessageForThread");
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatActions.findMessageByText("TextMessageForThread");
        pages.chatActions.openMessageContextMenu("TextMessageForThread");
        pages.messageBubbleContextMenu.verifyCommentButtonPresent();
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.commentsSteps.verifyNoCommentsYetSystemMessage();
    }

    @Disabled
    @ChannelAllureIds(web = "57762", desktop = "57788")
    @Test
    @DisplayName("Создание Комментария для аудио файла")
    public void createThreadForAudioMessage() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String messageId = String.valueOf(context.getOwnerBuilder().sendAudio(context.getGroupId(), ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("ThreadMessageForAudio");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent("ThreadMessageForAudio");
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(messageId);
    }

    @ChannelAllureIds(web = "57751", desktop = "57777")
    @Test
    @DisplayName("Создание Комментария для видеофайла")
    public void createThreadForVideoMessage() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String messageId = String.valueOf(context.getOwnerBuilder().sendVideo(context.getGroupId(), ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("ThreadMessageForVideo");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent("ThreadMessageForVideo");
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(messageId);
    }

    @ChannelAllureIds(web = "57752", desktop = "54596")
    @Test
    @DisplayName("Создание Комментария для изображения")
    public void createThreadForImageMessage() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String messageId = String.valueOf(context.getAdminBuilder().sendImage(context.getGroupId(), ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("ThreadMessageForImage");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent("ThreadMessageForImage");
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(messageId);
    }

    @ChannelAllureIds(web = "57756", desktop = "57782")
    @Test
    @DisplayName("Создание Комментария для изображения + текст")
    public void createThreadForImageAndTextMessage() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        // API не поддерживает составные сообщения (sendMultimedia) — пока используем одиночные медиа как fallback
        String messageId = String.valueOf(context.getOwnerBuilder().sendImage(context.getGroupId(), ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("ThreadMessageForImage+Text");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent("ThreadMessageForImage+Text");
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(messageId);
    }

    @ChannelAllureIds(web = "57761", desktop = "57787")
    @Test
    @DisplayName("Создание Комментария для карточки контакта")
    public void createThreadForContactUserCard() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs()));
        context.getOwnerBuilder().addContacts(context.getParticipant());
        String ws = String.valueOf(context.getWorkspaceId());
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendContact(context.getGroupId(), context.getParticipant().getId(), ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        String contactName = context.getParticipant().getName();
        pages.chatActions.sendTextMessagesUntilCommentedPostHeaderAppears(30, contactName);
        pages.chatActions.verifyCommentedPostHeaderDisplays(context.getOwner().getName(), contactName);
        pages.chatActions.clickCommentedPostHeaderAndVerifyNavigatedToOriginalPost(messageId, contactName);
        pages.commentsSteps.clickCommentButtonUnderBubbleById(messageId);
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("ThreadMessageForContactCard");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent("ThreadMessageForContactCard");
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(messageId);
    }

    @ChannelAllureIds(web = "57764", desktop = "57790")
    @Test
    @DisplayName("Создание Комментария для карточки неконтакта")
    public void createThreadForNonContactUserCard() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendContact(context.getGroupId(), context.getAdmin().getId(), ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("ThreadMessageForNonContactCard");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent("ThreadMessageForNonContactCard");
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(messageId);
    }

    @ChannelAllureIds(web = "57760", desktop = "57786")
    @Test
    @DisplayName("Создание Комментария для ссылки")
    public void createThreadForLinkMessage() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "https://google.com", ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("ThreadMessageForLink");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent("ThreadMessageForLink");
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(messageId);
    }

    @ChannelAllureIds(web = "57753", desktop = "57779")
    @Test
    @DisplayName("Создание Комментария для стека видео файлов")
    public void createThreadForVideoFileStack() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String messageId = String.valueOf(context.getOwnerBuilder().sendVideo(context.getGroupId(), ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("ThreadMessageForVideoStack");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent("ThreadMessageForVideoStack");
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(messageId);
    }

    @ChannelAllureIds(web = "57754", desktop = "57780")
    @Test
    @DisplayName("Создание Комментария для стека изображений")
    public void createThreadForImageFileStack() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String messageId = String.valueOf(context.getOwnerBuilder().sendImage(context.getGroupId(), ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("ThreadMessageForImageStack");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent("ThreadMessageForImageStack");
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(messageId);
    }

    @ChannelAllureIds(web = "57766", desktop = "57792")
    @Test
    @DisplayName("Создание Комментария для стека изображений/видео + текст(clone)")
    public void createThreadForImageAndVideoFilesStack() {
        GroupContext context = groupFactory.privateGroupInWs();
        String ws = String.valueOf(context.getWorkspaceId());
        String postMessageText = "Image+video+text placeholder";
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendVideoWithMessage(context.getGroupId(), postMessageText, ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.chatActions.sendImagesUntilCommentedPostHeaderAppears(30, postMessageText);
        pages.chatActions.verifyCommentedPostHeaderDisplays(context.getOwner().getName(), postMessageText);
    }

    @ChannelAllureIds(web = "57755", desktop = "57781")
    @Test
    @DisplayName("Создание Комментария для текстового сообщения + видео")
    public void createThreadForTextAndVideoMessage() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendVideoWithMessage(context.getGroupId(), "Text with video", ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("ThreadMessageForVideo+Text");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent("ThreadMessageForVideo+Text");
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(messageId);
    }

    @ChannelAllureIds(web = "57759", desktop = "57785")
    @Test
    @DisplayName("Создание Комментария для текстового сообщения + файл")
    public void createThreadForTextAndFileMessage() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendFileWithMessage(context.getGroupId(), "Text with file", ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("ThreadMessageForFile+Text");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent("ThreadMessageForFile+Text");
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(messageId);
    }

    @ChannelAllureIds(web = "57757", desktop = "57783")
    @Test
    @DisplayName("Создание Комментария для файла")
    public void createThreadForFileMessage() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String messageId = String.valueOf(context.getParticipantBuilder().sendFile(context.getGroupId(), ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("ThreadMessageForFile");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent("ThreadMessageForFile");
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(messageId);
    }

    @ChannelAllureIds(web = "57767", desktop = "57793")
    @Test
    @DisplayName("Создание Комментария для чужого сообщения")
    public void createThreadForPeerTextMessage() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String messageId = String.valueOf(
                context.getParticipantBuilder().sendTextMessage(context.getGroupId(), "Participant text message (workspace API)", ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("ThreadMessageForPeerText");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent("ThreadMessageForPeerText");
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(messageId);
    }

    @ChannelAllureIds(web = "57763", desktop = "57789")
    //todo: здесь нет отправки голосового, добавить
    @Test
    @DisplayName("Создание треда для голосового сообщения -> создается")
    public void createThreadForVoiceMessage() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs()));
        String ws = String.valueOf(context.getWorkspaceId());
        String messageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "Owner post for thread creation (via API)", ws));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("ThreadMessageForVoice");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent("ThreadMessageForVoice");
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(messageId);
    }

    @ChannelAllureIds(web = "57765", desktop = "57791")
    @Test
    @DisplayName("Создание треда для опроса -> создается")
    public void createThreadForPoll() {
        GroupContext context = groupFactory.withParticipant(
                groupFactory.withWorkspaceAndChatAdmin(groupFactory.privateGroupInWs()));
        context = pollFactory.withPoll(
                context,
                PollConfig.of(10001L, "Thread creation poll", "Option 1", "Option 2", 2678400000L, false),
                context.getAdmin());
        String messageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(messageId);
        pages.chatActions.openMessageContextMenuById(messageId);
        pages.messageBubbleContextMenu.clickCommentButton();
        pages.inputBarSteps.clickMessageInputField();
        pages.inputBarSteps.enterMessage("ThreadMessageForPoll");
        pages.inputBarSteps.clickSendMessageButton();
        pages.commentsSteps.verifyCommentSent("ThreadMessageForPoll");
        pages.commentsSteps.clickBackFromCommentsButton();
        pages.commentsSteps.verifyCommentButtonPresentUnderBubbleById(messageId);
    }

    //Сообщения в треде
    @Disabled
    @ChannelAllureIds(web = "57160", desktop = "57816")
    @Test
    @DisplayName("Не отправленное сообщение в Комментариях. Черновик")
    public void draftThreadMessage() {
        GroupContext context = groupFactory.privateGroup();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread messages (via API)"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
    }

    @Disabled
    @ChannelAllureIds(web = "57158", desktop = "57814")
    @Test
    @DisplayName("Отправка нетекстового сообщения в Комментарии")
    public void threadNoTextMessage() {
        GroupContext context = groupFactory.privateGroup();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread messages (via API)"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
    }

    @Disabled
    @ChannelAllureIds(web = "57157", desktop = "57813")
    @Test
    @DisplayName("Отправка сообщения в Комментариях НЕучастником группы")
    public void noparticipantSendThreadMessage() {
        GroupContext context = groupFactory.withNonParticipant(groupFactory.privateGroupWithAdminAndParticipant());
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread messages (via API)"));
        String inviteUrl = UrlConstants.getHomespaceGroupInviteUrl(
                context.getOwnerBuilder().regenerateInviteLink(context.getGroupId()));

        loginHelper.performUILogin(context.getNonParticipant());
        Selenide.open(Objects.requireNonNull(inviteUrl));
        pages.topbarSteps.verifyChatName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
    }

    @Disabled
    @ChannelAllureIds(web = "57155", desktop = "57811")
    @Test
    @DisplayName("Отправка сообщения в Комментариях участником группы")
    public void participantSendThreadMessage() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread messages (via API)"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
    }

    @Disabled
    @ChannelAllureIds(web = "57156", desktop = "57812")
    @Test
    @DisplayName("Просмотр Комментариев НЕучастником группы")
    public void noparticipantBrowseThreadMessages() {
        GroupContext context = groupFactory.withNonParticipant(groupFactory.privateGroupWithAdminAndParticipant());
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread messages (via API)"));
        String inviteUrl = UrlConstants.getHomespaceGroupInviteUrl(
                context.getOwnerBuilder().regenerateInviteLink(context.getGroupId()));

        loginHelper.performUILogin(context.getNonParticipant());
        Selenide.open(Objects.requireNonNull(inviteUrl));
        pages.topbarSteps.verifyChatName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
    }

    @Disabled
    @ChannelAllureIds(web = "57159", desktop = "57815")
    @Test
    @DisplayName("Упоминания (меншен) в Комментариях")
    public void threadMentionsAreNotAllowed() {
        GroupContext context = groupFactory.privateGroup();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread messages (via API)"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
    }

    //Удаление сообщений в комментариях
    @Disabled
    @ChannelAllureIds(web = "57745", desktop = "57772")
    @Test
    @DisplayName("Удаление через контекстное меню. Владелец/Админ")
    public void deleteThreadMessageViaContextMenu_OwnerAdmin() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread deletion (via API)"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
    }

    @Disabled
    @ChannelAllureIds(web = "57746", desktop = "57773")
    @Test
    @DisplayName("Удаление через контекстное меню. Участник. Свое сообщение")
    public void deleteThreadMessageViaContextMenu_Participant_OwnMessage() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread deletion (via API)"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
    }

    @Disabled
    @ChannelAllureIds(web = "57748", desktop = "57775")
    @Test
    @DisplayName("Удаление через контекстное меню. Участник. Чужое сообщение")
    public void deleteThreadMessageViaContextMenu_Participant_PeerMessage() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread deletion (via API)"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
    }

    @Disabled
    @ChannelAllureIds(web = "57747", desktop = "57774")
    @Test
    @DisplayName("Удаление через мультиселект. Владелец/Админ")
    public void deleteThreadMessageViaMultiselect_OwnerAdmin() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread deletion (via API)"));

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
    }

    @Disabled
    @ChannelAllureIds(web = "57749", desktop = "57776")
    @Test
    @DisplayName("Удаление через мультиселект. Участник")
    public void deleteThreadMessageViaMultiselect_Participant() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread deletion (via API)"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
    }

    //Управление тредами
    @Disabled
    @ChannelAllureIds(web = "57147", desktop = "57803")
    @Test
    @DisplayName("Включить комментарии (отображение в ленте у себя) > В контекстном меню сообщения появился пункт 'Прокомментировать'")
    public void enableThreadsInFeedAndBubble() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread management (via API)"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
    }

    @Disabled
    @ChannelAllureIds(web = "57148", desktop = "57804")
    @Test
    @DisplayName("Добавить Комментарий в сообщение где уже есть комментарии (комментарии включены)")
    public void addThreadMessageToDiscussion() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread management (via API)"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
    }

    @Disabled
    @ChannelAllureIds(web = "57149", desktop = "57805")
    @Test
    @DisplayName(" Добавить Комментарий в сообщение где уже есть комментарии (Комментарии выключены)")
    public void addThreadMessageToDiscussion_ThreadOff() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread management (via API)"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
    }

    @Disabled
    @ChannelAllureIds(web = "57146", desktop = "57802")
    @Test
    @DisplayName("Отключить комментарии (отображение в ленте у себя) > В контекстном меню сообщения отсутствует пункт 'Прокомментировать'")
    public void disableThreadsInFeedAndBubble() {
        GroupContext context = groupFactory.privateGroupWithAdminAndParticipant();
        String ownerPostMessageId = String.valueOf(
                context.getOwnerBuilder().sendTextMessage(
                        context.getGroupId(), "Owner post for thread management (via API)"));

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.findMessageById(ownerPostMessageId);
    }
}
