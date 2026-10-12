package org.foxtrot.test.chats_and_spaces;


import io.qameta.allure.AllureId;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.TestDataBuilder;
import org.foxtrot.utils.UserPoolManager;
import org.foxtrot.utils.UserTestDataCleaner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChatManagementTests extends BaseTest {

    private LoginHelper loginHelper;
    private UsersEnum owner;
    private UsersEnum admin;
    private TestDataBuilder builderOwner;
    private String homespaceChannelName;
    private String homespaceGroupName;
    private Long p2pGroupId;


    @BeforeAll
    public void beforeAll() {
        loginHelper = new LoginHelper();
        UserPoolManager userPoolManager = new UserPoolManager();
        owner = userPoolManager.getFreeUser();
        admin = userPoolManager.getFreeUser();
        builderOwner = new TestDataBuilder(owner);
        homespaceGroupName = TestData.generateHomespaceGroupName();
        homespaceChannelName = TestData.generateHomespaceChannelName();
        prepareTestData();
    }

    @AfterAll
    public void afterAll() {
        UserPoolManager.releaseCurrentUser();
        deleteTestData();
    }

    @AllureId("60239")
    @Test
    @Order(1)
    @DisplayName("Удаление p2p чата через контекстное меню")
    public void deleteChatOnContextMenu() {
        loginHelper.performUILogin(admin);
        pages.homespaceSteps.clickContextMenuChatByName(owner.getName());
        pages.chatContextMenuModalSteps.clickDeleteChatButton();
        pages.deleteChatModalSteps.clickDeleteChatButton();
        pages.snackbarsSteps.verifyChatDeletedSnackBarIsVisible();
        pages.homespaceSteps.verifyChatAbsent(owner.getName());
    }

    @AllureId("52509")
    @Test
    @Order(2)
    @DisplayName("Удаление группы")
    public void deleteGroup() {
        loginHelper.performUILogin(owner);
        pages.homespaceSteps.clickChatByName(homespaceGroupName);
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.deleteGroupButton();
        pages.deleteChatModalSteps.clickDeleteChatButton();
        pages.homespaceSteps.verifyChatAbsent(homespaceGroupName);
    }

    @AllureId("52565")
    @Test
    @Order(3)
    @DisplayName("Удаление канала")
    public void deleteChannel() {
        loginHelper.performUILogin(owner);
        pages.homespaceSteps.clickChatByName(homespaceChannelName);
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuSteps.DeleteChannelButton();
        pages.deleteChatModalSteps.clickDeleteChatButton();
        pages.homespaceSteps.verifyChatAbsent(homespaceChannelName);
    }


    private void prepareTestData() {
        p2pGroupId = builderOwner.createP2PGroup(admin);
        builderOwner.sendTextMassage("Тест", p2pGroupId);

        builderOwner.createGroup(TestDataBuilder.GroupConfig.ofGroup(homespaceGroupName).visible(false));
        builderOwner.createGroup(TestDataBuilder.GroupConfig.ofChannel(homespaceChannelName).visible(false));
    }

    private void deleteTestData() {
        UserTestDataCleaner userTestDataCleaner = new UserTestDataCleaner();
        userTestDataCleaner.clean(owner, admin);
    }

}
