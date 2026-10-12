package org.foxtrot.test.polls;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.enums.SystemMessageType;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.WorkspaceContext;
import org.foxtrot.utils.factories.core.PollFactory;
import org.foxtrot.utils.factories.core.PollFactory.PollConfig;
import org.foxtrot.utils.factories.core.WorkspaceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PollsTest extends BaseTest {

    private static final String GROUP_POLL_TITLE = "Group Poll";
    private static final String GROUP_POLL_OPTION_1 = "Poll group option 1";
    private static final String GROUP_POLL_OPTION_2 = "Poll group option 2";
    private static final String CHANNEL_POLL_TITLE = "Channel Poll";
    private static final String CHANNEL_POLL_OPTION_1 = "Poll channel option 1";
    private static final String CHANNEL_POLL_OPTION_2 = "Poll channel option 2";
    private static final String FUNCTIONAL_GROUP_POLL_TITLE = "Functional Group Poll";
    private static final String FUNCTIONAL_CHANNEL_POLL_TITLE = "Functional Channel Poll";
    private static final String FUNCTIONAL_POLL_OPTION_1 = "Functional option 1";
    private static final String FUNCTIONAL_POLL_OPTION_2 = "Functional option 2";
    private static final String NONPARTICIPANT_GROUP_POLL_TITLE = "NonParticipant Group Poll";
    private static final String OWNER_DELETE_GROUP_POLL_TITLE = "Owner delete group poll";
    private static final String NONPARTICIPANT_VOTE_GROUP_POLL_TITLE = "NonParticipant vote group poll";
    private static final String NONPARTICIPANT_VOTE_CHANNEL_POLL_TITLE = "NonParticipant vote channel poll";
    private static final String ENDED_POLL_TITLE = "Ended Poll (1s)";
    private static final String MULTI_VOTE_POLL_TITLE = "Multi-choice poll (4 options)";
    private static final String MULTI_VOTE_OPTION_1 = "Multi option 1";
    private static final String MULTI_VOTE_OPTION_2 = "Multi option 2";
    private static final String MULTI_VOTE_OPTION_3 = "Multi option 3";
    private static final String MULTI_VOTE_OPTION_4 = "Multi option 4";

    private final LoginHelper loginHelper = new LoginHelper();
    private final PollFactory pollFactory = new PollFactory();
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();

    @AfterEach
    void tearDown() {
        pollFactory.cleanUp();
        workspaceFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "58566", desktop = "58604")
    @DisplayName("Проверить поиск опроса в переписке по названию Опроса")
    public void inChatSearchPollByName() {
        var chats = pollFactory.homespaceGroupAndChannelWithParticipantAndPolls(
                searchGroupPollConfig(), searchChannelPollConfig());
        var groupContext = chats.group();
        var channelContext = chats.channel();

        loginHelper.performUILogin(groupContext.getOwner());
        pages.chatListSteps.clickChatByName(groupContext.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(String.valueOf(groupContext.getMessageId()));
        pages.topbarSteps.clickSearchButton();
        pages.localSearchUnitSteps.clickInputField();
        pages.localSearchUnitSteps.enterSearchText(GROUP_POLL_TITLE);
        pages.localSearchUnitSteps.verifyPollMessageFound(GROUP_POLL_TITLE);

        loginHelper.performUILoginForTest(groupContext.getOwner());
        pages.chatListSteps.clickChatByName(channelContext.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(String.valueOf(channelContext.getMessageId()));
        pages.topbarSteps.clickSearchButton();
        pages.localSearchUnitSteps.clickInputField();
        pages.localSearchUnitSteps.enterSearchText(CHANNEL_POLL_TITLE);
        pages.localSearchUnitSteps.verifyPollMessageFound(CHANNEL_POLL_TITLE);
    }

    @Test
    @ChannelAllureIds(web = "58567", desktop = "58607")
    @DisplayName("Проверить поиск опроса в переписке по названию ответа на Опрос")
    public void inChatSearchPollByAnswerOption() {
        var chats = pollFactory.homespaceGroupAndChannelWithParticipantAndPolls(
                searchGroupPollConfig(), searchChannelPollConfig());
        var groupContext = chats.group();
        var channelContext = chats.channel();

        loginHelper.performUILogin(groupContext.getOwner());
        pages.chatListSteps.clickChatByName(groupContext.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(String.valueOf(groupContext.getMessageId()));
        pages.topbarSteps.clickSearchButton();
        pages.localSearchUnitSteps.clickInputField();
        pages.localSearchUnitSteps.enterSearchText(GROUP_POLL_OPTION_1);
        pages.localSearchUnitSteps.verifyMessageNotFound(GROUP_POLL_OPTION_1);

        loginHelper.performUILoginForTest(groupContext.getOwner());
        pages.chatListSteps.clickChatByName(channelContext.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(String.valueOf(channelContext.getMessageId()));
        pages.topbarSteps.clickSearchButton();
        pages.localSearchUnitSteps.clickInputField();
        pages.localSearchUnitSteps.enterSearchText(CHANNEL_POLL_OPTION_1);
        pages.localSearchUnitSteps.verifyMessageNotFound(CHANNEL_POLL_OPTION_1);
    }

    @Test
    @ChannelAllureIds(web = "58572", desktop = "58606")
    @DisplayName("Проверить поиск опроса в чатах по названию Опроса")
    public void throughChatsSearchPollByName() {
        var chats = pollFactory.homespaceGroupAndChannelWithParticipantAndPolls(
                searchGroupPollConfig(), searchChannelPollConfig());
        var groupContext = chats.group();
        var channelContext = chats.channel();

        loginHelper.performUILogin(groupContext.getOwner());
        pages.chatListSteps.clickChatByName(groupContext.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(String.valueOf(groupContext.getMessageId()));
        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.clickSearchInputField();
        pages.searchUnitSteps.enterSearchText(GROUP_POLL_TITLE);
        pages.searchResultsListSteps.verifyMessageFound(GROUP_POLL_TITLE);

        loginHelper.performUILoginForTest(groupContext.getOwner());
        pages.chatListSteps.clickChatByName(channelContext.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(String.valueOf(channelContext.getMessageId()));
        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.clickSearchInputField();
        pages.searchUnitSteps.enterSearchText(CHANNEL_POLL_TITLE);
        pages.searchResultsListSteps.verifyMessageFound(CHANNEL_POLL_TITLE);
    }

    @Test
    @ChannelAllureIds(web = "58571", desktop = "58609")
    @DisplayName("Проверить поиск опроса в чатах по названию ответа на Опрос")
    public void throughChatsSearchPollByAnswerOption() {
        var chats = pollFactory.homespaceGroupAndChannelWithParticipantAndPolls(
                searchGroupPollConfig(), searchChannelPollConfig());
        var groupContext = chats.group();
        var channelContext = chats.channel();

        loginHelper.performUILogin(groupContext.getOwner());
        pages.chatListSteps.clickChatByName(groupContext.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(String.valueOf(groupContext.getMessageId()));
        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.clickSearchInputField();
        pages.searchUnitSteps.enterSearchText(GROUP_POLL_OPTION_1);
        pages.searchResultsListSteps.verifyMessageFound(GROUP_POLL_OPTION_1);

        loginHelper.performUILoginForTest(groupContext.getOwner());
        pages.chatListSteps.clickChatByName(channelContext.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(String.valueOf(channelContext.getMessageId()));
        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.clickSearchInputField();
        pages.searchUnitSteps.enterSearchText(CHANNEL_POLL_OPTION_1);
        pages.searchResultsListSteps.verifyMessageFound(CHANNEL_POLL_OPTION_1);
    }

    @Test
    @ChannelAllureIds(web = "58776", desktop = "95398")
    @DisplayName("Проверить корректность завершения опроса в соответствии с установленным периодом времени")
    public void verifyPollHasEndedAccordingRequiredTime() {
        var context = pollFactory.privateGroupWithParticipantAndPoll(endedPollConfig());
        context.getParticipantBuilder().votePollFirstOption(context);
        String pollMessageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(pollMessageId);
        pages.pollMessageSteps.waitForPollToEndByMessageId(pollMessageId);
        pages.pollMessageSteps.verifyPollEndedInMessageBubbleById(pollMessageId, ENDED_POLL_TITLE);
    }

    @Test
    @ChannelAllureIds(web = "58775", desktop = "95389")
    @DisplayName("Проверить, что после автоматического завершения опроса не отображается время завершения")
    public void verifyEndedPollDoesNotHaveExpirationTime() {
        var context = pollFactory.privateGroupWithParticipantAndPoll(endedPollConfig());
        context.getParticipantBuilder().votePollFirstOption(context);
        String pollMessageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(pollMessageId);
        pages.pollMessageSteps.waitForPollToEndByMessageId(pollMessageId);
        pages.pollMessageSteps.verifyPollEndedInMessageBubbleById(pollMessageId, ENDED_POLL_TITLE);
    }

    @Test
    @ChannelAllureIds(web = "58773", desktop = "95390")
    @DisplayName("Проверить, что после автоматического завершения опроса нет возможности проголосовать в нем")
    public void verifyEndedPollDoesNotHaveVoteOptions() {
        var context = pollFactory.privateGroupWithParticipantAndPoll(endedPollConfig());
        context.getParticipantBuilder().votePollFirstOption(context);
        String pollMessageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(pollMessageId);
        pages.pollMessageSteps.waitForPollToEndByMessageId(pollMessageId);
        pages.pollMessageSteps.verifyPollEndedInMessageBubbleById(pollMessageId, ENDED_POLL_TITLE);
    }

    @Test
    @ChannelAllureIds(web = "58774", desktop = "95391")
    @DisplayName("Проверить, что при включении настройки 'Таймер завершения опроса' по умолчанию проставляется период - 1 день")
    public void verifyDefaultTimeExpirationIs1DayWhenTimerIsDisabled() {
        var context = pollFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickOpenAttachMenuButton();
        pages.attachContextMenuSteps.clickCreatePollButton();
        pages.createPollModalSteps.verifyExpirationTimerSelectIsHidden();
        pages.createPollModalSteps.enableExpirationTimer();
        pages.createPollModalSteps.verifyDefaultExpirationPeriodOneDay();
    }

    @Test
    @ChannelAllureIds(web = "58761", desktop = "95379")
    @DisplayName("Заполнить поле 'Тема опроса' и одно поле ввода ответа -> Далее")
    public void createPollUnitValidation() {
        var context = pollFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickOpenAttachMenuButton();
        pages.attachContextMenuSteps.clickCreatePollButton();
        pages.createPollModalSteps.insertPollTheme("Poll Name");
        pages.createPollModalSteps.insertFirstOption("1stOption");
        pages.createPollModalSteps.clickNextButton();
        pages.createPollModalSteps.validationMessagePresented();
    }

    @Test
    @ChannelAllureIds(web = "58762", desktop = "95381")
    @DisplayName("Проверить включение настройки выбора количества ответов")
    public void multipleVotesEnabling() {
        var context = pollFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickOpenAttachMenuButton();
        pages.attachContextMenuSteps.clickCreatePollButton();
        pages.createPollModalSteps.insertPollTheme("Poll Name");
        pages.createPollModalSteps.insertFirstOption("1stOption");
        pages.createPollModalSteps.insertSecondOption("2ndOption");
        pages.createPollModalSteps.clickMultiOptionToggle();
        pages.createPollModalSteps.clickNextButton();
        pages.createPollModalSteps.clickCreatePollButton();
        pages.pollMessageSteps.verifyPollCreatedInChatByTitle("Poll Name");
    }

    @Test
    @ChannelAllureIds(web = "58759", desktop = "95382")
    @DisplayName("Проверить возможность создания опроса в чате 'Главный'")
    public void createPollInMainChat() {
        WorkspaceContext wsContext = workspaceFactory.createWorkspace();

        loginHelper.performUILogin(wsContext.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(wsContext.getWsName());
        pages.chatListSteps.clickMainChat();
        pages.inputBarSteps.clickOpenAttachMenuButton();
        pages.attachContextMenuSteps.clickCreatePollButton();
        pages.createPollModalSteps.insertPollTheme("General Poll Name");
        pages.createPollModalSteps.insertFirstOption("1stOption");
        pages.createPollModalSteps.insertSecondOption("2ndOption");
        pages.createPollModalSteps.clickMultiOptionToggle();
        pages.createPollModalSteps.clickNextButton();
        pages.createPollModalSteps.clickCreatePollButton();
        pages.pollMessageSteps.verifyPollCreatedInChatByTitle("General Poll Name");
    }

    @Test
    @ChannelAllureIds(web = "58758", desktop = "95396")
    @DisplayName("Создать опрос в групповом чате")
    public void createPollInGroupChat() {
        var context = pollFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickOpenAttachMenuButton();
        pages.attachContextMenuSteps.clickCreatePollButton();
        pages.createPollModalSteps.insertPollTheme("Group Poll Name");
        pages.createPollModalSteps.insertFirstOption("1stOption");
        pages.createPollModalSteps.insertSecondOption("2ndOption");
        pages.createPollModalSteps.clickNextButton();
        pages.createPollModalSteps.clickCreatePollButton();
        pages.pollMessageSteps.verifyPollCreatedInChatByTitle("Group Poll Name");
    }

    @Test
    @ChannelAllureIds(web = "58760", desktop = "95397")
    @DisplayName("Создать опрос в канале")
    public void createPollInChannel() {
        var context = pollFactory.privateChannelWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickOpenAttachMenuButton();
        pages.attachContextMenuSteps.clickCreatePollButton();
        pages.createPollModalSteps.insertPollTheme("Channel Poll Name");
        pages.createPollModalSteps.insertFirstOption("1stOption");
        pages.createPollModalSteps.insertSecondOption("2ndOption");
        pages.createPollModalSteps.clickNextButton();
        pages.createPollModalSteps.clickCreatePollButton();
        pages.pollMessageSteps.verifyPollCreatedInChatByTitle("Channel Poll Name");
    }

    @AllureId("58765")
    @Test
    @ChannelAllureIds(web = "58765", desktop = "95380")
    @DisplayName("Отменить голос")
    public void cancelVote() {
        var context = pollFactory.homespaceGroupWithAdminAndGuestAndPoll(functionalGroupPollConfig());
        context.getOwnerBuilder().votePollFirstOption(context);
        String pollMessageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(pollMessageId);
        pages.pollMessageSteps.verifyUserVoteIsPresent(pollMessageId);
        pages.chatActions.openMessageContextMenuById(pollMessageId);
        pages.messageBubbleContextMenu.clickCancelVoteButton();
        pages.pollMessageSteps.verifyNoUserVotes(pollMessageId);
    }

    @Test
    @ChannelAllureIds(web = "58766", desktop = "95384")
    @DisplayName("Проверить функцию 'Завершить опрос' в группе")
    public void endPollInGroup() {
        var context = pollFactory.homespaceGroupWithAdminAndGuestAndPoll(functionalGroupPollConfig());
        String pollMessageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(pollMessageId);
        pages.chatActions.openMessageContextMenuById(pollMessageId);
        pages.messageBubbleContextMenu.clickFinishPoll();
        pages.endPollModalSteps.clickFinishPollButton();
        pages.pollMessageSteps.verifyPollEndedById(pollMessageId);
    }

    @Test
    @ChannelAllureIds(web = "58771", desktop = "95385")
    @DisplayName("Проверить функцию 'Завершить опрос' в канале (владелец канала)")
    public void endPollInChannelByOwner() {
        var context = pollFactory.homespaceChannelWithAdminAndPoll(functionalChannelPollConfig());
        String pollMessageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(pollMessageId);
        pages.chatActions.openMessageContextMenuById(pollMessageId);
        pages.messageBubbleContextMenu.clickFinishPoll();
        pages.endPollModalSteps.clickFinishPollButton();
        pages.pollMessageSteps.verifyPollEndedById(pollMessageId);
    }

    @Test
    @ChannelAllureIds(web = "58559", desktop = "58619")
    @DisplayName("Проверить функцию 'Завершить опрос' НЕ участник группы (владелец опроса)")
    public void endPollInGroupByNonParticipant_pollOwner() {
        var context = pollFactory.homespaceGroupWithAdminAndGuestAndPollByGuest(
                PollConfig.of(20004L, NONPARTICIPANT_GROUP_POLL_TITLE, FUNCTIONAL_POLL_OPTION_1,
                        FUNCTIONAL_POLL_OPTION_2, 2678400000L, true));
        String pollMessageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getParticipant());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(pollMessageId);
        pages.chatActions.openMessageContextMenuById(pollMessageId);
        pages.messageBubbleContextMenu.clickFinishPoll();
        pages.endPollModalSteps.clickFinishPollButton();
        pages.pollMessageSteps.verifyPollEndedById(pollMessageId);
    }

    @Test
    @ChannelAllureIds(web = "58768", desktop = "95386")
    @DisplayName("Проверить функцию 'Удалить опрос' администратор группы")
    public void deletePollByGroupAdmin() {
        var context = pollFactory.homespaceGroupWithAdminAndGuestAndPoll(
                PollConfig.of(20003L, "Admin delete group poll", FUNCTIONAL_POLL_OPTION_1,
                        FUNCTIONAL_POLL_OPTION_2, 2678400000L, true));
        String pollMessageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getAdmin());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(pollMessageId);
        pages.chatActions.openMessageContextMenuById(pollMessageId);
        pages.messageBubbleContextMenu.clickDeleteButton();
        pages.deleteMessageModalSteps.clickDeleteButton();
        pages.chatVerify.verifySystemMessage(SystemMessageType.DELETED, "Вы удалили сообщение");
    }

    @Test
    @ChannelAllureIds(web = "58767", desktop = "95387")
    @DisplayName("Проверить функцию 'Удалить опрос' в группе владелец опроса")
    public void deleteByPollOwnerInGroupChat() {
        var context = pollFactory.homespaceGroupWithAdminAndGuestAndPoll(
                PollConfig.of(20005L, OWNER_DELETE_GROUP_POLL_TITLE, FUNCTIONAL_POLL_OPTION_1,
                        FUNCTIONAL_POLL_OPTION_2, 2678400000L, true));
        String pollMessageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(pollMessageId);
        pages.chatActions.openMessageContextMenuById(pollMessageId);
        pages.messageBubbleContextMenu.clickDeleteButton();
        pages.deleteMessageModalSteps.clickDeleteButton();
        pages.chatVerify.verifySystemMessage(SystemMessageType.DELETED, "Вы удалили сообщение");
    }

    @Test
    @ChannelAllureIds(web = "58772", desktop = "95388")
    @DisplayName("Проверить функцию 'Удалить опрос' владелец канала")
    public void deletePollByChannelOwner() {
        var context = pollFactory.homespaceChannelWithAdminAndPoll(
                PollConfig.of(20006L, "Owner delete channel poll", FUNCTIONAL_POLL_OPTION_1,
                        FUNCTIONAL_POLL_OPTION_2, 2678400000L, true));
        String pollMessageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(pollMessageId);
        pages.chatActions.openMessageContextMenuById(pollMessageId);
        pages.messageBubbleContextMenu.clickDeleteButton();
        pages.deleteMessageModalSteps.clickDeleteButton();
        pages.chatVerify.verifySystemMessage(SystemMessageType.DELETED, "удалил(а) публикацию");
    }

    @Test
    @ChannelAllureIds(web = "58763", desktop = "95392")
    @DisplayName("Проверка 'Проголосовать' в группе")
    public void placeAVoteOnGroupChatPoll() {
        var context = pollFactory.privateGroupWithParticipantAndPoll(searchGroupPollConfig());
        String pollMessageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(pollMessageId);
        pages.pollMessageSteps.clickVoteOptionByIndex(pollMessageId, 1);
        pages.pollMessageSteps.clickSubmitVoteButton(pollMessageId);
        pages.pollMessageSteps.verifyUserVoteIsPresent(pollMessageId);
    }

    @Test
    @ChannelAllureIds(web = "58764", desktop = "95393")
    @DisplayName("Проверка 'Проголосовать' в канале")
    public void placeAVoteOnChannelPoll() {
        var context = pollFactory.privateChannelWithParticipantAndPoll(searchChannelPollConfig());
        String pollMessageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(pollMessageId);
        pages.pollMessageSteps.clickVoteOptionByIndex(pollMessageId, 1);
        pages.pollMessageSteps.clickSubmitVoteButton(pollMessageId);
        pages.pollMessageSteps.verifyUserVoteIsPresent(pollMessageId);
    }

    @Test
    @ChannelAllureIds(web = "58769", desktop = "95394")
    @DisplayName("Проверка 'Проголосовать' НЕ участник группы (workspace)")
    public void placeAVoteOnGroupChatPollByNoParticipant() {
        var context = pollFactory.publicGroupInWsWithPollForWorkspaceGuest(
                PollConfig.of(10004L, NONPARTICIPANT_VOTE_GROUP_POLL_TITLE, FUNCTIONAL_POLL_OPTION_1,
                        FUNCTIONAL_POLL_OPTION_2, 2678400000L, true));
        String pollMessageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.groupsAndChannelsButton();
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(pollMessageId);
        pages.pollMessageSteps.clickVoteOptionByIndex(pollMessageId, 1);
        pages.pollMessageSteps.clickSubmitVoteButton(pollMessageId);
        pages.pollMessageSteps.verifyUserVoteIsPresent(pollMessageId);
    }

    @Test
    @ChannelAllureIds(web = "58770", desktop = "95395")
    @DisplayName("Проверка 'Проголосовать' НЕ участник канала (workspace)")
    public void placeAVoteOnChannelPollByNoParticipant() {
        var context = pollFactory.publicChannelInWsWithPollForWorkspaceGuest(
                PollConfig.of(10005L, NONPARTICIPANT_VOTE_CHANNEL_POLL_TITLE, FUNCTIONAL_POLL_OPTION_1,
                        FUNCTIONAL_POLL_OPTION_2, 2678400000L, true));
        String pollMessageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getParticipant());
        pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
        pages.spaceSteps.openSpaceMenu();
        pages.spaceMenuSteps.groupsAndChannelsButton();
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(pollMessageId);
        pages.pollMessageSteps.clickVoteOptionByIndex(pollMessageId, 1);
        pages.pollMessageSteps.clickSubmitVoteButton(pollMessageId);
        pages.pollMessageSteps.verifyUserVoteIsPresent(pollMessageId);
    }

    @AllureId("58506")
    @Test
    @ChannelAllureIds(web = "58506", desktop = "58618")
    @DisplayName("Проголосовать (ответить на все варианты ответа)")
    public void placeAVoteOnEachPollOption() {
        var context = pollFactory.privateGroupWithParticipantAndPoll(fourOptionMultiChoicePollConfig());
        String pollMessageId = String.valueOf(context.getMessageId());

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.pollMessageSteps.scrollToPollMessageById(pollMessageId);
        pages.pollMessageSteps.clickVoteOptionsByText(pollMessageId, List.of(
                MULTI_VOTE_OPTION_1, MULTI_VOTE_OPTION_2, MULTI_VOTE_OPTION_3, MULTI_VOTE_OPTION_4));
        pages.pollMessageSteps.clickSubmitVoteButton(pollMessageId);
        pages.pollMessageSteps.verifyUserVoteIsPresent(pollMessageId);
    }

    private static PollConfig searchGroupPollConfig() {
        return PollConfig.of(10001L, GROUP_POLL_TITLE, GROUP_POLL_OPTION_1, GROUP_POLL_OPTION_2, 2678400000L, false);
    }

    private static PollConfig searchChannelPollConfig() {
        return PollConfig.of(10002L, CHANNEL_POLL_TITLE, CHANNEL_POLL_OPTION_1, CHANNEL_POLL_OPTION_2, 2678400000L, false);
    }

    private static PollConfig functionalGroupPollConfig() {
        return PollConfig.of(20001L, FUNCTIONAL_GROUP_POLL_TITLE, FUNCTIONAL_POLL_OPTION_1,
                FUNCTIONAL_POLL_OPTION_2, 2678400000L, true);
    }

    private static PollConfig functionalChannelPollConfig() {
        return PollConfig.of(20002L, FUNCTIONAL_CHANNEL_POLL_TITLE, FUNCTIONAL_POLL_OPTION_1,
                FUNCTIONAL_POLL_OPTION_2, 2678400000L, true);
    }

    private static PollConfig endedPollConfig() {
        return PollConfig.of(10003L, ENDED_POLL_TITLE, "1", "2", 1000L, false);
    }

    private static PollConfig fourOptionMultiChoicePollConfig() {
        return PollConfig.withOptions(
                30006L,
                MULTI_VOTE_POLL_TITLE,
                List.of(MULTI_VOTE_OPTION_1, MULTI_VOTE_OPTION_2, MULTI_VOTE_OPTION_3, MULTI_VOTE_OPTION_4),
                2678400000L,
                true);
    }
}
