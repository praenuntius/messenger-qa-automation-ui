package org.foxtrot.mobile.test.chats_menu.groups_menu;

import org.foxtrot.api.dto.group.response.GetUserGroupStatesResponse;
import org.foxtrot.api.steps.GroupApiSteps;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.mobile.allure.AllureIds;
import org.foxtrot.mobile.test.BaseMobileTest;
import org.foxtrot.mobile.testdata.dictionary.ChatDictionary;
import org.foxtrot.mobile.utils.LoaderWaiter;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.TestDataBuilder;
import org.foxtrot.utils.UrlConstants;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.foxtrot.utils.factories.core.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;
import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public class GroupsLinkTest extends BaseMobileTest {

    private final GroupFactory groupFactory = new GroupFactory();
    private final UserFactory userFactory = new UserFactory();
    private GroupContext groupContext;
    private TestDataBuilder ownerBuilder;
    private UsersEnum participant;
    private String groupUrl;
    private String inviteLink;
    private String inviteUrl;

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @AllureIds(ios = "94095", android = "64445")
    @DisplayName("Перейти по ссылке из адресной строки на УДАЛЕННУЮ группу пространства, в котором пользователь СОСТОИТ")
    public void tapLinkDeletedPublicGroupParticipant() {
        prepareGroup(true);
        ownerBuilder.deleteGroup(groupContext.getGroupId(), groupContext.getWorkspaceId());
        Long p2pGroupId = ownerBuilder.createP2PGroup(participant);
        ownerBuilder.sendLinkMassage(groupUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(participant);
        pages.chats().openChatByName(groupContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(groupUrl)
                .clickChatMessage(1);
        if (isIos()) { // todo задача https://allprojects.atlassian.net/browse/FB-21583
            pages.modal().checkModalTitle(ChatDictionary.GROUP_NOT_CREATED);
        } else {
            pages.chat().checkAlertDisplayedOnChatScreen(ChatDictionary.GROUP_CHANNEL_NOT_EXIST);
        }
    }

    @Test
    @AllureIds(ios = "94096", android = "64442")
    @DisplayName("Переход по ссылке из адресной строки на закрытую группу в пространстве, в котором пользователь НЕ состоит")
    public void tapLinkPrivateGroupNotParticipantWs() {
        prepareGroup(false);
        UsersEnum user = userFactory.takeUser();
        Long p2pGroupId = ownerBuilder.createP2PGroup(user);
        ownerBuilder.sendLinkMassage(groupUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(user);
        pages.chats().openChatByName(groupContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(groupUrl)
                .clickChatMessage(1);
        if (isAndroid()) { // todo есть задача https://allprojects.atlassian.net/browse/FB-21584
            pages.modal().checkModalTitle(ChatDictionary.NO_ACCESS_NOT_PARTICIPANT);
        } else {
            pages.chat().checkAlertDisplayedOnChatScreen(ChatDictionary.LINK_NOT_ACTIVE);
        }
    }

    @Test
    @AllureIds(ios = "64424", android = "64441")
    @DisplayName("Переход по ссылке из адресной строки на закрытую группу в пространстве, в котором пользователь СОСТОИТ")
    public void tapLinkPrivateGroupParticipantWs() {
        prepareGroup(false);
        Long p2pGroupId = ownerBuilder.createP2PGroup(participant);
        ownerBuilder.sendLinkMassage(groupUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(participant);
        pages.chats().openChatByName(groupContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(groupUrl)
                .clickChatMessage(1)
                .checkAlertDisplayedOnChatScreen(ChatDictionary.NO_ACCESS_TO_GROUP_OR_CHANNEL);
    }

    @Test
    @AllureIds(ios = "51433", android = "95812")
    @DisplayName("Отправить ссылку пользователю на открытую группу, НЕ участнику пространства "
            + "> Ссылка неактивна или у вас нет доступа к пространству")
    public void tapLinkPublicGroupNotParticipantWs() {
        prepareGroup(true);
        UsersEnum user = userFactory.takeUser();
        Long p2pGroupId = ownerBuilder.createP2PGroup(user);
        ownerBuilder.sendLinkMassage(groupUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(user);
        pages.chats().openChatByName(groupContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(groupUrl)
                .clickChatMessage(1);
        if (isAndroid()) { // todo есть задача https://allprojects.atlassian.net/browse/FB-21584
            pages.modal().checkModalTitle(ChatDictionary.NO_ACCESS_NOT_PARTICIPANT);
        } else {
            pages.chat().checkAlertDisplayedOnChatScreen(ChatDictionary.LINK_NOT_ACTIVE);
        }
    }

    @Test
    @AllureIds(ios = "64429", android = "64437")
    @DisplayName("Перейти по ссылке-приглашению открытой группы в пространстве, в котором не состоит")
    public void tapInviteLinkPublicGroupNotParticipantWs() {
        prepareGroup(true);
        UsersEnum user = userFactory.takeUser();
        Long p2pGroupId = ownerBuilder.createP2PGroup(user);
        ownerBuilder.sendLinkMassage(inviteUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(user);
        pages.chats().openChatByName(groupContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(inviteUrl)
                .clickChatMessage(1);
        if (isAndroid()) { // todo есть задача https://allprojects.atlassian.net/browse/FB-21584
            pages.modal().checkModalTitle(ChatDictionary.NO_ACCESS_NOT_PARTICIPANT);
        } else {
            pages.chat().checkAlertDisplayedOnChatScreen(ChatDictionary.NO_ACCESS_NOT_PARTICIPANT);
        }
    }

    @Test
    @AllureIds(ios = "64426", android = "64436")
    @DisplayName("Перейти по ссылке-приглашению закрытой группы в пространстве, в котором не состоит")
    public void tapInviteLinkPrivateGroupNotParticipantWs() {
        prepareGroup(false);
        UsersEnum user = userFactory.takeUser();
        Long p2pGroupId = ownerBuilder.createP2PGroup(user);
        ownerBuilder.sendLinkMassage(inviteUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(user);
        pages.chats().openChatByName(groupContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(inviteUrl)
                .clickChatMessage(1);
        if (isAndroid()) { // todo есть задача https://allprojects.atlassian.net/browse/FB-21584
            pages.modal().checkModalTitle(ChatDictionary.NO_ACCESS_NOT_PARTICIPANT);
        } else {
            pages.chat().checkAlertDisplayedOnChatScreen(ChatDictionary.NO_ACCESS_NOT_PARTICIPANT);
        }
    }

    @Test
    @AllureIds(ios = "51438", android = "50352")
    @DisplayName("Перейти по ссылке открытой группы, если тип группы в момент перехода изменился на "
            + "закрытую (в пространстве) > Снэкбар \"Нет доступа к группе (каналу)\"")
    public void tapLinkPublicGroupTypeChangedToPrivateParticipantWs() {
        prepareGroup(true);
        Long p2pGroupId = ownerBuilder.createP2PGroup(participant);
        ownerBuilder.sendLinkMassage(groupUrl, p2pGroupId);
        ownerBuilder.updateGroup(
                groupContext.getGroupId(),
                groupContext.getWorkspaceId(),
                false,
                groupContext.getGroupName(),
                inviteLink);
        pages.mobileLogin().loginViaUi(participant);
        pages.chats().openChatByName(groupContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(groupUrl)
                .clickChatMessage(1)
                .checkAlertDisplayedOnChatScreen(ChatDictionary.NO_ACCESS_TO_GROUP_OR_CHANNEL);
    }

    @Test
    @AllureIds(ios = "51434", android = "50348")
    @DisplayName("Отправить ссылку пользователю, участнику пространства, но пространство не активно "
            + "> появляется модальное окно \"войти в пространство\"")
    public void openGroupLinkInactiveWorkspaceShowsEnterModal() {
        prepareGroup(true);
        Long p2pGroupId = ownerBuilder.createP2PGroup(participant);
        ownerBuilder.sendLinkMassage(groupUrl, p2pGroupId);
        new TestDataBuilder(participant).deactivateAllSpaces();
        pages.mobileLogin().loginViaUi(participant);
        pages.chats().openChatByName(groupContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(groupUrl)
                .clickChatMessage(1);
        pages.modal().checkModalTitle(ChatDictionary.ENTER_GROUP_INACTIVE_WS)
                .checkModalText(ChatDictionary.ENTER_GROUP_INACTIVE_WS_MSG)
                .clickEnterButton();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-21591
            pages.chat().checkChatScreenDisplayed()
                    .clickChatMessage(1);
        }
        LoaderWaiter.waitForNoLoader();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(groupContext.getGroupName())
                .checkJoinBtnDisplayed(isIos() ? ChatDictionary.JOIN : ChatDictionary.JOIN_GROUP, true)
                .clickJoinBtn(isIos() ? ChatDictionary.JOIN : ChatDictionary.JOIN_GROUP)
                .checkSystemMessage(2, ChatDictionary.YOU_JOINED_GROUP);
    }

    @Test
    @AllureIds(ios = "51432", android = "50346")
    @DisplayName("Открыть в p2p ссылку-приглашение на закрытую группу в пространстве")
    public void openPrivateGroupInviteLinkFromP2pAndJoin() {
        prepareGroup(false);
        Long p2pGroupId = ownerBuilder.createP2PGroup(participant);
        ownerBuilder.sendLinkMassage(inviteUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(participant);
        pages.chats().openChatByName(groupContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(inviteUrl)
                .clickChatMessage(1);
        pages.joinGroupBottomSheet()
                .checkBottomSheetDisplayed()
                .checkGroupName(groupContext.getGroupName())
                .checkWorkspaceName(groupContext.getWorkspaceName())
                .clickJoinGroupButton();
        pages.chat().checkChatScreenDisplayed(groupContext.getGroupName())
                .checkSystemMessage(2, ChatDictionary.YOU_JOINED_GROUP);
    }

    @Test
    @AllureIds(ios = "95943", android = "95947")
    @DisplayName("Из p2p перейти по ссылке-приглашению в закрытую группу в Чатах (хоумспейс)")
    public void openPrivateHomespaceGroupInviteLinkFromP2p() {
        groupContext = groupFactory.privateGroup();
        participant = userFactory.takeUser();
        ownerBuilder = groupContext.getOwnerBuilder();
        inviteLink = ownerBuilder.regenerateInviteLink(groupContext.getGroupId());
        inviteUrl = UrlConstants.getHomespaceGroupInviteUrl(inviteLink);
        Long p2pGroupId = ownerBuilder.createP2PGroup(participant);
        ownerBuilder.sendLinkMassage(inviteUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(participant);
        pages.chats().openChatByName(groupContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(inviteUrl)
                .clickChatMessage(1);
        pages.joinGroupBottomSheet()
                .checkBottomSheetDisplayed()
                .checkGroupName(groupContext.getGroupName())
                .clickJoinGroupButton();
        pages.chat().checkChatScreenDisplayed(groupContext.getGroupName())
                .checkSystemMessage(2, ChatDictionary.YOU_JOINED_GROUP);
    }

    @Test
    @AllureIds(ios = "51437", android = "50351")
    @DisplayName("Открыть в пространстве ссылку-приглашение на открытую группу в пространстве")
    public void openPublicGroupInviteLinkFromWorkspaceMainChat() {
        prepareGroup(true);
        long mainGroupId = getWorkspaceMainGroupId(groupContext.getOwner(), groupContext.getWorkspaceId());
        ownerBuilder.sendLinkMassage(inviteUrl, mainGroupId);
        pages.mobileLogin().loginViaUi(participant);
        pages.bottomMenu().clickWorkspacesButton();
        pages.workspace().checkWorkspaceScreenTitle(groupContext.getWorkspaceName());
        pages.chats().openChatByName("Главный");
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(inviteUrl)
                .clickChatMessage(1);
        pages.joinGroupBottomSheet()
                .checkBottomSheetDisplayed()
                .checkGroupName(groupContext.getGroupName())
                .clickJoinGroupButton();
        pages.chat().checkChatScreenDisplayed(groupContext.getGroupName())
                .checkSystemMessage(2, ChatDictionary.YOU_JOINED_GROUP);
    }

    @Test
    @AllureIds(ios = "51431", android = "50345")
    @DisplayName("Открыть в пространстве ссылку-приглашение на группу в хоумспейсе")
    public void openHomespaceGroupInviteLinkFromWorkspaceMainChat() {
        groupContext = groupFactory.publicGroupInWs();
        long wsId = groupContext.getWorkspaceId();
        participant = userFactory.takeUser();
        ownerBuilder = groupContext.getOwnerBuilder();
        ownerBuilder.addUsersToWorkspace(wsId, participant);
        String homespaceGroupName = TestData.generateHomespaceGroupName();
        long homespaceGroupId = ownerBuilder.createGroup(
                TestDataBuilder.GroupConfig.ofGroup(homespaceGroupName).visible(false));
        groupContext = groupContext.toBuilder()
                .groupId(homespaceGroupId)
                .groupName(homespaceGroupName)
                .build();
        inviteLink = ownerBuilder.regenerateInviteLink(homespaceGroupId);
        inviteUrl = UrlConstants.getHomespaceGroupInviteUrl(inviteLink);
        long mainGroupId = getWorkspaceMainGroupId(groupContext.getOwner(), groupContext.getWorkspaceId());
        ownerBuilder.sendLinkMassage(inviteUrl, mainGroupId);
        pages.mobileLogin().loginViaUi(participant);
        pages.bottomMenu().clickWorkspacesButton();
        pages.workspace().checkWorkspaceScreenTitle(groupContext.getWorkspaceName());
        pages.chats().openChatByName("Главный");
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(inviteUrl)
                .clickChatMessage(1);
        pages.joinGroupBottomSheet()
                .checkBottomSheetDisplayed()
                .checkGroupName(groupContext.getGroupName())
                .clickJoinGroupButton();
        pages.chat().checkChatScreenDisplayed(groupContext.getGroupName())
                .checkSystemMessage(2, ChatDictionary.YOU_JOINED_GROUP);
    }

    private long getWorkspaceMainGroupId(UsersEnum user, long workspaceId) {
        return new GroupApiSteps(user).getUserGroupStates(workspaceId).stream()
                .filter(state -> state.getGroup() != null
                        && "GENERAL".equals(state.getGroup().getType()))
                .map(GetUserGroupStatesResponse::getGroupId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Чат 'Главный' не найден"));
    }

    private void prepareGroup(boolean isPublic) {
        groupContext = isPublic
                ? groupFactory.publicGroupInWs()
                : groupFactory.privateGroupInWs();
        long wsId = groupContext.getWorkspaceId();
        participant = userFactory.takeUser();
        groupContext.getOwnerBuilder().addUsersToWorkspace(wsId, participant);
        ownerBuilder = groupContext.getOwnerBuilder();
        groupUrl = isPublic
                ? UrlConstants.getWorkspacePublicGroupUrl(
                String.valueOf(groupContext.getWorkspaceId()),
                String.valueOf(groupContext.getGroupId()))
                : UrlConstants.getWorkspacePrivateGroupUrl(
                String.valueOf(groupContext.getWorkspaceId()),
                String.valueOf(groupContext.getGroupId()));
        inviteLink = ownerBuilder.regenerateInviteLink(groupContext.getGroupId());
        inviteUrl = UrlConstants.getWorkspaceGroupInviteUrl(inviteLink);
    }
}
