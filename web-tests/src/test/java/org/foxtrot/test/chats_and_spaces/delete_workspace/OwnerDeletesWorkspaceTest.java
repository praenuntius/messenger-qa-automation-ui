package org.foxtrot.test.chats_and_spaces.delete_workspace;

import org.foxtrot.allure.ChannelAllureIds;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.UserTestDataCleaner;
import org.foxtrot.utils.factories.contexts.WorkspaceContext;
import org.foxtrot.utils.factories.core.WorkspaceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.foxtrot.utils.TestDataBuilder.GroupConfig.ofGroup;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OwnerDeletesWorkspaceTest extends BaseTest {

    private final UserTestDataCleaner cleaner = new UserTestDataCleaner();
    private final LoginHelper loginHelper = new LoginHelper();
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();

    private WorkspaceContext context;

    @AfterEach
    public void tearDown() {
        workspaceFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "57131", desktop = "57732")
    @DisplayName("Проверить удаление пространства, если оно единственное")
    public void deleteOnlyWorkspace() {
        var context = setupWorkspaceWithChat(false);

        loginAndDeleteWorkspace(context);

        pages.leftSidebarSteps.verifySpaceNotAvailableByName(context.getWsName());

        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps
                .verifySpaceNotAvailableByName(context.getWsName())
                .closeAvailableWorkspacesModal();

        verifySpaceNotPresentInForwardMenu(context.getWsName());
    }

    @Test
    @ChannelAllureIds(web = "57129", desktop = "57725")
    @DisplayName("Проверить удаленное пространство в левом сайдбаре пространств")
    public void deletedWorkspaceNotVisibleInSidebar() {
        var context = setupWorkspaceWithChat(true);

        loginAndDeleteWorkspace(context);

        pages.leftSidebarSteps.verifySpaceNotAvailableByName(context.getWsName());

        loginHelper.performUILoginForTest(context.getParticipant());
        pages.leftSidebarSteps.verifySpaceNotAvailableByName(context.getWsName());
    }

    @Test
    @ChannelAllureIds(web = "57128", desktop = "57731")
    @DisplayName("Проверить удаленное пространство в меню доступных пространств")
    public void deletedWorkspaceNotVisibleInSpacesMenu() {
        var context = setupWorkspaceWithChat(false);

        loginAndDeleteWorkspace(context);

        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.verifySpaceNotAvailableByName(context.getWsName());
    }

    @Test
    @ChannelAllureIds(web = "57130", desktop = "57726")
    @DisplayName("Проверить удаленное пространство в списке для форварда")
    public void deletedWorkspaceNotVisibleInForwardList() {
        var context = setupWorkspaceWithChat(false);

        loginAndDeleteWorkspace(context);
        verifySpaceNotPresentInForwardMenu(context.getWsName());
    }


    // --- Private Helpers ---

    private WorkspaceContext setupWorkspaceWithChat(boolean withParticipant) {
        var context = withParticipant
                ? workspaceFactory.createWorkspaceWithParticipant()
                : workspaceFactory.createWorkspace();

        var owner = context.getOwnerBuilder();

        if (withParticipant) {
            context.getParticipantBuilder().sendTextMessage(context.getParticipant().getSavedChatId(), "test");
        }else {
            owner.sendTextMessage(context.getOwner().getSavedChatId(), "test");
        }
        return context;
    }

    private void loginAndDeleteWorkspace(WorkspaceContext context) {
        loginHelper.performUILogin(context.getOwner());
        context.getOwnerBuilder().deleteWorkspace(context.getWsId());
    }

    private void verifySpaceNotPresentInForwardMenu(String wsName) {
        pages.chatListSteps.clickSavedChat();
        pages.chatActions.openMessageContextMenu("test");
        pages.messageBubbleContextMenu.clickForwardButton();
        pages.forwardMenuSteps
                .openForwardSpacesList()
                .verifySpaceNotInForwardList(wsName);
    }
}
