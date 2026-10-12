package org.foxtrot.mobile.test.create_chats.create_in_homespace;

import org.foxtrot.enums.UsersEnum;
import org.foxtrot.mobile.allure.AllureIds;
import org.foxtrot.mobile.core.emulator.DeviceCleanupManager;
import org.foxtrot.mobile.test.BaseMobileTest;
import org.foxtrot.mobile.testdata.dictionary.CreateNewMessageDictionary;
import org.foxtrot.mobile.testdata.dictionary.GroupMembersDictionary;
import org.foxtrot.mobile.utils.ContactsHelper;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.foxtrot.utils.factories.core.UserFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class P2PCreationTest extends BaseMobileTest {
    private UsersEnum ownerUser;
    private UsersEnum contactUser;
    private UsersEnum notContactUser;
    private String groupName;
    private String contactFullName;
    private final UserFactory userFactory = new UserFactory();
    private final GroupFactory groupFactory = new GroupFactory();

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
    @DisplayName("Создать p2p чат с контактом")
    @AllureIds(ios = "51423", android = "50238")
    public void createP2pChatWithContact() {
        String messageText = TestData.randomText(10);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().clickPlusBtn();
        pages.createNewMessageFromHomespace().checkCreateNewMessageScreenTitle(CreateNewMessageDictionary.SCREEN_TITLE_HOMESPACE)
                /* todo задача https://allprojects.atlassian.net/browse/FB-20881
                .checkCreateNewMessageScreenSubtitle(CreateNewMessageDictionary.SCREEN_SUBTITLE_HOMESPACE)*/
                .checkContactNameDisplayed(1, contactFullName)
                .checkBtnTextsOnCreateNewMessageScreen(CreateNewMessageDictionary.CREATE_GROUP,
                        CreateNewMessageDictionary.CREATE_CHANNEL, CreateNewMessageDictionary.INVITE_TO_APP);
        pages.createNewMessageFromHomespace().clickContactByNumber(1);
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed().checkChatTitle(contactFullName);
        pages.chat().sendTextMessage(messageText)
                .checkMessageDisplayed(messageText);
    }

    @Test
    @DisplayName("Создать p2p чат с неконтактом")
    @AllureIds(ios = "51424", android = "50239")
    public void createP2pChatWithNonContact() {
        String messageText = TestData.randomText(10);
        pages.mobileLogin().loginViaUi(ownerUser);
        pages.chats().openChatByName(groupName);
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(groupName);
        pages.chat().clickChatNameInHeader();
        pages.groupPreferences().checkChatPreferencesScreenPresent()
                .openParticipantsList();
        pages.groupMembers().checkScreenTitle(GroupMembersDictionary.SCREEN_TITLE)
                .clickContactByName(notContactUser.getName());
        pages.contactInformation().checkScreenTitle(notContactUser.getName())
                .clickMessageBtn();
        pages.onboarding().clickOnboardingActionButtonIfNeeded();
        pages.chat().checkChatScreenDisplayed()
                .checkChatTitle(notContactUser.getName());
        pages.chat().sendTextMessage(messageText)
                .checkMessageDisplayed(messageText);
    }

    private void prepareTestData() {
        GroupContext groupContext = groupFactory.privateGroupWithParticipant();
        ownerUser = groupContext.getOwner();
        notContactUser = groupContext.getParticipant();
        groupName = groupContext.getGroupName();
        contactUser = userFactory.takeUser();
        contactFullName = "%s %s".formatted(TestData.randomLastName(), TestData.randomLastName());
        ContactsHelper.addContact(contactFullName, contactUser.getPhoneNumber());
    }
}
