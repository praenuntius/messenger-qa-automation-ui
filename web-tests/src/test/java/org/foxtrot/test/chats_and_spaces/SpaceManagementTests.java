package org.foxtrot.test.chats_and_spaces;

import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.NoRestoreSession;
import org.foxtrot.utils.factories.contexts.WorkspaceContext;
import org.foxtrot.utils.factories.core.WorkspaceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpaceManagementTests extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();

    @AfterEach
    void tearDown() {
        workspaceFactory.cleanUp();
    }

    @ChannelAllureIds(web = "59976", desktop = "96245")
    @Test
    @DisplayName("Удаление пространства владельцем, перманентно")
    @NoRestoreSession
    public void hardRemoveSpaceByOwner() {
        WorkspaceContext context = workspaceFactory.createWorkspace();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.deleteButton();
        pages.deleteWorkspaceModalSteps.hardDeleteWorkspace();
        pages.workspaceSnackBarsSteps.verifyWorkspaceDeletedSnackBarIsVisible();
        pages.leftSidebarSteps.verifySpaceNotAvailableByName(context.getWsName());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.verifySpaceNotAvailableByName(context.getWsName());
    }

    @ChannelAllureIds(web = "59972", desktop = "96248")
    @Test
    @DisplayName("Удаление пространства владельцем, с возможностью восстановления")
    @NoRestoreSession
    public void softRemoveSpaceByOwner() {
        WorkspaceContext context = workspaceFactory.createWorkspace();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.deleteButton();
        pages.deleteWorkspaceModalSteps.softDeleteWorkspace();
        pages.workspaceSnackBarsSteps.verifyWorkspaceDeletedSnackBarIsVisible();
        pages.leftSidebarSteps.verifySpaceNotAvailableByName(context.getWsName());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.verifySpaceNotAvailableByName(context.getWsName());
    }

    @ChannelAllureIds(web = "57134", desktop = "96243")
    @Test
    @DisplayName("Удаление пространства владельцем, когда в нем находятся админ")
    public void spaceDeletionByOwnerWithAdminPresent() {
        WorkspaceContext context = workspaceFactory.createWorkspaceWithWorkspaceAdmin();

        loginHelper.performUILogin(context.getAdmin());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        context.getOwnerBuilder().deleteWorkspace(context.getWsId());
        pages.workspaceDeletedNotificationModalSteps.verifyWorkspaceDeletedModalIsVisible();
        pages.workspaceDeletedNotificationModalSteps.clickConfirmButton();
        pages.leftSidebarSteps.verifySpaceNotAvailableByName(context.getWsName());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.verifySpaceNotAvailableByName(context.getWsName());
    }

    @ChannelAllureIds(web = "57135", desktop = "96244")
    @Test
    @DisplayName("Удаление пространства владельцем, когда в нем находятся участник")
    @NoRestoreSession
    public void spaceDeletionByOwnerWithParticipantPresent() {
        WorkspaceContext context = workspaceFactory.createWorkspaceWithParticipant();

        loginHelper.performUILogin(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        context.getOwnerBuilder().deleteWorkspace(context.getWsId());
        pages.workspaceDeletedNotificationModalSteps.verifyWorkspaceDeletedModalIsVisible();
        pages.workspaceDeletedNotificationModalSteps.clickConfirmButton();
        pages.leftSidebarSteps.verifySpaceNotAvailableByName(context.getWsName());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.verifySpaceNotAvailableByName(context.getWsName());
    }
}
