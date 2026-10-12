package org.foxtrot.test.global_search.search_sort_navigation;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsersTest extends BaseTest {

    private UserTestDataCleaner cleaner;
    private UsersEnum firstUser;
    private UsersEnum secondUser;
    private UsersEnum thirdUser;
    private UsersEnum fourthUser;
    private TestDataBuilder builderFirst;
    private TestDataBuilder builderSecond;
    private TestDataBuilder builderThird;
    private TestDataBuilder builderFourth;
    private LoginHelper loginHelper;

    @BeforeAll
    public void beforeAll() {
        UserPoolManager userPoolManager = new UserPoolManager();
        cleaner = new UserTestDataCleaner();
        firstUser = userPoolManager.getFreeUser();
        secondUser = userPoolManager.getFreeUser();
        thirdUser = userPoolManager.getFreeUser();
        fourthUser = userPoolManager.getFreeUser();
        builderFirst = new TestDataBuilder(firstUser);
        builderSecond = new TestDataBuilder(secondUser);
        builderThird = new TestDataBuilder(thirdUser);
        builderFourth = new TestDataBuilder(fourthUser);
        loginHelper = new LoginHelper();
    }

    @AfterAll
    public void afterAll() {
        UserPoolManager.releaseCurrentUser();
    }

    @Test
    @AllureId("54306")
    @DisplayName("Поиск контакта по имени от 1 символа (начало, середина, конец имени), с кем есть p2p")
    public void searchContactWithP2PChatByName() {
        builderFirst.addContacts(secondUser);
        Long p2pID = builderFirst.createP2PGroup(secondUser);
        Long messageId = builderFirst.sendTextMessage(p2pID, "Тест");
        try {
            loginHelper.performUILogin(firstUser);

            String query = secondUser.getName().substring(0, 1);
            performUserSearchAndOpen(query);

            pages.topbarSteps.verifyChatName(secondUser.getName());
            pages.chatVerify.verifyMessageWithIdIsDisplayed(String.valueOf(messageId), true);
        } finally {
            cleanupUsers();
        }
    }

    @Test
    @AllureId("54342")
    @DisplayName("Поиск контакта по имени от 1 символа с кем нет p2p")
    public void searchContactWithoutP2PChatByName() {
        builderFirst.addContacts(secondUser);
        try {
            loginHelper.performUILogin(firstUser);

            String query = secondUser.getName().substring(0, 1);
            performUserSearchAndOpen(query);

            pages.topbarSteps.verifyChatName(secondUser.getName());
            pages.chatVerify.verifyChatIsEmpty();
        } finally {
            cleanupUsers();
        }
    }

    @Test
    @AllureId("54334")
    @DisplayName("Поиск контакта по номеру телефона от 1 символа, с кем есть p2p (номер открыт)")
    public void searchContactWithP2PChatByPhone() {
        builderFirst.addContacts(secondUser);
        Long p2pID = builderFirst.createP2PGroup(secondUser);
        Long messageId = builderFirst.sendTextMessage(p2pID, "Тест");
        try {
            loginHelper.performUILogin(firstUser);

            String query = secondUser.getPhoneNumber().substring(0, 5);
            performUserSearchAndOpen(query);

            pages.topbarSteps.verifyChatName(secondUser.getName());
            pages.chatVerify.verifyMessageWithIdIsDisplayed(String.valueOf(messageId), true);
        } finally {
            cleanupUsers();
        }
    }

    @Test
    @AllureId("54531")
    @DisplayName("Поиск контакта по номеру телефона от 1 символа, с кем нет p2p (номер открыт)")
    public void searchContactWithoutP2PChatByPhone() {
        builderFirst.addContacts(secondUser);
        try {
            loginHelper.performUILogin(firstUser);

            String query = secondUser.getPhoneNumber().substring(0, 5);
            performUserSearchAndOpen(query);

            pages.topbarSteps.verifyChatName(secondUser.getName());
            pages.chatVerify.verifyChatIsEmpty();
        } finally {
            cleanupUsers();
        }
    }

    @Test
    @AllureId("54332")
    @DisplayName("Поиск контакта по отчеству от 1 символа, с кем есть p2p")
    public void searchContactWithP2PChatByMiddleName() {
        String middleName = "Отчество";
        builderSecond.updateFullProfile(secondUser.getFirstName(), secondUser.getLastName(), middleName);
        builderFirst.addContacts(secondUser);
        Long p2pID = builderFirst.createP2PGroup(secondUser);
        Long messageId = builderFirst.sendTextMessage(p2pID, "Тест");
        try {
            loginHelper.performUILogin(firstUser);

            String query = middleName.substring(0, 3);
            performUserSearchAndOpen(query);

            pages.topbarSteps.clickP2pHeader();
            pages.p2pMenuVerifier.verifyUserName(
                    secondUser.getFirstName() + " " + middleName + " " + secondUser.getLastName());
            pages.chatVerify.verifyMessageWithIdIsDisplayed(String.valueOf(messageId), true);

        } finally {
            cleaner.resetUserProfile(secondUser);
            cleanupUsers();
        }
    }

    @Test
    @AllureId("54313")
    @DisplayName("Поиск контакта по фамилии от 1 символа, с кем есть p2p")
    public void searchContactWithP2PChatByLastName() {
        builderFirst.addContacts(secondUser);
        Long p2pID = builderFirst.createP2PGroup(secondUser);
        Long messageId = builderFirst.sendTextMessage(p2pID, "Тест");
        try {
            loginHelper.performUILogin(firstUser);

            String query = secondUser.getLastName().substring(0, 1);
            performUserSearchAndOpen(query);

            pages.topbarSteps.verifyChatName(secondUser.getName());
            pages.chatVerify.verifyMessageWithIdIsDisplayed(String.valueOf(messageId), true);
        } finally {
            cleanupUsers();
        }
    }

    @Test
    @AllureId("54530")
    @DisplayName("Поиск контакта по фамилии от 1 символа, с кем нет p2p")
    public void searchContactWithoutP2PChatBySurname() {
        builderFirst.addContacts(secondUser);
        try {
            loginHelper.performUILogin(firstUser);

            String query = secondUser.getLastName().substring(0, 1);
            performUserSearchAndOpen(query);

            pages.topbarSteps.verifyChatName(secondUser.getName());
            pages.chatVerify.verifyChatIsEmpty();
        } finally {
            cleanupUsers();
        }
    }

    @Test
    @AllureId("54336")
    @DisplayName("Поиск контактов по номеру телефона от 1 символа, с кем есть p2p (номер скрыт)-> Не найдено")
    public void searchContactWithP2PChatByPhoneHidden() {
        builderSecond.hidePhone();
        builderFirst.addContacts(secondUser);
        Long p2pID = builderFirst.createP2PGroup(secondUser);
        builderFirst.sendTextMessage(p2pID, "Тест");

        try {
            loginHelper.performUILogin(firstUser);

            pages.groupAndChannelMenuSteps.clickSearchButton();
            pages.searchUnitSteps.enterSearchText(secondUser.getPhoneNumber());
            pages.searchUnitSteps.waitForSearchCompletion();

            pages.searchResultsListSteps.verifyNoResultsFound();
            pages.searchUnitSteps.clickPeopleTab();
            pages.searchResultsListSteps.verifyNoResultsFound();
        } finally {
            builderSecond.showPhone();
            cleanupUsers();
        }
    }

    @Test
    @AllureId("54555")
    @DisplayName("Поиск неконтакта по имени от 1 символа (начало, середина, конец имени), с кем есть p2p")
    public void searchNonContactWithP2PChatByName() {
        Long p2pID = builderFirst.createP2PGroup(secondUser);
        Long messageId = builderFirst.sendTextMessage(p2pID, "Тест");
        try {
            loginHelper.performUILogin(firstUser);

            String query = secondUser.getName().substring(0, 1);
            performUserSearchAndOpen(query);

            pages.topbarSteps.verifyChatName(secondUser.getName());
            pages.chatVerify.verifyMessageWithIdIsDisplayed(String.valueOf(messageId), true);
        } finally {
            cleanupUsers();
        }
    }

    @Test
    @AllureId("55632")
    @DisplayName("Поиск неконтакта по имени от 1 символа, с кем нет p2p, состоят в одном пространстве")
    public void searchNonContactInSameSpaceByName() {
        String wsName = TestData.generateWorkspaceName();
        long spaceId = builderFirst.createWorkspace(wsName);
        builderFirst.addUsersToWorkspace(spaceId, secondUser);
        try {
            loginHelper.performUILogin(firstUser);
            pages.leftSidebarSteps.clickSpaceByName(wsName);
            pages.leftSidebarSteps.clickChatsButton();

            String query = secondUser.getName().substring(0, 1);
            performUserSearchAndOpen(query);

            pages.topbarSteps.verifyChatName(secondUser.getName());
            pages.chatVerify.verifyChatIsEmpty();
        } finally {
            cleanupUsers();
        }
    }

    @Test
    @AllureId("54558")
    @DisplayName("Поиск неконтакта по номеру телефона от 1 символа, с кем есть p2p (номер открыт)")
    public void searchNonContactWithP2PByPhone() {
        Long p2pID = builderFirst.createP2PGroup(secondUser);
        Long messageId = builderFirst.sendTextMessage(p2pID, "Тест");
        try {
            loginHelper.performUILogin(firstUser);

            String query = secondUser.getPhoneNumber().substring(0, 5);
            performUserSearchAndOpen(query);

            pages.topbarSteps.verifyChatName(secondUser.getName());
            pages.chatVerify.verifyMessageWithIdIsDisplayed(String.valueOf(messageId), true);
        } finally {
            cleanupUsers();
        }
    }

    @Test
    @AllureId("55782")
    @DisplayName("Поиск неконтакта по номеру телефона от 1 символа, с кем нет p2p, состоят в одном пространстве")
    public void searchNonContactInSameSpaceByPhone() {
        String wsName = TestData.generateWorkspaceName();
        long spaceId = builderFirst.createWorkspace(wsName);
        builderFirst.addUsersToWorkspace(spaceId, secondUser);
        try {
            loginHelper.performUILogin(firstUser);
            pages.leftSidebarSteps.clickSpaceByName(wsName);
            pages.leftSidebarSteps.clickChatsButton();

            String query = secondUser.getPhoneNumber().substring(0, 5);
            performUserSearchAndOpen(query);

            pages.topbarSteps.verifyChatName(secondUser.getName());
            pages.chatVerify.verifyChatIsEmpty();
        } finally {
            cleanupUsers();
        }
    }

    @Test
    @AllureId("54557")
    @DisplayName("Поиск неконтакта по отчеству от 1 символа, с кем есть p2p")
    public void searchNonContactWithP2PByMiddleName() {
        String middleName = "Отчество";
        builderSecond.updateFullProfile(secondUser.getFirstName(), secondUser.getLastName(), middleName);
        Long p2pID = builderFirst.createP2PGroup(secondUser);
        Long messageId = builderFirst.sendTextMessage(p2pID, "Тест");
        try {
            loginHelper.performUILogin(firstUser);

            String query = middleName.substring(0, 3);
            performUserSearchAndOpen(query);

            pages.topbarSteps.clickP2pHeader();
            pages.p2pMenuVerifier.verifyUserName
                    (secondUser.getFirstName() + " " + middleName + " " + secondUser.getLastName());
            pages.chatVerify.verifyMessageWithIdIsDisplayed(String.valueOf(messageId), true);
        } finally {
            cleaner.resetUserProfile(secondUser);
            cleanupUsers();
        }
    }

    @Test
    @AllureId("54556")
    @DisplayName("Поиск неконтакта по фамилии от 1 символа, с кем есть p2p")
    public void searchNonContactWithP2PByLastName() {
        Long p2pID = builderFirst.createP2PGroup(secondUser);
        Long messageId = builderFirst.sendTextMessage(p2pID, "Тест");
        try {
            loginHelper.performUILogin(firstUser);

            String query = secondUser.getLastName().substring(0, 1);
            performUserSearchAndOpen(query);

            pages.topbarSteps.verifyChatName(secondUser.getName());
            pages.chatVerify.verifyMessageWithIdIsDisplayed(String.valueOf(messageId), true);
        } finally {
            cleanupUsers();
        }
    }

    @Test
    @AllureId("55754")
    @DisplayName("Поиск неконтакта по фамилии от 1 символа, с кем нет p2p, состоят в одном пространстве")
    public void searchNonContactInSameSpaceByLastName() {
        String wsName = TestData.generateWorkspaceName();
        long spaceId = builderFirst.createWorkspace(wsName);
        builderFirst.addUsersToWorkspace(spaceId, secondUser);
        try {
            loginHelper.performUILogin(firstUser);
            pages.leftSidebarSteps.clickSpaceByName(wsName);
            pages.leftSidebarSteps.clickChatsButton();

            String query = secondUser.getLastName().substring(0, 1);
            performUserSearchAndOpen(query);

            pages.topbarSteps.verifyChatName(secondUser.getName());
            pages.chatVerify.verifyChatIsEmpty();
        } finally {
            cleanupUsers();
        }
    }

    @Test
    @AllureId("55784")
    @DisplayName("Сортировка пользователей в результатах поиска")
    public void searchUsersResultsSortingTest() {
        try {
            builderSecond.updateFullProfile("Иван", "Сотников", "");
            builderThird.updateFullProfile("Роман", "Иванов", "");
            builderFourth.updateFullProfile("Мерива", "Ланская", "");
            builderFirst.addContacts(secondUser, thirdUser, fourthUser);

            loginHelper.performUILogin(firstUser);
            pages.groupAndChannelMenuSteps.clickSearchButton();
            pages.searchUnitSteps.enterSearchText("Ива");
            pages.searchUnitSteps.clickPeopleTab();
            pages.searchUnitSteps.waitForSearchCompletion();

            pages.searchResultsListSteps.verifySearchResultsRelativeOrder(
                    "Иван Сотников",
                    "Роман Иванов",
                    "Мерива Ланская");
        } finally {
            cleaner.resetUserProfile(secondUser);
            cleaner.resetUserProfile(thirdUser);
            cleaner.resetUserProfile(fourthUser);
        }
    }

    private void cleanupUsers() {
        cleaner.clean(firstUser);
        cleaner.clean(secondUser);
    }

    private void performUserSearchAndOpen(String query) {
        String sectionType = "Люди";
        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(query);
        pages.searchUnitSteps.waitForSearchCompletion();

        pages.searchUnitSteps.clickALLTab();
        pages.searchResultsListSteps.verifyResultInSection(sectionType, secondUser.getName());
        pages.searchUnitSteps.clickPeopleTab();

        pages.searchResultsListSteps.hoverOverEntity(secondUser.getName());
        pages.searchResultsListSteps.verifyEntityHighlight(secondUser.getName());
        pages.searchResultsListSteps.clickEntityByGroupName(secondUser.getName());
    }
}