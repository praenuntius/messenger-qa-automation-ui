package org.foxtrot.test.chats_and_spaces;

import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.WorkspaceContext;
import org.foxtrot.utils.factories.core.UserFactory;
import org.foxtrot.utils.factories.core.WorkspaceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ActiveSpacesSessionTest extends BaseTest {

    private static final String TEST_MESSAGE = "active spaces relogin test message";

    private final LoginHelper loginHelper = new LoginHelper();
    private final UserFactory userFactory = new UserFactory();
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();

    @AfterEach
    public void tearDown() {
        workspaceFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "58946", desktop = "58980")
    @DisplayName("Активные пространства отображены после релогина (левый сайдбар, список доступных пространств, меню форварда)")
    public void activeSpacesAreDisplayedAfterRelogin() {
        List<WorkspaceContext> workspaces = workspaceFactory.createMultipleWorkspaces(2);
        UsersEnum user = workspaces.get(0).getOwner();
        workspaces.get(0).getOwnerBuilder().sendTextMessage(user.getSavedChatId(), TEST_MESSAGE);

        loginHelper.performUILogin(user);
        verifyActiveSpacesDisplayed(workspaces.get(0).getWsName(), workspaces.get(1).getWsName());

        loginHelper.performUILoginForTest(user);
        verifyActiveSpacesDisplayed(workspaces.get(0).getWsName(), workspaces.get(1).getWsName());

        pages.chatListSteps.clickSavedChat();
        pages.chatActions.openMessageContextMenu(TEST_MESSAGE);
        pages.messageBubbleContextMenu.clickForwardButton();
        pages.forwardMenuSteps.verifyForwardMenuAvailable()
                .openForwardSpacesList()
                .verifySpaceInForwardList(workspaces.get(0).getWsName())
                .verifySpaceInForwardList(workspaces.get(1).getWsName());

    }

    @Test
    @ChannelAllureIds(web = "58949", desktop = "58983")
    @DisplayName("Владелец: Активные и неактивные пространства отображены после релогина в соответствующем порядке")
    public void activeAndInactiveSpacesAreDisplayedInCorrectOrderAfterRelogin() {
        List<WorkspaceContext> workspaces = workspaceFactory.createMultipleWorkspaces(3);
        UsersEnum user = workspaces.get(0).getOwner();
        String firstActiveWorkspace = workspaces.get(0).getWsName();
        String secondActiveWorkspace = workspaces.get(1).getWsName();
        String inactiveWorkspace = workspaces.get(2).getWsName();

        loginHelper.performUILogin(user);
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps
                .exitingWorkspacesByNames(inactiveWorkspace)
                .closeAvailableWorkspacesModal();

        loginHelper.performUILoginForTest(user);
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps
                .isActiveSpaceAvailableByName(firstActiveWorkspace)
                .isActiveSpaceAvailableByName(secondActiveWorkspace)
                .isInactiveSpaceAvailableByName(inactiveWorkspace)
                .verifyActiveWorkspacesDisplayedBeforeInactiveWorkspaces(
                        List.of(firstActiveWorkspace, secondActiveWorkspace),
                        List.of(inactiveWorkspace));
    }

    @Test
    @ChannelAllureIds(web = "58952", desktop = "58986")
    @DisplayName("Владелец: Логаут -> Добавлен в спейс - Спейс отображается активным")
    public void workspaceIsDisplayedActiveWhenUserWasAddedAfterLogout() {
        var workspace = workspaceFactory.createWorkspace();
        var addedUser = userFactory.takeUser();

        loginHelper.performUILogin(addedUser);
        pages.leftSidebarSteps.verifySpaceNotAvailableByName(workspace.getWsName())
                .enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps
                .verifySpaceNotAvailableByName(workspace.getWsName())
                .closeAvailableWorkspacesModal();

        loginHelper.performUILoginForTest(workspace.getOwner());
        workspace.getOwnerBuilder().addUsersToWorkspace(workspace.getWsId(), addedUser);

        loginHelper.performUILoginForTest(addedUser);
        verifyActiveSpacesDisplayed(workspace.getWsName());
    }

    @Test
    @ChannelAllureIds(web = "58951", desktop = "58985")
    @DisplayName("Владелец: Удаленный спейс не отображается в списке активных после релогина")
    public void deletedWorkspaceIsNotDisplayedInActiveListAfterRelogin() {
        var workspace = workspaceFactory.createWorkspace();
        var user = workspace.getOwner();

        loginHelper.performUILogin(user);
        pages.leftSidebarSteps.isSpaceAvailableByName(workspace.getWsName());

        workspace.getOwnerBuilder().deleteWorkspace(workspace.getWsId());
        pages.leftSidebarSteps.verifySpaceNotAvailableByName(workspace.getWsName());

        loginHelper.performUILoginForTest(user);
        pages.leftSidebarSteps.verifySpaceNotAvailableByName(workspace.getWsName());
    }

    @Test
    @ChannelAllureIds(web = "58947", desktop = "58981")
    @DisplayName("Вышел из пространств -> Пространства не отображаются активными после релогина")
    public void exitedWorkspacesAreNotDisplayedActiveAfterRelogin() {
        List<WorkspaceContext> workspaces = workspaceFactory.createMultipleWorkspaces(2);
        var user = workspaces.get(0).getOwner();
        String firstWorkspace = workspaces.get(0).getWsName();
        String secondWorkspace = workspaces.get(1).getWsName();

        loginHelper.performUILogin(user);
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps
                .exitingWorkspacesByNames(firstWorkspace, secondWorkspace)
                .closeAvailableWorkspacesModal();

        loginHelper.performUILoginForTest(user);
        pages.leftSidebarSteps
                .verifySpaceNotAvailableByName(firstWorkspace)
                .verifySpaceNotAvailableByName(secondWorkspace)
                .enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps
                .isInactiveSpaceAvailableByName(firstWorkspace)
                .isInactiveSpaceAvailableByName(secondWorkspace)
                .closeAvailableWorkspacesModal();
    }

    private void verifyActiveSpacesDisplayed(String... workspaceNames) {
        for (String workspaceName : workspaceNames) {
            pages.leftSidebarSteps.isSpaceAvailableByName(workspaceName);
        }

        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        for (String workspaceName : workspaceNames) {
            pages.availableWorkspacesModalSteps.isActiveSpaceAvailableByName(workspaceName);
        }
        pages.availableWorkspacesModalSteps.closeAvailableWorkspacesModal();
    }
}