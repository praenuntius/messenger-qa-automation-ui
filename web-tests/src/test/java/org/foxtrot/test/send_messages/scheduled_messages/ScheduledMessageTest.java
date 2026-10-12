package org.foxtrot.test.send_messages.scheduled_messages;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.UrlConstants;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.open;
import static java.lang.System.currentTimeMillis;

@Tag("Scheduled")
@ExtendWith(AllureJunit5.class)
public class ScheduledMessageTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    private GroupContext blockedByContext;

    @AfterEach
    void tearDown() {
        if (blockedByContext != null) {
            blockedByContext.getParticipantBuilder().unBlockUser(blockedByContext.getOwner());
            blockedByContext = null;
        }
        groupFactory.cleanUp();
    }

    @AllureId("72916")
    @ParameterizedTest
    @ValueSource(strings = {"p2p", "group", "channel"})
    @DisplayName("Создание отложенного сообщения")
    public void createScheduledMessage(String chatType) {
        String scheduledText = "Отложенное сообщение" + currentTimeMillis();

        var context = switch (chatType) {
            case "p2p" -> groupFactory.p2pChat();
            case "group" -> groupFactory.privateGroup();
            case "channel" -> groupFactory.privateChannel();
            default -> throw new IllegalArgumentException("Unsupported chat type: " + chatType);
        };

        context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "p2pЧат");
        String chatName = context.getParticipant() != null ? context.getParticipant().getName() : context.getGroupName();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(chatName);
        pages.inputBarSteps.enterMessage(scheduledText);
        pages.inputBarSteps.contextClickSendMessageButton();
        pages.sendButtonContextMenuSteps.clickDeferSending();
        pages.inputBarSteps.clickSendMessageButton();
        pages.scheduleMessageMenuSteps.clickBackButton();
        pages.inputBarSteps.verifyScheduledMessagesButtonVisible();
        pages.chatVerify.verifyMessageText(scheduledText, 61);
    }

    @Disabled("Нужна загрузка файлов")
    @AllureId("72929")
    @ParameterizedTest(name = "Доступность просмотра {0} со страницы отложенных сообщений")
    @ValueSource(strings = {"image", "video"})
    public void viewMediaFromScheduledMessagesPage(String mediaType) {
        var context = groupFactory.privateGroup();
        Long scheduledGroupId = context.getOwnerBuilder().createScheduledGroup(context.getGroupId());
        Long imageMessageId = context.getOwnerBuilder().sendScheduledImage(scheduledGroupId);
        Long videoMessageId = context.getOwnerBuilder().sendScheduledVideo(scheduledGroupId);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();

        switch (mediaType) {
            case "image" -> {
                pages.chatActions.clickMessageById(imageMessageId);
                pages.fullScreenMediaViewSteps.verifyImageIsPresentInFullscreen();
            }
            case "video" -> {
                pages.chatActions.clickMessageById(videoMessageId);
                pages.fullScreenMediaViewSteps.verifyVideoIsPresentInFullscreen();
            }
            default -> throw new IllegalArgumentException("Unsupported media type: " + mediaType);
        }
    }

    @AllureId("72924")
    @Test
    @DisplayName("Изменить время на текущее и дождаться отправки")
    public void rescheduleMessageToNow() {
        var context = groupFactory.privateGroup();
        Long scheduledGroupId = context.getOwnerBuilder().createScheduledGroup(context.getGroupId());
        Long videoMessageId = context.getOwnerBuilder().sendScheduledVideo(scheduledGroupId);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();
        pages.chatActions.openMessageContextMenuById(String.valueOf(videoMessageId));
        pages.scheduledMessageContextMenuSteps.clickChangTimeButton();
        pages.scheduledMessageEditTimeModalSteps.setTodayAsScheduledDate();
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageText("Видео", 65);
    }

    @AllureId("72925")
    @Test
    @DisplayName("Копировать текст отложенного сообщения через контекстное меню")
    public void copyTextRescheduleMessage() {
        var context = groupFactory.privateGroup();

        String scheduledText = "Отложенное сообщение" + currentTimeMillis();

        Long scheduledGroupId = context.getOwnerBuilder().createScheduledGroup(context.getGroupId());
        context.getOwnerBuilder().sendScheduledText(scheduledGroupId, scheduledText);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();
        pages.chatActions.openMessageContextMenu(scheduledText);
        pages.scheduledMessageContextMenuSteps.clickCopyButton();
        pages.chatListSteps.clickSavedChat();
        pages.inputBarSteps.pasteTextIntoInput();
        pages.inputBarSteps.clickSendMessageButton();
        pages.chatVerify.verifyMessageText(scheduledText);
    }

    @AllureId("72923")
    @Test
    @DisplayName("Кликнуть 'Отправить сейчас' в контекстном меню")
    public void sendNowInContextMenuRescheduleMessage() {
        var context = groupFactory.privateGroup();

        String scheduledText = "Отложенное сообщение" + currentTimeMillis();

        Long scheduledGroupId = context.getOwnerBuilder().createScheduledGroup(context.getGroupId());
        Long messageId = context.getOwnerBuilder().sendScheduledText(scheduledGroupId, scheduledText);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();
        pages.scheduleMessageMenuSteps.openMessageContextMenuById(String.valueOf(messageId));
        pages.scheduledMessageContextMenuSteps.clickSendNowButton();
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageText(scheduledText);
    }

    @AllureId("72930")
    @Test
    @DisplayName("Переход по реплаю с экрана отложенных сообщений")
    public void navigateViaReplyFromScheduledMessagesScreen() {
        var context = groupFactory.privateGroup();

        String replyText = "Сообщение для реплая" + currentTimeMillis();

        Long scheduledGroupId = context.getOwnerBuilder().createScheduledGroup(context.getGroupId());
        Long replyMessageId = context.getOwnerBuilder().sendTextMessage(context.getGroupId(), replyText);
        context.getOwnerBuilder().sendScheduledTextWithReply(scheduledGroupId, "reply", replyMessageId);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();
        pages.chatActions.clickReplyInsideMessageByText(replyText);
        pages.topbarSteps.verifyChatName(context.getGroupName());
        pages.chatVerify.verifyMessageText(replyText);
    }

    @AllureId("72920")
    @Test
    @DisplayName("Попытка создания отложенного сообщения в прошедшем времени")
    public void attemptToCreateScheduledMessageInPassTime() {
        int pastMinute = LocalTime.now(ZoneId.of("Europe/Moscow")).getMinute();
        var context = groupFactory.privateGroup();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.enterMessage("Cообщение в прошлое");
        pages.inputBarSteps.contextClickSendMessageButton();
        pages.sendButtonContextMenuSteps.clickDeferSending();
        pages.scheduleMessageMenuSteps.clickMinutePicker();
        pages.scheduleMessageMenuSteps.verifyClickOnMinuteHasNoEffect(pastMinute);
    }

    @AllureId("72922")
    @Test
    @DisplayName("Отображение разделителей 'Запланировано на' для разных дат")
    public void verifyMessageDelimitersAreDisplayed() {
        String dateInOneMonth = LocalDate.now().plusDays(30)
                .format(DateTimeFormatter.ofPattern("d MMMM", new Locale("ru")));

        var context = groupFactory.privateChannelWithAdmin();

        long today = TimeUnit.HOURS.toMillis(1);
        long tomorrow = TimeUnit.DAYS.toMillis(1);
        long after = TimeUnit.DAYS.toMillis(30);

        Long scheduledGroupId = context.getOwnerBuilder().createScheduledGroup(context.getGroupId());

        context.getAdminBuilder().sendScheduledTextAt(scheduledGroupId, "Тест сегодня", today);
        context.getAdminBuilder().sendScheduledTextAt(scheduledGroupId, "Тест завтра", tomorrow);
        context.getAdminBuilder().sendScheduledTextAt(scheduledGroupId, "Тест через месяц", after);

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();

        pages.chatVerify.verifyScheduledForDate("сегодня", true);
        pages.chatVerify.verifyScheduledForDate("завтра", true);
        pages.chatVerify.verifyScheduledForDate(dateInOneMonth, true);
    }


    @AllureId("81203")
    @Test
    @DisplayName("Создание отложенного сообщения админом канала")
    public void createScheduledMessageAsChannelAdmin() {
        var context = groupFactory.privateChannelWithAdmin();

        String scheduledText = "Тест" + currentTimeMillis();

        Long scheduledGroupId = context.getOwnerBuilder().createScheduledGroup(context.getGroupId());

        loginHelper.performUILogin(context.getAdmin());
        context.getAdminBuilder().sendScheduledTextAt(scheduledGroupId, scheduledText, 25000);

        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();
        pages.chatVerify.verifyMessageSent(scheduledText);

        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageText(scheduledText, 65);
    }

    @AllureId("81204")
    @Test
    @DisplayName("Проверить сохранение отложенного сообщения в канале, при понижении роли автора")
    public void scheduledMessagePersistsAfterAuthorDemotion() {
        var context = groupFactory.privateChannelWithAdmin();
        String scheduledText = "Тест" + currentTimeMillis();
        Long scheduledGroupId = context.getOwnerBuilder().createScheduledGroup(context.getGroupId());

        loginHelper.performUILogin(context.getOwner());
        context.getAdminBuilder().sendScheduledTextAt(scheduledGroupId, scheduledText, 15000);

        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();
        pages.chatVerify.verifyMessageSent(scheduledText);
        context.getOwnerBuilder().editAdmin(context.getGroupId(), context.getAdmin(), false);
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageText(scheduledText, 65);
    }

    @AllureId("81205")
    @Test
    @DisplayName("Проверить сохранение отложенного сообщения в канале, при удалении автора")
    public void scheduledMessagePersistsAfterAuthorDeletion() {
        var context = groupFactory.privateChannelWithAdmin();
        String scheduledText = "Тест" + currentTimeMillis();
        Long scheduledGroupId = context.getOwnerBuilder().createScheduledGroup(context.getGroupId());

        loginHelper.performUILogin(context.getOwner());
        context.getAdminBuilder().sendScheduledTextAt(scheduledGroupId, scheduledText, 15000);

        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();
        pages.chatVerify.verifyMessageSent(scheduledText);
        context.getOwnerBuilder().removeUser(context.getGroupId(), context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageText(scheduledText, 65);
    }

    @AllureId("81206")
    @Test
    @DisplayName("Проверить сохранение отложенного сообщения в канале, при выходе владельца канала")
    public void scheduledMessagePersistsAfterChannelOwnerLeaves() {
        var context = groupFactory.privateChannelWithAdmin();
        String scheduledText = "Тест" + currentTimeMillis();
        Long scheduledGroupId = context.getOwnerBuilder().createScheduledGroup(context.getGroupId());

        loginHelper.performUILogin(context.getAdmin());
        context.getAdminBuilder().sendScheduledTextAt(scheduledGroupId, scheduledText, 15000);

        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();
        pages.chatVerify.verifyMessageSent(scheduledText);
        context.getOwnerBuilder().removeUser(context.getGroupId(), context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatVerify.verifyMessageText(scheduledText, 65);
    }

    @AllureId("81292")
    @Test
    @DisplayName("Удаление scheduled-группы после выхода и повторного входа в группу")
    public void scheduledGroupIsDeletedAfterRejoiningGroup() {
        var context = groupFactory.privateGroupWithAdmin();
        String scheduledText = "Тест" + currentTimeMillis();

        Long scheduledGroupId = context.getAdminBuilder().createScheduledGroup(context.getGroupId());
        String invite = context.getOwnerBuilder().regenerateInviteLink(context.getGroupId());
        String link = UrlConstants.getHomespaceGroupInviteUrl(invite);

        loginHelper.performUILogin(context.getAdmin());
        context.getAdminBuilder().sendScheduledTextAt(scheduledGroupId, scheduledText, 60000);
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();
        pages.chatVerify.verifyMessageSent(scheduledText);
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.leaveButton();
        pages.leaveGroupAndChannelModalSteps.clickLeaveButton();
        open(link);
        pages.joinGroupModal.clickDoneButton();
        pages.chatVerify.verifyChatIsEmpty();
        pages.inputBarSteps.clickMessageInputField();
        pages.chatVerify.verifyScheduledMessagesButtonNotVisible();
    }

    @AllureId("81293")
    @Test
    @DisplayName("Удаление scheduled-группы после того как пользователя исключили и добавили в группу заново")
    public void scheduledGroupIsDeletedAfterUserReaddedToGroup() {
        var context = groupFactory.privateGroupWithAdmin();
        String scheduledText = "Тест" + currentTimeMillis();

        Long scheduledGroupId = context.getAdminBuilder().createScheduledGroup(context.getGroupId());

        loginHelper.performUILogin(context.getAdmin());
        context.getAdminBuilder().sendScheduledTextAt(scheduledGroupId, scheduledText, 60000);
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();
        pages.chatVerify.verifyMessageSent(scheduledText);
        pages.chatListSteps.clickChatByName(context.getGroupName());
        context.getOwnerBuilder().removeUser(context.getGroupId(), context.getAdmin());
        context.getOwnerBuilder().addUsers(context.getGroupId(), context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuVerifier.verifyGroupName(context.getGroupName());
        pages.chatVerify.verifyScheduledMessagesButtonNotVisible();
    }

    @AllureId("72927")
    @Test
    @DisplayName("Удаление отложенного сообщения")
    public void deleteScheduledMessage() {
        var context = groupFactory.privateGroupWithAdmin();
        String scheduledText = "Тест" + currentTimeMillis();

        Long scheduledGroupId = context.getAdminBuilder().createScheduledGroup(context.getGroupId());
        long today = TimeUnit.HOURS.toMillis(1);
        long tomorrow = TimeUnit.DAYS.toMillis(1);
        long afterTomorrow = TimeUnit.DAYS.toMillis(2);

        Long deleteMessageId = context.getAdminBuilder().sendScheduledTextAt(scheduledGroupId, scheduledText, tomorrow);
        context.getAdminBuilder().sendScheduledTextAt(scheduledGroupId, scheduledText, afterTomorrow);
        context.getAdminBuilder().sendScheduledTextAt(scheduledGroupId, scheduledText, today);

        loginHelper.performUILogin(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();
        pages.chatVerify.verifyMessageWithIdIsDisplayed(String.valueOf(deleteMessageId), true);
        pages.chatVerify.verifyScheduledForDate("завтра", true);
        pages.chatActions.openMessageContextMenuById(String.valueOf(deleteMessageId));
        pages.scheduledMessageContextMenuSteps.clickDeleteButton();
        pages.deleteMessageModalSteps.clickDeleteButton();
        pages.chatVerify.verifyMessageWithIdIsDisplayed(String.valueOf(deleteMessageId), false);
        pages.chatVerify.verifyScheduledForDate("завтра", false);
    }

    @AllureId("81251")
    @Test
    @DisplayName("Удаление чужого отложенного сообщения админом канала")
    public void adminDeletesForeignScheduledMessage() {
        var context = groupFactory.privateChannelWithAdmin();
        String scheduledText = "Тест" + currentTimeMillis();
        Long scheduledGroupId = context.getOwnerBuilder().createScheduledGroup(context.getGroupId());

        loginHelper.performUILogin(context.getOwner());
        context.getAdminBuilder().sendScheduledTextAt(scheduledGroupId, scheduledText, 60000);

        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();
        pages.chatVerify.verifyMessageSent(scheduledText);
        pages.chatActions.openMessageContextMenu(scheduledText);
        pages.scheduledMessageContextMenuSteps.clickDeleteButton();
        pages.deleteMessageModalSteps.clickDeleteButton();
        pages.scheduleMessageMenuSteps.clickBackButton();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(scheduledText, 10);
    }

    @AllureId("81250")
    @Test
    @DisplayName("Отправить чужое отложенное сообщение админом канала")
    public void adminSendsForeignScheduledMessageNow() {
        var context = groupFactory.privateChannelWithAdmin();
        String scheduledText = "Тест" + currentTimeMillis();
        Long scheduledGroupId = context.getOwnerBuilder().createScheduledGroup(context.getGroupId());

        loginHelper.performUILogin(context.getOwner());
        context.getAdminBuilder().sendScheduledTextAt(scheduledGroupId, scheduledText, 60000);

        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();
        pages.chatVerify.verifyMessageSent(scheduledText);
        pages.chatActions.openMessageContextMenu(scheduledText);
        pages.scheduledMessageContextMenuSteps.clickSendNowButton();
        pages.scheduleMessageMenuSteps.clickBackButton();
        pages.chatVerify.verifyMessageSent(scheduledText);
    }

    @AllureId("81249")
    @Test
    @DisplayName("Изменение времени отправки чужого отложенного сообщения, админом канала")
    public void adminReschedulesForeignScheduledMessage() {
        var context = groupFactory.privateChannelWithAdmin();
        String scheduledText = "Тест" + currentTimeMillis();
        long tomorrow = TimeUnit.DAYS.toMillis(1);
        Long scheduledGroupId = context.getOwnerBuilder().createScheduledGroup(context.getGroupId());

        loginHelper.performUILogin(context.getOwner());
        context.getAdminBuilder().sendScheduledTextAt(scheduledGroupId, scheduledText, tomorrow);

        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.chatActions.clickScheduledMessagesMenu();
        pages.chatVerify.verifyMessageSent(scheduledText);
        pages.chatActions.openMessageContextMenu(scheduledText);
        pages.scheduledMessageContextMenuSteps.clickChangTimeButton();
        pages.scheduledMessageEditTimeModalSteps.setTodayAsScheduledDate();
        pages.scheduleMessageMenuSteps.clickBackButton();
        pages.chatVerify.verifyMessageText(scheduledText, 65);
    }

    @AllureId("81294")
    @Test
    @DisplayName("Удаление scheduled группы в p2p при блокировке")
    public void scheduledGroupIsDeletedInP2pOnUserBlock() {
        var context = groupFactory.p2pChat();
        String testMessage = "Тест " + currentTimeMillis();
        context.getOwnerBuilder().sendTextMessage(context.getGroupId(), "p2pЧат");
        Long scheduledGroupId = context.getOwnerBuilder().createScheduledGroup(context.getGroupId());

        loginHelper.performUILogin(context.getOwner());
        context.getOwnerBuilder().sendScheduledText(scheduledGroupId, testMessage);

        pages.chatListSteps.clickChatByName(context.getParticipant().getName());
        pages.chatActions.clickScheduledMessagesMenu();
        context.getParticipantBuilder().blockUser(context.getOwner());
        blockedByContext = context;
        pages.chatVerify.verifyMessageSent(testMessage);
        pages.chatActions.openMessageContextMenu(testMessage);
        pages.scheduledMessageContextMenuSteps.clickChangTimeButton();
        pages.scheduledMessageEditTimeModalSteps.setTodayAsScheduledDate();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(testMessage, 65);
        pages.scheduleMessageMenuSteps.clickBackButton();
        pages.chatVerify.verifyMessageWithTextIsNotDisplayed(testMessage, 5);
    }

    @AllureId("72919")
    @Test
    @DisplayName("Создать отложенное сообщение с пользователем с которым не было p2p")
    public void createScheduledMessageWithUserWithoutP2pHistory() {
        var context = groupFactory.p2pChat();
        Long scheduledGroupId = context.getOwnerBuilder().createScheduledGroup(context.getGroupId());

        loginHelper.performUILogin(context.getParticipant());
        context.getOwnerBuilder().sendScheduledTextAt(scheduledGroupId, "Тест", 10000);
        pages.chatListSteps.verifyChatWithNameIsNotDisplayed(context.getOwner().getName());
        pages.chatListSteps.verifyChatWithNameIsDisplayed(context.getOwner().getName(), 65);
    }
}