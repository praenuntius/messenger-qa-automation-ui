package org.foxtrot.test.chats_and_spaces.delete_workspace.reaction_on_deletion;

import org.foxtrot.allure.ChannelAllureIds;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.UserTestDataCleaner;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.foxtrot.utils.TestDataBuilder.GroupConfig.ofGroup;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GroupsTest extends BaseTest {

    private final UserTestDataCleaner cleaner = new UserTestDataCleaner();
    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    private GroupContext context;

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "57393", desktop = "57696")
    @DisplayName("Удаление пространства в момент нахождения в группе пространства")
    public void deleteWorkspaceWhileInGroup() {
        context = setupGroupContext();

        loginHelper.performUILogin(context.getParticipant());

        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());

        verifyPostDeletionState(context);
    }

    @Test
    @ChannelAllureIds(web = "57394", desktop = "57697")
    @DisplayName("Удаление пространства в момент нахождения в меню группы пространства")
    public void deleteWorkspaceWhileInGroupMenu() {
        context = setupGroupContext();

        loginHelper.performUILogin(context.getParticipant());

        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuVerifier.verifyParticipantMenu(context.getGroupName(), "Закрытая группа");

        verifyPostDeletionState(context);
    }

    @Test
    @ChannelAllureIds(web = "57395", desktop = "57698")
    @DisplayName("Удаление пространства в момент нахождения в списке участников группового чата в пространстве")
    public void deleteWorkspaceWhileInGroupMemberMenu() {
        context = setupGroupContext();

        loginHelper.performUILogin(context.getParticipant());

        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickMembersButton();
        pages.membersMenuSteps.checkUserPresenceInSpace(context.getParticipant().getName());

        verifyPostDeletionState(context);
    }

    @Test
    @ChannelAllureIds(web = "57397", desktop = "57700")
    @DisplayName("Удаление пространства в момент нахождения на экране 'Настроек группы'")
    public void deleteWorkspaceWhileInGroupSettings() {
        context = setupGroupContext();

        loginHelper.performUILogin(context.getOwner());

        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickSettingsButton();
        pages.groupAndChannelEditModalSteps.verifyGroupSettingsHeaderIsVisible();

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

    private GroupContext setupGroupContext() {
        var context = groupFactory.privateGroupInWsWithParticipant();
        context.getOwnerBuilder().sendTextMessage(context.getOwner().getSavedChatId(), "test");
        context.getParticipantBuilder().sendTextMessage(context.getParticipant().getSavedChatId(), "test");

        return context;
    }
}