package org.foxtrot.test.chats_and_spaces;

import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.pages.key_interactions.PressKeys;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.foxtrot.utils.TestDataBuilder.GroupConfig.ofGroup;
import static org.foxtrot.utils.UrlConstants.*;

public class MarkSpaceAsReadTest extends BaseTest {

    private static final String HOME_SPACE = "homeSpace";
    private static final String WORK_SPACE = "workSpace";
    private static final String HOME_SPACE_NAME = "Чаты";
    private static final long NO_WORKSPACE_ID = -1L;

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @ChannelAllureIds(web = "95726", desktop = "82781")
    @ParameterizedTest
    @ValueSource(strings = {HOME_SPACE, WORK_SPACE})
    @DisplayName("Индикатор непрочитанности остается после перелогина")
    public void testUnreadBadgePersistsAfterRelogin(String spaceType) {
        GroupContext context = createContext(spaceType);
        context.getParticipantBuilder().sendTextMessage(context.getGroupId(), "txt");

        loginHelper.performUILogin(context.getOwner());
        String spaceName = getSpaceName(context, spaceType);
        pages.leftSidebarSteps.verifyUnreadBadgeOnSpace(spaceName);

        loginHelper.performUILoginForTest(context.getOwner());
        pages.leftSidebarSteps.verifyUnreadBadgeOnSpace(spaceName);
    }

    @ChannelAllureIds(web = "95727", desktop = "82784")
    @ParameterizedTest
    @ValueSource(strings = {HOME_SPACE, WORK_SPACE})
    @DisplayName("Пометить прочитанным, когда в пространстве есть всё: непрочитанные незамьюченные/замьюченные чаты, чаты с меткой непрочитанности")
    public void markSpaceAsReadMixedChatsTest(String spaceType) {
        GroupContext context = createContext(spaceType);
        Long wsId = getWsId(context, spaceType);
        String spaceName = getSpaceName(context, spaceType);

        var ownerApi = context.getOwnerBuilder();
        var participantApi = context.getParticipantBuilder();

        Long mutedChatId = ownerApi.createGroup(ofGroup("MutedChat").inWorkspace(wsId));
        Long markedChatId = ownerApi.createGroup(ofGroup("MarkedChat").inWorkspace(wsId));

        ownerApi.updateGroupNotificationOFF(mutedChatId, wsId);
        ownerApi.addUsers(mutedChatId, wsId, context.getParticipant());
        ownerApi.updateGroupUnread(markedChatId, wsId);

        participantApi.sendTextMessage(context.getGroupId(), "msg1", String.valueOf(wsId));
        participantApi.sendTextMessage(mutedChatId, "msg2", String.valueOf(wsId));

        performMarkAsReadAndVerify(context, spaceName, "MutedChat", "MarkedChat");

        loginHelper.performUILoginForTest(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(spaceName);

        pages.chatListSteps.clickChatByName("MutedChat");
        pages.chatVerify.verifyMessageStatusSeen("msg2");

        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageStatusSeen("msg1");
    }

    @ChannelAllureIds(web = "95728", desktop = "82780")
    @ParameterizedTest
    @ValueSource(strings = {HOME_SPACE, WORK_SPACE})
    @DisplayName("Пометить прочитанным, когда в пространстве есть только непрочитанные замьюченные чаты")
    public void markSpaceAsReadWithOnlyMutedChatsTest(String spaceType) {
        GroupContext context = createContext(spaceType);
        Long wsId = getWsId(context, spaceType);
        String spaceName = getSpaceName(context, spaceType);

        var ownerApi = context.getOwnerBuilder();
        var participantApi = context.getParticipantBuilder();

        Long mutedChatId2 = ownerApi.createGroup(ofGroup("MutedChat2").inWorkspace(wsId));
        Long mutedChatId3 = ownerApi.createGroup(ofGroup("MutedChat3").inWorkspace(wsId));

        ownerApi.updateGroupNotificationOFF(context.getGroupId(), wsId);
        ownerApi.updateGroupNotificationOFF(mutedChatId2, wsId);
        ownerApi.updateGroupNotificationOFF(mutedChatId3, wsId);

        ownerApi.addUsers(mutedChatId2, wsId, context.getParticipant());
        ownerApi.addUsers(mutedChatId3, wsId, context.getParticipant());

        String wsIdStr = String.valueOf(wsId);
        participantApi.sendTextMessage(context.getGroupId(), "msg", wsIdStr);
        participantApi.sendTextMessage(mutedChatId2, "msg", wsIdStr);
        participantApi.sendTextMessage(mutedChatId3, "msg", wsIdStr);

        loginHelper.performUILogin(context.getOwner());

        pages.leftSidebarSteps.openSpaceContextMenu(spaceName);
        pages.spaceContextMenuSteps.clickMarkAsReadButton();

        pages.leftSidebarSteps.openSpaceContextMenu(spaceName);
        pages.spaceContextMenuSteps.checkMarkAsReadButtonIsDisabled();
        PressKeys.pressEscape();

        pages.leftSidebarSteps.verifyUnreadBadgeIsNotPresent(spaceName);
        pages.leftSidebarSteps.clickSpaceByName(spaceName);
        pages.chatListSteps.verifyChatCountersAbsent(context.getGroupName(), "MutedChat2", "MutedChat3");

        loginHelper.performUILoginForTest(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(spaceName);

        for (String chatName : new String[]{context.getGroupName(), "MutedChat2", "MutedChat3"}) {
            pages.chatListSteps.clickChatByName(chatName);
            pages.chatVerify.verifyMessageStatusSeen("msg");
        }
    }

    @ChannelAllureIds(web = "95729", desktop = "82779")
    @ParameterizedTest
    @ValueSource(strings = {HOME_SPACE, WORK_SPACE})
    @DisplayName("Пометить прочитанным, когда в пространстве есть только непрочитанные незамьюченные чаты")
    public void markAsReadWhenSpaceHasOnlyUnmutedChats(String spaceType) {
        GroupContext context = createContext(spaceType);
        Long wsId = getWsId(context, spaceType);
        String spaceName = getSpaceName(context, spaceType);

        var ownerApi = context.getOwnerBuilder();
        var participantApi = context.getParticipantBuilder();

        Long chatId2 = ownerApi.createGroup(ofGroup("Chat2").inWorkspace(wsId));
        Long chatId3 = ownerApi.createGroup(ofGroup("Chat3").inWorkspace(wsId));

        ownerApi.addUsers(chatId2, wsId, context.getParticipant());
        ownerApi.addUsers(chatId3, wsId, context.getParticipant());

        String wsIdStr = String.valueOf(wsId);
        participantApi.sendTextMessage(context.getGroupId(), "msg", wsIdStr);
        participantApi.sendTextMessage(chatId2, "msg", wsIdStr);
        participantApi.sendTextMessage(chatId3, "msg", wsIdStr);

        performMarkAsReadAndVerify(context, spaceName, context.getGroupName(), "Chat2", "Chat3");

        loginHelper.performUILoginForTest(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(spaceName);
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageStatusSeen("msg");
    }

    @ChannelAllureIds(web = "95730", desktop = "82782")
    @ParameterizedTest
    @ValueSource(strings = {HOME_SPACE, WORK_SPACE})
    @DisplayName("Пометить прочитанным, когда в пространстве есть только чаты, помеченные непрочитанными")
    public void markAsReadWhenSpaceHasOnlyMarkedAsUnreadChats(String spaceType) {
        GroupContext context = createContext(spaceType);
        Long wsId = getWsId(context, spaceType);
        String spaceName = getSpaceName(context, spaceType);

        var ownerApi = context.getOwnerBuilder();

        Long markedChatId2 = ownerApi.createGroup(ofGroup("MarkedChat2").inWorkspace(wsId));
        Long markedChatId3 = ownerApi.createGroup(ofGroup("MarkedChat3").inWorkspace(wsId));

        ownerApi.updateGroupUnread(context.getGroupId(), wsId);
        ownerApi.updateGroupUnread(markedChatId2, wsId);
        ownerApi.updateGroupUnread(markedChatId3, wsId);

        performMarkAsReadAndVerify(context, spaceName, context.getGroupName(), "MarkedChat2", "MarkedChat3");
    }

    @ChannelAllureIds(web = "95781", desktop = "95778")
    @ParameterizedTest
    @ValueSource(strings = {HOME_SPACE, WORK_SPACE})
    @DisplayName("Метка 'Непрочитанное' не отображается при открытии чата")
    public void unreadMarkRemovesWhenOpeningChat(String spaceType) {
        GroupContext context = createContext(spaceType);
        Long wsId = getWsId(context, spaceType);
        String wsName = getSpaceName(context, spaceType);

        context.getParticipantBuilder().sendTextMessage(context.getGroupId(), "msg", wsId);
        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(wsName);

        pages.chatListSteps.verifyChatCounter(context.getGroupName(), "1");
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatListSteps.verifyChatCountersAbsent(context.getGroupName());
    }

    @ChannelAllureIds(web = "95778", desktop = "95778")
    @ParameterizedTest
    @ValueSource(strings = {HOME_SPACE, WORK_SPACE})
    @DisplayName("Метка 'Непрочитанное' не отображается после 'Пометить прочитанным'")
    public void unreadMarkRemovesAfterMarkAsRead(String spaceType) {
        GroupContext context = createContext(spaceType);
        Long wsId = getWsId(context, spaceType);
        String wsName = getSpaceName(context, spaceType);

        context.getParticipantBuilder().sendTextMessage(context.getGroupId(), "msg", wsId);
        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(wsName);

        pages.chatListSteps.verifyChatCounter(context.getGroupName(), "1");
        pages.chatListSteps.openContextMenuByChatName(context.getGroupName());
        pages.chatContextMenuModalSteps.clickMarkAsReadButton();
        pages.chatListSteps.verifyChatCountersAbsent(context.getGroupName());
    }

    @ChannelAllureIds(web = "95783", desktop = "95782")
    @ParameterizedTest
    @ValueSource(strings = {HOME_SPACE, WORK_SPACE})
    @DisplayName("Метка 'Непрочитанное' не отображается при переходе из глобального поиска")
    public void unreadMarkRemovesWhenOpeningFromGlobalSearch(String spaceType) {
        GroupContext context = createContext(spaceType);
        Long wsId = getWsId(context, spaceType);
        String wsName = getSpaceName(context, spaceType);

        Long messageId = context.getParticipantBuilder().sendTextMessage(context.getGroupId(), "msg", wsId);
        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(wsName);
        pages.chatListSteps.verifyChatCounter(context.getGroupName(), "1");
        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(context.getGroupName());
        pages.searchResultsListSteps.clickEntityByGroupName(context.getGroupName());
        pages.chatVerify.verifyMessageTextById(String.valueOf(messageId), "msg");
        pages.leftSidebarSteps.clickSpaceByName(wsName);
        pages.chatListSteps.verifyChatCountersAbsent(context.getGroupName());
    }

    @ChannelAllureIds(web = "95786", desktop = "95786")
    @ParameterizedTest
    @ValueSource(strings = {HOME_SPACE, WORK_SPACE})
    @DisplayName("Метка 'Непрочитанное' не отображается при переходе по упоминанию")
    public void unreadMarkRemovesWhenOpeningByMention(String spaceType) {
        GroupContext context = createContext(spaceType);
        Long wsId = getWsId(context, spaceType);
        String wsName = getSpaceName(context, spaceType);

        context.getParticipantBuilder().sendTextMessage(context.getGroupId(), "msg", wsId);
        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(wsName);

        pages.chatListSteps.verifyChatCounter(context.getGroupName(), "1");
        Long mentionId = context.getOwnerBuilder().sendGroupMentionMessage(
                context.getOwner().getSavedChatId(), context.getGroupName(), context.getGroupId());
        pages.leftSidebarSteps.clickChatsButton();
        pages.chatListSteps.clickSavedChat();
        pages.chatActions.clickOnLinkInMessageById(mentionId);
        pages.chatListSteps.verifyChatCountersAbsent(context.getGroupName());
    }

    @ChannelAllureIds(web = "95784", desktop = "95784")
    @ParameterizedTest
    @ValueSource(strings = {HOME_SPACE, WORK_SPACE})
    @DisplayName("Метка 'Непрочитанное' не отображается при переходе по ссылке")
    public void unreadMarkRemovesWhenOpeningByLink(String spaceType) {
        GroupContext context = createContext(spaceType);
        Long wsId = getWsId(context, spaceType);
        String wsName = getSpaceName(context, spaceType);
        context.getParticipantBuilder().sendTextMessage(context.getGroupId(),"msg", wsId);
        String link = getWorkspacePrivateChannelUrl(String.valueOf(wsId),String.valueOf(context.getGroupId()));

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(wsName);
        pages.chatListSteps.verifyChatCounter(context.getGroupName(), "1");

        Long linkId =  context.getOwnerBuilder().sendLinkMassage(link, context.getOwner().getSavedChatId());
        pages.leftSidebarSteps.clickChatsButton();
        pages.chatListSteps.clickSavedChat();
        pages.chatActions.clickOnLinkInMessageById(linkId);
        pages.chatListSteps.verifyChatCountersAbsent(context.getGroupName());
    }

    private void performMarkAsReadAndVerify(GroupContext context, String spaceName, String... chatNames) {
        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.verifyUnreadBadgeOnSpace(spaceName);

        pages.leftSidebarSteps.openSpaceContextMenu(spaceName);
        pages.spaceContextMenuSteps.clickMarkAsReadButton();

        pages.leftSidebarSteps.openSpaceContextMenu(spaceName);
        pages.spaceContextMenuSteps.checkMarkAsReadButtonIsDisabled();
        PressKeys.pressEscape();

        pages.leftSidebarSteps.verifyUnreadBadgeIsNotPresent(spaceName);
        pages.leftSidebarSteps.clickSpaceByName(spaceName);
        pages.chatListSteps.verifyChatCountersAbsent(chatNames);
    }

    private GroupContext createContext(String spaceType) {
        if (WORK_SPACE.equals(spaceType)) {
            return groupFactory.privateGroupInWsWithParticipant();
        }
        return groupFactory.privateGroupWithParticipant();
    }

    private Long getWsId(GroupContext context, String spaceType) {
        return WORK_SPACE.equals(spaceType) ? context.getWorkspaceId() : NO_WORKSPACE_ID;
    }

    private String getSpaceName(GroupContext context, String spaceType) {
        return HOME_SPACE.equals(spaceType) ? HOME_SPACE_NAME : context.getWorkspaceName();
    }
}