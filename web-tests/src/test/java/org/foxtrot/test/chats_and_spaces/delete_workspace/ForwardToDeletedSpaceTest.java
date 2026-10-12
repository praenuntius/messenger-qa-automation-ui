package org.foxtrot.test.chats_and_spaces.delete_workspace;

import org.foxtrot.allure.ChannelAllureIds;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.UserTestDataCleaner;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.foxtrot.utils.TestDataBuilder.GroupConfig.ofGroup;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ForwardToDeletedSpaceTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    private GroupContext context;

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "57405", desktop = "57702")
    @DisplayName("Удаление пространства в момент форварда в ГРУППУ удаляемого пространства")
    public void deleteSpaceWhileForwardingToGroup() {
        context = groupFactory.privateGroupInWsWithParticipant();
        Long sourceMsgId = prepareSourceChat(context);

        loginHelper.performUILogin(context.getParticipant());

        openMessageContextMenu(sourceMsgId, "name");
        pages.messageBubbleContextMenu.clickForwardButton();
        pages.forwardMenuSteps
                .openForwardSpacesList()
                .clickSpaceByName(context.getWorkspaceName())
                .selectChatForForward(context.getGroupName());

        context.getOwnerBuilder().deleteWorkspace(context.getWorkspaceId());

        pages.inputBarSteps.clickSendMessageButton();

        pages.diveIntoChatViaUrlLinkSteps
                .verifyAccessRestrictionMessageVisible()
                .clickCloseButton();

        pages.leftSidebarSteps.verifySpaceIsActive("Чаты");
    }

    @Test
    @ChannelAllureIds(web = "57406", desktop = "57703")
    @DisplayName("Удаление пространства в момент форварда в КАНАЛ удаляемого пространства")
    public void deleteSpaceWhileForwardingToChannel() {
        context = groupFactory.privateChannelInWsWithParticipant();
        Long sourceMsgId = prepareSourceChat(context);

        loginHelper.performUILogin(context.getOwner());

        openMessageContextMenu(sourceMsgId, "name");
        pages.messageBubbleContextMenu.clickForwardButton();
        pages.forwardMenuSteps
                .openForwardSpacesList()
                .clickSpaceByName(context.getWorkspaceName())
                .selectChatForForward(context.getGroupName());

        context.getOwnerBuilder().deleteWorkspace(context.getWorkspaceId());

        pages.inputBarSteps.clickSendMessageButton();

        pages.diveIntoChatViaUrlLinkSteps
                .verifyAccessRestrictionMessageVisible()
                .clickCloseButton();

        pages.leftSidebarSteps.verifySpaceIsActive("Чаты");
    }

    @Test
    @ChannelAllureIds(web = "57404", desktop = "57709")
    @DisplayName("Удаление пространства в момент форварда ИЗ КАНАЛА удаляемого пространства")
    public void deleteSpaceOnForwardFromChannel() {
        context = groupFactory.privateChannelInWsWithParticipant();
        var owner = context.getOwnerBuilder();
        Long msgInSpace = owner.sendTextMessage(context.getGroupId(), "тест", context.getWorkspaceId());
        Long msgInOtherChat = prepareSourceChat(context);

        loginHelper.performUILogin(context.getParticipant());

        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        openMessageContextMenu(msgInSpace, context.getGroupName());
        pages.messageBubbleContextMenu.clickForwardButton();
        pages.forwardMenuSteps.openForwardSpacesList();

        owner.deleteWorkspace(context.getWorkspaceId());

        pages.workspaceDeletedNotificationModalSteps
                .verifyWorkspaceDeletedModalIsVisible()
                .clickConfirmButton();

        verifySpaceMissingInUI(context.getWorkspaceName());

        openMessageContextMenu(msgInOtherChat, "name");
        pages.messageBubbleContextMenu.clickForwardButton();
        pages.forwardMenuSteps.openForwardSpacesList()
                .verifySpaceNotInForwardList(context.getWorkspaceName());
    }

    @Test
    @ChannelAllureIds(web = "57403", desktop = "57708")
    @DisplayName("Удаление пространства в момент форварда ИЗ ГРУППЫ удаляемого пространства")
    public void deleteSpaceOnForwardFromGroup() {
        context = groupFactory.privateGroupInWsWithParticipant();
        var owner = context.getOwnerBuilder();
        Long msgInSpace = owner.sendTextMessage(context.getGroupId(), "тест", context.getWorkspaceId());
        Long msgInOtherChat = prepareSourceChat(context);

        loginHelper.performUILogin(context.getParticipant());

        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        openMessageContextMenu(msgInSpace, context.getGroupName());
        pages.messageBubbleContextMenu.clickForwardButton();
        pages.forwardMenuSteps.openForwardSpacesList();

        owner.deleteWorkspace(context.getWorkspaceId());

        pages.workspaceDeletedNotificationModalSteps
                .verifyWorkspaceDeletedModalIsVisible()
                .clickConfirmButton();

        verifySpaceMissingInUI(context.getWorkspaceName());

        openMessageContextMenu(msgInOtherChat, "name");
        pages.messageBubbleContextMenu.clickForwardButton();
        pages.forwardMenuSteps.openForwardSpacesList()
                .verifySpaceNotInForwardList(context.getWorkspaceName());
    }

    private Long prepareSourceChat(GroupContext context) {
        var owner = context.getOwnerBuilder();
        Long groupId = owner.createGroup(ofGroup("name"));
        owner.addUsers(groupId, context.getParticipant());
        return owner.sendTextMessage(groupId, "test");
    }

    private void openMessageContextMenu(Long messageId, String chatName) {
        pages.chatListSteps.clickChatByName(chatName);
        pages.chatActions.openMessageContextMenuById(String.valueOf(messageId));
    }

    private void verifySpaceMissingInUI(String workspaceName) {
        pages.leftSidebarSteps
                .verifySpaceIsActive("Чаты")
                .verifySpaceNotAvailableByName(workspaceName)
                .enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps
                .verifySpaceNotAvailableByName(workspaceName)
                .closeAvailableWorkspacesModal();
    }
}

