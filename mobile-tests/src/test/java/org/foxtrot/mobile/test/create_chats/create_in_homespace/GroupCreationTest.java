package org.foxtrot.mobile.test.create_chats.create_in_homespace;

import org.foxtrot.enums.MediaFileExtension;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.mobile.allure.AllureIds;
import org.foxtrot.mobile.core.emulator.DeviceCleanupManager;
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

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;
import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public class GroupCreationTest extends BaseMobileTest {
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
    @DisplayName("Совершить поиск в модальном окне добавления участников в группу по номеру")
    @AllureIds(ios = "51416", android = "50246")
    public void searchParticipantsToGroupByNumber() {
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace()
                .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_HOMESPACE)*/
                .checkContactNameDisplayed(1, contactFullName)
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL, CreateNewMessageDictionary.INVITE_TO_APP)
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .fillGroupName(TestData.randomText(10))
                .selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByNumber(1, "TDM")
                    .checkContactInfoByNumber(2, participantUser.getName())
                    .checkContactInfoByNumber(3, contactFullName)
                    .clickSearchBtn()
                    .searchByText(contactUser.getPhoneNumber())
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(participantUser.getPhoneNumber())
                    .checkContactInfoByNumber(1, participantUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
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
    @DisplayName("Совершить поиск в модальном окне добавления участников в группу по фамилии")
    @AllureIds(ios = "51415", android = "50245")
    public void searchParticipantsToGroupBySurname() {
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace()
                .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_HOMESPACE)*/
                .checkContactNameDisplayed(1, contactFullName)
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL, CreateNewMessageDictionary.INVITE_TO_APP)
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .fillGroupName(TestData.randomText(10))
                .selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByNumber(1, "TDM")
                    .checkContactInfoByNumber(2, participantUser.getName())
                    .checkContactInfoByNumber(3, contactFullName)
                    .clickSearchBtn()
                    .searchByText(contactLastName)
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(participantUser.getLastName())
                    .checkContactInfoByNumber(1, participantUser.getName());
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
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
    @DisplayName("Совершить поиск в модальном окне добавления участников в группу по имени")
    @AllureIds(ios = "51414", android = "50244")
    public void searchParticipantsToGroupByName() {
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace()
                .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_HOMESPACE)*/
                .checkContactNameDisplayed(1, contactFullName)
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL, CreateNewMessageDictionary.INVITE_TO_APP)
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .fillGroupName(TestData.randomText(10))
                .selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByNumber(1, "TDM")
                    .checkContactInfoByNumber(2, participantUser.getName())
                    .checkContactInfoByNumber(3, contactFullName)
                    .clickSearchBtn()
                    .searchByText(contactFirstName)
                    .checkContactInfoByNumber(1, contactFullName)
                    .searchByText(participantUser.getFirstName())
                    .checkContactInfoByNumber(1, participantUser.getName());
        } else if (isIos()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
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
    @DisplayName("Создать групповой чат с установленной аватаркой, без пользователей")
    @AllureIds(ios = "51411", android = "50240")
    public void createGroupNoParticipants() {
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
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .fillGroupName(chatName)
                .selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByNumber(1, "TDM")
                    .checkContactInfoByNumber(2, participantUser.getName())
                    .checkContactInfoByNumber(3, contactFullName);
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByName("TDM")
                    .checkContactInfoByName(participantUser.getName())
                    .checkContactInfoByName(contactFullName);
        }
        pages.addSubscribers().clickNextBtn();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(chatName)
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_PARTICIPANTS.formatted(1));
        if (isIos()) { // todo нет локаторов
            pages.chat().checkSystemMessage(1, ChatDictionary.GROUP_WAS_CREATED.formatted(chatName))
                    .checkNoMessageViewsOnChatScreen()
                    .checkChatAvatar(false);
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.groupPreferences().checkChatPreferencesScreenPresent();
        }
        pages.groupPreferences().checkChatPreferencesScreenGroupName(chatName)
                .checkChatPreferencesScreenGroupSubName(ChatPreferenceDictionary.PRIVATE_GROUP)
                .checkParticipantsNumber("1");
        if (isIos()) {
            pages.groupPreferences().checkChatAvatar(false);
        }
    }

    @Test
    @DisplayName("Создать групповой чат с установленной аватаркой, названием, с пользователями")
    @AllureIds(ios = "51412", android = "50241")
    public void createGroupWithParticipants() {
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
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .fillGroupName(chatName)
                .selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .clickNextBtn();
        if (isAndroid()) { // todo задача https://allprojects.atlassian.net/browse/FB-20800 и https://allprojects.atlassian.net/browse/FB-20803
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                    .checkContactInfoByNumber(1, "TDM")
                    .checkContactInfoByNumber(2, participantUser.getName())
                    .checkContactInfoByNumber(3, contactFullName);
        } else if (isIos()) {
            pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
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
                .checkStatusContentInHeader(ChatDictionary.NUMBER_OF_PARTICIPANTS.formatted(3) + "а");
        if (isIos()) { // todo нет локаторов
            pages.chat().checkSystemMessage(1, ChatDictionary.GROUP_WAS_CREATED.formatted(chatName))
                    .checkSystemMessageContainsText(2,
                            new String[]{ChatDictionary.YOU_INVITED, participantUser.getName(), contactFullName})
                    .checkNoMessageViewsOnChatScreen()
                    .checkChatAvatar(false);
        }
        pages.chat().clickChatNameInHeader();
        if (isIos()) { // todo нет локаторов
            pages.groupPreferences().checkChatPreferencesScreenPresent();
        }
        pages.groupPreferences().checkChatPreferencesScreenGroupName(chatName)
                .checkChatPreferencesScreenGroupSubName(ChatPreferenceDictionary.PRIVATE_GROUP)
                .checkParticipantsNumber("3");
        if (isIos()) {
            pages.groupPreferences().checkChatAvatar(false);
        }
    }

    @Test
    @DisplayName("Загрузить аватарку, удалить аватарку, указать название и описание, добавить юзеров и нажать кнопку \"Готово\" > группа создана")
    @AllureIds(ios = "51413", android = "50243")
    public void createGroupWithAvatarNameDescriptionAndParticipants() {
        String chatName = TestData.randomText(10);
        String chatDescription = TestData.randomSentence(5);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace()
                .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .deleteAvatarForGroup()
                .fillGroupName(chatName)
                .fillGroupDescription(chatDescription)
                .clickNextBtn();
        pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                .selectContactsByName(new String[]{participantUser.getName(), contactFullName})
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
    @AllureIds(ios = "94024", android = "50242")
    public void createGroupWithAvatarNameDescriptionWithoutParticipants() {
        String chatName = TestData.randomText(10);
        String chatDescription = TestData.randomSentence(5);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace()
                .checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                .clickCreateGroupBtn();
        pages.createNewGroup().checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .selectAvatarForGroup()
                .checkCreateNewGroupScreenTitle(CreateNewGroupDictionary.SCREEN_TITLE)
                .deleteAvatarForGroup()
                .fillGroupName(chatName)
                .fillGroupDescription(chatDescription)
                .clickNextBtn();
        pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP)
                .clickNextBtn();
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
