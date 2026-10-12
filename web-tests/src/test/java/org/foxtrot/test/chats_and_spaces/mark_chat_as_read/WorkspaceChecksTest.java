package org.foxtrot.test.chats_and_spaces.mark_chat_as_read;

import io.qameta.allure.AllureId;
import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class WorkspaceChecksTest extends BaseTest {

    private static final String TEST_MESSAGE = "test";
    private static final String MAIN_CHAT = "Главный";

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Tag("MarkAsRead")
    @Test
    @AllureId("53817")
    @DisplayName("Индикатор непрочитанности показывается ярким цветом, если чат замьючен и выставлен признак непрочитанности")
    public void mutedChatWithUnreadMarkHasBrightUnreadIndicator() {
        var ct = groupFactory.privateGroupInWsWithParticipant();
        ct.getOwnerBuilder().updateGroupNotificationOFF(ct.getGroupId(), ct.getWorkspaceId());
        ct.getOwnerBuilder().updateGroupUnread(ct.getGroupId(), ct.getWorkspaceId());

        loginHelper.performUILogin(ct.getOwner());

        pages.leftSidebarSteps.clickSpaceByName(ct.getWorkspaceName());
        pages.chatListSteps.verifyChatCounterWithoutCountAndRedColor(ct.getGroupName());
    }

    @Tag("MarkAsRead")
    @Test
    @AllureId("53806")
    @DisplayName("Недоступность функции \"Пометить непрочитанным\", если уже стоит признак \"непрочитанности\"")
    public void markAsUnreadActionIsUnavailableWhenChatAlreadyMarkedAsUnread() {
        var ct = groupFactory.privateGroupInWsWithParticipant();
        ct.getOwnerBuilder().updateGroupUnread(ct.getGroupId(), ct.getWorkspaceId());

        loginHelper.performUILogin(ct.getOwner());

        pages.leftSidebarSteps.clickSpaceByName(ct.getWorkspaceName());
        pages.chatListSteps.openContextMenuByChatName(ct.getGroupName());
        pages.chatContextMenuModalSteps.verifyMarkAsUnreadIsNotVisible();
    }

    @Tag("MarkAsRead")
    @Test
    @ChannelAllureIds(web = "53809", desktop = "54034")
    @DisplayName("Переход в отмеченный как непрочитанный \"Главный\" чат из списка чатов")
    public void ad() {
        var ct = groupFactory.privateGroupInWs();
        ct.getOwnerBuilder().updateGroupUnread(ct.getGroupId(), ct.getWorkspaceId());
        String mainChat = "Главный";

        loginHelper.performUILogin(ct.getOwner());

        pages.leftSidebarSteps.clickSpaceByName(ct.getWorkspaceName());
        pages.chatListSteps.openContextMenuByChatName(mainChat);
        pages.chatContextMenuModalSteps.clickMarkAsUnreadButton();
        pages.chatListSteps.clickChatByName(ct.getGroupName());

        int chatPositionBefore = pages.chatListSteps.getChatPosition(mainChat);
        pages.chatListSteps.verifyChatCounterWithoutCountAndRedColor(mainChat);

        pages.chatListSteps.clickChatByName(mainChat);
        pages.chatListSteps.verifyChatCountersAbsent(mainChat);

        int chatPositionAfter = pages.chatListSteps.getChatPosition(mainChat);
        pages.assertionsSteps.assertChatPositionNotChanged(chatPositionBefore, chatPositionAfter);

        pages.chatListSteps.openContextMenuByChatName(mainChat);
        pages.chatContextMenuModalSteps.verifyMarkAsReadIsNotVisible()
                .verifyMarkAsUnreadIsVisible();
    }

    @Tag("MarkAsRead")
    @Test
    @ChannelAllureIds(web = "53811", desktop = "95548")
    @DisplayName("Переход в отмеченный как непрочитанный чат Workspace из списка чатов")
    public void openWorkspaceChatMarkedAsUnreadFromChatList() {
        var ct = groupFactory.privateGroupInWs();
        ct.getOwnerBuilder().updateGroupUnread(ct.getGroupId(), ct.getWorkspaceId());

        loginHelper.performUILogin(ct.getOwner());

        pages.leftSidebarSteps.clickSpaceByName(ct.getWorkspaceName());
        pages.chatListSteps.verifyChatCounterWithoutCountAndRedColor(ct.getGroupName())
                .clickChatByName(MAIN_CHAT);

        int chatPositionBefore = pages.chatListSteps.getChatPosition(ct.getGroupName());

        pages.chatListSteps.clickChatByName(ct.getGroupName());
        pages.chatListSteps.verifyChatCountersAbsent(ct.getGroupName());

        int chatPositionAfter = pages.chatListSteps.getChatPosition(ct.getGroupName());
        pages.assertionsSteps.assertChatPositionNotChanged(chatPositionBefore, chatPositionAfter);

        pages.chatListSteps.openContextMenuByChatName(ct.getGroupName());
        pages.chatContextMenuModalSteps.verifyMarkAsUnreadIsVisible()
                .verifyMarkAsReadIsNotVisible();
    }

    @Tag("MarkAsRead")
    @Test
    @ChannelAllureIds(web = "53807", desktop = "54033")
    @DisplayName("Получение сообщения в отмеченный непрочитанным чат закрытой группы Workspace")
    public void receiveMessageInWorkspaceChatMarkedAsUnread() {
        var ct = groupFactory.privateGroupInWsWithParticipant();
        ct.getOwnerBuilder().updateGroupUnread(ct.getGroupId(), ct.getWorkspaceId());

        loginHelper.performUILogin(ct.getOwner());

        pages.leftSidebarSteps.clickSpaceByName(ct.getWorkspaceName());
        pages.chatListSteps.verifyChatCounterWithoutCountAndRedColor(ct.getGroupName());

        ct.getParticipantBuilder().sendTextMessage(ct.getGroupId(), TEST_MESSAGE, String.valueOf(ct.getWorkspaceId()));

        pages.chatListSteps.verifyChatCounter(ct.getGroupName(), "1");
    }

    @Tag("MarkAsRead")
    @ChannelAllureIds(web = "53802", desktop = "95549")
    @DisplayName("Пометить непрочитанным в контекстном меню для чата Workspace, если все сообщения прочитаны")
    @ParameterizedTest
    @ValueSource(strings = {"privateGroup", "publicGroup", "privateChannel", "publicChannel"})
    public void markReadWorkspaceChatAsUnreadFromContextMenu(String chatType) {
        var ct = switch (chatType) {
            case "privateGroup" -> groupFactory.privateGroupInWs();
            case "publicGroup" -> groupFactory.publicGroupInWs();
            case "privateChannel" -> groupFactory.privateChannelInWs();
            case "publicChannel" -> groupFactory.publicChannelInWs();
            default -> throw new IllegalStateException("Unexpected value: " + chatType);
        };
        ct.getOwnerBuilder().updateGroupUnread(ct.getGroupId(), true);
        loginHelper.performUILogin(ct.getOwner());

        pages.leftSidebarSteps.clickSpaceByName(ct.getWorkspaceName());
        pages.chatListSteps.clickChatByName(ct.getGroupName())
                .verifyChatCountersAbsent(ct.getGroupName())
                .clickChatByName(MAIN_CHAT);

        int chatPositionBefore = pages.chatListSteps.getChatPosition(ct.getGroupName());

        pages.chatListSteps.openContextMenuByChatName(ct.getGroupName());
        pages.chatContextMenuModalSteps.verifyMarkAsUnreadIsVisible()
                .clickMarkAsUnreadButton();

        pages.chatListSteps.verifyChatCounterWithoutCountAndRedColor(ct.getGroupName());
        int chatPositionAfter = pages.chatListSteps.getChatPosition(ct.getGroupName());
        pages.assertionsSteps.assertChatPositionNotChanged(chatPositionBefore, chatPositionAfter);

        pages.chatListSteps.openContextMenuByChatName(ct.getGroupName());
        pages.chatContextMenuModalSteps.verifyMarkAsReadIsVisible();
    }

    @Tag("MarkAsRead")
    @Test
    @ChannelAllureIds(web = "53823", desktop = "54026")
    @DisplayName("Проставление признака прочитанности в открытой группе с признаком “Непрочитанности“ (нет непрочитанных сообщений)")
    public void markPublicWorkspaceGroupWithUnreadMarkAsRead() {
        var ct = groupFactory.publicGroupInWs();

        loginHelper.performUILogin(ct.getOwner());

        pages.leftSidebarSteps.clickSpaceByName(ct.getWorkspaceName());
        pages.chatListSteps.clickChatByName(ct.getGroupName())
                .verifyChatCountersAbsent(ct.getGroupName())
                .clickChatByName(MAIN_CHAT);

        int chatPositionBefore = pages.chatListSteps.getChatPosition(ct.getGroupName());

        pages.chatListSteps.openContextMenuByChatName(ct.getGroupName());
        pages.chatContextMenuModalSteps.verifyMarkAsUnreadIsVisible()
                .clickMarkAsUnreadButton();

        pages.chatListSteps.verifyChatCounterWithoutCountAndRedColor(ct.getGroupName());
        int chatPositionAfterMarkAsUnread = pages.chatListSteps.getChatPosition(ct.getGroupName());
        pages.assertionsSteps.assertChatPositionNotChanged(chatPositionBefore, chatPositionAfterMarkAsUnread);

        pages.chatListSteps.openContextMenuByChatName(ct.getGroupName());
        pages.chatContextMenuModalSteps.verifyMarkAsReadIsVisible()
                .clickMarkAsReadButton();

        pages.chatListSteps.verifyChatCountersAbsent(ct.getGroupName());
        int chatPositionAfterMarkAsRead = pages.chatListSteps.getChatPosition(ct.getGroupName());
        pages.assertionsSteps.assertChatPositionNotChanged(chatPositionAfterMarkAsUnread, chatPositionAfterMarkAsRead);

        pages.chatListSteps.openContextMenuByChatName(ct.getGroupName());
        pages.chatContextMenuModalSteps.verifyMarkAsUnreadIsVisible();
    }
}
