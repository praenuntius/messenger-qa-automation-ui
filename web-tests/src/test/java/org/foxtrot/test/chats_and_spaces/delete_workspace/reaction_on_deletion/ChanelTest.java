package org.foxtrot.test.chats_and_spaces.delete_workspace.reaction_on_deletion;

import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.allure.ChannelAllureIds;
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
public class ChanelTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    private GroupContext context;

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "57398", desktop = "57701")
    @DisplayName("Удаление пространства в момент нахождения в канале пространства")
    public void deleteWorkspaceWhileInChannel() {
        context = setupChannelContext();

        loginHelper.performUILogin(context.getParticipant());

        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());

        verifyPostDeletionState(context);
    }

    @Test
    @ChannelAllureIds(web = "57399", desktop = "57704")
    @DisplayName("Удаление пространства в момент нахождения в меню канала пространства")
    public void deleteWorkspaceWhileInChannelMenu() {
        context = setupChannelContext();

        loginHelper.performUILogin(context.getParticipant());

        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuVerifier.verifyParticipantMenu(context.getGroupName(), "Закрытый канал");

        verifyPostDeletionState(context);
    }

    @Test
    @ChannelAllureIds(web = "57400", desktop = "57706")
    @DisplayName("Удаление пространства в момент нахождения на экране комментариев к публикациям")
    public void deleteWorkspaceWhileInGroupCommentMenu() {
        context = groupFactory.privateChannelInWsWithParticipant();
        var owner = context.getOwnerBuilder();

        Long messageId = owner.sendTextMessage(context.getGroupId(), "test", context.getWorkspaceId());
        Long commentGroup = owner.createCommentGroup(context.getGroupId(), messageId);
        owner.sendTextMessage(commentGroup, "test", context.getWorkspaceId());

        context.getParticipantBuilder().sendTextMessage(context.getParticipant().getSavedChatId(), "test");

        loginHelper.performUILogin(context.getParticipant());

        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.openMessageCommentById(messageId);

        pages.chatVerify.verifyMessageText("test");

        verifyPostDeletionState(context);
    }

    @Test
    @ChannelAllureIds(web = "57402", desktop = "57705")
    @DisplayName("Удаление пространства в момент нахождения на экране 'Настроек канала'")
    public void deleteWorkspaceWhileInChannelSettings() {
        context = setupChannelContext();

        loginHelper.performUILogin(context.getOwner());

        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.openChannelSettings();
        pages.groupAndChannelEditModalSteps.verifyChannelSettingsHeaderIsVisible();

        verifyPostDeletionState(context);
    }

    private void verifyPostDeletionState(GroupContext context) {
        context.getOwnerBuilder().deleteWorkspace(context.getWorkspaceId());
        pages.workspaceDeletedNotificationModalSteps
                .verifyWorkspaceDeletedModalIsVisible()
                .clickConfirmButton();

        pages.leftSidebarSteps.verifySpaceNotAvailableByName(context.getWorkspaceName());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps
                .verifySpaceNotAvailableByName(context.getWorkspaceName())
                .closeAvailableWorkspacesModal();

        pages.chatListSteps.clickSavedChat();
        pages.chatActions.openMessageContextMenu("test");
        pages.messageBubbleContextMenu.clickForwardButton();
        pages.forwardMenuSteps
                .openForwardSpacesList()
                .verifySpaceNotInForwardList(context.getWorkspaceName());
    }

    private GroupContext setupChannelContext() {
        var context = groupFactory.privateChannelInWsWithParticipant();
        context.getOwnerBuilder().sendTextMessage(context.getOwner().getSavedChatId(), "test");
        context.getParticipantBuilder().sendTextMessage(context.getParticipant().getSavedChatId(), "test");

        return context;
    }
}