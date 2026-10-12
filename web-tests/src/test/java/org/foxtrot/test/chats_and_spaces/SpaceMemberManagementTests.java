package org.foxtrot.test.chats_and_spaces;

import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.TestDataBuilder;
import org.foxtrot.utils.factories.contexts.WorkspaceContext;
import org.foxtrot.utils.factories.core.UserFactory;
import org.foxtrot.utils.factories.core.WorkspaceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpaceMemberManagementTests extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();
    private final UserFactory userFactory = new UserFactory();

    @AfterEach
    void tearDown() {
        workspaceFactory.cleanUp();
        userFactory.releaseAll();
    }

    @ChannelAllureIds(web = "55509", desktop = "96285")
    @Test
    @DisplayName("Удаление участника из пространства владельцем")
    public void removeParticipantByOwner() {
        WorkspaceContext context = workspaceFactory.createWorkspaceWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.deleteFromWorkspace(context.getParticipant().getName());
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.checkUserNotPresentInSpace(context.getParticipant().getName());
    }

    @ChannelAllureIds(web = "55518", desktop = "96279")
    @Test
    @DisplayName("Удаление участника админом")
    public void removeParticipantByAdmin() {
        WorkspaceContext context = workspaceFactory.createWorkspaceWithWorkspaceAdmin();
        UsersEnum member = userFactory.takeUser();
        context.getOwnerBuilder().addUsersToWorkspace(context.getWsId(), member);

        loginHelper.performUILogin(context.getAdmin());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.deleteFromWorkspace(member.getName());
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.checkUserNotPresentInSpace(member.getName());
    }

    @ChannelAllureIds(web = "55510", desktop = "96276")
    @Test
    @DisplayName("Удаление админа владельцем пространства без снятия с должности")
    public void removeAdminByOwnerDirectly() {
        WorkspaceContext context = workspaceFactory.createWorkspaceWithWorkspaceAdmin();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.deleteFromWorkspace(context.getAdmin().getName());
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.checkUserNotPresentInSpace(context.getAdmin().getName());
    }

    @ChannelAllureIds(web = "55521", desktop = "96256")
    @Test
    @DisplayName("Один админ снимает второго с должности, другой удаляет его из пространства")
    public void demoteAndRemoveAdmin() {
        WorkspaceContext context = workspaceFactory.createWorkspaceWithWorkspaceAdmin();
        UsersEnum secondAdmin = userFactory.takeUser();
        context.getOwnerBuilder().addUsersToWorkspace(context.getWsId(), secondAdmin);
        context.getOwnerBuilder().updateWorkspaceRole(context.getWsId(), secondAdmin, "ADMIN");

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.removeFromAdmins(secondAdmin.getName());
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.verifyUserIsNotAdministrator(secondAdmin.getName());

        loginHelper.performUILoginForTest(context.getAdmin());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.deleteFromWorkspace(secondAdmin.getName());
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.checkUserNotPresentInSpace(secondAdmin.getName());
    }

    @ChannelAllureIds(web = "55528", desktop = "96253")
    @Test
    @DisplayName("Ограничение доступа для участника к контекстным меню")
    public void participantContextMenuRestriction() {
        WorkspaceContext context = workspaceFactory.createWorkspaceWithAdminAndParticipant();

        loginHelper.performUILogin(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.verifyContextMenuNotAvailableForUser(context.getAdmin().getName());
        pages.workspaceMembersMenuSteps.verifyContextMenuNotAvailableForUser(context.getOwner().getName());
    }

    @ChannelAllureIds(web = "55516", desktop = "96278")
    @Test
    @DisplayName("Удаление единственного участника открытых чатов с авто-удалением")
    public void removeSoleParticipantFromOpenChats() {
        WorkspaceContext context = workspaceFactory.createWorkspaceWithParticipant();

        String publicChannelName = TestData.generateWorkspacePublicChannelName();
        String publicGroupName = TestData.generateWorkspacePublicGroupName();
        context.getParticipantBuilder().createGroup(
                TestDataBuilder.GroupConfig.ofChannel(publicChannelName)
                        .inWorkspace(context.getWsId())
                        .visible(true));
        context.getParticipantBuilder().createGroup(
                TestDataBuilder.GroupConfig.ofGroup(publicGroupName)
                        .inWorkspace(context.getWsId())
                        .visible(true));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.groupsAndChannelsButton();
        pages.groupAndChannelMenuSteps.verifyGroupPresentInList(publicChannelName);
        pages.groupAndChannelMenuSteps.verifyGroupPresentInList(publicGroupName);

        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.deleteFromWorkspace(context.getParticipant().getName());
        pages.spaceMenuSteps.membersButton();
        pages.workspaceMembersMenuSteps.checkUserNotPresentInSpace(context.getParticipant().getName());

        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.groupsAndChannelsButton();
        pages.groupAndChannelMenuSteps.verifyGroupNotPresentInList(publicChannelName);
        pages.groupAndChannelMenuSteps.verifyGroupNotPresentInList(publicGroupName);
    }

    @ChannelAllureIds(web = "55524", desktop = "96262")
    @Test
    @DisplayName("Проверка статуса чата 'без владельца'")
    public void CheckOwnerlessStatus() {
        WorkspaceContext context = workspaceFactory.createWorkspaceWithParticipant();
        String ownerlessGroupName = TestData.generateWorkspacePublicGroupName();
        Long ownerlessGroupId = context.getParticipantBuilder().createGroup(
                TestDataBuilder.GroupConfig.ofGroup(ownerlessGroupName)
                        .inWorkspace(context.getWsId())
                        .visible(true));
        context.getParticipantBuilder().addUsers(ownerlessGroupId, context.getWsId(), context.getOwner());
        context.getOwnerBuilder().deleteUserFromWorkspace(context.getWsId(), context.getParticipant());

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.homespaceSteps.clickChatByName(ownerlessGroupName);
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickMembersButton();
        pages.membersMenuSteps.verifyGroupWithoutOwnerBanner();
        pages.membersMenuSteps.verifyInfoTulTip();
    }
}
