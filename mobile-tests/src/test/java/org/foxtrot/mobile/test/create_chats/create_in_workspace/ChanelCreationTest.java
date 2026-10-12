package org.foxtrot.mobile.test.create_chats.create_in_workspace;

import org.foxtrot.enums.MediaFileExtension;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.mobile.allure.AllureIds;
import org.foxtrot.mobile.core.emulator.DeviceCleanupManager;
import org.foxtrot.mobile.elements.actions.Direction;
import org.foxtrot.mobile.elements.actions.SwipeHelper;
import org.foxtrot.mobile.steps.*;
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

public class ChanelCreationTest extends BaseMobileTest {
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
    @DisplayName("Совершить поиск в модальном окне добавления участников в канал по номеру")
    @AllureIds(android = "53129", ios = "51410")
    public void searchParticipantsToChannelByNumber() {
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
                .clickCreateChannelBtn();
        String title = isIos() ? CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE : CreateNewChannelDictionary.SCREEN_TITLE_WORKSPACE;
        pages.createNewChannel().checkCreateNewChannelScreenTitle(title)
                .fillChannelName(TestData.randomText(10))
                .selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(title)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName())
                    .clickSearchBtn()
                    .searchByText(contactUser.getPhoneNumber())
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(notContactUser.getPhoneNumber())
                    .checkContactInfoByNumber(1, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByName(notContactUser.getName())
                    .checkContactInfoByName(contactFullName)
                    .searchByText(contactUser.getPhoneNumber())
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(notContactUser.getPhoneNumber())
                    .checkContactInfoByNumber(1, notContactUser.getName());
        }
    }

    @Test
    @DisplayName("Совершить поиск в модальном окне добавления участников в канал по фамилии")
    @AllureIds(android = "53128", ios = "51409")
    public void searchParticipantsToChannelBySurname() {
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
                .clickCreateChannelBtn();
        String title = isIos() ? CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE : CreateNewChannelDictionary.SCREEN_TITLE_WORKSPACE;
        pages.createNewChannel().checkCreateNewChannelScreenTitle(title)
                .fillChannelName(TestData.randomText(10))
                .selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(title)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName())
                    .clickSearchBtn()
                    .searchByText(contactLastName)
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(notContactUser.getLastName())
                    .checkContactInfoByNumber(1, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByName(notContactUser.getName())
                    .checkContactInfoByName(contactFullName)
                    .searchByText(contactLastName)
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(notContactUser.getLastName())
                    .checkContactInfoByNumber(1, notContactUser.getName());
        }
    }

    @Test
    @DisplayName("Совершить поиск в модальном окне добавления участников в канал по имени")
    @AllureIds(android = "53127", ios = "51408")
    public void searchParticipantsToChannelByName() {
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
                .clickCreateChannelBtn();
        String title = isIos() ? CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE : CreateNewChannelDictionary.SCREEN_TITLE_WORKSPACE;
        pages.createNewChannel().checkCreateNewChannelScreenTitle(title)
                .fillChannelName(TestData.randomText(10))
                .selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(title)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName())
                    .clickSearchBtn()
                    .searchByText(contactFirstName)
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(notContactUser.getFirstName())
                    .checkContactInfoByNumber(1, notContactUser.getName());
        } else if (isIos()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByName(notContactUser.getName())
                    .checkContactInfoByName(contactFullName)
                    .searchByText(contactFirstName)
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(notContactUser.getFirstName())
                    .checkContactInfoByName(notContactUser.getName());
        }
    }

    @Test
    @DisplayName("Создать 'Закрытый' канал с установленной аватаркой, без пользователей")
    @AllureIds(android = "53122", ios = "93658")
    public void createPrivateChannelNoParticipants() {
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
                .clickCreateChannelBtn();
        String title = isIos() ? CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE : CreateNewChannelDictionary.SCREEN_TITLE_WORKSPACE;
        pages.createNewChannel().checkCreateNewChannelScreenTitle(title)
                .fillChannelName(chatName);
        SwipeHelper.swipeOnScreen(Direction.UP, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().checkTypeOfChannelSelected(false)
                .selectTypeOfChannel(false);
        SwipeHelper.swipeOnScreen(Direction.DOWN, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(title);
        pages.createNewChannel().clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByName(notContactUser.getName())
                    .checkContactInfoByName(contactFullName);
        }
        pages.addSubscribers().clickNextBtn();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(chatName)
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_SUBS.formatted(1));
        if (isIos()) { // todo нет локаторов
            pages.chat().checkSystemMessage(1, ChatDictionary.CHANNEL_WAS_CREATED.formatted(chatName))
                    .checkNoMessageViewsOnChatScreen();
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenPresent();
        }
        if (isAndroid()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenChannelName(chatName)
                    .checkChatPreferencesScreenChannelSubName(ChatPreferenceDictionary.PRIVATE_CHANNEL);
        }
    }

    @Test
    @DisplayName("Создать 'Открытый' канал с установленной аватаркой, без пользователей")
    @AllureIds(android = "53121", ios = "93655")
    public void createPublicChannelNoParticipants() {
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
                .clickCreateChannelBtn();
        String title = isIos() ? CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE : CreateNewChannelDictionary.SCREEN_TITLE_WORKSPACE;
        pages.createNewChannel().checkCreateNewChannelScreenTitle(title)
                .fillChannelName(chatName);
        SwipeHelper.swipeOnScreen(Direction.UP, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().checkTypeOfChannelSelected(false)
                .selectTypeOfChannel(true);
        SwipeHelper.swipeOnScreen(Direction.DOWN, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(title);
        pages.createNewChannel().clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByName(notContactUser.getName())
                    .checkContactInfoByName(contactFullName);
        }
        pages.addSubscribers().clickNextBtn();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(chatName)
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_SUBS.formatted(1));
        if (isIos()) { // todo нет локаторов
            pages.chat().checkSystemMessage(1, ChatDictionary.CHANNEL_WAS_CREATED.formatted(chatName))
                    .checkNoMessageViewsOnChatScreen();
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenPresent();
        }
        if (isAndroid()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenChannelName(chatName)
                    .checkChatPreferencesScreenChannelSubName(ChatPreferenceDictionary.PUBLIC_CHANNEL);
        }
    }

    @Test
    @DisplayName("Создать 'Закрытый' канал с установленной аватаркой, с пользователями")
    @AllureIds(android = "53124", ios = "93654")
    public void createPrivateChannelWithParticipants() {
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
                .clickCreateChannelBtn();
        String title = isIos() ? CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE : CreateNewChannelDictionary.SCREEN_TITLE_WORKSPACE;
        pages.createNewChannel().checkCreateNewChannelScreenTitle(title)
                .fillChannelName(chatName);
        SwipeHelper.swipeOnScreen(Direction.UP, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().checkTypeOfChannelSelected(false)
                .selectTypeOfChannel(false);
        SwipeHelper.swipeOnScreen(Direction.DOWN, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(title);
        pages.createNewChannel().clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
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
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_SUBS.formatted(3) + "а");
        if (isIos()) { // todo нет локаторов
            pages.chat().checkSystemMessage(1, ChatDictionary.CHANNEL_WAS_CREATED.formatted(chatName))
                    .checkNoMessageViewsOnChatScreen();
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenPresent();
        }
        if (isAndroid()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenChannelName(chatName)
                    .checkChatPreferencesScreenChannelSubName(ChatPreferenceDictionary.PRIVATE_CHANNEL);
        }
    }

    @Test
    @DisplayName("Создать 'Открытый' канал с установленной аватаркой, с пользователями")
    @AllureIds(android = "53123", ios = "51406")
    public void createPublicChannelWithParticipants() {
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
                .clickCreateChannelBtn();
        String title = isIos() ? CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE : CreateNewChannelDictionary.SCREEN_TITLE_WORKSPACE;
        pages.createNewChannel().checkCreateNewChannelScreenTitle(title)
                .fillChannelName(chatName);
        SwipeHelper.swipeOnScreen(Direction.UP, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().checkTypeOfChannelSelected(false)
                .selectTypeOfChannel(true);
        SwipeHelper.swipeOnScreen(Direction.DOWN, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(title);
        pages.createNewChannel().clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
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
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_SUBS.formatted(3) + "а");
        if (isIos()) { // todo нет локаторов
            pages.chat().checkSystemMessage(1, ChatDictionary.CHANNEL_WAS_CREATED.formatted(chatName))
                    .checkNoMessageViewsOnChatScreen();
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenPresent();
        }
        if (isAndroid()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenChannelName(chatName)
                    .checkChatPreferencesScreenChannelSubName(ChatPreferenceDictionary.PUBLIC_CHANNEL);
        }
    }

    @Test
    @DisplayName("Загрузить аватарку, удалить аватарку, указать название и описание, добавить юзеров и нажать кнопку \"Готово\" > канал создан")
    @AllureIds(android = "53126", ios = "94626")
    public void createChannelWithAvatarNameDescriptionAndParticipants() {
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
                .clickCreateChannelBtn();
        String title = isIos() ? CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE : CreateNewChannelDictionary.SCREEN_TITLE_WORKSPACE;
        pages.createNewChannel().checkCreateNewChannelScreenTitle(title)
                .selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(title)
                .deleteAvatarForChannel()
                .fillChannelName(chatName)
                .fillChannelDescription(chatDescription)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
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
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_SUBS.formatted(3) + "а");
        if (isIos()) {
            pages.chat().checkChatAvatar(true);
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenPresent();
        }
        if (isAndroid()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenChannelName(chatName)
                    .checkChatPreferencesScreenChannelDescription(chatDescription);
        }
        if (isIos()) {
            pages.channelPreferences().checkChatAvatar(true);
        }
    }

    @Test
    @DisplayName("Загрузить аватарку, удалить аватарку, указать название и описание, не добавляя юзеров нажать кнопку \"Готово\" > канал создан")
    @AllureIds(android = "53125", ios = "51407")
    public void createChannelWithAvatarNameDescriptionWithoutParticipants() {
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
                .clickCreateChannelBtn();
        String title = isIos() ? CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE : CreateNewChannelDictionary.SCREEN_TITLE_WORKSPACE;
        pages.createNewChannel().checkCreateNewChannelScreenTitle(title)
                .selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(title)
                .deleteAvatarForChannel()
                .fillChannelName(chatName)
                .fillChannelDescription(chatDescription)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, contactFullName)
                    .checkContactInfoByNumber(2, notContactUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByName(notContactUser.getName())
                    .checkContactInfoByName(contactFullName);
        }
        pages.addSubscribers().clickNextBtn();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(chatName)
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_SUBS.formatted(1));
        if (isIos()) {
            pages.chat().checkChatAvatar(true);
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenPresent();
        }
        if (isAndroid()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenChannelName(chatName)
                    .checkChatPreferencesScreenChannelDescription(chatDescription);
        }
        if (isIos()) {
            pages.channelPreferences().checkChatAvatar(true);
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
