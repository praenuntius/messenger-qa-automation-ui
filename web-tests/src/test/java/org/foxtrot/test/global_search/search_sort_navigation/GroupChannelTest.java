package org.foxtrot.test.global_search.search_sort_navigation;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.enums.GroupTypesEnum;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInRelativeOrder;

//todo ***   нужен ремонт класса и тк в алюре   ***

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GroupChannelTest extends BaseTest {

    private UsersEnum firstUser;
    private UsersEnum secondUser;
    private TestDataBuilder builderFirst;
    private TestDataBuilder builderSecond;
    private LoginHelper loginHelper;

    private String closedChannelInWorkspaceName;
    private String closedChannelInChatsName;
    private String closedGroupInWorkspaceName;
    private String closedGroupInChatsName;

    private String groupNamePriority1, groupNamePriority2, groupNamePriority3;

    @BeforeAll
    public void beforeAll() {
        UserPoolManager userPoolManager = new UserPoolManager();
        firstUser = userPoolManager.getFreeUser();
        secondUser = userPoolManager.getFreeUser();
        builderFirst = new TestDataBuilder(firstUser);
        builderSecond = new TestDataBuilder(secondUser);
        loginHelper = new LoginHelper();
        prepareTestData();
    }

    @AfterAll
    public void afterAll() {
        try {
            UserTestDataCleaner cleaner = new UserTestDataCleaner();
            cleaner.clean(firstUser, secondUser);
            try { Selenide.closeWebDriver(); } catch (Exception ignored) {}
        } finally {
            UserPoolManager.releaseCurrentUser();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"open", "closed"})
    @AllureId("56132")
    @DisplayName("Поиск/переход по названию канала в пространстве от 1 символа, в котором состоит пользователь")
    public void searchChannelInWorkspaceByOneSymbol(String type) {
        Allure.parameter("channelType", type);
        boolean isVisible = type.equals("open");

        String wsName = TestData.generateWorkspaceName();
        Long wsId = builderFirst.createWorkspace(wsName);
        String channelName = createEntityInWorkspace(wsId, GroupTypesEnum.INFO_CHANNEL, isVisible);

        loginHelper.performUILogin(firstUser);
        pages.leftSidebarSteps.clickSpaceByName(wsName);
        pages.leftSidebarSteps.clickChatsButton();
        pages.groupAndChannelMenuSteps.clickSearchButton();

        String query = channelName.substring(0, 5);
        pages.searchUnitSteps.enterSearchText(query);
        pages.searchUnitSteps.clickChannelTab();
        pages.searchResultsListSteps.verifyGroupInCustomWorkspaceIcon(wsName, channelName);

        pages.searchUnitSteps.clickChannelTab();
        pages.searchResultsListSteps.clickEntityByGroupName(channelName);

        pages.inputBarSteps.verifyInputBarIsVisible();
        pages.leftSidebarSteps.verifySpaceIsActive(wsName);
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuVerifier.verifyChannelName(channelName);
        pages.searchUnitSteps.verifyGlobalSearchElementsVisible();
    }

    @ParameterizedTest
    @ValueSource(strings = {"open", "closed"})
    @AllureId("56115")
    @DisplayName("Поиск/переход по названию группы в пространстве от 1 символа, в котором состоит пользователь")
    public void searchGroupInWorkspaceByOneSymbol(String type) {
        Allure.parameter("groupType", type);
        boolean isVisible = type.equals("open");

        String wsName = TestData.generateWorkspaceName();
        Long wsId = builderFirst.createWorkspace(wsName);
        String groupName = createEntityInWorkspace(wsId, GroupTypesEnum.GROUP, isVisible);

        loginHelper.performUILogin(firstUser);
        pages.leftSidebarSteps.clickSpaceByName(wsName);
        pages.leftSidebarSteps.clickChatsButton();
        pages.groupAndChannelMenuSteps.clickSearchButton();

        String query = groupName.substring(0, 5);
        pages.searchUnitSteps.enterSearchText(query);
        pages.searchUnitSteps.clickGroupsTab();
        pages.searchResultsListSteps.verifyGroupInCustomWorkspaceIcon(wsName, groupName);

        pages.searchUnitSteps.clickGroupsTab();
        pages.searchResultsListSteps.clickEntityByGroupName(groupName);

        pages.inputBarSteps.verifyInputBarIsVisible();
        pages.leftSidebarSteps.verifySpaceIsActive(wsName);
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuVerifier.verifyGroupName(groupName);
        pages.searchUnitSteps.verifyGlobalSearchElementsVisible();
    }

    @Test
    @AllureId("56042")
    @DisplayName("Поиск/переход по названию закрытой группы в чатах от 1 символа, в которой состоит пользователь")
    public void searchClosedGroupInChatsByOneSymbol() {
        String groupName = TestData.generateHomespaceGroupName();
        builderFirst.createGroup(TestDataBuilder.GroupConfig.ofGroup(groupName).visible(false));

        loginHelper.performUILogin(firstUser);
        pages.groupAndChannelMenuSteps.clickSearchButton();

        String query = groupName.substring(0, 5);
        pages.searchUnitSteps.enterSearchText(query);
        pages.searchUnitSteps.clickGroupsTab();
        pages.searchResultsListSteps.verifyGroupHasHomeIcon(groupName);

        pages.searchUnitSteps.clickGroupsTab();
        pages.searchResultsListSteps.clickEntityByGroupName(groupName);

        pages.topbarSteps.clickGroupHeader();
        pages.leftSidebarSteps.verifySpaceIsActive("Чаты");
        pages.groupMenuVerifier.verifyGroupName(groupName);
        pages.searchUnitSteps.verifyGlobalSearchElementsVisible();
        pages.searchResultsListSteps.verifyGroupHasHomeIcon(groupName);
    }

    @ParameterizedTest
    @ValueSource(strings = {"open", "closed"})
    @AllureId("56104")
    @DisplayName("Поиск/переход по названию канала в пространстве от 1 символа, в котором состоит пользователь")
    public void searchChannelInHomeSpaceByOneSymbol(String type) {
        Allure.parameter("channelType", type);
        boolean isVisible = type.equals("open");

        String channelName = createEntityInWorkspace(null, GroupTypesEnum.INFO_CHANNEL, isVisible);

        loginHelper.performUILogin(firstUser);
        pages.groupAndChannelMenuSteps.clickSearchButton();

        String query = channelName.substring(0, 5);
        pages.searchUnitSteps.enterSearchText(query);
        pages.searchUnitSteps.clickChannelTab();
        pages.searchResultsListSteps.verifyGroupHasHomeIcon(channelName);

        pages.searchUnitSteps.clickChannelTab();
        pages.searchResultsListSteps.clickEntityByGroupName(channelName);

        pages.inputBarSteps.verifyInputBarIsVisible();
        pages.leftSidebarSteps.verifySpaceIsActive("Чаты");
        pages.topbarSteps.clickChannelHeader();
        pages.channelMenuVerifier.verifyChannelName(channelName);
        pages.searchUnitSteps.verifyGlobalSearchElementsVisible();
    }

    @Test
    @AllureId("56143")
    @DisplayName("Поиск/переход по названию открытого канала в пространстве от 1 символа, в котором не состоит пользователь, но состоит в пространстве")
    public void searchOpenChannelInWorkspaceNotMemberByOneSymbol() {
        String wsName = TestData.generateWorkspaceName();
        Long wsId = builderSecond.createWorkspace(wsName);
        builderSecond.addUsersToWorkspace(wsId, firstUser);
        String channelName = TestData.generateWorkspacePublicChannelName();
        builderSecond.createGroup(
                TestDataBuilder
                        .GroupConfig.ofChannel(channelName)
                        .inWorkspace(wsId)
                        .visible(true));


        loginHelper.performUILogin(firstUser);
        pages.leftSidebarSteps.clickSpaceByName(wsName);
        pages.leftSidebarSteps.clickChatsButton();
        pages.groupAndChannelMenuSteps.clickSearchButton();

        String query = channelName.substring(0, 5);
        pages.searchUnitSteps.enterSearchText(query);

        pages.searchUnitSteps.clickChannelTab();
        pages.searchResultsListSteps.verifyGroupInCustomWorkspaceIcon(wsName, channelName);

        pages.searchUnitSteps.clickChannelTab();
        pages.searchResultsListSteps.verifyGroupInCustomWorkspaceIcon(wsName, channelName);

        pages.searchResultsListSteps.clickEntityByGroupName(channelName);

        pages.searchUnitSteps.verifyGlobalSearchElementsVisible();
        pages.leftSidebarSteps.verifySpaceIsActive(wsName);
        pages.inputBarSteps.verifySubscribeToChannelButtonVisible();
    }

    @Test
    @AllureId("56144")
    @DisplayName("Поиск/переход по названию открытого канала в чатах от 1 символа, " +
            "в котором не состоит пользователь")
    public void searchOpenChannelInChatsNotMemberByOneSymbol() {
        String channelName = TestData.generateWorkspacePublicChannelName();

        builderSecond.createGroup(
                TestDataBuilder.GroupConfig.ofChannel(channelName)
                        .visible(true));

        loginHelper.performUILogin(firstUser);
        pages.groupAndChannelMenuSteps.clickSearchButton();

        String query = channelName.substring(0, 5);
        pages.searchUnitSteps.enterSearchText(query);

        pages.searchUnitSteps.clickChannelTab();
        pages.searchResultsListSteps.verifyGroupHasHomeIcon(channelName);

        pages.searchUnitSteps.clickChannelTab();
        pages.searchResultsListSteps.verifyGroupHasHomeIcon(channelName);
        pages.searchResultsListSteps.clickEntityByGroupName(channelName);

        pages.searchUnitSteps.verifyGlobalSearchElementsVisible();
        pages.leftSidebarSteps.verifySpaceIsActive("Чаты");
        pages.inputBarSteps.verifySubscribeToChannelButtonVisible();
    }

    @Test
    @AllureId("56145")
    @DisplayName("Поиск/переход по названию открытой группы в пространстве от 1 символа, " +
            "в которой не состоит пользователь")
    public void searchOpenGroupInWorkspaceNotMemberByOneSymbol() {
        String wsName = TestData.generateWorkspaceName();
        Long wsId = builderSecond.createWorkspace(wsName);
        builderSecond.addUsersToWorkspace(wsId, firstUser);

        String groupName = TestData.generateWorkspacePrivateGroupName();
        builderSecond.createGroup(
                TestDataBuilder.GroupConfig.ofGroup(groupName)
                        .inWorkspace(wsId)
                        .visible(true));

        loginHelper.performUILogin(firstUser);
        pages.leftSidebarSteps.clickSpaceByName(wsName);
        pages.leftSidebarSteps.clickChatsButton();
        pages.groupAndChannelMenuSteps.clickSearchButton();

        String query = groupName.substring(0, 5);
        pages.searchUnitSteps.enterSearchText(query);

        pages.searchUnitSteps.clickGroupsTab();
        pages.searchResultsListSteps.verifyGroupInCustomWorkspaceIcon(wsName, groupName);

        pages.searchUnitSteps.clickGroupsTab();
        pages.searchResultsListSteps.verifyGroupInCustomWorkspaceIcon(wsName, groupName);

        pages.searchResultsListSteps.clickEntityByGroupName(groupName);

        pages.searchUnitSteps.verifyGlobalSearchElementsVisible();
        pages.leftSidebarSteps.verifySpaceIsActive(wsName);
        pages.inputBarSteps.verifyJoinToGroupButtonVisible();
    }

    @Test
    @AllureId("56144")
    @DisplayName("Поиск по названию закрытого канала в пространстве от 1 символа, " +
            "в котором не состоит пользователь -> Не найдено")
    public void searchClosedChannelInWorkspaceNotMemberNotFound() {
        loginHelper.performUILogin(firstUser);
        pages.leftSidebarSteps.clickChatsButton();

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(closedChannelInWorkspaceName);

        pages.searchResultsListSteps.verifyNoResultsFound();
        pages.searchUnitSteps.clickChannelTab();
        pages.searchResultsListSteps.verifyNoResultsFound();
    }

    @Test
    @AllureId("56147")
    @DisplayName("Поиск по названию закрытого канала в чатах от 1 символа, " +
            "в котором не состоит пользователь -> Не найдено")
    public void searchClosedChannelInChatsNotMemberNotFound() {
        loginHelper.performUILogin(firstUser);

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(closedChannelInChatsName);

        pages.searchResultsListSteps.verifyNoResultsFound();
        pages.searchUnitSteps.clickChannelTab();
        pages.searchResultsListSteps.verifyNoResultsFound();
    }

    @Test
    @AllureId("56148")
    @DisplayName("Поиск по названию закрытой группы в пространстве от 1 символа, " +
            "в которой не состоит пользователь -> Не найдено")
    public void searchClosedGroupInWorkspaceNotMemberNotFound() {
        loginHelper.performUILogin(firstUser);

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(closedGroupInWorkspaceName);

        pages.searchResultsListSteps.verifyNoResultsFound();
        pages.searchUnitSteps.clickGroupsTab();
        pages.searchResultsListSteps.verifyNoResultsFound();
    }

    @Test
    @AllureId("56149")
    @DisplayName("Поиск по названию закрытой группы в чатах от 1 символа, " +
            "в которой не состоит пользователь -> Не найдено")
    public void searchClosedGroupInChatsNotMemberNotFound() {
        loginHelper.performUILogin(firstUser);

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(closedGroupInChatsName);

        pages.searchResultsListSteps.verifyNoResultsFound();
        pages.searchUnitSteps.clickGroupsTab();
        pages.searchResultsListSteps.verifyNoResultsFound();
    }

    @Test
    @AllureId("56150")
    @DisplayName("Поиск чата \"Главный\" -> Найдено. Отображен бейдж с иконкой пространства. Находится в категории \"Группы\"")
    public void searchMainChatInWorkspace() {
        String wsName = TestData.generateWorkspaceName();
        builderSecond.createWorkspace(wsName);

        loginHelper.performUILogin(secondUser);
        pages.leftSidebarSteps.clickSpaceByName(wsName);
        pages.leftSidebarSteps.clickChatsButton();
        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText("Главный");

        pages.searchResultsListSteps.verifyResultInSection("Группы", "Главный");
        pages.searchResultsListSteps.verifyGroupInCustomWorkspaceIcon(wsName, "Главный");
    }

    @Test
    @AllureId("56040")
    @DisplayName("Поиск чата \"Сохраненные\" -> Найдено. Отображена иконка чата. Находится в категории \"Группы\"")
    public void searchSavedChatInHomeSpace() {
        loginHelper.performUILogin(firstUser);
        pages.groupAndChannelMenuSteps.clickSearchButton();

        pages.searchUnitSteps.enterSearchText("Сохраненные");
        pages.searchResultsListSteps.verifyResultInSection("Группы", "Сохраненные");
        pages.searchResultsListSteps.verifyGroupHasAvatar("Сохраненные");
    }

    @ParameterizedTest(name = "tabType: {0}")
    @ValueSource(strings = {"all", "group", "channel"})
    @AllureId("56145")
    @DisplayName("Сортировка групп/каналов в результатах поиска")
    public void searchResultsSortingTest(String tabType) {
        Allure.parameter("tabType", tabType);

        loginHelper.performUILogin(firstUser);
        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText("Мир");

        if ("group".equals(tabType)) pages.searchUnitSteps.clickGroupsTab();
        else if ("channel".equals(tabType)) pages.searchUnitSteps.clickChannelTab();

        pages.searchUnitSteps.waitForSearchCompletion();
        if ("all".equals(tabType))pages.searchResultsListSteps.verifyResultInSection("Группы", groupNamePriority1);

        List<String> allResults = pages.searchResultsListSteps.getSearchResultNames();

        assertThat(allResults, containsInRelativeOrder(
                groupNamePriority1,
                groupNamePriority2,
                groupNamePriority3
        ));
    }


    private void prepareTestData() {
        createEntitiesForNotMemberTests();
        prepareSortingTestData();
    }

    private String createEntityInWorkspace(Long wsId, GroupTypesEnum type, boolean isOpen) {
        String name;
        TestDataBuilder.GroupConfig config;

        if (type == GroupTypesEnum.INFO_CHANNEL) {
            name = isOpen ? TestData.generateWorkspacePublicChannelName() : TestData.generateWorkspacePrivateChannelName();
            config = TestDataBuilder.GroupConfig.ofChannel(name).inWorkspace(wsId).visible(isOpen);
        } else {
            name = TestData.generateWorkspacePrivateGroupName();
            config = TestDataBuilder.GroupConfig.ofGroup(name).inWorkspace(wsId).visible(isOpen);
        }

        builderFirst.createGroup(config);
        return name;
    }

    private void createEntitiesForNotMemberTests() {
        String wsName = TestData.generateWorkspaceName();
        Long wsID = builderSecond.createWorkspace(wsName);

        closedChannelInWorkspaceName = TestData.generateWorkspacePrivateChannelName();
        builderSecond.createGroup(
                TestDataBuilder.GroupConfig.ofChannel(closedChannelInWorkspaceName)
                        .inWorkspace(wsID)
                        .visible(false));

        closedChannelInChatsName = TestData.generateWorkspacePrivateChannelName();
        builderSecond.createGroup(
                TestDataBuilder.GroupConfig.ofChannel(closedChannelInChatsName)
                        .inWorkspace(null)
                        .visible(false));

        closedGroupInWorkspaceName = TestData.generateWorkspacePrivateGroupName();
        builderSecond.createGroup(
                TestDataBuilder.GroupConfig.ofGroup(closedGroupInWorkspaceName)
                        .inWorkspace(wsID)
                        .visible(false));

        closedGroupInChatsName = TestData.generateWorkspacePrivateGroupName();
        builderSecond.createGroup(
                TestDataBuilder.GroupConfig.ofGroup(closedGroupInChatsName)
                        .inWorkspace(null)
                        .visible(false));
    }

    private void prepareSortingTestData() {
        String nameP1 = "Мир";
        String nameP2 = "Новый Мир";
        String nameP3 = "Полумир";

        this.groupNamePriority1 = nameP1;
        this.groupNamePriority2 = nameP2;
        this.groupNamePriority3 = nameP3;

        builderFirst.createGroup(TestDataBuilder.GroupConfig.ofGroup(nameP1));
        builderFirst.createGroup(TestDataBuilder.GroupConfig.ofGroup(nameP2));
        builderFirst.createGroup(TestDataBuilder.GroupConfig.ofGroup(nameP3));

        builderFirst.createGroup(TestDataBuilder.GroupConfig.ofChannel(nameP1));
        builderFirst.createGroup(TestDataBuilder.GroupConfig.ofChannel(nameP2));
        builderFirst.createGroup(TestDataBuilder.GroupConfig.ofChannel(nameP3));
    }
}