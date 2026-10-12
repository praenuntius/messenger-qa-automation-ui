package org.foxtrot.test.chats_and_spaces;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import lombok.Builder;
import lombok.Getter;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.TestDataBuilder;
import org.foxtrot.utils.UrlConstants;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.foxtrot.utils.factories.core.UserFactory;
import org.foxtrot.utils.factories.core.WorkspaceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NonParticipantTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();
    private final UserFactory userFactory = new UserFactory();

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
        workspaceFactory.cleanUp();
    }

    @Test
    @DisplayName("проверка меню P2p-чата не контактом")
    public void uiLoginDiveIntoP2p() {
        var context = p2pWithOwnerAndNonParticipant();

        context.getOwnerBuilder().sendTextMessage(
                context.getP2pGroupId(), "autotest precondition " + System.currentTimeMillis());

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.topbarSteps.clickP2pHeader();
        pages.p2pMenuVerifier.verifyP2pSidebarMenuElements("noncontact", "enabled", context.getOwner().getName());
    }

    @Test
    @DisplayName("Переход по Url в группу в домашнем пространстве не участником чата")
    public void diveIntoHomespaceGroupViaUrlLink() {
        var context = homespaceGroupLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getHomespaceGroupUrl());
        pages.diveIntoChatViaUrlLinkSteps.verifyAccessRestrictionMessageVisible();
    }

    @Test
    @DisplayName("Переход по ссылке-приглашению в группу в домашнем пространстве не участником чата")
    public void diveIntoHomespaceGroupViaInviteLink() {
        var context = homespaceGroupLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getInviteHomespaceGroup());
        pages.diveIntoChatViaInviteLinkSteps.verifyJoinGroupButtonVisible();
    }

    @Test
    @DisplayName("Переход по Url в канал в домашнем пространстве не участником чата")
    public void diveIntoHomespaceChannelViaUrlLink() {
        var context = homespaceChannelLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getHomespaceChannelUrl());
        pages.diveIntoChatViaUrlLinkSteps.verifyAccessRestrictionMessageVisible();
    }

    @Test
    @DisplayName("Переход по ссылке-приглашению в канал в домашнем пространстве не участником чата")
    public void diveIntoHomespaceChannelViaInviteLink() {
        var context = homespaceChannelLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getInviteHomespaceChannel());
        pages.diveIntoChatViaInviteLinkSteps.verifyJoinGroupButtonVisible();
    }

    @AllureId("64378")
    @Test
    @DisplayName("Переход по Url в приватный канал рабочего пространства не участником пространства")
    public void diveIntoNotMemberWorkSpacePrivateChannelViaUrlLink() {
        var context = workspaceNotMemberLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getWorkspacePrivateChannelUrl());
        pages.diveIntoWorkSpaceViaUrlLinkSteps.verifyAccessRestrictionMessageVisible();
    }

    @Test
    @DisplayName("Переход по ссылке-приглашению в приватный канал рабочего пространства не участником пространства")
    public void diveIntoNotMemberWorkspaceViaPrivateChannelInviteLink() {
        var context = workspaceNotMemberLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getInvitePrivateChannelWorkspace());
        pages.diveIntoWorkSpaceViaUrlLinkSteps.verifyAccessRestrictionMessageVisible();
    }

    @Test
    @DisplayName("Переход по Url в публичный канал рабочего пространства не участником пространства")
    public void diveIntoNotMemberWorkSpacePublicChannelViaUrlLink() {
        var context = workspaceNotMemberLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getWorkspacePublicChannelUrl());
        pages.diveIntoWorkSpaceViaUrlLinkSteps.verifyAccessRestrictionMessageVisible();
    }

    @Test
    @DisplayName("Переход по Url в приватную группу рабочего пространства не участником пространства")
    public void diveIntoNotMemberWorkSpacePrivateGroupViaUrlLink() {
        var context = workspaceNotMemberLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getWorkspacePrivateGroupUrl());
        pages.diveIntoWorkSpaceViaUrlLinkSteps.verifyAccessRestrictionMessageVisible();
    }

    @AllureId("52481")
    @Test
    @DisplayName("Переход по ссылке-приглашению в приватную группу рабочего пространства не участником пространства")
    public void diveIntoNotMemberWorkspaceViaPrivateGroupInviteLink() {
        var context = workspaceNotMemberLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getInvitePrivateGroupWorkspace());
        pages.diveIntoWorkSpaceViaUrlLinkSteps.verifyAccessRestrictionMessageVisible();
    }

    @Test
    @DisplayName("Переход по Url в публичную группу рабочего пространства не участником пространства")
    public void diveIntoNotMemberWorkSpacePublicGroupViaUrlLink() {
        var context = workspaceNotMemberLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getWorkspacePublicGroupUrl());
        pages.diveIntoWorkSpaceViaUrlLinkSteps.verifyAccessRestrictionMessageVisible();
    }

    @AllureId("52540")
    @Test
    @DisplayName("Переход по Url в пространство")
    public void diveIntoWorkSpaceViaUrlLink() {
        var context = workspaceNotMemberLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getWorkspaceUrl());
        pages.diveIntoWorkSpaceViaUrlLinkSteps.verifyAccessRestrictionMessageVisible();
    }

    @Test
    @DisplayName("Переход по ссылке-приглашению в пространство не участником")
    public void diveIntoWorkspaceViaInviteLink() {
        var context = workspaceNotMemberLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getWorkspaceInviteUrl());
        pages.spaceSteps.verifySpaceName(context.getWorkspaceName());
        context.getOwnerBuilder().addUsersToWorkspace(context.getWorkspaceId(), context.getNotParticipant());
    }

    @AllureId("64378")
    @Test
    @DisplayName("Переход по Url в приватный канал рабочего пространства не участником")
    public void diveIntoWorkSpacePrivateChannelViaUrlLink() {
        var context = workspaceMemberLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getWorkspacePrivateChannelUrl());
        pages.diveIntoChatViaUrlLinkSteps.verifyAccessRestrictionMessageVisible();
    }

    @Test
    @DisplayName("Переход по ссылке-приглашению в приватный канал рабочего пространства не участником")
    public void diveIntoWorkspaceViaPrivateChannelInviteLink() {
        var context = workspaceMemberLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getInvitePrivateChannelWorkspace());
        pages.diveIntoChatViaInviteLinkSteps.verifyJoinGroupButtonVisible();
    }

    @Test
    @DisplayName("Переход по Url в публичный канал рабочего пространства не участником")
    public void diveIntoWorkSpacePublicChannelViaUrlLink() {
        var context = workspaceMemberLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getWorkspacePublicChannelUrl());
        pages.topbarSteps.verifyChatName(context.getWorkspacePublicChannelName());
    }

    @AllureId("52540")
    @Test
    @DisplayName("Переход по Url в приватную группу рабочего пространства не участником")
    public void diveIntoWorkSpacePrivateGroupViaUrlLink() {
        var context = workspaceMemberLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getWorkspacePrivateGroupUrl());
        pages.diveIntoChatViaUrlLinkSteps.verifyAccessRestrictionMessageVisible();
    }

    @Test
    @DisplayName("Переход по ссылке-приглашению в приватную группу рабочего пространства не участником")
    public void diveIntoWorkspaceViaPrivateGroupInviteLink() {
        var context = workspaceMemberLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getInvitePrivateGroupWorkspace());
        pages.diveIntoChatViaInviteLinkSteps.verifyJoinGroupButtonVisible();
    }

    @Test
    @DisplayName("Переход по Url в публичную группу рабочего пространства не участником")
    public void diveIntoWorkSpacePublicGroupViaUrlLink() {
        var context = workspaceMemberLinks();

        loginHelper.performUILogin(context.getNotParticipant());
        pages.chatListSteps.clickChatByName(context.getOwner().getName());
        pages.chatActions.findAndClickLinkMessageByText(context.getWorkspacePublicGroupUrl());
        pages.topbarSteps.verifyChatName(context.getWorkspacePublicGroupName());
    }

    @Getter
    @Builder(toBuilder = true)
    private static class NonParticipantContext {
        private final UsersEnum owner;
        private final UsersEnum notParticipant;
        private final Long p2pGroupId;
        private final TestDataBuilder ownerBuilder;

        private final String workspaceName;
        private final Long workspaceId;

        private final String homespaceGroupUrl;
        private final String inviteHomespaceGroup;
        private final String homespaceChannelUrl;
        private final String inviteHomespaceChannel;

        private final String workspaceUrl;
        private final String workspaceInviteUrl;
        private final String workspacePrivateChannelUrl;
        private final String invitePrivateChannelWorkspace;
        private final String workspacePublicChannelUrl;
        private final String workspacePublicChannelName;
        private final String workspacePrivateGroupUrl;
        private final String invitePrivateGroupWorkspace;
        private final String workspacePublicGroupUrl;
        private final String workspacePublicGroupName;
    }

    private NonParticipantContext p2pWithOwnerAndNonParticipant() {
        UsersEnum owner = userFactory.takeUser();
        UsersEnum notParticipant = userFactory.takeUser();
        TestDataBuilder ownerBuilder = new TestDataBuilder(owner);
        long p2pGroupId = ownerBuilder.createP2PGroup(notParticipant);

        return NonParticipantContext.builder()
                .owner(owner)
                .notParticipant(notParticipant)
                .p2pGroupId(p2pGroupId)
                .ownerBuilder(ownerBuilder)
                .build();
    }

    private NonParticipantContext homespaceGroupLinks() {
        var ctx = p2pWithOwnerAndNonParticipant();
        String groupName = TestData.generateHomespaceGroupName();
        long groupId = ctx.getOwnerBuilder().createGroup(
                TestDataBuilder.GroupConfig.ofGroup(groupName).visible(false));

        String groupUrl = UrlConstants.getHomespaceGroupUrl(String.valueOf(groupId));
        String inviteCode = ctx.getOwnerBuilder().regenerateInviteLink(groupId);
        String inviteUrl = UrlConstants.getHomespaceGroupInviteUrl(inviteCode);

        sendLinksToP2p(ctx, groupUrl, inviteUrl);

        return ctx.toBuilder()
                .homespaceGroupUrl(groupUrl)
                .inviteHomespaceGroup(inviteUrl)
                .build();
    }

    private NonParticipantContext homespaceChannelLinks() {
        var ctx = p2pWithOwnerAndNonParticipant();
        String channelName = TestData.generateHomespaceChannelName();
        long channelId = ctx.getOwnerBuilder().createGroup(
                TestDataBuilder.GroupConfig.ofChannel(channelName).visible(false));

        String channelUrl = UrlConstants.getHomespaceGroupUrl(String.valueOf(channelId));
        String inviteCode = ctx.getOwnerBuilder().regenerateInviteLink(channelId);
        String inviteUrl = UrlConstants.getHomespaceGroupInviteUrl(inviteCode);

        sendLinksToP2p(ctx, channelUrl, inviteUrl);

        return ctx.toBuilder()
                .homespaceChannelUrl(channelUrl)
                .inviteHomespaceChannel(inviteUrl)
                .build();
    }

    private NonParticipantContext workspaceNotMemberLinks() {
        var ctx = p2pWithOwnerAndNonParticipant();
        TestDataBuilder builder = ctx.getOwnerBuilder();

        String workspaceName = TestData.generateWorkspaceName();
        TestDataBuilder.WorkspaceInfo wsInfo = builder.createWorkspaceInfo(workspaceName);
        long workspaceId = wsInfo.getId();

        String workspaceUrl = UrlConstants.getWorkspaceUrl(String.valueOf(workspaceId));
        String workspaceInviteUrl = UrlConstants.getWorkspaceInviteUrl(wsInfo.getInviteCode());
        sendLinksToP2p(ctx, workspaceUrl, workspaceInviteUrl);

        String privateChannelName = TestData.generateWorkspacePrivateChannelName();
        long privateChannelId = builder.createGroup(
                TestDataBuilder.GroupConfig.ofChannel(privateChannelName)
                        .inWorkspace(workspaceId)
                        .visible(false));
        String privateChannelUrl = UrlConstants.getWorkspacePrivateChannelUrl(
                String.valueOf(workspaceId), String.valueOf(privateChannelId));
        String privateChannelInvite = UrlConstants.getWorkspaceGroupInviteUrl(
                builder.regenerateInviteLink(privateChannelId));
        sendLinksToP2p(ctx, privateChannelUrl, privateChannelInvite);

        String publicChannelName = TestData.generateWorkspacePublicChannelName();
        long publicChannelId = builder.createGroup(
                TestDataBuilder.GroupConfig.ofChannel(publicChannelName)
                        .inWorkspace(workspaceId)
                        .visible(true));
        String publicChannelUrl = UrlConstants.getWorkspacePrivateChannelUrl(
                String.valueOf(workspaceId), String.valueOf(publicChannelId));
        builder.sendLinkMassage(publicChannelUrl, ctx.getP2pGroupId());

        String privateGroupName = TestData.generateWorkspacePrivateGroupName();
        long privateGroupId = builder.createGroup(
                TestDataBuilder.GroupConfig.ofGroup(privateGroupName)
                        .inWorkspace(workspaceId)
                        .visible(false));
        String privateGroupUrl = UrlConstants.getWorkspacePrivateGroupUrl(
                String.valueOf(workspaceId), String.valueOf(privateGroupId));
        String privateGroupInvite = UrlConstants.getWorkspaceGroupInviteUrl(
                builder.regenerateInviteLink(privateGroupId));
        sendLinksToP2p(ctx, privateGroupUrl, privateGroupInvite);

        String publicGroupName = TestData.generateWorkspacePublicGroupName();
        long publicGroupId = builder.createGroup(
                TestDataBuilder.GroupConfig.ofGroup(publicGroupName)
                        .inWorkspace(workspaceId)
                        .visible(true));
        String publicGroupUrl = UrlConstants.getWorkspacePublicGroupUrl(
                String.valueOf(workspaceId), String.valueOf(publicGroupId));
        builder.sendLinkMassage(publicGroupUrl, ctx.getP2pGroupId());

        return ctx.toBuilder()
                .workspaceName(workspaceName)
                .workspaceId(workspaceId)
                .workspaceUrl(workspaceUrl)
                .workspaceInviteUrl(workspaceInviteUrl)
                .workspacePrivateChannelUrl(privateChannelUrl)
                .invitePrivateChannelWorkspace(privateChannelInvite)
                .workspacePublicChannelUrl(publicChannelUrl)
                .workspacePublicChannelName(publicChannelName)
                .workspacePrivateGroupUrl(privateGroupUrl)
                .invitePrivateGroupWorkspace(privateGroupInvite)
                .workspacePublicGroupUrl(publicGroupUrl)
                .workspacePublicGroupName(publicGroupName)
                .build();
    }

    private NonParticipantContext workspaceMemberLinks() {
        NonParticipantContext ctx = workspaceNotMemberLinks();
        ctx.getOwnerBuilder().addUsersToWorkspace(ctx.getWorkspaceId(), ctx.getNotParticipant());
        return ctx;
    }

    private void sendLinksToP2p(NonParticipantContext ctx, String... urls) {
        for (String url : urls) {
            ctx.getOwnerBuilder().sendLinkMassage(url, ctx.getP2pGroupId());
        }
    }
}
