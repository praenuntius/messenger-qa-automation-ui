package org.foxtrot.mobile.test.create_chats.create_in_workspace;

import org.foxtrot.enums.MediaFileExtension;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.mobile.allure.AllureIds;
import org.foxtrot.mobile.core.emulator.DeviceCleanupManager;
import org.foxtrot.mobile.elements.actions.Direction;
import org.foxtrot.mobile.elements.actions.SwipeHelper;
import org.foxtrot.mobile.test.BaseMobileTest;
import org.foxtrot.mobile.testdata.dictionary.*;
import org.foxtrot.mobile.utils.ContactsHelper;
import org.foxtrot.mobile.utils.FileUtils;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.factories.core.UserFactory;
import org.foxtrot.utils.factories.core.WorkspaceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;
import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public class GroupCreationTest extends BaseMobileTest {
    private UsersEnum contactUser;
    private UsersEnum notContactUser;
    private UsersEnum ownerUser;
    private String contactFirstName;
    private String contactLastName;
    private String contactFullName;
    private String wsName;
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();
    private final UserFactory userFactory = new UserFactory();

    @BeforeEach
    public void setUp() {
        prepareTestData();
    }

    @AfterEach
    public void tearDown() {
        workspaceFactory.cleanUp();
        DeviceCleanupManager.cleanDeviceState();
    }

    @Test
    @DisplayName("Совершить поиск в модальном окне добавления участников в группу по номеру")
    @AllureIds(android = "53120", ios = "51405")
    public void searchParticipantsToGroupByNumber() {
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.bottomMenu().clickWorkspacesButton();
        pages.workspace().checkWorkspaceScreenTitle(wsName)
                .clickPlusBtn();
        pages.createNewMessageFromWorkspace().checkCreateNewMessageScreenTitle(
                        CreateNewMessageDictionary.SCREEN_TITLE_WORKSPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_WORKSPACE)*/
                .checkChatNameDisplayed("Главный")
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL)
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .fillGroupName(TestData.randomText(10))
                .selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName())
                    .clickSearchBtn()
                    .searchByText(contactUser.getPhoneNumber())
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(notContactUser.getPhoneNumber())
                    .checkContactInfoByNumber(1, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByName(notContactUser.getName())
                    .checkContactInfoByName(contactFullName)
                    .searchByText(contactUser.getPhoneNumber())
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(notContactUser.getPhoneNumber())
                    .checkContactInfoByNumber(1, notContactUser.getName());
        }
    }

    @Test
    @DisplayName("Совершить поиск в модальном окне добавления участников в группу по фамилии")
    @AllureIds(android = "53119", ios = "51397")
    public void searchParticipantsToGroupBySurname() {
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.bottomMenu().clickWorkspacesButton();
        pages.workspace().checkWorkspaceScreenTitle(wsName)
                .clickPlusBtn();
        pages.createNewMessageFromWorkspace().checkCreateNewMessageScreenTitle(
                        CreateNewMessageDictionary.SCREEN_TITLE_WORKSPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_WORKSPACE)*/
                .checkChatNameDisplayed("Главный")
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL)
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .fillGroupName(TestData.randomText(10))
                .selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName())
                    .clickSearchBtn()
                    .searchByText(contactLastName)
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(notContactUser.getLastName())
                    .checkContactInfoByNumber(1, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByName(notContactUser.getName())
                    .checkContactInfoByName(contactFullName)
                    .searchByText(contactLastName)
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(notContactUser.getLastName())
                    .checkContactInfoByNumber(1, notContactUser.getName());
        }
    }

    @Test
    @DisplayName("Совершить поиск в модальном окне добавления участников в группу по имени")
    @AllureIds(android = "53118", ios = "51396")
    public void searchParticipantsToGroupByName() {
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.bottomMenu().clickWorkspacesButton();
        pages.workspace().checkWorkspaceScreenTitle(wsName)
                .clickPlusBtn();
        pages.createNewMessageFromWorkspace().checkCreateNewMessageScreenTitle(
                        CreateNewMessageDictionary.SCREEN_TITLE_WORKSPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_WORKSPACE)*/
                .checkChatNameDisplayed("Главный")
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL)
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .fillGroupName(TestData.randomText(10))
                .selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName())
                    .clickSearchBtn()
                    .searchByText(contactFirstName)
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(notContactUser.getFirstName())
                    .checkContactInfoByNumber(1, notContactUser.getName());
        } else if (isIos()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByName(notContactUser.getName())
                    .checkContactInfoByName(contactFullName)
                    .searchByText(contactFirstName)
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(notContactUser.getFirstName())
                    .checkContactInfoByName(notContactUser.getName());
        }
    }

    @Test
    @DisplayName("Создать \"Закрытый\" групповой чат с установленной аватаркой, без пользователей")
    @AllureIds(android = "53115", ios = "94622")
    public void createPrivateGroupNoParticipants() {
        String chatName = TestData.randomText(10);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.bottomMenu().clickWorkspacesButton();
        pages.workspace().checkWorkspaceScreenTitle(wsName)
                .clickPlusBtn();
        pages.createNewMessageFromWorkspace().checkCreateNewMessageScreenTitle(
                        CreateNewMessageDictionary.SCREEN_TITLE_WORKSPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_WORKSPACE)*/
                .checkChatNameDisplayed("Главный")
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL)
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .fillGroupName(chatName);
        SwipeHelper.swipeOnScreen(Direction.UP, 0.1d, Duration.ofMillis(100));
        pages.createNewGroup().checkTypeOfGroupSelected(false)
                .selectTypeOfGroup(false);
        SwipeHelper.swipeOnScreen(Direction.DOWN, 0.1d, Duration.ofMillis(100));
        pages.createNewGroup().selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByName(notContactUser.getName())
                    .checkContactInfoByName(contactFullName);
        }
        pages.addSubscribers().clickNextBtn();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(chatName)
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_PARTICIPANTS.formatted(1));
        if (isIos()) { // todo нет локаторов
            pages.chat().checkSystemMessage(1, ChatDictionary.GROUP_WAS_CREATED.formatted(chatName))
                    .checkNoMessageViewsOnChatScreen();
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.groupPreferences().checkChatPreferencesScreenPresent();
        }
        pages.groupPreferences().checkChatPreferencesScreenGroupName(chatName)
                .checkChatPreferencesScreenGroupSubName(ChatPreferenceDictionary.PRIVATE_GROUP)
                .checkParticipantsNumber("1");
    }

    @Test
    @DisplayName("Создать \"Закрытый\" групповой чат с установленной аватаркой, названием, с пользователями")
    @AllureIds(android = "53117", ios = "51395")
    public void createPrivateGroupWithParticipants() {
        String chatName = TestData.randomText(10);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.bottomMenu().clickWorkspacesButton();
        pages.workspace().checkWorkspaceScreenTitle(wsName)
                .clickPlusBtn();
        pages.createNewMessageFromWorkspace().checkCreateNewMessageScreenTitle(
                        CreateNewMessageDictionary.SCREEN_TITLE_WORKSPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_WORKSPACE)*/
                .checkChatNameDisplayed("Главный")
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL)
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .fillGroupName(chatName);
        SwipeHelper.swipeOnScreen(Direction.UP, 0.1d, Duration.ofMillis(100));
        pages.createNewGroup().checkTypeOfGroupSelected(false)
                .selectTypeOfGroup(false);
        SwipeHelper.swipeOnScreen(Direction.DOWN, 0.1d, Duration.ofMillis(100));
        pages.createNewGroup().selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByName(notContactUser.getName())
                    .checkContactInfoByName(contactFullName);
        }
        String[] bubbleNames = isIos() ? new String[]{notContactUser.getFirstName(), contactFirstName} :
                new String[]{notContactUser.getName(), contactFullName};
        pages.addSubscribers().selectContactsByName(new String[]{notContactUser.getName(), contactFullName})
                // todo есть задача https://allprojects.atlassian.net/browse/FB-21006
                .checkBubblesByNames(bubbleNames)
                .clickNextBtn();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(chatName)
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_PARTICIPANTS.formatted(3) + "а");
        if (isIos()) { // todo нет локаторов
            pages.chat().checkSystemMessage(1, ChatDictionary.GROUP_WAS_CREATED.formatted(chatName))
                    .checkSystemMessageContainsText(2,
                            new String[]{ChatDictionary.YOU_INVITED, notContactUser.getName(), contactFullName})
                    .checkNoMessageViewsOnChatScreen();
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.groupPreferences().checkChatPreferencesScreenPresent();
        }
        pages.groupPreferences().checkChatPreferencesScreenGroupName(chatName)
                .checkChatPreferencesScreenGroupSubName(ChatPreferenceDictionary.PRIVATE_GROUP)
                .checkParticipantsNumber("3");
    }

    @Test
    @DisplayName("Создать \"Открытый\" групповой чат с установленной аватаркой, без пользователей")
    @AllureIds(android = "53114", ios = "51394")
    public void createPublicGroupNoParticipants() {
        String chatName = TestData.randomText(10);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.bottomMenu().clickWorkspacesButton();
        pages.workspace().checkWorkspaceScreenTitle(wsName)
                .clickPlusBtn();
        pages.createNewMessageFromWorkspace().checkCreateNewMessageScreenTitle(
                        CreateNewMessageDictionary.SCREEN_TITLE_WORKSPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_WORKSPACE)*/
                .checkChatNameDisplayed("Главный")
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL)
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .fillGroupName(chatName);
        SwipeHelper.swipeOnScreen(Direction.UP, 0.1d, Duration.ofMillis(100));
        pages.createNewGroup().checkTypeOfGroupSelected(false)
                .selectTypeOfGroup(true);
        SwipeHelper.swipeOnScreen(Direction.DOWN, 0.1d, Duration.ofMillis(100));
        pages.createNewGroup().selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByName(notContactUser.getName())
                    .checkContactInfoByName(contactFullName);
        }
        pages.addSubscribers().clickNextBtn();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(chatName)
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_PARTICIPANTS.formatted(1));
        if (isIos()) { // todo нет локаторов
            pages.chat().checkSystemMessage(1, ChatDictionary.GROUP_WAS_CREATED.formatted(chatName))
                    .checkNoMessageViewsOnChatScreen();
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.groupPreferences().checkChatPreferencesScreenPresent();
        }
        pages.groupPreferences().checkChatPreferencesScreenGroupName(chatName)
                .checkChatPreferencesScreenGroupSubName(ChatPreferenceDictionary.PUBLIC_GROUP)
                .checkParticipantsNumber("1");
    }

    @Test
    @DisplayName("Создать \"Открытый\" групповой чат с установленной аватаркой, названием, с пользователями")
    @AllureIds(android = "53116", ios = "94623")
    public void createPublicGroupWithParticipants() {
        String chatName = TestData.randomText(10);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.bottomMenu().clickWorkspacesButton();
        pages.workspace().checkWorkspaceScreenTitle(wsName)
                .clickPlusBtn();
        pages.createNewMessageFromWorkspace().checkCreateNewMessageScreenTitle(
                        CreateNewMessageDictionary.SCREEN_TITLE_WORKSPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_WORKSPACE)*/
                .checkChatNameDisplayed("Главный")
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL)
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .fillGroupName(chatName);
        SwipeHelper.swipeOnScreen(Direction.UP, 0.1d, Duration.ofMillis(100));
        pages.createNewGroup().checkTypeOfGroupSelected(false)
                .selectTypeOfGroup(true);
        SwipeHelper.swipeOnScreen(Direction.DOWN, 0.1d, Duration.ofMillis(100));
        pages.createNewGroup().selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByName(notContactUser.getName())
                    .checkContactInfoByName(contactFullName);
        }
        String[] bubbleNames = isIos() ? new String[]{notContactUser.getFirstName(), contactFirstName} :
                new String[]{notContactUser.getName(), contactFullName};
        pages.addSubscribers().selectContactsByName(new String[]{notContactUser.getName(), contactFullName})
                // todo есть задача https://allprojects.atlassian.net/browse/FB-21006
                .checkBubblesByNames(bubbleNames)
                .clickNextBtn();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(chatName)
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_PARTICIPANTS.formatted(3) + "а");
        if (isIos()) { // todo нет локаторов
            pages.chat().checkSystemMessage(1, ChatDictionary.GROUP_WAS_CREATED.formatted(chatName))
                    .checkSystemMessageContainsText(2,
                            new String[]{ChatDictionary.YOU_INVITED, notContactUser.getName(), contactFullName})
                    .checkNoMessageViewsOnChatScreen();
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.groupPreferences().checkChatPreferencesScreenPresent();
        }
        if (isAndroid()) { // todo нет локаторов
            pages.groupPreferences().checkChatPreferencesScreenGroupName(chatName)
                    .checkChatPreferencesScreenGroupSubName(ChatPreferenceDictionary.PUBLIC_GROUP);
        }
    }

    @Test
    @DisplayName("Загрузить аватарку, удалить аватарку, указать название и описание, добавить юзеров и нажать кнопку \"Готово\" > группа создана")
    @AllureIds(android = "94022", ios = "94624")
    public void createGroupWithAvatarNameDescriptionAndParticipants() {
        String chatName = TestData.randomText(10);
        String chatDescription = TestData.randomSentence(5);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.bottomMenu().clickWorkspacesButton();
        pages.workspace().checkWorkspaceScreenTitle(wsName)
                .clickPlusBtn();
        pages.createNewMessageFromWorkspace().checkCreateNewMessageScreenTitle(
                        CreateNewMessageDictionary.SCREEN_TITLE_WORKSPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_WORKSPACE)*/
                .checkChatNameDisplayed("Главный")
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL)
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .deleteAvatarForGroup()
                .fillGroupName(chatName)
                .fillGroupDescription(chatDescription)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByName(notContactUser.getName())
                    .checkContactInfoByName(contactFullName);
        }
        String[] bubbleNames = isIos() ? new String[]{notContactUser.getFirstName(), contactFirstName} :
                new String[]{notContactUser.getName(), contactFullName};
        pages.addSubscribers().selectContactsByName(new String[]{notContactUser.getName(), contactFullName})
                // todo есть задача https://allprojects.atlassian.net/browse/FB-21006
                .checkBubblesByNames(bubbleNames)
                .clickNextBtn();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(chatName)
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_PARTICIPANTS.formatted(3) + "а");
        if (isIos()) {
            pages.chat().checkChatAvatar(true);
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.groupPreferences().checkChatPreferencesScreenPresent();
        }
        pages.groupPreferences().checkChatPreferencesScreenGroupName(chatName)
                .checkChatPreferencesScreenGroupDescription(chatDescription)
                .checkParticipantsNumber("3");
        if (isIos()) {
            pages.groupPreferences().checkChatAvatar(true);
        }
    }

    @Test
    @DisplayName("Загрузить аватарку, удалить аватарку, указать название и описание, не добавляя юзеров нажать кнопку \"Готово\" > группа создана")
    @AllureIds(android = "94023", ios = "94625")
    public void createGroupWithAvatarNameDescriptionWithoutParticipants() {
        String chatName = TestData.randomText(10);
        String chatDescription = TestData.randomSentence(5);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.bottomMenu().clickWorkspacesButton();
        pages.workspace().checkWorkspaceScreenTitle(wsName)
                .clickPlusBtn();
        pages.createNewMessageFromWorkspace().checkCreateNewMessageScreenTitle(
                        CreateNewMessageDictionary.SCREEN_TITLE_WORKSPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_WORKSPACE)*/
                .checkChatNameDisplayed("Главный")
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL)
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .deleteAvatarForGroup()
                .fillGroupName(chatName)
                .fillGroupDescription(chatDescription)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByName(notContactUser.getName())
                    .checkContactInfoByName(contactFullName);
        }
        pages.addSubscribers().clickNextBtn();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(chatName)
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_PARTICIPANTS.formatted(1));
        if (isIos()) {
            pages.chat().checkChatAvatar(true);
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.groupPreferences().checkChatPreferencesScreenPresent();
        }
        pages.groupPreferences().checkChatPreferencesScreenGroupName(chatName)
                .checkChatPreferencesScreenGroupDescription(chatDescription)
                .checkParticipantsNumber("1");
        if (isIos()) {
            pages.groupPreferences().checkChatAvatar(true);
        }
    }

    private void prepareTestData() {
        var wsContext = workspaceFactory.createWorkspace();
        var ownerBuilder = wsContext.getOwnerBuilder();
        wsName = wsContext.getWsName();
        ownerUser = wsContext.getOwner();
        contactUser = userFactory.takeUser();
        notContactUser = userFactory.takeUser();
        ownerBuilder.addUsersToWorkspace(wsContext.getWsId(), contactUser);
        ownerBuilder.addUsersToWorkspace(wsContext.getWsId(), notContactUser);
        contactFirstName = TestData.randomFirstName();
        contactLastName = TestData.randomLastName();
        contactFullName = "%s %s".formatted(contactFirstName, contactLastName);
        ContactsHelper.addContact(contactFullName, contactUser.getPhoneNumber());
        FileUtils.addMediaFile(MediaFileExtension.JPG);
    }
}
