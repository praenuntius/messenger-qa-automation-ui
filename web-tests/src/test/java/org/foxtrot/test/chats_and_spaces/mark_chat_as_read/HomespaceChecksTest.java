package org.foxtrot.test.chats_and_spaces.mark_chat_as_read;

import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class HomespaceChecksTest extends BaseTest {

    private static final String HOME_SPACE = "Чаты";
    private static final String TEST_MESSAGE = "test";

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Tag("MarkAsRead")
    @ChannelAllureIds(web = "54011", desktop = "95447")
    @DisplayName("Переход в чат, отмеченный пользователем как непрочитанный, из списка чатов")
    @ParameterizedTest
    @ValueSource(strings = {"privateGroup", "privateChannel", "publicChannel"})
    public void openChatMarkedAsUnreadFromChatList(String chatType) {
        GroupContext context;
        context = switch (chatType) {
            case "privateChannel" -> groupFactory.privateChannel();
            case "privateGroup" -> groupFactory.privateGroup();
            case "publicChannel" -> groupFactory.publicChannel();
            default -> throw new IllegalStateException("Unexpected value: " + chatType);
        };
        context.getOwnerBuilder().updateGroupUnread(context.getGroupId(), true);

        loginHelper.performUILogin(context.getOwner());

        pages.chatListSteps.verifyChatCounterWithoutCountAndRedColor(context.getGroupName());

        int chatPositionBefore = pages.chatListSteps.getChatPosition(context.getGroupName());

        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatListSteps.verifyChatCountersAbsent(context.getGroupName());

        int chatPositionAfter = pages.chatListSteps.getChatPosition(context.getGroupName());
        pages.assertionsSteps.assertChatPositionNotChanged(chatPositionBefore, chatPositionAfter);

        pages.chatListSteps.openContextMenuByChatName(context.getGroupName());
        pages.chatContextMenuModalSteps.verifyMarkAsUnreadIsVisible();
    }

    @Tag("MarkAsRead")
    @Test
    @ChannelAllureIds(web = "54009", desktop = "53934")
    @DisplayName("Получение сообщения в отмеченный непрочитанным чат закрытой группы")
    public void receiveMessageInChatMarkedAsUnread() {
        GroupContext context = groupFactory.privateGroupWithParticipant();
        context.getOwnerBuilder().updateGroupUnread(context.getGroupId(), true);

        loginHelper.performUILogin(context.getOwner());

        pages.leftSidebarSteps.verifyUnreadBadgeOnSpace(HOME_SPACE);
        pages.chatListSteps.verifyChatCounterWithoutCountAndRedColor(context.getGroupName());

        context.getParticipantBuilder().sendTextMessage(context.getGroupId(), TEST_MESSAGE);

        pages.chatListSteps.verifyChatCounter(context.getGroupName(), "1");
    }

    @Tag("MarkAsRead")
    @ChannelAllureIds(web = "54006", desktop = "95480")
    @DisplayName("Пометить непрочитанным в контекстном меню для чата Homespace, если все сообщения прочитаны")
    @ParameterizedTest
    @ValueSource(strings = {"privateGroup", "privateChannel", "publicChannel"})
    public void markReadChatAsUnreadFromContextMenu(String chatType) {
        GroupContext context;
        context = switch (chatType) {
            case "privateChannel" -> groupFactory.privateChannel();
            case "privateGroup" -> groupFactory.privateGroup();
            case "publicChannel" -> groupFactory.publicChannel();
            default -> throw new IllegalStateException("Unexpected value: " + chatType);
        };
        loginHelper.performUILogin(context.getOwner());
        context.getOwnerBuilder().updateGroupUnread(context.getGroupId(), true);
        pages.chatListSteps.clickChatByName(context.getGroupName());

        pages.chatListSteps.verifyChatCountersAbsent(context.getGroupName());
        pages.chatListSteps.openContextMenuByChatName(context.getGroupName());
        pages.chatContextMenuModalSteps.verifyMarkAsUnreadIsVisible()
                .clickMarkAsUnreadButton();
        pages.chatListSteps.verifyChatCounterWithoutCountAndRedColor(context.getGroupName())
                .openContextMenuByChatName(context.getGroupName());
        pages.chatContextMenuModalSteps.verifyMarkAsReadIsVisible();
    }

    @Tag("MarkAsRead")
    @Test
    @ChannelAllureIds(web = "53772", desktop = "53952")
    @DisplayName("Проставление признака прочитанности в p2p в чате с признаком “Непрочитанности“ (нет непрочитанных сообщений)")
    public void markP2pChatWithUnreadMarkAsRead() {
        GroupContext context = groupFactory.p2pChat();
        String chatName = context.getParticipant().getName();
        context.getOwnerBuilder().sendTextMessage(context.getGroupId(), TEST_MESSAGE);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(chatName);

        pages.chatListSteps.verifyChatCountersAbsent(chatName);
        int chatPositionBefore = pages.chatListSteps.getChatPosition(chatName);

        pages.chatListSteps.openContextMenuByChatName(chatName);
        pages.chatContextMenuModalSteps.verifyMarkAsUnreadIsVisible()
                .clickMarkAsUnreadButton();

        pages.chatListSteps.verifyChatCounterWithoutCountAndRedColor(chatName);
        int chatPositionAfterMarkAsUnread = pages.chatListSteps.getChatPosition(chatName);
        pages.assertionsSteps.assertChatPositionNotChanged(chatPositionBefore, chatPositionAfterMarkAsUnread);

        pages.chatListSteps.openContextMenuByChatName(chatName);
        pages.chatContextMenuModalSteps.verifyMarkAsReadIsVisible()
                .clickMarkAsReadButton();

        pages.chatListSteps.verifyChatCountersAbsent(chatName);
        int chatPositionAfterMarkAsRead = pages.chatListSteps.getChatPosition(chatName);
        pages.assertionsSteps.assertChatPositionNotChanged(chatPositionAfterMarkAsUnread, chatPositionAfterMarkAsRead);

        pages.chatListSteps.openContextMenuByChatName(chatName);
        pages.chatContextMenuModalSteps.verifyMarkAsUnreadIsVisible();
    }
}
