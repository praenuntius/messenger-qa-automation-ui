package org.foxtrot.test.profile;

import io.qameta.allure.Allure;
import io.qameta.allure.junit5.AllureJunit5;
import io.qameta.allure.model.Label;
import org.foxtrot.api.DataGenerator;
import org.foxtrot.api.dto.group.AddUsersRequest;
import org.foxtrot.api.dto.group.CreateCommentGroupRequest;
import org.foxtrot.api.dto.group.CreateGroupRequest;
import org.foxtrot.api.dto.group.CreateP2PGroupRequest;
import org.foxtrot.api.dto.group.response.CreateCommentGroupResponse;
import org.foxtrot.api.dto.group.response.CreateGroupResponse;
import org.foxtrot.api.dto.group.response.CreateP2PGroupResponse;
import org.foxtrot.api.dto.massage.SendTextMassageRequest;
import org.foxtrot.api.dto.massage.response.SendTextMassageResponse;
import org.foxtrot.api.dto.message.PinMessageRequest;
import org.foxtrot.api.dto.message.TypingRequest;
import org.foxtrot.api.dto.message.response.SendTextMessageResponse;
import org.foxtrot.api.dto.users.BlockRequest;
import org.foxtrot.api.dto.users.UnBlockRequest;
import org.foxtrot.api.dto.workSpace.AddUserToWorkspaceRequest;
import org.foxtrot.api.dto.workSpace.CreateWorkSpaceRequest;
import org.foxtrot.api.dto.workSpace.response.CreateWorkSpaceResponse;
import org.foxtrot.api.steps.AccountApiSteps;
import org.foxtrot.api.steps.AccountSettingsApiSteps;
import org.foxtrot.api.steps.GroupApiSteps;
import org.foxtrot.api.steps.MassageApiSteps;
import org.foxtrot.api.steps.MessageApiSteps;
import org.foxtrot.api.steps.UsersApiSteps;
import org.foxtrot.api.steps.WorkSpaceApiSteps;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.TestContext;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.UserPoolManager;
import org.foxtrot.utils.UserTestDataCleaner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.foxtrot.enums.GroupTypesEnum.GROUP;
import static org.foxtrot.enums.GroupTypesEnum.INFO_CHANNEL;

@Disabled
@ExtendWith(AllureJunit5.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Проверка отображения не контакта:")
public class NonContactDisplayTest extends BaseTest {

    private GroupApiSteps groupApiStepsObserverSession;
    private GroupApiSteps groupApiStepsNonContactSession;
    private AccountApiSteps accountApiStepsNonContactSession;
    private AccountApiSteps accountApiStepsBlockUserSession;
    private AccountSettingsApiSteps accountSettingsApiStepsObserverSession;
    private MessageApiSteps messageApiStepsObserverSession;
    private MessageApiSteps messageApiStepsNonContactSession;
    private MassageApiSteps massageApiStepsNonContactSession;
    private WorkSpaceApiSteps workSpaceApiStepsObserverSession;
    private UsersApiSteps usersApiStepsObserverSession;
    private LoginHelper loginHelper;
    private UsersEnum observer;
    private UsersEnum blockUser;
    private static UsersEnum nonContact;
    private String homespaceGroupNameOne;
    private String homespaceGroupNameTwo;
    private String homespaceChannelNameOne;
    private String homespaceChannelNameTwo;
    private String workspaceName;

    enum AvatarType {
        AVATAR,
        INITIALS,
        PLACEHOLDER
    }

    @BeforeAll
    public void beforeAll() {
        loginHelper = new LoginHelper();
        UserPoolManager userPoolManager = new UserPoolManager();
        observer = userPoolManager.getFreeUser();
        nonContact = userPoolManager.getFreeUser();
        blockUser = userPoolManager.getFreeUser();
        groupApiStepsObserverSession = new GroupApiSteps(observer);
        accountApiStepsNonContactSession = new AccountApiSteps(nonContact);
        accountApiStepsBlockUserSession = new AccountApiSteps(blockUser);
        accountSettingsApiStepsObserverSession = new AccountSettingsApiSteps(observer);
        groupApiStepsNonContactSession = new GroupApiSteps(nonContact);
        massageApiStepsNonContactSession = new MassageApiSteps(nonContact);
        messageApiStepsObserverSession = new MessageApiSteps(observer);
        messageApiStepsNonContactSession = new MessageApiSteps(nonContact);
        workSpaceApiStepsObserverSession = new WorkSpaceApiSteps(observer);
        usersApiStepsObserverSession = new UsersApiSteps(observer);
        homespaceChannelNameOne = TestData.generateHomespaceChannelName();
        homespaceChannelNameTwo = TestData.generateHomespaceChannelName();
        homespaceGroupNameOne = TestData.generateHomespaceGroupName();
        homespaceGroupNameTwo = TestData.generateHomespaceGroupName();
        workspaceName = TestData.generateWorkspaceName();
        prepareTestData();
    }

    @AfterAll
    public void afterAll() {
        UserPoolManager.releaseCurrentUser();
        deleteTestData();
    }


    static Stream<Arguments> provideNonContactScenarios() {
        String name = nonContact.getName();
        String phone = nonContact.getFormattedPhone();

        return Stream.of(
                Arguments.of(false, false, false, name, AvatarType.INITIALS, "без аватарки, номер скрыт, показывать номера — нет", "54168"),
                Arguments.of(false, true, false, name, AvatarType.INITIALS, "без аватарки, номер открыт, показывать номера — нет", "54164"),
                Arguments.of(true, false, false, name, AvatarType.AVATAR, "с аватаркой, номер скрыт, показывать номера — нет", "54092"),
                Arguments.of(true, true, false, name, AvatarType.AVATAR, "с аватаркой, номер открыт, показывать номера — нет", "54049"),

                Arguments.of(false, false, true, name, AvatarType.PLACEHOLDER, "без аватарки, номер скрыт, показывать номера — да", "54167"),
                Arguments.of(false, true, true, phone, AvatarType.PLACEHOLDER, "без аватарки, номер открыт, показывать номера — да", "54166"),
                Arguments.of(true, false, true, name, AvatarType.PLACEHOLDER, "с аватаркой, номер скрыт, показывать номера — да", "54160"),
                Arguments.of(true, true, true, phone, AvatarType.PLACEHOLDER, "с аватаркой, номер открыт, показывать номера — да", "54134")
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("Проверка отображения профиля не контакта")
    public void verifyNonContactProfileHeader(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId) {

        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(expectedDisplayText);
        pages.topbarSteps.clickP2pHeader();
        pages.p2pMenuVerifier.verifyUserName(expectedDisplayText);
        switch (expectedAvatarType) {
            case AVATAR -> pages.p2pMenuVerifier.verifyProfileHeaderHasAvatar();
            case INITIALS -> pages.p2pMenuVerifier.verifyProfileHeaderHasInitialsAvatarPlaceholder();
            case PLACEHOLDER -> pages.p2pMenuVerifier.verify2pHasUserSilhouettePlaceholder();
        }
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("В ленте p2p чата")
    public void verifyNonContactProfileP2pFeed(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(expectedDisplayText);
        pages.chatVerify.verifyMessageSent();

        switch (expectedAvatarType) {
            case AVATAR -> pages.chatVerify.verify2pHasAvatar();
            case INITIALS -> pages.chatVerify.verify2pHasInitialsAvatarPlaceholder();
            case PLACEHOLDER -> pages.chatVerify.verify2pHasUserSilhouettePlaceholder();
        }
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("В хедере p2p чата")
    public void verifyNonContactProfileHeaderP2p(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(expectedDisplayText);
        pages.chatVerify.verifyMessageSent();

        pages.topbarSteps.verifyChatName(expectedDisplayText);

        switch (expectedAvatarType) {
            case AVATAR -> pages.topbarSteps.verifyP2pHeaderHasAvatar();
            case INITIALS -> pages.topbarSteps.verifyP2pHeaderHasInitialsAvatarPlaceholder();
            case PLACEHOLDER -> pages.topbarSteps.verifyP2pHeaderHasAvatarPlaceholder();
        }
    }


    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("В списке участников группы (для владельца)")
    public void verifyNonContactProfileInGroupParticipant(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(homespaceGroupNameOne);
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickMembersButton();

        switch (expectedAvatarType) {
            case AVATAR -> pages.subscribersMenuSteps.verifyChannelSubscribersHasAvatar(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case INITIALS -> pages.subscribersMenuSteps.verifyChannelSubscribersHasInitialsAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case PLACEHOLDER -> pages.subscribersMenuSteps.verifyChannelSubscribersHasAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone(), phoneVisible
            );
        }
        pages.subscribersMenuSteps.verifySubscriberExists(expectedDisplayText);
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("В списке пользователей на добавление при создании группы")
    public void verifyNonContactProfileWhenCreateGroup(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.homespaceSteps.clickCreateNewChatButton();
        pages.newMessageModalSteps.clickCreateGroupButton();
        pages.newGroupModalSteps.createHomespaceGroup("TestGroup");

        switch (expectedAvatarType) {
            case AVATAR -> pages.addSubscribersListSteps.verifyChannelSubscribersHasAvatar(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case INITIALS -> pages.addSubscribersListSteps.verifyChannelSubscribersHasInitialsAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case PLACEHOLDER -> pages.addSubscribersListSteps.verifyChannelSubscribersHasAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone(), phoneVisible
            );
        }
    }


    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("В ленте группы")
    public void verifyNonContactProfileInGroupFeed(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(homespaceGroupNameOne);
        pages.chatVerify.verifyMessageSent();
        pages.chatVerify.verifySenderName(expectedDisplayText, "Тест");

        switch (expectedAvatarType) {
            case AVATAR -> pages.chatVerify.verify2pHasAvatar();
            case INITIALS -> pages.chatVerify.verify2pHasInitialsAvatarPlaceholder();
            case PLACEHOLDER -> pages.chatVerify.verify2pHasUserSilhouettePlaceholder();
        }
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("При mention в группе")
    public void verifyNonContactProfileInGroupMentionModal(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(homespaceGroupNameOne);
        pages.chatActions.clickMentionButton();

        switch (expectedAvatarType) {
            case AVATAR -> pages.mentionModalSteps.verifyMentionedUserHasAvatar(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case INITIALS -> pages.mentionModalSteps.verifyMentionedUserHasInitialsAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case PLACEHOLDER -> pages.mentionModalSteps.verifyMentionedUserHasAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone(), phoneVisible
            );
        }
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("В системном сообщении о добавлении в группу")
    public void verifyNonContactProfileInGroupInviteSystemMessage(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(homespaceGroupNameOne);

        pages.chatVerify.verifyGroupInviteSystemMessage(expectedDisplayText);
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("Проверка отображения в меню закрепленных сообщений")
    public void verifyNonContactProfileInGroupPinedMessage(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(homespaceGroupNameOne);
        pages.topbarSteps.clickPinnedMessageButton();

        switch (expectedAvatarType) {
            case AVATAR -> pages.pinnedMessageMenuSteps.verifyUserHasAvatarInPinnedMessagesMenu(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case INITIALS -> pages.pinnedMessageMenuSteps.verifyUserHasInitialsAvatarPlaceholderInPinnedMessagesMenu(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case PLACEHOLDER -> pages.pinnedMessageMenuSteps.verifyUserHasAvatarPlaceholderInPinnedMessagesMenu(
                    nonContact.getName(), nonContact.getFormattedPhone(), phoneVisible
            );
        }
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("Когда не контакт набирает сообщение в группу")
    public void verifyNonContactTypingIndicatorIsVisible(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(homespaceGroupNameOne);
        massageApiStepsNonContactSession.typing(new TypingRequest(TestContext.getLong(homespaceGroupNameOne)));

        pages.topbarSteps.verifyTypingIndicatorIsVisible(expectedDisplayText);
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("Проверка отображения в пузыре ответа (reply)")
    public void verifyNonContactProfileInReplyMessage(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(homespaceGroupNameOne);

        massageApiStepsNonContactSession.sendTextMassage(new SendTextMassageRequest(
                "replay",
                0,
                TestContext.getLong(homespaceGroupNameOne),
                TestContext.getLong("Replay")
        ));
        pages.chatVerify.verifyUserAppearanceInReplyBubble(expectedDisplayText);
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("В списке подписчиков канала (для владельца)")
    public void verifyNonContactProfileInChannelSubscribers(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(homespaceChannelNameOne);
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.clickSubscribersButton();

        switch (expectedAvatarType) {
            case AVATAR -> pages.subscribersMenuSteps.verifyChannelSubscribersHasAvatar(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case INITIALS -> pages.subscribersMenuSteps.verifyChannelSubscribersHasInitialsAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case PLACEHOLDER -> pages.subscribersMenuSteps.verifyChannelSubscribersHasAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone(), phoneVisible
            );
        }
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("В списке пользователей на добавление в уже существующий канал")
    public void verifyNonContactProfileWhenAddedToChannel(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(homespaceChannelNameTwo);
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.clickSubscribersButton();
        pages.subscribersMenuSteps.clickAddSubscribers();

        switch (expectedAvatarType) {
            case AVATAR -> pages.addSubscribersListSteps.verifyChannelSubscribersHasAvatar(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case INITIALS -> pages.addSubscribersListSteps.verifyChannelSubscribersHasInitialsAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case PLACEHOLDER -> pages.addSubscribersListSteps.verifyChannelSubscribersHasAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone(), phoneVisible
            );
        }
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("В списке пользователей на добавление при создании канала")
    public void verifyNonContactProfileWhenCreateChannel(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.homespaceSteps.clickCreateNewChatButton();
        pages.newMessageModalSteps.clickCreateChannelButton();
        pages.newChannelModalSteps.createChannel("testChannel");

        switch (expectedAvatarType) {
            case AVATAR -> pages.addSubscribersListSteps.verifyChannelSubscribersHasAvatar(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case INITIALS -> pages.addSubscribersListSteps.verifyChannelSubscribersHasInitialsAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case PLACEHOLDER -> pages.addSubscribersListSteps.verifyChannelSubscribersHasAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone(), phoneVisible
            );
        }
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("В комментариях поста в группе")
    public void verifyNonContactProfileInCommentToGroupPost(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(homespaceGroupNameOne);
        pages.chatActions.openMessageCommentById(TestContext.getLong("Replay"));

        pages.chatVerify.verifySenderName(expectedDisplayText, "Комментарий к посту");

        switch (expectedAvatarType) {
            case AVATAR -> pages.chatVerify.verify2pHasAvatar();
            case INITIALS -> pages.chatVerify.verify2pHasInitialsAvatarPlaceholder();
            case PLACEHOLDER -> pages.chatVerify.verify2pHasUserSilhouettePlaceholder();
        }
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("В списке пользователей пространства (для владельца)")
    public void verifyNonContactProfileInWorkspaceMembers(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.leftSidebarSteps.clickSpaceByName(workspaceName);
        pages.spaceSteps.clickParticipantsButton();

        switch (expectedAvatarType) {
            case AVATAR -> pages.workspaceMembersMenuSteps.verifyChannelSubscribersHasAvatar(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case INITIALS -> pages.workspaceMembersMenuSteps.verifyChannelSubscribersHasInitialsAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case PLACEHOLDER -> pages.workspaceMembersMenuSteps.verifyChannelSubscribersHasAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone(), phoneVisible
            );
        }
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("В списке чатов для форварда сообщения в Чаты")
    public void verifyNonContactProfileInListChatsToForward(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(expectedDisplayText);
        pages.chatActions.openMessageContextMenu("Тест");
        pages.messageBubbleContextMenu.clickForwardButton();

        switch (expectedAvatarType) {
            case AVATAR -> pages.forwardMenuSteps.verifyForwardRecipientsHasAvatar(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case INITIALS -> pages.forwardMenuSteps.verifyForwardRecipientsHasInitialsAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case PLACEHOLDER -> pages.forwardMenuSteps.verifyForwardRecipientsHasAvatarPlaceholder(
                    nonContact.getName(), nonContact.getFormattedPhone(), phoneVisible
            );
        }
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("Проверка отображения в списке заблокированных пользователей")
    public void verifyNonContactProfileInBlockList(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.leftSidebarSteps.openSettings();
        pages.settingsMenuSteps.openBlackList();

        switch (expectedAvatarType) {
            case AVATAR -> pages.blackListTabSteps.verifyUserInBlacklistHasAvatar(
                    blockUser.getName(), blockUser.getFormattedPhone()
            );
            case INITIALS -> pages.blackListTabSteps.verifyUserInBlacklistHasInitialsAvatarPlaceholder(
                    blockUser.getName(), blockUser.getFormattedPhone()
            );
            case PLACEHOLDER -> pages.blackListTabSteps.verifyUserInBlacklistHasAvatarPlaceholder(
                    blockUser.getName(), blockUser.getFormattedPhone(), phoneVisible
            );
        }
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("При просмотре медиа в полноэкранном режиме")
    public void verifySenderAvatarDisplayInFullScreenMediaView(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(homespaceGroupNameOne);
        pages.chatActions.clickMediaMessageById(TestContext.getString("imageMessage"));

        switch (expectedAvatarType) {
            case AVATAR -> pages.fullScreenMediaViewSteps.verifyUserHasAvatarInMediaView(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case INITIALS -> pages.fullScreenMediaViewSteps.verifyUserHasInitialsAvatarPlaceholderInMediaView(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case PLACEHOLDER -> pages.fullScreenMediaViewSteps.verifyUserHasAvatarPlaceholderInMediaView(
                    nonContact.getName(), nonContact.getFormattedPhone(), phoneVisible
            );
        }
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("В диалоговом окне блокировки пользователя")
    public void verifyNonContactProfileInUserBlockingDialog(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            @SuppressWarnings("unused") AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(expectedDisplayText);
        pages.topbarSteps.clickP2pHeader();
        pages.p2pChatMenuSteps.blockUserButton();
        pages.blockUserModalSteps.verifyUserAppearanceInUserBlockingDialog(expectedDisplayText);
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("В модальном окне реакций и просмотров")
    public void verifyNonContactProfileInReactionsAndViewsModal(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);
        pages.chatListSteps.clickChatByName(homespaceGroupNameOne);
        pages.chatActions.openMessageContextMenuById(TestContext.getString("imageMessage"));
        pages.messageBubbleContextMenu.clickReactionsButton();

        switch (expectedAvatarType) {
            case AVATAR -> pages.reactionsAndViewsModalSteps.verifyUserHasAvatarInReactionsAndViewsModal(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case INITIALS ->
                    pages.reactionsAndViewsModalSteps.verifyUserHasInitialsAvatarPlaceholderInReactionsAndViewsModal(
                            nonContact.getName(), nonContact.getFormattedPhone()
                    );
            case PLACEHOLDER ->
                    pages.reactionsAndViewsModalSteps.verifyUserHasAvatarPlaceholderInReactionsAndViewsModal(
                            nonContact.getName(), nonContact.getFormattedPhone(), phoneVisible
                    );
        }
    }

    @ParameterizedTest
    @MethodSource("provideNonContactScenarios")
    @DisplayName("Проверка отображения в превью чатов")
    public void verifyNonContactProfileInChatsPreview(
            boolean hasAvatar,
            boolean phoneVisible,
            boolean showPhoneForNonContacts,
            String expectedDisplayText,
            AvatarType expectedAvatarType,
            String scenarioDescription,
            String allureTestCaseId
    ) {
        linkToTms(allureTestCaseId);
        prepareTestEnvironment(hasAvatar, phoneVisible, showPhoneForNonContacts);
        loginHelper.performUILogin(observer);

        switch (expectedAvatarType) {
            case AVATAR -> pages.chatListSteps.verifyUserHasAvatarInChatsPreview(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case INITIALS -> pages.chatListSteps.verifyUserHasInitialsAvatarPlaceholderInChatsPreview(
                    nonContact.getName(), nonContact.getFormattedPhone()
            );
            case PLACEHOLDER -> pages.chatListSteps.verifyUserHasAvatarPlaceholderInChatsPreview(
                    nonContact.getName(), nonContact.getFormattedPhone(), phoneVisible
            );
        }
    }

    private void prepareTestEnvironment(boolean hasAvatar, boolean phoneVisible, boolean showPhoneForNonContacts) {
        if (showPhoneForNonContacts) {
            accountSettingsApiStepsObserverSession.setNonContactPreviewAttention();
        } else {
            accountSettingsApiStepsObserverSession.setNonContactPreviewLikeContact();
        }

        if (!phoneVisible) {
            accountApiStepsNonContactSession.updateUserPrivacyHidePhone();
            accountApiStepsBlockUserSession.updateUserPrivacyHidePhone();
        } else {
            accountApiStepsNonContactSession.updateUserPrivacyAll();
            accountApiStepsBlockUserSession.updateUserPrivacyAll();
        }

        if (hasAvatar) {
            accountApiStepsNonContactSession.UpdateProfilePhoto(
                    DataGenerator.getDefaultUpdatePhotoRequest());
            accountApiStepsBlockUserSession.UpdateProfilePhoto(
                    DataGenerator.getDefaultUpdatePhotoRequest());
        } else {
            accountApiStepsNonContactSession.removePhotoHistoryItem(
                    DataGenerator.getDefaultRemovePhotoHistoryItemRequest());
            accountApiStepsBlockUserSession.removePhotoHistoryItem(
                    DataGenerator.getDefaultRemovePhotoHistoryItemRequest());
        }
    }

    private void linkToTms(String allureTestCaseId) {
        Allure.getLifecycle().updateTestCase(testResult ->
                testResult.getLabels().add(new Label().setName("tms").setValue(allureTestCaseId))
        );
    }

    private void prepareTestData() {
        usersApiStepsObserverSession.blockUser(new BlockRequest(blockUser.getId()));

        CreateP2PGroupResponse adminGroupId = groupApiStepsNonContactSession.createP2PGroup(
                new CreateP2PGroupRequest(observer.getId()));

        massageApiStepsNonContactSession.sendTextMassage(
                new SendTextMassageRequest("Тест", 0, adminGroupId.getGroupId()));

        CreateGroupResponse homeGroupOne = groupApiStepsObserverSession.createGroup(
                new CreateGroupRequest(
                        homespaceGroupNameOne,
                        GROUP.name(),
                        false,
                        null,
                        new CreateGroupRequest.ChannelOptions(true)));
        TestContext.put(homespaceGroupNameOne, homeGroupOne.getGroupId());

        groupApiStepsObserverSession.addUsers(
                new AddUsersRequest(homeGroupOne.getGroupId(), List.of(nonContact.getId())));

        SendTextMassageResponse messages = massageApiStepsNonContactSession.sendTextMassage(
                new SendTextMassageRequest("Тест", 0, homeGroupOne.getGroupId()));

        TestContext.put("Replay", messages.getMessageId());

        messageApiStepsObserverSession.pinMessage(
                new PinMessageRequest(
                        String.valueOf(homeGroupOne.getGroupId()),
                        List.of(String.valueOf(messages.getMessageId()))));

        CreateCommentGroupResponse commentGroupResponse = groupApiStepsNonContactSession.createCommentGroup(
                new CreateCommentGroupRequest(
                        homeGroupOne.getGroupId(),
                        messages.getMessageId()));

        massageApiStepsNonContactSession.sendTextMassage(
                new SendTextMassageRequest(
                        "Комментарий к посту",
                        0,
                        commentGroupResponse.getGroupId()));

        SendTextMessageResponse imageMessage = messageApiStepsNonContactSession.sendImage(
                DataGenerator.getDefaultSendImageMessageRequest(homeGroupOne.getGroupId()), "-1");
        TestContext.put("imageMessage", imageMessage.getMessageId());

        groupApiStepsObserverSession.createGroup(
                new CreateGroupRequest(homespaceGroupNameTwo, GROUP.name(), false));

        messageApiStepsNonContactSession.addReaction(
                TestContext.getLong("imageMessage"),
                homeGroupOne.getGroupId(),
                "🫠",
                "-1");

        CreateGroupResponse homeChannelOne = groupApiStepsObserverSession.createGroup(
                new CreateGroupRequest(
                        homespaceChannelNameOne,
                        INFO_CHANNEL.name(),
                        false));

        groupApiStepsObserverSession.addUsers(
                new AddUsersRequest(homeChannelOne.getGroupId(), List.of(nonContact.getId())));

        groupApiStepsObserverSession.createGroup(
                new CreateGroupRequest(homespaceChannelNameTwo, INFO_CHANNEL.name(), false));

        CreateWorkSpaceResponse workSpace = workSpaceApiStepsObserverSession.createWorkspace(
                new CreateWorkSpaceRequest(
                        new CreateWorkSpaceRequest.Workspace(workspaceName, "")));

        workSpaceApiStepsObserverSession.addUserToWorkspace(
                new AddUserToWorkspaceRequest(List.of(nonContact.getId())),
                workSpace.getId());
    }

    private void deleteTestData() {
        UserTestDataCleaner userTestDataCleaner = new UserTestDataCleaner();
        userTestDataCleaner.clean(observer, nonContact, blockUser);
        usersApiStepsObserverSession.unBlockUser(
                new UnBlockRequest(blockUser.getId()));
        accountApiStepsNonContactSession.removePhotoHistoryItem(
                DataGenerator.getDefaultRemovePhotoHistoryItemRequest());
        accountSettingsApiStepsObserverSession.setNonContactPreviewLikeContact();
        accountApiStepsNonContactSession.updateUserPrivacyAll();
    }
}