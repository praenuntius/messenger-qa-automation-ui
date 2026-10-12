package org.foxtrot.test.send_messages.autodelete_messages;

import com.codeborne.selenide.WebDriverRunner;
import org.foxtrot.allure.ChannelAllureIds;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.BrowserSession;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GroupTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();
    private GroupContext context;
    String msg = "test!@#";

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
        context.getOwnerBuilder().updateMessageAutoDeletion(context.getGroupId());
    }

    @Test
    @ChannelAllureIds(web = "61222", desktop = "64933")
    @DisplayName("Автоудаление текстового сообщения не происходит до момента истечения таймера")
    void textMessageNotDeletedBeforeAutodeleteTimerExpires() {
        WebDriver owner = WebDriverRunner.getWebDriver();
        try (BrowserSession guest = BrowserSession.open()) {
            context = groupFactory.privateGroupWithParticipant();
            BrowserSession.with(owner, () -> {
                loginHelper.performUILogin(context.getOwner());
                pages.chatListSteps.clickChatByName(context.getGroupName());
            });

            guest.run(() -> {
                pages.baseSteps.openStartPage();
                loginHelper.performUILogin(context.getParticipant());
                pages.chatListSteps.clickChatByName(context.getGroupName());
            });

            context.getOwnerBuilder().updateMessageAutoDeletion(context.getGroupId(), 15L);
            context.getOwnerBuilder().sendTextMessage(context.getGroupId(), msg);

            assertTrue(Math.abs(
                    BrowserSession.with(owner, () -> getTimer(msg)) -
                            guest.call(() -> getTimer(msg))) <= 2,
                    "Рассинхрон таймеров!");

            BrowserSession.with(owner, () -> pages.chatVerify.verifyMessageWithTextIsNotDisplayed(msg, 20));
            guest.run(() -> pages.chatVerify.verifyMessageWithTextIsNotDisplayed(msg, 5));
        }
    }

    @Test
    @ChannelAllureIds(web = "61209", desktop = "64938")
    @DisplayName("Автоудаление первого в чате сообщения (оба пользователя удалили чат)")
    void autoDeleteFirstMessageWhenBothUsersClearedChatTest() {
        context = groupFactory.p2pChatWithMessage();
        var ownerBld = context.getOwnerBuilder();
        var participantBld = context.getParticipantBuilder();

        ownerBld.updateMessageAutoDeletion(context.getGroupId(), 10L);
        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getParticipant());
        pages.chatVerify.verifyAutodeleteSystemMessages();

        ownerBld.hideGroup(context.getGroupId());
        participantBld.hideGroup(context.getGroupId());
        Long messageId = ownerBld.sendTextMessage(context.getGroupId(), msg);

        pages.chatListSteps.clickChatByName(context.getParticipant());
        pages.chatActions.openMessageContextMenu(msg);
        pages.messageBubbleContextMenu.verifyAutoDeleteTimerWaitingForRead();

        participantBld.confirmMessage(messageId, context.getGroupId());

        pages.chatActions.openMessageContextMenu(msg);
        pages.messageBubbleContextMenu.verifyAutoDeleteTimerVisible();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(msg, 25);
    }

    @Test
    @ChannelAllureIds(web = "61208", desktop = "64937")
    @DisplayName("Автоудаление первого в чате сообщения (один из пользователей удалил чат)")
    void autoDeleteFirstMessageWhenOneUserClearedChatTest() {
        context = groupFactory.p2pChatWithMessage();
        var ownerBld = context.getOwnerBuilder();
        var participantBld = context.getParticipantBuilder();

        ownerBld.updateMessageAutoDeletion(context.getGroupId(), 10L);
        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getParticipant());
        pages.chatVerify.verifyAutodeleteSystemMessages();

        ownerBld.hideGroup(context.getGroupId());
        Long messageId = ownerBld.sendTextMessage(context.getGroupId(), msg);

        pages.chatListSteps.clickChatByName(context.getParticipant());
        pages.chatActions.openMessageContextMenu(msg);
        pages.messageBubbleContextMenu.verifyAutoDeleteTimerWaitingForRead();

        participantBld.confirmMessage(messageId, context.getGroupId());

        pages.chatActions.openMessageContextMenu(msg);
        pages.messageBubbleContextMenu.verifyAutoDeleteTimerVisible();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(msg, 15);
    }

    @ChannelAllureIds(web = "61213", desktop = "64934")
    @Test
    @DisplayName("Автоудаление текстового сообщения: 5 минут")
    void textMessageAutoDeleteTimerFiveMinutesTest() {
        context = groupFactory.p2pChatWithMessage();
        var ownerBld = context.getOwnerBuilder();
        var participantBld = context.getParticipantBuilder();
        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getParticipant());

        pages.topbarSteps.clickP2pHeader();
        pages.p2pChatMenuSteps.openAutodeleteMessagesMenu();
        pages.autodeleteP2pMessagesMenuSteps.selectAutodelete5Minutes();
        pages.chatVerify.verifyAutodeleteSystemMessages();

        Long messageId = ownerBld.sendTextMessage(context.getGroupId(), msg);
        participantBld.confirmMessage(messageId, context.getGroupId());

        pages.chatActions.openMessageContextMenu(msg);
        pages.messageBubbleContextMenu.verifyTimerStartsFromFiveMinutes();
    }

    @ChannelAllureIds(web = "61206", desktop = "64935")
    @Test
    @DisplayName("Автоудаление текстового сообщения не происходит до момента прочтения сообщения оппонентом")
    void messageAutoDeleteTimerStartsOnlyAfterReading() {
        WebDriver owner = WebDriverRunner.getWebDriver();
        try (BrowserSession guest = BrowserSession.open()) {
            context = groupFactory.p2pChatWithMessage();
            String firstMessage = "Wait for read: " + System.currentTimeMillis();
            String secondMessage = "No timer here: " + System.currentTimeMillis();
            var builder = context.getOwnerBuilder();

            BrowserSession.with(owner, () -> {
                loginHelper.performUILogin(context.getOwner());
                pages.chatListSteps.clickChatByName(context.getParticipant());
            });

            guest.run(() -> {
                pages.baseSteps.openStartPage();
                loginHelper.performUILogin(context.getParticipant());
            });

            BrowserSession.with(owner, () -> {
                builder.updateMessageAutoDeletion(context.getGroupId(), 15L);
                builder.sendTextMessage(context.getGroupId(), firstMessage);

                pages.chatActions.openMessageContextMenu(firstMessage);
                pages.messageBubbleContextMenu.verifyAutoDeleteTimerWaitingForRead();

                builder.updateMessageAutoDeletion(context.getGroupId());
                builder.sendTextMessage(context.getGroupId(), secondMessage);

                pages.chatActions.openMessageContextMenu(secondMessage);
                pages.messageBubbleContextMenu.verifyAutoDeleteNotDisplayed();
            });

            guest.run(() -> {
                pages.chatListSteps.clickChatByName(context.getOwner());

                pages.chatVerify.verifyMessageText(secondMessage);
                pages.chatActions.openMessageContextMenu(secondMessage);
                pages.messageBubbleContextMenu.verifyAutoDeleteNotDisplayed();

                pages.chatActions.openMessageContextMenu(firstMessage);
                pages.messageBubbleContextMenu.verifyAutoDeleteTimerVisible();
            });

            BrowserSession.with(owner, () -> {
                pages.chatActions.openMessageContextMenu(secondMessage);
                pages.messageBubbleContextMenu.verifyAutoDeleteNotDisplayed();

                pages.chatActions.openMessageContextMenu(firstMessage);
                pages.messageBubbleContextMenu.verifyAutoDeleteTimerVisible();

                pages.chatVerify.verifyMessageWithTextIsNotDisplayed(firstMessage, 15);
                pages.chatVerify.verifyMessageText(secondMessage);

            });

            guest.run(() -> {
                pages.chatVerify.verifyMessageWithTextIsNotDisplayed(firstMessage, 15);
                pages.chatVerify.verifyMessageText(secondMessage);
            });
        }
    }

    @ChannelAllureIds(web = "61207", desktop = "64936")
    @Test
    @DisplayName("Автоудаление текстового сообщения происходит при отключении оппонентом автоудаления до истечения таймера")
    void disablingAutoDeleteDoesNotCancelExistingMessageTimers() {
        WebDriver owner = WebDriverRunner.getWebDriver();
        try (BrowserSession guest = BrowserSession.open()) {
            context = groupFactory.p2pChatWithMessage();
            String messageText = "Timer persistence test: " + System.currentTimeMillis();
            var ownerBuilder = context.getOwnerBuilder();
            var guestBuilder = context.getParticipantBuilder();

            BrowserSession.with(owner, () -> {
                loginHelper.performUILogin(context.getOwner());
                ownerBuilder.updateMessageAutoDeletion(context.getGroupId(), 10L);
                pages.chatListSteps.clickChatByName(context.getParticipant());
            });

            guest.run(() -> {
                pages.baseSteps.openStartPage();
                loginHelper.performUILogin(context.getParticipant());
            });

            BrowserSession.with(owner, () -> {
                ownerBuilder.sendTextMessage(context.getGroupId(), messageText);
                pages.chatVerify.verifyMessageText(messageText);
                pages.chatActions.openMessageContextMenu(messageText);
                pages.messageBubbleContextMenu.verifyAutoDeleteTimerWaitingForRead();
            });

            guest.run(() -> {
                pages.chatListSteps.clickChatByName(context.getOwner());
                BrowserSession.with(owner, () -> pages.chatVerify.verifyMessageStatusSeen(messageText));
                pages.chatActions.openMessageContextMenu(messageText);
                pages.messageBubbleContextMenu.verifyAutoDeleteTimerVisible();
            });

            BrowserSession.with(owner, () -> {
                pages.chatActions.openMessageContextMenu(messageText);
                pages.messageBubbleContextMenu.verifyAutoDeleteTimerVisible();

                pages.messageBubbleContextMenu.waitForTimerValueQuietly("05");
                guestBuilder.updateMessageAutoDeletion(context.getGroupId());

                pages.chatActions.openMessageContextMenu(messageText);
                pages.messageBubbleContextMenu.verifyAutoDeleteTimerVisible();
                pages.chatVerify.verifyMessageWithTextIsNotDisplayed(messageText, 45);
            });

            guest.run(() -> pages.chatVerify.verifyMessageWithTextIsNotDisplayed(messageText, 5));
        }
    }

    @ChannelAllureIds(web = "61212", desktop = "95814")
    @Test
    @DisplayName("Сохранение сообщений между двумя группами автоудаления")
    void messagesShouldPersistInMiddleOfAutoDeleteSequence() {
        context = groupFactory.p2pChatWithMessage();
        var ownerBld = context.getOwnerBuilder();
        var participantBld = context.getParticipantBuilder();
        Long groupId = context.getGroupId();
        String deleteMsg = "Удаляемое!@#";
        String firstMsg = "Неудаляемое 1";
        String secondMsg = "Неудаляемое 2";

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getParticipant());

        ownerBld.updateMessageAutoDeletion(groupId, 300L);
        pages.chatVerify.verifyAutodeleteSystemMessages();
        pages.inputBarSteps.verifyAutoDeleteIndicatorIsVisible();
        ownerBld.updateMessageAutoDeletion(groupId, 10L);

        participantBld.confirmMessage(ownerBld.sendTextMessage(groupId, deleteMsg), groupId);
        pages.chatVerify.verifyMessageRead(deleteMsg);
        pages.chatActions.openMessageContextMenu(deleteMsg);
        pages.messageBubbleContextMenu.verifyAutoDeleteTimerVisible();

        ownerBld.updateMessageAutoDeletion(groupId);
        pages.messageBubbleContextMenu.verifyAutoDeleteTimerVisible();
        pages.chatVerify.verifyAutodeleteDisabledSystemMessage();
        pages.inputBarSteps.verifyAutoDeleteIndicatorIsNotVisible();

        ownerBld.sendTextMessage(groupId, firstMsg);
        ownerBld.sendTextMessage(groupId, secondMsg);

        pages.inputBarSteps.clickMessageInputField();
        pages.chatActions.openMessageContextMenu(firstMsg);
        pages.messageBubbleContextMenu.verifyAutoDeleteNotDisplayed();
        pages.chatActions.openMessageContextMenu(secondMsg);
        pages.messageBubbleContextMenu.verifyAutoDeleteNotDisplayed();

        ownerBld.updateMessageAutoDeletion(groupId, 300L);
        pages.chatVerify.verifyAutodeleteSystemMessages();
        pages.inputBarSteps.verifyAutoDeleteIndicatorIsVisible();
        ownerBld.updateMessageAutoDeletion(groupId, 5L);

        Long secondDeleteMsgId = ownerBld.sendTextMessage(groupId, deleteMsg);
        participantBld.confirmMessage(secondDeleteMsgId, groupId);
        pages.chatVerify.verifyMessageRead(deleteMsg);
        pages.chatActions.openMessageContextMenuById(String.valueOf(secondDeleteMsgId));
        pages.messageBubbleContextMenu.verifyAutoDeleteTimerVisible();

        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(deleteMsg, 15);
        pages.chatVerify.verifyMessageSent(firstMsg);
        pages.chatVerify.verifyMessageSent(secondMsg);
    }

    @ChannelAllureIds(web = "61210", desktop = "95815")
    @Test
    @DisplayName("Автоудаление: сохранение сообщений выше (до) зоны автоудаления")
    void messagesShouldPersistAtStartOfAutoDeleteSequence() {
        context = groupFactory.p2pChat();
        var ownerBld = context.getOwnerBuilder();
        var participantBld = context.getParticipantBuilder();
        Long groupId = context.getGroupId();
        for (int i = 1; i <= 3; i++) {
            ownerBld.sendTextMessage(groupId, "Неудаляемое до " + i);
        }

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getParticipant());
        pages.chatActions.openMessageContextMenu("Неудаляемое до 3");
        pages.messageBubbleContextMenu.verifyAutoDeleteNotDisplayed();

        ownerBld.updateMessageAutoDeletion(groupId, 300L);
        pages.chatVerify.verifyAutodeleteSystemMessages();
        pages.inputBarSteps.verifyAutoDeleteIndicatorIsVisible();
        ownerBld.updateMessageAutoDeletion(groupId, 15L);

        participantBld.confirmMessage(ownerBld.sendTextMessage(groupId, "Удалить 1"), groupId);
        participantBld.confirmMessage(ownerBld.sendTextMessage(groupId, "Удалить 2"), groupId);

        pages.chatActions.openMessageContextMenu("Удалить 2");
        pages.messageBubbleContextMenu.verifyAutoDeleteTimerVisible();
        pages.chatListSteps.clickChatByName(context.getParticipant());

        pages.chatVerify.verifyMessageWithTextIsNotDisplayed("Удалить 1", 25);
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed("Удалить 2", 25);
        for (int i = 1; i <= 3; i++) {
            pages.chatVerify.verifyMessageSent("Неудаляемое до " + i);
        }
    }

    @ChannelAllureIds(web = "61211", desktop = "95816")
    @Test
    @DisplayName("Автоудаление: сохранение сообщений ниже (после) зоны автоудаления")
    void messagesShouldPersistAtEndOfAutoDeleteSequence() {
        context = groupFactory.p2pChatWithMessage();
        var ownerBld = context.getOwnerBuilder();
        var participantBld = context.getParticipantBuilder();
        Long groupId = context.getGroupId();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getParticipant());

        ownerBld.updateMessageAutoDeletion(groupId, 300L);
        pages.chatVerify.verifyAutodeleteSystemMessages();
        pages.inputBarSteps.verifyAutoDeleteIndicatorIsVisible();
        ownerBld.updateMessageAutoDeletion(groupId, 15L);

        participantBld.confirmMessage(ownerBld.sendTextMessage(groupId, "Удалить 1"), groupId);
        participantBld.confirmMessage(ownerBld.sendTextMessage(groupId, "Удалить 2"), groupId);

        pages.chatActions.openMessageContextMenu("Удалить 2");
        pages.messageBubbleContextMenu.verifyAutoDeleteTimerVisible();
        pages.chatListSteps.clickChatByName(context.getParticipant());

        ownerBld.updateMessageAutoDeletion(groupId);
        pages.chatVerify.verifyAutodeleteDisabledSystemMessage();
        pages.inputBarSteps.verifyAutoDeleteIndicatorIsNotVisible();

        for (int i = 1; i <= 3; i++) {
            ownerBld.sendTextMessage(groupId, "Неудаляемое после " + i);
        }

        pages.chatActions.openMessageContextMenu("Неудаляемое после 3");
        pages.messageBubbleContextMenu.verifyAutoDeleteNotDisplayed();
        pages.chatListSteps.clickChatByName(context.getParticipant());

        pages.chatVerify.verifyMessageWithTextIsNotDisplayed("Удалить 1", 20);
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed("Удалить 2", 1);

        for (int i = 1; i <= 3; i++) {
            pages.chatVerify.verifyMessageSent("Неудаляемое после " + i);
        }
    }

    private int getTimer(String msg) {
        pages.chatActions.openMessageContextMenu(msg);
        return Integer.parseInt(
                pages.messageBubbleContextMenu.getAutoDeleteTimerText().replaceAll(".*:", "").trim());
    }
}