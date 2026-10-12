package org.foxtrot.test.chats_and_spaces.delete_workspace.reaction_on_deletion;

import org.foxtrot.allure.ChannelAllureIds;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.WorkspaceContext;
import org.foxtrot.utils.factories.core.WorkspaceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpaceMenuTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();

    private WorkspaceContext context;

    @AfterEach
    public void tearDown() {
        workspaceFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "57136", desktop = "57729")
    @DisplayName("Удаление пространства в момент 'Входа' в удаляемое пространство в списке доступных")
    public void deleteWorkspaceWhileJoiningIt() {
        context = prepareWorkspaceWithChatHistory(true);

        loginHelper.performUILogin(context.getParticipant());
        context.getParticipantBuilder().deactivateAllSpaces();

        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        context.getOwnerBuilder().deleteWorkspace(context.getWsId());
        pages.availableWorkspacesModalSteps.enterWorkspaceByName(context.getWsName());

        pages.diveIntoChatViaUrlLinkSteps.verifyAccessRestrictionMessageVisible();
        pages.availableWorkspacesModalSteps.verifySpaceNotAvailableByName(context.getWsName())
                .closeAvailableWorkspacesModal();

        pages.leftSidebarSteps.verifySpaceNotAvailableByName(context.getWsName());

        pages.chatListSteps.clickSavedChat();
        pages.chatActions.openMessageContextMenu("test");
        pages.messageBubbleContextMenu.clickForwardButton();
        pages.forwardMenuSteps
                .openForwardSpacesList()
                .verifySpaceNotInForwardList(context.getWsName());
    }

    @Test
    @ChannelAllureIds(web = "57137", desktop = "57736")
    @DisplayName("Удаление пространства в момент 'Выхода' из удаляемого пространства в списке доступных")
    public void deleteWorkspaceWhileExiting() {
        context = prepareWorkspaceWithChatHistory(true);

        loginHelper.performUILogin(context.getParticipant());

        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        context.getOwnerBuilder().deleteWorkspace(context.getWsId());

        pages.availableWorkspacesModalSteps.verifySpaceNotAvailableByName(context.getWsName());
    }

    @Test
    @ChannelAllureIds(web = "57133", desktop = "57735")
    @DisplayName("Удаление пространства в момент нахождения в списке участников пространства")
    public void deleteWorkspaceInMembersList() {
        context = prepareWorkspaceWithChatHistory(true);

        loginHelper.performUILogin(context.getParticipant());

        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.checkUserPresenceInSpace(context.getParticipant().getName());

        verifyPostDeletionState(context);
    }

    @Test
    @ChannelAllureIds(web = "57132", desktop = "57734")
    @DisplayName("Удаление пространства в момент нахождения в списке чатов пространства")
    public void deleteWorkspaceInChatList() {
        context = prepareWorkspaceWithChatHistory(true);

        loginHelper.performUILogin(context.getParticipant());

        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.verifyChatIsDisplayed("Главный");

        verifyPostDeletionState(context);
    }

    @Test
    @ChannelAllureIds(web = "57138", desktop = "57733")
    @DisplayName("Удаление пространства в момент нахождения на экране 'Редактирование пространства'")
    public void deleteWorkspaceInSettings() {
        context = prepareWorkspaceWithChatHistory(false);

        loginHelper.performUILogin(context.getOwner());

        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.editButton();

        verifyPostDeletionState(context);
    }

    private void verifyPostDeletionState(WorkspaceContext context) {
        context.getOwnerBuilder().deleteWorkspace(context.getWsId());
        pages.workspaceDeletedNotificationModalSteps
                .verifyWorkspaceDeletedModalIsVisible()
                .clickConfirmButton();

        pages.leftSidebarSteps
                .verifySpaceNotAvailableByName(context.getWsName())
                .enterOrCreateSpaceButton();

        pages.availableWorkspacesModalSteps
                .verifySpaceNotAvailableByName(context.getWsName())
                .closeAvailableWorkspacesModal();

        pages.chatListSteps.clickSavedChat();
        pages.chatActions.openMessageContextMenu("test");
        pages.messageBubbleContextMenu
                .clickForwardButton();

        pages.forwardMenuSteps.openForwardSpacesList()
                .verifySpaceNotInForwardList(context.getWsName());
    }

    private WorkspaceContext prepareWorkspaceWithChatHistory(boolean withParticipant) {
        var context = withParticipant ? workspaceFactory.createWorkspaceWithParticipant() : workspaceFactory.createWorkspace();
        var user = withParticipant ? context.getParticipant() : context.getOwner();
        var builder = withParticipant ? context.getParticipantBuilder() : context.getOwnerBuilder();

        builder.sendTextMessage(user.getSavedChatId(), "test");

        return context;
    }
}