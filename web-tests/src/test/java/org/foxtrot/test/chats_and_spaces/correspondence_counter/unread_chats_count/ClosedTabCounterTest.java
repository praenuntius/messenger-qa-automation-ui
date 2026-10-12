package org.foxtrot.test.chats_and_spaces.correspondence_counter.unread_chats_count;

import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WindowType;

import static com.codeborne.selenide.Selenide.closeWindow;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.foxtrot.utils.TestDataBuilder.GroupConfig.ofChannel;
import static org.foxtrot.utils.TestDataBuilder.GroupConfig.ofGroup;

public class ClosedTabCounterTest extends BaseTest {

    private static final String HOME_SPACE = "Чаты";
    private static final String TEST_MESSAGE = "test";

    private static final String GROUP_CHAT_NAME = "GroupChat";
    private static final String CHANNEL_NAME = "Channel";
    private static final String WS_CHANNEL_NAME = "WsChannel";

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "52715", desktop = "82071")
    @DisplayName("Отображение счетчика при поступлении публикаций в канал")
    public void testUnreadCounterOnChannelPublications() {
        var ct = groupFactory.privateChannelWithParticipant();

        loginHelper.performUILogin(ct.getParticipant());

        resetCurrentTab();

        ct.getParticipantBuilder().markSpaceRead(-1l);
        ct.getOwnerBuilder().sendTextMessages(ct.getGroupId(), TEST_MESSAGE, TEST_MESSAGE);

        pages.baseSteps.openStartPage();
        pages.localStorageJsSteps.switchToGroupsCounterMode();
        pages.leftSidebarSteps.verifyUnreadBadgeCountOnSpace(HOME_SPACE, "1");
        pages.chatListSteps.verifyChatCounter(ct.getGroupName(), "2")
                .clickChatByName(ct.getGroupName());
        pages.leftSidebarSteps.verifyUnreadBadgeIsNotPresent(HOME_SPACE);
        pages.chatListSteps.verifyChatCountersAbsent(ct.getGroupName());
    }

    @Test
    @ChannelAllureIds(web = "52716", desktop = "82072")
    @DisplayName("Отображение счетчика при поступлении публикаций в канал пространства")
    public void testUnreadCounterOnWsChannelPublications() {
        var ct = groupFactory.privateChannelInWsWithParticipant();

        loginHelper.performUILogin(ct.getParticipant());

        resetCurrentTab();

        ct.getParticipantBuilder().markSpaceRead(ct.getWorkspaceId());
        ct.getOwnerBuilder().sendTextMessages(ct.getGroupId(), TEST_MESSAGE, TEST_MESSAGE);

        pages.baseSteps.openStartPage();
        pages.localStorageJsSteps.switchToGroupsCounterMode();
        pages.leftSidebarSteps.clickSpaceByName(ct.getWorkspaceName());
        pages.leftSidebarSteps.verifyUnreadBadgeCountOnSpace(ct.getWorkspaceName(), "1");
        pages.chatListSteps.verifyChatCounter(ct.getGroupName(), "2")
                .clickChatByName(ct.getGroupName());
        pages.leftSidebarSteps.verifyUnreadBadgeIsNotPresent(ct.getWorkspaceName());
        pages.chatListSteps.verifyChatCountersAbsent(ct.getGroupName());
    }

    @Test
    @ChannelAllureIds(web = "52712", desktop = "82068")
    @DisplayName("Отображение счетчика при поступлении сообщений в p2p-чат")
    public void testUnreadCounterOnP2pMessage() {
        var ct = groupFactory.p2pChat();

        loginHelper.performUILogin(ct.getParticipant());

        resetCurrentTab();

        ct.getParticipantBuilder().markSpaceRead(-1l);
        ct.getOwnerBuilder().sendTextMessages(ct.getGroupId(), TEST_MESSAGE, TEST_MESSAGE);

        pages.baseSteps.openStartPage();
        pages.localStorageJsSteps.switchToGroupsCounterMode();
        pages.leftSidebarSteps.verifyUnreadBadgeCountOnSpace(HOME_SPACE, "1");
        pages.leftSidebarSteps.verifyUnreadBadgeCountOnP2p(ct.getOwner().getId(),"2");
        pages.chatListSteps.verifyChatCounter(ct.getOwner().getName(), "2")
                .clickChatByName(ct.getOwner().getName());
        pages.leftSidebarSteps.verifyUnreadBadgeIsNotPresent(HOME_SPACE)
                .verifyP2pUnreadBadgeIsNotPresent(ct.getOwner().getId());
        pages.chatListSteps.verifyChatCountersAbsent(ct.getOwner().getName());
    }

    @Test
    @ChannelAllureIds(web = "52713", desktop = "82069")
    @DisplayName("Отображение счетчика при поступлении сообщений в групповой чат")
    public void testUnreadCounterOnGroupPublications() {
        var ct = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(ct.getParticipant());

        resetCurrentTab();

        ct.getParticipantBuilder().markSpaceRead(-1l);
        ct.getOwnerBuilder().sendTextMessages(ct.getGroupId(), TEST_MESSAGE, TEST_MESSAGE);

        pages.baseSteps.openStartPage();
        pages.localStorageJsSteps.switchToGroupsCounterMode();
        pages.leftSidebarSteps.verifyUnreadBadgeCountOnSpace(HOME_SPACE, "1");
        pages.chatListSteps.verifyChatCounter(ct.getGroupName(), "2")
                .clickChatByName(ct.getGroupName());
        pages.leftSidebarSteps.verifyUnreadBadgeIsNotPresent(HOME_SPACE);
        pages.chatListSteps.verifyChatCountersAbsent(ct.getGroupName());
    }

    @Test
    @ChannelAllureIds(web = "52714", desktop = "82070")
    @DisplayName("Отображение счетчика при поступлении сообщений в групповой чат пространства")
    public void testUnreadCounterOnWsGroupPublications() {
        var ct = groupFactory.privateGroupInWsWithParticipant();

        loginHelper.performUILogin(ct.getParticipant());
        resetCurrentTab();
        ct.getParticipantBuilder().markSpaceRead(ct.getWorkspaceId());
        ct.getOwnerBuilder().sendTextMessages(ct.getGroupId(), TEST_MESSAGE, TEST_MESSAGE);

        pages.baseSteps.openStartPage();
        pages.localStorageJsSteps.switchToGroupsCounterMode();
        pages.leftSidebarSteps.clickSpaceByName(ct.getWorkspaceName());
        pages.leftSidebarSteps.verifyUnreadBadgeCountOnSpace(ct.getWorkspaceName(), "1");
        pages.chatListSteps.verifyChatCounter(ct.getGroupName(), "2")
                .clickChatByName(ct.getGroupName());
        pages.leftSidebarSteps.verifyUnreadBadgeIsNotPresent(ct.getWorkspaceName());
        pages.chatListSteps.verifyChatCountersAbsent(ct.getGroupName());
    }

    @Test
    @ChannelAllureIds(web = "52717", desktop = "82073")
    @DisplayName("Отображение счетчика при поступлении сообщений/публикаци в p2p-чат, групповой чат, канал, групповой чат пространства и канал пространства")
    public void testUnreadCountersForAllChatTypesSimultaneously() {
        var baseContext = groupFactory.privateGroupInWsWithParticipant();
        var ownerApi = baseContext.getOwnerBuilder();
        var participantApi = baseContext.getParticipantBuilder();
        var participant = baseContext.getParticipant();
        var workspaceId = baseContext.getWorkspaceId();
        var workspaceName = baseContext.getWorkspaceName();

        Long p2pChatId = ownerApi.createP2PGroup(participant);
        Long groupChatId = ownerApi.createGroup(ofGroup(GROUP_CHAT_NAME));
        ownerApi.addUsers(groupChatId, participant);
        Long channelId = ownerApi.createGroup(ofChannel(CHANNEL_NAME));
        ownerApi.addUsers(channelId, participant);
        Long wsChannelId = ownerApi.createGroup(ofChannel(WS_CHANNEL_NAME).inWorkspace(workspaceId));
        ownerApi.addUsers(wsChannelId, workspaceId, participant);
        Long wsGroupId = baseContext.getGroupId();
        String wsGroupName = baseContext.getGroupName();

        participantApi.markSpaceRead(-1L);
        participantApi.markSpaceRead(workspaceId);
        String workspaceIdStr = String.valueOf(workspaceId);

        loginHelper.performUILogin(participant);

        resetCurrentTab();

        ownerApi.sendTextMessages(p2pChatId, TEST_MESSAGE, TEST_MESSAGE);
        ownerApi.sendTextMessages(groupChatId, TEST_MESSAGE, TEST_MESSAGE);
        ownerApi.sendTextMessages(channelId, TEST_MESSAGE, TEST_MESSAGE);
        ownerApi.sendTextMessage(wsGroupId, TEST_MESSAGE, workspaceIdStr);
        ownerApi.sendTextMessage(wsGroupId, TEST_MESSAGE, workspaceIdStr);
        ownerApi.sendTextMessage(wsChannelId, TEST_MESSAGE, workspaceIdStr);
        ownerApi.sendTextMessage(wsChannelId, TEST_MESSAGE, workspaceIdStr);

        pages.baseSteps.openStartPage();
        pages.localStorageJsSteps.switchToGroupsCounterMode();
        pages.leftSidebarSteps.verifyUnreadBadgeCountOnSpace(HOME_SPACE, "3");
        pages.leftSidebarSteps.verifyUnreadBadgeCountOnP2p(baseContext.getOwner().getId(),"2");
        pages.chatListSteps.verifyChatCounter(GROUP_CHAT_NAME, "2")
                .verifyChatCounter(CHANNEL_NAME, "2")
                .verifyChatCounter(baseContext.getOwner().getName(), "2");

        pages.leftSidebarSteps.clickSpaceByName(workspaceName);
        pages.leftSidebarSteps.verifyUnreadBadgeCountOnSpace(workspaceName, "2");
        pages.chatListSteps.verifyChatCounter(wsGroupName, "2")
                .verifyChatCounter(WS_CHANNEL_NAME, "2")
                .clickChatByName(WS_CHANNEL_NAME)
                .verifyChatCountersAbsent(WS_CHANNEL_NAME)
                .clickChatByName(wsGroupName)
                .verifyChatCountersAbsent(wsGroupName);
        pages.leftSidebarSteps.verifyUnreadBadgeIsNotPresent(workspaceName);

        pages.leftSidebarSteps.clickSpaceByName(HOME_SPACE);
        pages.chatListSteps.clickChatByName(GROUP_CHAT_NAME)
                .verifyChatCountersAbsent(GROUP_CHAT_NAME)
                .clickChatByName(CHANNEL_NAME)
                .verifyChatCountersAbsent(CHANNEL_NAME)
                .clickChatByName(baseContext.getOwner().getName())
                .verifyChatCountersAbsent(baseContext.getOwner().getName());
        pages.leftSidebarSteps.verifyUnreadBadgeIsNotPresent(HOME_SPACE);
    }

    public void resetCurrentTab() {
        switchTo().newWindow(WindowType.TAB);
        switchTo().window(0);
        closeWindow();
        switchTo().window(0);
    }
}