package org.foxtrot.mobile.test.create_chats.create_in_homespace;

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
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.foxtrot.utils.factories.core.UserFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;
import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public class ChanelCreationTest extends BaseMobileTest {
    private UsersEnum contactUser;
    private UsersEnum participantUser;
    private UsersEnum ownerUser;
    private String contactFirstName;
    private String contactLastName;
    private String contactFullName;
    private final GroupFactory groupFactory = new GroupFactory();
    private final UserFactory userFactory = new UserFactory();

    @BeforeAll
    public void beforeAll() {
        prepareTestData();
    }

    @AfterAll
    public void tearDown() {
        groupFactory.cleanUp();
        DeviceCleanupManager.cleanDeviceState();
    }

    @Test
    @DisplayName("Совершить поиск в модальном окне добавления участников в канал по номеру")
    @AllureIds(ios = "51422", android = "50301")
    public void searchParticipantsToChannelByNumber() {
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace()
                .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_HOMESPACE)*/
                .checkContactNameDisplayed(1, contactFullName)
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL, CreateNewMessageDictionary.INVITE_TO_APP)
                .clickCreateChannelBtn();
        pages.createNewChannel().checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE)
                .fillChannelName(TestData.randomText(10))
                .selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, "TDM")
                    .checkContactInfoByNumber(2, participantUser.getName())
                    .checkContactInfoByNumber(3, contactFullName)
                    .clickSearchBtn()
                    .searchByText(contactUser.getPhoneNumber())
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(participantUser.getPhoneNumber())
                    .checkContactInfoByNumber(1, participantUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByName("TDM")
                    .checkContactInfoByName(participantUser.getName())
                    .checkContactInfoByName(contactFullName)
                    .searchByText(contactUser.getPhoneNumber())
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(participantUser.getPhoneNumber())
                    .checkContactInfoByNumber(1, participantUser.getName());
        }
    }

    @Test
    @DisplayName("Совершить поиск в модальном окне добавления участников в канал по фамилии")
    @AllureIds(ios = "51421", android = "50300")
    public void searchParticipantsToChannelBySurname() {
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace()
                .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_HOMESPACE)*/
                .checkContactNameDisplayed(1, contactFullName)
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL, CreateNewMessageDictionary.INVITE_TO_APP)
                .clickCreateChannelBtn();
        pages.createNewChannel().checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE)
                .fillChannelName(TestData.randomText(10))
                .selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, "TDM")
                    .checkContactInfoByNumber(2, participantUser.getName())
                    .checkContactInfoByNumber(3, contactFullName)
                    .clickSearchBtn()
                    .searchByText(contactLastName)
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(participantUser.getLastName())
                    .checkContactInfoByNumber(1, participantUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByName("TDM")
                    .checkContactInfoByName(participantUser.getName())
                    .checkContactInfoByName(contactFullName)
                    .searchByText(contactLastName)
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(participantUser.getLastName())
                    .checkContactInfoByNumber(1, participantUser.getName());
        }
    }

    @Test
    @DisplayName("Совершить поиск в модальном окне добавления участников в канал по имени")
    @AllureIds(ios = "51420", android = "50299")
    public void searchParticipantsToChannelByName() {
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace()
                .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_HOMESPACE)*/
                .checkContactNameDisplayed(1, contactFullName)
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL, CreateNewMessageDictionary.INVITE_TO_APP)
                .clickCreateChannelBtn();
        pages.createNewChannel().checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE)
                .fillChannelName(TestData.randomText(10))
                .selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, "TDM")
                    .checkContactInfoByNumber(2, participantUser.getName())
                    .checkContactInfoByNumber(3, contactFullName)
                    .clickSearchBtn()
                    .searchByText(contactFirstName)
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(participantUser.getFirstName())
                    .checkContactInfoByNumber(1, participantUser.getName());
        } else if (isIos()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByName("TDM")
                    .checkContactInfoByName(participantUser.getName())
                    .checkContactInfoByName(contactFullName)
                    .searchByText(contactFirstName)
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(participantUser.getFirstName())
                    .checkContactInfoByName(participantUser.getName());
        }
    }

    @Test
    @DisplayName("Создать 'Закрытый' канал с установленной аватаркой, без пользователей")
    @AllureIds(ios = "51418", android = "50294")
    public void createPrivateChannelNoParticipants() {
        String chatName = TestData.randomText(10);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace()
                .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_HOMESPACE)*/
                .checkContactNameDisplayed(1, contactFullName)
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL, CreateNewMessageDictionary.INVITE_TO_APP)
                .clickCreateChannelBtn();
        pages.createNewChannel().checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE)
                .fillChannelName(chatName);
        SwipeHelper.swipeOnScreen(Direction.UP, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().checkTypeOfChannelSelected(false)
                .selectTypeOfChannel(false);
        SwipeHelper.swipeOnScreen(Direction.DOWN, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE);
        pages.createNewChannel().clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, "TDM")
                    .checkContactInfoByNumber(2, participantUser.getName())
                    .checkContactInfoByNumber(3, contactFullName);
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByName("TDM")
                    .checkContactInfoByName(participantUser.getName())
                    .checkContactInfoByName(contactFullName);
        }
        pages.addSubscribers().clickNextBtn();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(chatName)
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_SUBS.formatted(1));
        if (isIos()) { // todo нет локаторов
            pages.chat().checkSystemMessage(1, ChatDictionary.CHANNEL_WAS_CREATED.formatted(chatName))
                    .checkNoMessageViewsOnChatScreen()
                    .checkChatAvatar(false);
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenPresent();
        }
        pages.channelPreferences().checkChatPreferencesScreenChannelName(chatName)
                .checkChatPreferencesScreenChannelSubName(ChatPreferenceDictionary.PRIVATE_CHANNEL);
        if (isIos()) {
            pages.channelPreferences().checkChatAvatar(false);
        }
    }

    @Test
    @DisplayName("Создать 'Открытый' канал с установленной аватаркой, без пользователей")
    @AllureIds(ios = "51417", android = "50293")
    public void createPublicChannelNoParticipants() {
        String chatName = TestData.randomText(10);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace()
                .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_HOMESPACE)*/
                .checkContactNameDisplayed(1, contactFullName)
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL, CreateNewMessageDictionary.INVITE_TO_APP)
                .clickCreateChannelBtn();
        pages.createNewChannel().checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE)
                .fillChannelName(chatName);
        SwipeHelper.swipeOnScreen(Direction.UP, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().checkTypeOfChannelSelected(false)
                .selectTypeOfChannel(true);
        SwipeHelper.swipeOnScreen(Direction.DOWN, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE);
        pages.createNewChannel().clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, "TDM")
                    .checkContactInfoByNumber(2, participantUser.getName())
                    .checkContactInfoByNumber(3, contactFullName);
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByName("TDM")
                    .checkContactInfoByName(participantUser.getName())
                    .checkContactInfoByName(contactFullName);
        }
        pages.addSubscribers().clickNextBtn();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(chatName)
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_SUBS.formatted(1));
        if (isIos()) { // todo нет локаторов
            pages.chat().checkSystemMessage(1, ChatDictionary.CHANNEL_WAS_CREATED.formatted(chatName))
                    .checkNoMessageViewsOnChatScreen()
                    .checkChatAvatar(false);
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenPresent();
        }
        pages.channelPreferences().checkChatPreferencesScreenChannelName(chatName)
                .checkChatPreferencesScreenChannelSubName(ChatPreferenceDictionary.PUBLIC_CHANNEL);
        if (isIos()) {
            pages.channelPreferences().checkChatAvatar(false);
        }
    }

    @Test
    @DisplayName("Создать 'Закрытый' канал с установленной аватаркой, с пользователями")
    @AllureIds(ios = "93590", android = "50296")
    public void createPrivateChannelWithParticipants() {
        String chatName = TestData.randomText(10);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace()
                .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_HOMESPACE)*/
                .checkContactNameDisplayed(1, contactFullName)
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL, CreateNewMessageDictionary.INVITE_TO_APP)
                .clickCreateChannelBtn();
        pages.createNewChannel().checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE)
                .fillChannelName(chatName);
        SwipeHelper.swipeOnScreen(Direction.UP, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().checkTypeOfChannelSelected(false)
                .selectTypeOfChannel(false);
        SwipeHelper.swipeOnScreen(Direction.DOWN, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE);
        pages.createNewChannel().clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, "TDM")
                    .checkContactInfoByNumber(2, participantUser.getName())
                    .checkContactInfoByNumber(3, contactFullName);
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByName("TDM")
                    .checkContactInfoByName(participantUser.getName())
                    .checkContactInfoByName(contactFullName);
        }
        String[] bubbleNames = isIos() ? new String[]{participantUser.getFirstName(), contactFirstName} :
                new String[]{participantUser.getName(), contactFullName};
        pages.addSubscribers().selectContactsByName(new String[]{participantUser.getName(), contactFullName})
                // todo есть задача https://allprojects.atlassian.net/browse/FB-21006
                .checkBubblesByNames(bubbleNames)
                .clickNextBtn();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(chatName)
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_SUBS.formatted(3) + "а");
        if (isIos()) { // todo нет локаторов
            pages.chat().checkSystemMessage(1, ChatDictionary.CHANNEL_WAS_CREATED.formatted(chatName))
                    .checkNoMessageViewsOnChatScreen()
                    .checkChatAvatar(false);
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenPresent();
        }
        pages.channelPreferences().checkChatPreferencesScreenChannelName(chatName)
                .checkChatPreferencesScreenChannelSubName(ChatPreferenceDictionary.PRIVATE_CHANNEL);
        if (isIos()) {
            pages.channelPreferences().checkChatAvatar(false);
        }
    }

    @Test
    @DisplayName("Создать 'Открытый' канал с установленной аватаркой, с пользователями")
    @AllureIds(ios = "93589", android = "50295")
    public void createPublicChannelWithParticipants() {
        String chatName = TestData.randomText(10);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace()
                .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_HOMESPACE)*/
                .checkContactNameDisplayed(1, contactFullName)
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL, CreateNewMessageDictionary.INVITE_TO_APP)
                .clickCreateChannelBtn();
        pages.createNewChannel().checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE)
                .fillChannelName(chatName);
        SwipeHelper.swipeOnScreen(Direction.UP, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().checkTypeOfChannelSelected(false)
                .selectTypeOfChannel(true);
        SwipeHelper.swipeOnScreen(Direction.DOWN, 0.1d, Duration.ofMillis(100));
        pages.createNewChannel().selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE);
        pages.createNewChannel().clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByNumber(1, "TDM")
                    .checkContactInfoByNumber(2, participantUser.getName())
                    .checkContactInfoByNumber(3, contactFullName);
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                    .checkContactInfoByName("TDM")
                    .checkContactInfoByName(participantUser.getName())
                    .checkContactInfoByName(contactFullName);
        }
        String[] bubbleNames = isIos() ? new String[]{participantUser.getFirstName(), contactFirstName} :
                new String[]{participantUser.getName(), contactFullName};
        pages.addSubscribers().selectContactsByName(new String[]{participantUser.getName(), contactFullName})
                // todo есть задача https://allprojects.atlassian.net/browse/FB-21006
                .checkBubblesByNames(bubbleNames)
                .clickNextBtn();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(chatName)
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_SUBS.formatted(3) + "а");
        if (isIos()) { // todo нет локаторов
            pages.chat().checkSystemMessage(1, ChatDictionary.CHANNEL_WAS_CREATED.formatted(chatName))
                    .checkNoMessageViewsOnChatScreen()
                    .checkChatAvatar(false);
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.channelPreferences().checkChatPreferencesScreenPresent();
        }
        pages.channelPreferences().checkChatPreferencesScreenChannelName(chatName)
                .checkChatPreferencesScreenChannelSubName(ChatPreferenceDictionary.PUBLIC_CHANNEL);
        if (isIos()) {
            pages.channelPreferences().checkChatAvatar(false);
        }
    }

    @Test
    @DisplayName("Загрузить аватарку, удалить аватарку, указать название и описание, добавить юзеров и нажать кнопку \"Готово\" > канал создан")
    @AllureIds(ios = "51419", android = "50298")
    public void createChannelWithAvatarNameDescriptionAndParticipants() {
        String chatName = TestData.randomText(10);
        String chatDescription = TestData.randomSentence(5);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace()
                .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                .clickCreateChannelBtn();
        pages.createNewChannel().checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE)
                .selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE)
                .deleteAvatarForChannel()
                .fillChannelName(chatName)
                .fillChannelDescription(chatDescription)
                .clickNextBtn();
        pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                .selectContactsByName(new String[]{participantUser.getName(), contactFullName})
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
        pages.channelPreferences().checkChatPreferencesScreenChannelName(chatName)
                .checkChatPreferencesScreenChannelDescription(chatDescription);
        if (isIos()) {
            pages.channelPreferences().checkChatAvatar(true);
        }
    }

    @Test
    @DisplayName("Загрузить аватарку, удалить аватарку, указать название и описание, не добавляя юзеров нажать кнопку \"Готово\" > канал создан")
    @AllureIds(ios = "94025", android = "50297")
    public void createChannelWithAvatarNameDescriptionWithoutParticipants() {
        String chatName = TestData.randomText(10);
        String chatDescription = TestData.randomSentence(5);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace()
                .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                .clickCreateChannelBtn();
        pages.createNewChannel().checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE)
                .selectAvatarForChannel()
                .checkCreateNewChannelScreenTitle(CreateNewChannelDictionary.SCREEN_TITLE_HOMESPACE)
                .deleteAvatarForChannel()
                .fillChannelName(chatName)
                .fillChannelDescription(chatDescription)
                .clickNextBtn();
        pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL)
                .clickNextBtn();
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
        pages.channelPreferences().checkChatPreferencesScreenChannelName(chatName)
                .checkChatPreferencesScreenChannelDescription(chatDescription);
        if (isIos()) {
            pages.channelPreferences().checkChatAvatar(true);
        }
    }

    private void prepareTestData() {
        contactUser = userFactory.takeUser();
        contactFirstName = TestData.randomFirstName();
        contactLastName = TestData.randomLastName();
        contactFullName = "%s %s".formatted(contactFirstName, contactLastName);
        ContactsHelper.addContact(contactFullName, contactUser.getPhoneNumber());
        FileUtils.addMediaFile(MediaFileExtension.JPG);
        GroupContext context = groupFactory.p2pChat();
        context.getParticipantBuilder().markSpaceRead(-1L);
        context.getOwnerBuilder().sendTextMessages(context.getGroupId(), TestData.randomText(10));
        participantUser = context.getParticipant();
        ownerUser = context.getOwner();
    }
}
