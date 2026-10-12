package org.foxtrot.mobile.test.chats_menu.channels_menu;

import org.foxtrot.enums.MediaFileExtension;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.mobile.allure.AllureIds;
import org.foxtrot.mobile.core.emulator.DeviceCleanupManager;
import org.foxtrot.mobile.test.BaseMobileTest;
import org.foxtrot.mobile.testdata.dictionary.AddSubscribersDictionary;
import org.foxtrot.mobile.testdata.dictionary.ChannelMembersDictionary;
import org.foxtrot.mobile.testdata.dictionary.ChatPreferenceDictionary;
import org.foxtrot.mobile.utils.ContactsHelper;
import org.foxtrot.mobile.utils.FileUtils;
import org.foxtrot.mobile.utils.LoaderWaiter;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.foxtrot.utils.factories.core.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ChannelsMenuTest extends BaseMobileTest {

    private final GroupFactory groupFactory = new GroupFactory();
    private final UserFactory userFactory = new UserFactory();
    private GroupContext channelContext;
    private UsersEnum contactUser;
    private UsersEnum notContactUser;
    private String contactFirstName;
    private String contactLastName;
    private String contactFullName;

    @BeforeEach
    public void setUpContact() {
        notContactUser = userFactory.takeUser();
        contactUser = userFactory.takeUser();
        contactFirstName = TestData.randomFirstName();
        contactLastName = TestData.randomLastName();
        contactFullName = "%s %s".formatted(contactFirstName, contactLastName);
        ContactsHelper.addContact(contactFullName, contactUser.getPhoneNumber());
    }

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
        DeviceCleanupManager.cleanDeviceState();
    }

    @Test
    @AllureIds(ios = "51483", android = "50458")
    @DisplayName("Изменить тип канала, сохранить данные > тип канала изменен")
    public void changeChannelType() {
        channelContext = groupFactory.privateChannel();
        pages.mobileLogin().loginViaUi(channelContext.getOwner());
        pages.chats().openChatByName(channelContext.getGroupName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .clickChatNameInHeader();
        pages.channelPreferences().checkChatPreferencesScreenPresent()
                .checkChatPreferencesScreenChannelName(channelContext.getGroupName())
                .checkChatPreferencesScreenChannelSubName(ChatPreferenceDictionary.PRIVATE_CHANNEL);
        pages.channelPreferences().openEditChannel();
        pages.createNewChannel()
                .selectTypeOfChannel(true)
                .clickDoneBtn();
        LoaderWaiter.waitForNoLoader();
        pages.channelPreferences()
                .checkChatPreferencesScreenChannelSubName(ChatPreferenceDictionary.PUBLIC_CHANNEL);
    }

    @Test
    @AllureIds(ios = "95788", android = "50457")
    @DisplayName("Изменить описание канала, сохранить данные > описание канала изменено")
    public void changeChannelDescription() {
        String initialDescription = TestData.randomText(20);
        String newDescription = TestData.randomText(20);
        channelContext = groupFactory.privateChannel(initialDescription);
        pages.mobileLogin().loginViaUi(channelContext.getOwner());
        pages.chats().openChatByName(channelContext.getGroupName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .clickChatNameInHeader();
        pages.channelPreferences().checkChatPreferencesScreenPresent()
                .checkChatPreferencesScreenChannelName(channelContext.getGroupName())
                .checkChatPreferencesScreenChannelDescription(initialDescription);
        pages.channelPreferences().openEditChannel();
        pages.createNewChannel()
                .fillChannelDescription(newDescription)
                .clickDoneBtn();
        LoaderWaiter.waitForNoLoader();
        pages.channelPreferences().checkChatPreferencesScreenChannelDescription(newDescription);
    }

    @Test
    @AllureIds(ios = "51482", android = "50456")
    @DisplayName("Изменить название канала, сохранить данные > название канала изменено")
    public void changeChannelName() {
        channelContext = groupFactory.privateChannel();
        String initialName = channelContext.getGroupName();
        String newName = TestData.randomText(10);
        pages.mobileLogin().loginViaUi(channelContext.getOwner());
        pages.chats().openChatByName(initialName);
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .clickChatNameInHeader();
        pages.channelPreferences().checkChatPreferencesScreenPresent()
                .checkChatPreferencesScreenChannelName(initialName);
        pages.channelPreferences().openEditChannel();
        pages.createNewChannel()
                .fillChannelName(newName)
                .clickDoneBtn();
        LoaderWaiter.waitForNoLoader();
        pages.channelPreferences().checkChatPreferencesScreenChannelName(newName);
    }

    @Test
    @AllureIds(ios = "51481", android = "50455")
    @DisplayName("Изменить аву канала, сохранить данные > ава канала изменена")
    public void changeChannelAvatar() {
        FileUtils.addMediaFile(MediaFileExtension.JPG);
        channelContext = groupFactory.privateChannel();
        pages.mobileLogin().loginViaUi(channelContext.getOwner());
        pages.chats().openChatByName(channelContext.getGroupName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .clickChatNameInHeader();
        pages.channelPreferences().checkChatPreferencesScreenPresent()
                .checkChatAvatar(true);
        pages.channelPreferences().openEditChannel();
        pages.createNewChannel().selectAvatarForChannel()
                .clickDoneBtn();
        LoaderWaiter.waitForNoLoader();
        pages.channelPreferences().checkChatPreferencesScreenPresent()
                .checkChatAvatar(false);
    }

    @Test
    @AllureIds(ios = "51473", android = "50447")
    @DisplayName("Добавить несколько пользователей в существующий канал > пользователи добавлены")
    public void addUsersToExistingChannel() {
        prepareChannelWithContactAndNotContact();
        openChannelMembersScreen();
        pages.channelMembers().clickAddParticipants();
        pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL_BY_SETTINGS)
                .selectContactsByName(notContactUser.getName(), contactFullName)
                .clickNextBtn();
        pages.channelPreferences().checkChatPreferencesScreenPresent()
                .openParticipantsList();
        pages.channelMembers().checkScreenTitle(ChannelMembersDictionary.SCREEN_TITLE)
                .checkContactInfoByName(notContactUser.getName())
                .checkContactInfoByName(contactFullName);
    }

    @Test
    @AllureIds(ios = "51474", android = "50448")
    @DisplayName("Совершить поиск в модальном окне добавления пользователей в канал по имени > "
            + "(найден контакт и неконтакт с видимым номером телефона)")
    public void searchUsersToAddByName() {
        prepareChannelWithContactAndNotContact();
        openChannelMembersScreen();
        pages.channelMembers().clickAddParticipants();
        pages.addSubscribers()
                .checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL_BY_SETTINGS)
                .checkContactInfoByName(notContactUser.getName())
                .checkContactInfoByName(contactFullName)
                .clickSearchBtn()
                .searchByText(contactFirstName)
                .checkContactInfoByNumber(1, contactFullName)
                .searchByText(notContactUser.getFirstName())
                .checkContactInfoByName(notContactUser.getName());
    }

    @Test
    @AllureIds(ios = "51475", android = "50449")
    @DisplayName("Совершить поиск в модальном окне добавления пользователей в канал по фамилии > "
            + "(найден контакт и неконтакт с видимым номером телефона)")
    public void searchUsersToAddBySurname() {
        prepareChannelWithContactAndNotContact();
        openChannelMembersScreen();
        pages.channelMembers().clickAddParticipants();
        pages.addSubscribers()
                .checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL_BY_SETTINGS)
                .checkContactInfoByName(notContactUser.getName())
                .checkContactInfoByName(contactFullName)
                .clickSearchBtn()
                .searchByText(contactLastName)
                .checkContactInfoByNumber(1, contactFullName)
                .searchByText(notContactUser.getLastName())
                .checkContactInfoByNumber(1, notContactUser.getName());
    }

    @Test
    @AllureIds(ios = "51476", android = "50450")
    @DisplayName("Совершить поиск в модальном окне добавления пользователей в канал по номеру > "
            + "(найден контакт и неконтакт с видимым номером телефона)")
    public void searchUsersToAddByNumber() {
        prepareChannelWithContactAndNotContact();
        openChannelMembersScreen();
        pages.channelMembers().clickAddParticipants();
        pages.addSubscribers()
                .checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_CHANNEL_BY_SETTINGS)
                .checkContactInfoByName(notContactUser.getName())
                .checkContactInfoByName(contactFullName)
                .clickSearchBtn()
                .searchByPhone(contactUser.getPhoneNumber())
                .checkContactInfoByNumber(1, contactFullName)
                .searchByPhone(notContactUser.getPhoneNumber())
                .checkContactInfoByNumber(1, notContactUser.getName());
    }

    private void prepareChannelWithContactAndNotContact() {
        channelContext = groupFactory.privateChannel();
        Long p2pGroupId = channelContext.getOwnerBuilder().createP2PGroup(notContactUser);
        channelContext.getOwnerBuilder().sendTextMessages(p2pGroupId, TestData.randomText(10));
    }

    private void openChannelMembersScreen() {
        pages.mobileLogin().loginViaUi(channelContext.getOwner());
        pages.chats().openChatByName(channelContext.getGroupName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .clickChatNameInHeader();
        pages.channelPreferences().checkChatPreferencesScreenPresent()
                .openParticipantsList();
        pages.channelMembers().checkScreenTitle(ChannelMembersDictionary.SCREEN_TITLE);
    }
}
