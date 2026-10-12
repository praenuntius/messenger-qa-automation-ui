package org.foxtrot.test.chats_and_spaces.delete_workspace;

import org.foxtrot.allure.ChannelAllureIds;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.contexts.WorkspaceContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.foxtrot.utils.factories.core.WorkspaceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.foxtrot.utils.UrlConstants.getWorkspaceGroupInviteUrl;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LinkNavigationTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();

    private WorkspaceContext wsContext;
    private GroupContext groupContext;

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "66655", desktop = "57715")
    @DisplayName("Переход по ссылке в ЗАКРЫТУЮ группу, в которой состоял, удаленного пространства")
    public void openLinkToClosedGroupWhereMember() {
        runLinkNavigationTest(groupFactory.privateGroupInWsWithParticipant(), false);
    }

    @Test
    @ChannelAllureIds(web = "57475", desktop = "57721")
    @DisplayName("Переход по ссылке в ЗАКРЫТУЮ группу (не состоял) удаленного пространства")
    public void openLinkToClosedGroupWhereNotMember() {
        runLinkNavigationTest(groupFactory.privateGroupInWsWithParticipant(), true);
    }

    @Test
    @ChannelAllureIds(web = "57477", desktop = "57730")
    @DisplayName("Переход по ссылке в ЗАКРЫТЫЙ канал (не состоял) удаленного пространства")
    public void openLinkToClosedChannelWhereNotMember() {
        runLinkNavigationTest(groupFactory.privateChannelInWsWithParticipant(), true);
    }

    @Test
    @ChannelAllureIds(web = "57474", desktop = "57719")
    @DisplayName("Переход по ссылке в ОТКРЫТУЮ группу(не состоял), удаленного пространства")
    public void openLinkToPublicGroupWhereNotMember() {
        runLinkNavigationTest(groupFactory.publicGroupInWsWithParticipant(), true);
    }

    @Test
    @ChannelAllureIds(web = "66654", desktop = "57714")
    @DisplayName("Переход по ссылке в ОТКРЫТУЮ группу(состоял), удаленного пространства")
    public void openLinkToPublicGroupWhereMember() {
        runLinkNavigationTest(groupFactory.publicGroupInWsWithParticipant(), false);
    }

    @Test
    @ChannelAllureIds(web = "57476", desktop = "57723")
    @DisplayName("Переход по ссылке в ОТКРЫТЫЙ канал (не состоял) удаленного пространства")
    public void openLinkToPublicChannelWhereNotMember() {
        runLinkNavigationTest(groupFactory.publicChannelInWsWithParticipant(), true);
    }

    @Test
    @ChannelAllureIds(web = "57467", desktop = "57712")
    @DisplayName("Переход по ссылке-приглашению в удаленное пространство, в котором не состоял")
    public void openLinkToDeletedWorkspaceWhereNotMember() {
        wsContext = workspaceFactory.createWorkspaceWithParticipant();
        wsContext.getOwnerBuilder().deleteUserFromWorkspace(wsContext.getWsId(), wsContext.getParticipant());

        String inviteLink = wsContext.getOwnerBuilder().getWorkSpaceInviteLink(wsContext.getWsId());
        Long p2p = wsContext.getOwnerBuilder().createP2PGroup(wsContext.getParticipant());
        wsContext.getOwnerBuilder().sendLinkMassage(inviteLink, p2p);

        loginHelper.performUILogin(wsContext.getParticipant());

        wsContext.getOwnerBuilder().deleteWorkspace(wsContext.getWsId());

        pages.chatListSteps.clickChatByName(wsContext.getOwner());
        pages.chatActions.findAndClickLinkMessageByText(inviteLink);

        pages.snackbarsSteps.verifyInviteLinkInactive();
        pages.topbarSteps.verifyChatName(wsContext.getOwner().getName());
    }


    @Test
    @ChannelAllureIds(web = "57468", desktop = "57716")
    @DisplayName("Переход по ссылке-приглашению в удаленное пространство, в котором состоял")
    public void openLinkToDeletedWorkspaceWhereMember() {
        wsContext = workspaceFactory.createWorkspaceWithParticipant();
        String inviteLink = wsContext.getOwnerBuilder().getWorkSpaceInviteLink(wsContext.getWsId());
        Long p2p = wsContext.getOwnerBuilder().createP2PGroup(wsContext.getParticipant());
        wsContext.getOwnerBuilder().sendLinkMassage(inviteLink, p2p);

        loginHelper.performUILogin(wsContext.getParticipant());

        wsContext.getOwnerBuilder().deleteWorkspace(wsContext.getWsId());

        pages.chatListSteps.clickChatByName(wsContext.getOwner());
        pages.chatActions.findAndClickLinkMessageByText(inviteLink);

        pages.snackbarsSteps.verifyInviteLinkInactive();
        pages.topbarSteps.verifyChatName(wsContext.getOwner().getName());
    }


    @Test
    @ChannelAllureIds(web = "57469", desktop = "57713")
    @DisplayName("Переход по ссылке-приглашению из пространства в удаленное пространство, в котором не состоял")
    public void openLinkFromWorkspaceToDeletedWorkspaceWhereNotMember() {
        wsContext = workspaceFactory.createWorkspace();
        var ownerBld = wsContext.getOwnerBuilder();
        String inviteLink = ownerBld.getWorkSpaceInviteLink(wsContext.getWsId());

        groupContext = groupFactory.privateGroupInWsWithParticipant();
        groupContext.getOwnerBuilder().sendLinkMassage(inviteLink, groupContext.getGroupId());

        loginHelper.performUILogin(groupContext.getParticipant());

        ownerBld.deleteWorkspace(wsContext.getWsId());

        pages.leftSidebarSteps.clickSpaceByName(groupContext.getWorkspaceName());
        pages.chatListSteps.clickChatByName(groupContext.getGroupName());
        pages.chatActions.findAndClickLinkMessageByText(inviteLink);

        pages.snackbarsSteps.verifyInviteLinkInactive();
        pages.topbarSteps.verifyChatName(groupContext.getGroupName());
    }


    private void runLinkNavigationTest(GroupContext context, boolean shouldRemoveUser) {
        var ownerBuilder = context.getOwnerBuilder();

        if (shouldRemoveUser) {
            ownerBuilder.removeUser(context.getGroupId(), context.getParticipant());
        }

        String inviteLink = ownerBuilder.regenerateInviteLink(context.getGroupId());
        Long p2p = ownerBuilder.createP2PGroup(context.getParticipant());
        ownerBuilder.sendLinkMassage(getWorkspaceGroupInviteUrl(inviteLink), p2p);

        loginHelper.performUILogin(context.getParticipant());
        ownerBuilder.deleteWorkspace(context.getWorkspaceId());

        pages.chatListSteps.clickChatByName(context.getOwner());
        pages.chatActions.findAndClickLinkMessageByText(inviteLink);

        pages.diveIntoWorkSpaceViaUrlLinkSteps.verifyAccessRestrictionMessageVisible();
        pages.groupUnavailableModalSteps.clickCloseButton();

        pages.leftSidebarSteps.verifySpaceIsActive("Чаты");
        pages.topbarSteps.verifyChatName(context.getOwner().getName());
    }
}