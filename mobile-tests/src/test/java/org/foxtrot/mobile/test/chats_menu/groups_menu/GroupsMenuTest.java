package org.foxtrot.mobile.test.chats_menu.groups_menu;

import org.foxtrot.enums.MediaFileExtension;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.mobile.allure.AllureIds;
import org.foxtrot.mobile.core.emulator.DeviceCleanupManager;
import org.foxtrot.mobile.test.BaseMobileTest;
import org.foxtrot.mobile.testdata.dictionary.AddSubscribersDictionary;
import org.foxtrot.mobile.testdata.dictionary.ChatPreferenceDictionary;
import org.foxtrot.mobile.testdata.dictionary.ChatsDictionary;
import org.foxtrot.mobile.testdata.dictionary.GroupMembersDictionary;
import org.foxtrot.mobile.utils.ClipboardUtils;
import org.foxtrot.mobile.utils.ContactsHelper;
import org.foxtrot.mobile.utils.FileUtils;
import org.foxtrot.mobile.utils.LoaderWaiter;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.UrlConstants;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.foxtrot.utils.factories.core.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;
import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public class GroupsMenuTest extends BaseMobileTest {

    private final GroupFactory groupFactory = new GroupFactory();
    private final UserFactory userFactory = new UserFactory();
    private GroupContext groupContext;
    private UsersEnum contactUser;
    private UsersEnum notContactUser;
    private String contactFirstName;
    private String contactLastName;
    private String contactFullName;

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
        DeviceCleanupManager.cleanDeviceState();
    }

    @Test
    @AllureIds(ios = "95057", android = "50366")
    @DisplayName("Изменить описание группы, сохранить данные > описание группы изменено")
    public void changeGroupDescription() {
        String initialDescription = TestData.randomText(20);
        String newDescription = TestData.randomText(20);
        groupContext = groupFactory.privateGroup(initialDescription);
        pages.mobileLogin().loginViaUi(groupContext.getOwner());
        pages.chats().openChatByName(groupContext.getGroupName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .clickChatNameInHeader();
        pages.groupPreferences().checkChatPreferencesScreenPresent()
                .checkChatPreferencesScreenGroupName(groupContext.getGroupName())
                .checkChatPreferencesScreenGroupDescription(initialDescription);
        pages.groupPreferences().openEditGroup();
        pages.createNewGroup()
                .fillGroupDescription(newDescription)
                .clickSaveBtn();
        LoaderWaiter.waitForNoLoader();
        pages.groupPreferences().checkChatPreferencesScreenGroupDescription(newDescription);
    }

    @Test
    @AllureIds(ios = "51449", android = "50365")
    @DisplayName("Изменить название группы, сохранить данные > название группы изменено")
    public void changeGroupName() {
        groupContext = groupFactory.privateGroup();
        String initialName = groupContext.getGroupName();
        String newName = TestData.randomText(10);
        pages.mobileLogin().loginViaUi(groupContext.getOwner());
        pages.chats().openChatByName(initialName);
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .clickChatNameInHeader();
        pages.groupPreferences().checkChatPreferencesScreenPresent()
                .checkChatPreferencesScreenGroupName(initialName);
        pages.groupPreferences().openEditGroup();
        pages.createNewGroup()
                .fillGroupName(newName)
                .clickSaveBtn();
        LoaderWaiter.waitForNoLoader();
        pages.groupPreferences().checkChatPreferencesScreenGroupName(newName);
    }

    @Test
    @AllureIds(ios = "51450", android = "50367")
    @DisplayName("Изменить тип группы, сохранить данные > тип группы изменен (только в пространстве)")
    public void changeGroupTypeInWorkspace() {
        groupContext = groupFactory.privateGroupInWs();
        pages.mobileLogin().loginViaUi(groupContext.getOwner());
        pages.bottomMenu().clickWorkspacesButton();
        pages.workspace().checkWorkspaceScreenTitle(groupContext.getWorkspaceName());
        pages.chats().openChatByName(groupContext.getGroupName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .clickChatNameInHeader();
        pages.groupPreferences().checkChatPreferencesScreenPresent()
                .checkChatPreferencesScreenGroupName(groupContext.getGroupName())
                .checkChatPreferencesScreenGroupSubName(ChatPreferenceDictionary.PRIVATE_GROUP);
        pages.groupPreferences().openEditGroup();
        pages.createNewGroup()
                .selectTypeOfGroup(true)
                .clickSaveBtn();
        LoaderWaiter.waitForNoLoader();
        pages.groupPreferences()
                .checkChatPreferencesScreenGroupSubName(ChatPreferenceDictionary.PUBLIC_GROUP);
    }

    @Test
    @AllureIds(ios = "51448", android = "50364")
    @DisplayName("Изменить аву группы, сохранить данные > ава группы изменена")
    public void changeGroupAvatar() {
        FileUtils.addMediaFile(MediaFileExtension.JPG);
        groupContext = groupFactory.privateGroup();
        pages.mobileLogin().loginViaUi(groupContext.getOwner());
        pages.chats().openChatByName(groupContext.getGroupName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .clickChatNameInHeader();
        pages.groupPreferences().checkChatPreferencesScreenPresent()
                .checkChatAvatar(true);
        pages.groupPreferences().openEditGroup();
        pages.createNewGroup().selectAvatarForGroup()
                .clickSaveBtn();
        LoaderWaiter.waitForNoLoader();
        pages.groupPreferences().checkChatPreferencesScreenPresent()
                .checkChatAvatar(false);
    }

    @Test
    @AllureIds(ios = "51440", android = "50355")
    @DisplayName("Добавить несколько пользователей в существующую группу > пользователи добавлены")
    public void addUsersToExistingGroup() {
        prepareGroupWithContactAndNotContact();
        openGroupMembersScreen();
        pages.groupMembers().clickAddParticipants();
        pages.addSubscribers().checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP_BY_SETTINGS)
                .selectContactsByName(notContactUser.getName(), contactFullName)
                .clickNextBtn();
        if (isAndroid()) {
            pages.chat().checkChatScreenDisplayed()
                    .clickChatNameInHeader();
        }
        pages.groupPreferences().checkChatPreferencesScreenPresent()
                .checkParticipantsNumber("3")
                .openParticipantsList();
        pages.groupMembers().checkScreenTitle(GroupMembersDictionary.SCREEN_TITLE)
                .checkContactInfoByName(notContactUser.getName())
                .checkContactInfoByName(contactFullName);
    }

    @Test
    @AllureIds(ios = "51441", android = "50356")
    @DisplayName("Совершить поиск в модальном окне добавления пользователей в группу по имени > "
            + "(найден контакт и неконтакт с видимым номером телефона)")
    public void searchUsersToAddByName() {
        prepareGroupWithContactAndNotContact();
        openGroupMembersScreen();
        pages.groupMembers().clickAddParticipants();
        pages.addSubscribers()
                .checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP_BY_SETTINGS)
                .checkContactInfoByName(notContactUser.getName())
                .checkContactInfoByName(contactFullName)
                .clickSearchBtn()
                .searchByText(contactFirstName)
                .checkContactInfoByNumber(1, contactFullName)
                .searchByText(notContactUser.getFirstName())
                .checkContactInfoByName(notContactUser.getName());
    }

    @Test
    @AllureIds(ios = "51442", android = "50357")
    @DisplayName("Совершить поиск в модальном окне добавления пользователей в группу по фамилии > "
            + "(найден контакт и неконтакт с видимым номером телефона)")
    public void searchUsersToAddBySurname() {
        prepareGroupWithContactAndNotContact();
        openGroupMembersScreen();
        pages.groupMembers().clickAddParticipants();
        pages.addSubscribers()
                .checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP_BY_SETTINGS)
                .checkContactInfoByName(notContactUser.getName())
                .checkContactInfoByName(contactFullName)
                .clickSearchBtn()
                .searchByText(contactLastName)
                .checkContactInfoByNumber(1, contactFullName)
                .searchByText(notContactUser.getLastName())
                .checkContactInfoByNumber(1, notContactUser.getName());
    }

    @Test
    @AllureIds(ios = "51443", android = "50358")
    @DisplayName("Совершить поиск в модальном окне добавления пользователей в группу по номеру > "
            + "(найден контакт и неконтакт с видимым номером телефона)")
    public void searchUsersToAddByNumber() {
        prepareGroupWithContactAndNotContact();
        openGroupMembersScreen();
        pages.groupMembers().clickAddParticipants();
        pages.addSubscribers()
                .checkAddSubscribersScreenTitle(AddSubscribersDictionary.SCREEN_TITLE_GROUP_BY_SETTINGS)
                .checkContactInfoByName(notContactUser.getName())
                .checkContactInfoByName(contactFullName)
                .clickSearchBtn()
                .searchByPhone(contactUser.getPhoneNumber())
                .checkContactInfoByNumber(1, contactFullName)
                .searchByPhone(notContactUser.getPhoneNumber())
                .checkContactInfoByNumber(1, notContactUser.getName());
    }

    @Test
    @AllureIds(ios = "51456", android = "50373")
    @DisplayName("Покинуть группу")
    public void leaveGroup() {
        groupContext = groupFactory.privateGroupWithParticipant();
        pages.mobileLogin().loginViaUi(groupContext.getParticipant());
        pages.chats().openChatByName(groupContext.getGroupName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .clickChatNameInHeader();
        pages.groupPreferences().checkChatPreferencesScreenPresent()
                .clickLeaveGroup();
        pages.modal().checkModalTitle(ChatPreferenceDictionary.LEAVE_GROUP_MODAL_TITLE.formatted(groupContext.getGroupName()));
        pages.modal().clickLeaveButton();
        pages.chats().checkChatsScreenTitle(ChatsDictionary.SCREEN_TITLE)
                .checkChatDisplayed(groupContext.getGroupName(), false);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"owner", "admin", "participant"})
    @AllureIds(ios = "51455", android = "50372")
    @DisplayName("Присутствует кнопка: Покинуть группу (только для админа и участника)")
    public void leaveGroupButtonVisibleForAdminAndParticipantOnly(String role) {
        groupContext = groupFactory.privateGroupWithAdminAndParticipant();
        UsersEnum user = switch (role) {
            case "owner" -> groupContext.getOwner();
            case "admin" -> groupContext.getAdmin();
            case "participant" -> groupContext.getParticipant();
            default -> throw new IllegalArgumentException("Неизвестная роль: " + role);
        };
        pages.mobileLogin().loginViaUi(user);
        pages.chats().openChatByName(groupContext.getGroupName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .clickChatNameInHeader();
        pages.groupPreferences().checkChatPreferencesScreenPresent();
        if ("owner".equals(role)) {
            if (isIos()) { // todo сделать задачу
                pages.groupPreferences().checkLeaveGroupButtonDisplayed(false);
            } else {
                pages.groupPreferences().clickLeaveGroup();
                pages.modal().checkModalTitle(ChatPreferenceDictionary.LEAVE_GROUP_OWNER_MODAL_TITLE);
            }
            return;
        }
        pages.groupPreferences().clickLeaveGroup();
        pages.modal().checkModalTitle(
                ChatPreferenceDictionary.LEAVE_GROUP_MODAL_TITLE.formatted(groupContext.getGroupName()));
    }

    @Test
    @AllureIds(ios = "51439", android = "50353")
    @DisplayName("Присутствует кнопка: Участники > отображаются все участники группы")
    public void participantsButtonShowsAllGroupMembers() {
        groupContext = groupFactory.privateGroupWithAdminAndParticipant();
        pages.mobileLogin().loginViaUi(groupContext.getOwner());
        pages.chats().openChatByName(groupContext.getGroupName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .clickChatNameInHeader();
        pages.groupPreferences().checkChatPreferencesScreenPresent()
                .checkParticipantsButtonDisplayed(true)
                .checkParticipantsNumber("3")
                .openParticipantsList();
        pages.groupMembers().checkScreenTitle(GroupMembersDictionary.SCREEN_TITLE)
                .checkContactInfoByName(groupContext.getOwner().getName())
                .checkContactInfoByName(groupContext.getAdmin().getName())
                .checkContactInfoByName(groupContext.getParticipant().getName());
    }

    @Test
    @AllureIds(ios = "51436", android = "50350")
    @DisplayName("Присутствует кнопка: Скопировать ссылку на группу (для открытых групп)")
    public void copyGroupLinkButtonVisibleForPublicGroup() {
        groupContext = groupFactory.publicGroupInWs();
        String groupUrl = UrlConstants.getWorkspacePublicGroupUrl(
                String.valueOf(groupContext.getWorkspaceId()),
                String.valueOf(groupContext.getGroupId()));
        pages.mobileLogin().loginViaUi(groupContext.getOwner());
        pages.bottomMenu().clickWorkspacesButton();
        pages.workspace().checkWorkspaceScreenTitle(groupContext.getWorkspaceName());
        pages.chats().openChatByName(groupContext.getGroupName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .clickChatNameInHeader();
        pages.groupPreferences().checkChatPreferencesScreenPresent()
                .checkCopyGroupLinkButtonDisplayed(true)
                .clickCopyGroupLink()
                .checkToastDisplayed(ChatPreferenceDictionary.LINK_COPIED);
        ClipboardUtils.checkClipboardEquals(groupUrl);
    }

    @Test
    @AllureIds(ios = "51435", android = "50349")
    @DisplayName("Проверить работу кнопки: Сгенерировать новую ссылку > ссылка сгенерирована")
    public void generateNewGroupInviteLink() {
        groupContext = groupFactory.privateGroup();
        String inviteUrl = UrlConstants.getHomespaceGroupInviteUrl(
                groupContext.getOwnerBuilder().regenerateInviteLink(groupContext.getGroupId()));
        pages.mobileLogin().loginViaUi(groupContext.getOwner());
        pages.chats().openChatByName(groupContext.getGroupName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .clickChatNameInHeader();
        pages.groupPreferences().checkChatPreferencesScreenPresent()
                .openInviteByLink();
        pages.groupInviteByLink().checkScreenPresent()
                .checkScreenTitle(ChatPreferenceDictionary.INVITE_BY_LINK_SCREEN_TITLE)
                .checkInviteLink(inviteUrl)
                .clickGenerateNewLink();
        pages.modal().checkModalTitle(ChatPreferenceDictionary.GENERATE_NEW_LINK_MODAL_TITLE)
                .clickInviteButton();
        pages.groupInviteByLink().checkScreenPresent()
                .checkInviteLinkChanged(inviteUrl);
    }

    private void prepareGroupWithContactAndNotContact() {
        notContactUser = userFactory.takeUser();
        contactUser = userFactory.takeUser();
        contactFirstName = TestData.randomFirstName();
        contactLastName = TestData.randomLastName();
        contactFullName = "%s %s".formatted(contactFirstName, contactLastName);
        ContactsHelper.addContact(contactFullName, contactUser.getPhoneNumber());
        groupContext = groupFactory.privateGroup();
        Long p2pGroupId = groupContext.getOwnerBuilder().createP2PGroup(notContactUser);
        groupContext.getOwnerBuilder().sendTextMessages(p2pGroupId, TestData.randomText(10));
    }

    private void openGroupMembersScreen() {
        pages.mobileLogin().loginViaUi(groupContext.getOwner());
        pages.chats().openChatByName(groupContext.getGroupName());
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .clickChatNameInHeader();
        pages.groupPreferences().checkChatPreferencesScreenPresent()
                .openParticipantsList();
        pages.groupMembers().checkScreenTitle(GroupMembersDictionary.SCREEN_TITLE);
    }
}
