package org.foxtrot.mobile.test.chats_menu.channels_menu;

import org.foxtrot.enums.UsersEnum;
import org.foxtrot.mobile.allure.AllureIds;
import org.foxtrot.mobile.test.BaseMobileTest;
import org.foxtrot.mobile.testdata.dictionary.ChatDictionary;
import org.foxtrot.mobile.utils.LoaderWaiter;
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

public class ChannelsLinkTest extends BaseMobileTest {

    private final GroupFactory groupFactory = new GroupFactory();
    private final UserFactory userFactory = new UserFactory();
    private GroupContext channelContext;
    private TestDataBuilder ownerBuilder;
    private UsersEnum participant;
    private String channelUrl;
    private String inviteLink;
    private String inviteUrl;

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @AllureIds(ios = "94627", android = "64446")
    @DisplayName("Перейти по ссылке из адресной строки на УДАЛЕННЫЙ канал пространства, в котором пользователь СОСТОИТ")
    public void tapLinkDeletedPublicChannelParticipant() {
        prepareChannelInWs(true);
        ownerBuilder.deleteGroup(channelContext.getGroupId(), channelContext.getWorkspaceId());
        Long p2pGroupId = ownerBuilder.createP2PGroup(participant);
        ownerBuilder.sendLinkMassage(channelUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(participant);
        pages.chats().openChatByName(channelContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(channelUrl)
                .clickChatMessage(1);
        if (isIos()) { // todo задача https://allprojects.atlassian.net/browse/FB-21583
            pages.modal().checkModalTitle(ChatDictionary.GROUP_NOT_CREATED);
        } else {
            pages.chat().checkAlertDisplayedOnChatScreen(ChatDictionary.GROUP_CHANNEL_NOT_EXIST);
        }
    }

    @Test
    @AllureIds(ios = "94629", android = "64443")
    @DisplayName("Переход по ссылке из адресной строки на закрытый канал в пространстве, в котором пользователь НЕ состоит")
    public void tapLinkPrivateChannelNotParticipantWs() {
        prepareChannelInWs(false);
        UsersEnum user = userFactory.takeUser();
        Long p2pGroupId = ownerBuilder.createP2PGroup(user);
        ownerBuilder.sendLinkMassage(channelUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(user);
        pages.chats().openChatByName(channelContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(channelUrl)
                .clickChatMessage(1);
        if (isAndroid()) { // todo есть задача https://allprojects.atlassian.net/browse/FB-21584
            pages.modal().checkModalTitle(ChatDictionary.NO_ACCESS_NOT_PARTICIPANT);
        } else {
            pages.chat().checkAlertDisplayedOnChatScreen(ChatDictionary.LINK_NOT_ACTIVE);
        }
    }

    @Test
    @AllureIds(ios = "64425", android = "64440")
    @DisplayName("Переход по ссылке из адресной строки на закрытый канал в пространстве, в котором пользователь СОСТОИТ")
    public void tapLinkPrivateChannelParticipantWs() {
        prepareChannelInWs(false);
        Long p2pGroupId = ownerBuilder.createP2PGroup(participant);
        ownerBuilder.sendLinkMassage(channelUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(participant);
        pages.chats().openChatByName(channelContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(channelUrl)
                .clickChatMessage(1)
                .checkAlertDisplayedOnChatScreen(ChatDictionary.NO_ACCESS_TO_GROUP_OR_CHANNEL);
    }

    @Test
    @AllureIds(ios = "95936", android = "95932")
    @DisplayName("Отправить ссылку пользователю на открытый канал в пространстве, НЕ участнику пространства "
            + "> Ссылка неактивна или у вас нет доступа к пространству")
    public void tapLinkPublicChannelNotParticipantWs() {
        prepareChannelInWs(true);
        UsersEnum user = userFactory.takeUser();
        Long p2pGroupId = ownerBuilder.createP2PGroup(user);
        ownerBuilder.sendLinkMassage(channelUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(user);
        pages.chats().openChatByName(channelContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(channelUrl)
                .clickChatMessage(1);
        if (isAndroid()) { // todo есть задача https://allprojects.atlassian.net/browse/FB-21584
            pages.modal().checkModalTitle(ChatDictionary.NO_ACCESS_NOT_PARTICIPANT);
        } else {
            pages.chat().checkAlertDisplayedOnChatScreen(ChatDictionary.LINK_NOT_ACTIVE);
        }
    }

    @Test
    @AllureIds(ios = "64431", android = "64439")
    @DisplayName("Перейти по ссылке-приглашению открытого канала в пространстве, в котором не состоит")
    public void tapInviteLinkPublicChannelNotParticipantWs() {
        prepareChannelInWs(true);
        UsersEnum user = userFactory.takeUser();
        Long p2pGroupId = ownerBuilder.createP2PGroup(user);
        ownerBuilder.sendLinkMassage(inviteUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(user);
        pages.chats().openChatByName(channelContext.getOwner().getName());
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
    @AllureIds(ios = "64430", android = "64438")
    @DisplayName("Перейти по ссылке-приглашению закрытого канала в пространстве, в котором не состоит")
    public void tapInviteLinkPrivateChannelNotParticipantWs() {
        prepareChannelInWs(false);
        UsersEnum user = userFactory.takeUser();
        Long p2pGroupId = ownerBuilder.createP2PGroup(user);
        ownerBuilder.sendLinkMassage(inviteUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(user);
        pages.chats().openChatByName(channelContext.getOwner().getName());
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
    @AllureIds(ios = "51472", android = "50445")
    @DisplayName("Перейти по ссылке открытого канала, если тип канала в момент перехода изменился на "
            + "закрытый (в пространстве) > тост: \"Нет доступа к группе (каналу)\"")
    public void tapLinkPublicChannelTypeChangedToPrivateParticipantWs() {
        prepareChannelInWs(true);
        Long p2pGroupId = ownerBuilder.createP2PGroup(participant);
        ownerBuilder.sendLinkMassage(channelUrl, p2pGroupId);
        ownerBuilder.updateGroup(
                channelContext.getGroupId(),
                channelContext.getWorkspaceId(),
                false,
                channelContext.getGroupName(),
                inviteLink);
        pages.mobileLogin().loginViaUi(participant);
        pages.chats().openChatByName(channelContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(channelUrl)
                .clickChatMessage(1)
                .checkAlertDisplayedOnChatScreen(ChatDictionary.NO_ACCESS_TO_GROUP_OR_CHANNEL);
    }

    @Test
    @AllureIds(ios = "51467", android = "50440")
    @DisplayName("Отправить ссылку пользователю, участнику пространства, но пространство не активно "
            + "> появляется модальное окно \"войти в пространство\"")
    public void openChannelLinkInactiveWorkspaceShowsEnterModal() {
        prepareChannelInWs(true);
        Long p2pGroupId = ownerBuilder.createP2PGroup(participant);
        ownerBuilder.sendLinkMassage(channelUrl, p2pGroupId);
        new TestDataBuilder(participant).deactivateAllSpaces();
        pages.mobileLogin().loginViaUi(participant);
        pages.chats().openChatByName(channelContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(channelUrl)
                .clickChatMessage(1);
        pages.modal().checkModalTitle(ChatDictionary.ENTER_CHANNEL_INACTIVE_WS)
                .checkModalText(ChatDictionary.ENTER_CHANNEL_INACTIVE_WS_MSG)
                .clickEnterButton();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-21591
            pages.chat().checkChatScreenDisplayed()
                    .clickChatMessage(1);
        }
        LoaderWaiter.waitForNoLoader();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(channelContext.getGroupName())
                .checkSubscribeBtnDisplayed(ChatDictionary.SUBSCRIBE_TO_CHANNEL, true)
                .clickSubscribeBtn(ChatDictionary.SUBSCRIBE_TO_CHANNEL)
                .checkSubscribeBtnDisplayed(ChatDictionary.SUBSCRIBE_TO_CHANNEL, false);
    }

    @Test
    @AllureIds(ios = "95937", android = "95933")
    @DisplayName("Переход по ссылке из адресной строки на открытый канал в пространстве, в котором пользователь СОСТОИТ")
    public void tapLinkPublicChannelParticipantWs() {
        prepareChannelInWs(true);
        Long p2pGroupId = ownerBuilder.createP2PGroup(participant);
        ownerBuilder.sendLinkMassage(channelUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(participant);
        pages.chats().openChatByName(channelContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(channelUrl)
                .clickChatMessage(1);
        pages.chat().checkChatScreenDisplayed(channelContext.getGroupName())
                .checkSubscribeBtnDisplayed(ChatDictionary.SUBSCRIBE_TO_CHANNEL, true)
                .clickSubscribeBtn(ChatDictionary.SUBSCRIBE_TO_CHANNEL)
                .checkSubscribeBtnDisplayed(ChatDictionary.SUBSCRIBE_TO_CHANNEL, false);
    }

    @Test
    @AllureIds(ios = "51470", android = "50443")
    @DisplayName("Из p2p перейти по ссылке на открытый канал в Чатах")
    public void openPublicHomespaceChannelLinkFromP2pAndSubscribe() {
        prepareChannelInHs(true);
        Long p2pGroupId = ownerBuilder.createP2PGroup(participant);
        ownerBuilder.sendLinkMassage(channelUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(participant);
        pages.chats().openChatByName(channelContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(channelUrl)
                .clickChatMessage(1);
        pages.chat().checkChatScreenDisplayed(channelContext.getGroupName())
                .checkSubscribeBtnDisplayed(ChatDictionary.SUBSCRIBE_TO_CHANNEL, true)
                .clickSubscribeBtn(ChatDictionary.SUBSCRIBE_TO_CHANNEL)
                .checkSubscribeBtnDisplayed(ChatDictionary.SUBSCRIBE_TO_CHANNEL, false);
    }

    @Test
    @AllureIds(ios = "51465", android = "50438")
    @DisplayName("Из p2p перейти по ссылке-приглашению в закрытый канал пространства")
    public void openPrivateChannelInviteLinkFromP2pAndJoin() {
        prepareChannelInWs(false);
        Long p2pGroupId = ownerBuilder.createP2PGroup(participant);
        ownerBuilder.sendLinkMassage(inviteUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(participant);
        pages.chats().openChatByName(channelContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(inviteUrl)
                .clickChatMessage(1);
        pages.joinChannelBottomSheet()
                .checkBottomSheetDisplayed()
                .checkChannelName(channelContext.getGroupName())
                .checkWorkspaceName(channelContext.getWorkspaceName())
                .clickSubscribeButton();
        pages.chat().checkChatScreenDisplayed(channelContext.getGroupName())
                .checkSubscribeBtnDisplayed(ChatDictionary.SUBSCRIBE_TO_CHANNEL, false);
    }

    @Test
    @AllureIds(ios = "95939", android = "95935")
    @DisplayName("Из p2p перейти по ссылке-приглашению в закрытый канал в Чатах (хоумспейс)")
    public void tapInviteLinkPrivateChannelHomespace() {
        prepareChannelInHs(false);
        Long p2pGroupId = ownerBuilder.createP2PGroup(participant);
        ownerBuilder.sendLinkMassage(inviteUrl, p2pGroupId);
        pages.mobileLogin().loginViaUi(participant);
        pages.chats().openChatByName(channelContext.getOwner().getName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkMessageDisplayed(inviteUrl)
                .clickChatMessage(1);
        pages.joinChannelBottomSheet()
                .checkBottomSheetDisplayed()
                .checkChannelName(channelContext.getGroupName())
                .clickSubscribeButton();
        pages.chat().checkChatScreenDisplayed(channelContext.getGroupName());
    }

    private void prepareChannelInHs(boolean isPublic) {
        channelContext = isPublic
                ? groupFactory.publicChannel()
                : groupFactory.privateChannel();
        participant = userFactory.takeUser();
        ownerBuilder = channelContext.getOwnerBuilder();
        channelUrl = isPublic
                ? UrlConstants.getHomespaceChannelUrl(String.valueOf(channelContext.getGroupId()))
                : UrlConstants.getHomespacePrivateChannelUrl(String.valueOf(channelContext.getGroupId()));
        inviteLink = ownerBuilder.regenerateInviteLink(channelContext.getGroupId());
        inviteUrl = UrlConstants.getHomespaceGroupInviteUrl(inviteLink);
    }

    private void prepareChannelInWs(boolean isPublic) {
        channelContext = isPublic
                ? groupFactory.publicChannelInWs()
                : groupFactory.privateChannelInWs();
        long wsId = channelContext.getWorkspaceId();
        participant = userFactory.takeUser();
        channelContext.getOwnerBuilder().addUsersToWorkspace(wsId, participant);
        ownerBuilder = channelContext.getOwnerBuilder();
        channelUrl = isPublic
                ? UrlConstants.getWorkspaceChannelUrl(
                String.valueOf(channelContext.getWorkspaceId()),
                String.valueOf(channelContext.getGroupId()))
                : UrlConstants.getWorkspacePrivateChannelUrl(
                String.valueOf(channelContext.getWorkspaceId()),
                String.valueOf(channelContext.getGroupId()));
        inviteLink = ownerBuilder.regenerateInviteLink(channelContext.getGroupId());
        inviteUrl = UrlConstants.getWorkspaceGroupInviteUrl(inviteLink);
    }
}
