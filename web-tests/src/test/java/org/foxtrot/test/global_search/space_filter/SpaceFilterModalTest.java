package org.foxtrot.test.global_search.space_filter;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.TestDataBuilder;
import org.foxtrot.utils.UserPoolManager;
import org.foxtrot.utils.UserTestDataCleaner;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.foxtrot.utils.TestDataBuilder.GroupConfig.ofGroup;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpaceFilterModalTest extends BaseTest {

    private UsersEnum mainUser;
    private UsersEnum secondaryUser;
    private TestDataBuilder builderUser;
    private LoginHelper loginHelper;
    private UserTestDataCleaner cleaner;


    private String firstWorkspace;
    private String secondWorkspace;
    private Long firstWorkspaceId;
    private Long secondWorkspaceId;
    private Long workspaceGroupId;


    @BeforeAll
    public void beforeAll() {
        UserPoolManager userPoolManager = new UserPoolManager();
        cleaner = new UserTestDataCleaner();

        mainUser = userPoolManager.getFreeUser();
        secondaryUser = userPoolManager.getFreeUser();

        builderUser = new TestDataBuilder(mainUser);
        loginHelper = new LoginHelper();

        firstWorkspace = TestData.generateWorkspaceName();
        secondWorkspace = TestData.generateWorkspaceName();

        prepareTestData();
    }

    @AfterAll
    public void afterAll() {
        try {
            cleaner.clean(mainUser);
        } finally {
            UserPoolManager.releaseCurrentUser();
        }
    }

    @AllureId("54268")
    @Test
    @DisplayName("Изменение списка пространств в фильтре")
    public void changeSpaceListInFilter() {
        setupFilterContext();
        assertInitialFilterState();
        uncheckSingleWorkspace(firstWorkspace);
        selectAllWorkspacesViaSelectAllCheckbox();
        deselectAllWorkspacesViaSelectAllCheckbox();
    }

    @AllureId("54278")
    @Test
    @DisplayName("Кнопка 'Применить' без изменения списка пространств -> Не кликабельна")
    public void applyButtonNotClickableWithoutChanges() {
        setupFilterContext();
        assertInitialFilterState();
    }

    @Disabled//неактуальный кейс
    @AllureId("54295")
    @Test
    @DisplayName("Отображение иконок выбранного фильтра (выбрано 2 пространства)")
    public void displaySelectedFilterIcons() {
        setupFilterContext();
        assertInitialFilterState();

        uncheckSingleWorkspace(secondWorkspace);
        pages.spaceFilterModalSteps.clickApplyButton();
        pages.searchUnitSteps.clickSpaceFilterButton();
        pages.searchFilterModalSteps.clickSpaseButton();
        pages.searchUnitSteps.verifySpaceIconsInFilter(true, firstWorkspace);
        pages.searchUnitSteps.verifySpaceIconsInFilter(false, secondWorkspace);
    }

    @AllureId("54283")
    @Test
    @DisplayName("Применение фильтра для поискового запроса")
    public void applyFilterForSearchQuery() {
        loginHelper.performUILogin(mainUser);
        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(secondaryUser.getName());
        pages.searchUnitSteps.waitForSearchCompletion();
        pages.searchUnitSteps.clickSpaceFilterButton();
        pages.searchFilterModalSteps.clickSpaseButton();
        pages.searchResultsListSteps.verifyResultInSection("Люди", secondaryUser.getName());
        pages.searchResultsListSteps.verifyResultInSection("Группы", secondaryUser.getName());
        Selenide.sleep(1000);

        deselectAllWorkspacesViaSelectAllCheckbox();
        pages.spaceFilterModalSteps.clickApplyButton();
        pages.searchUnitSteps.waitForSearchCompletion();

        pages.searchResultsListSteps.verifyResultNotInSection("Люди", secondaryUser.getName());
        pages.searchResultsListSteps.verifyResultNotInSection("Группы", secondaryUser.getName());
    }

    @AllureId("54253")
    @Test
    @DisplayName("Список пространств в фильтре")
    public void spaceListInFilter() {
        String firstWorkspace = "aWork";
        String secondWorkspace = "bWork";
        builderUser.createWorkspace(firstWorkspace);
        builderUser.createWorkspace(secondWorkspace);

        loginHelper.performUILogin(mainUser);

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.clickSpaceFilterButton();
        pages.searchFilterModalSteps.clickSpaseButton();

        pages.spaceFilterModalSteps.verifyApplyButtonActivity(false);
        pages.spaceFilterModalSteps.verifySpacesCheckmarkState(true, firstWorkspace, secondWorkspace);
        pages.spaceFilterModalSteps.verifySpacesAreSortedAlphabetically();
    }

    private void prepareTestData() {
        firstWorkspaceId = builderUser.createWorkspace(firstWorkspace);
        builderUser.addUsersToWorkspace(firstWorkspaceId, secondaryUser);
        workspaceGroupId = builderUser.createGroup(
                ofGroup(secondaryUser
                        .getName())
                        .inWorkspace(firstWorkspaceId));

        builderUser.addUsers(workspaceGroupId, firstWorkspaceId, secondaryUser);
        secondWorkspaceId = builderUser.createWorkspace(secondWorkspace);
    }

    private void setupFilterContext() {
        loginHelper.performUILogin(mainUser);
        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.clickSpaceFilterButton();
        pages.searchFilterModalSteps.clickSpaseButton();
    }

    private void assertInitialFilterState() {
        pages.spaceFilterModalSteps.verifyApplyButtonActivity(false);
        pages.spaceFilterModalSteps.verifySpacesCheckmarkState(true, firstWorkspace, secondWorkspace);
    }

    private void uncheckSingleWorkspace(String workspaceName) {
        pages.spaceFilterModalSteps.clickSpaceCheckmark(workspaceName);
        pages.spaceFilterModalSteps.verifySpacesCheckmarkState(false, workspaceName);
        pages.spaceFilterModalSteps.verifyApplyButtonActivity(true);
    }

    private void selectAllWorkspacesViaSelectAllCheckbox() {
        pages.spaceFilterModalSteps.clickSelectAllCheckbox();
        pages.spaceFilterModalSteps.verifySelectAllCheckboxState(true);
        pages.spaceFilterModalSteps.verifySpacesCheckmarkState(true, firstWorkspace, secondWorkspace);
        pages.spaceFilterModalSteps.verifyApplyButtonActivity(false);
    }

    private void deselectAllWorkspacesViaSelectAllCheckbox() {
        pages.spaceFilterModalSteps.clickSelectAllCheckbox();
        pages.spaceFilterModalSteps.verifySelectAllCheckboxState(false);
        pages.spaceFilterModalSteps.verifySpacesCheckmarkState(false, firstWorkspace, secondWorkspace);
        pages.spaceFilterModalSteps.verifyApplyButtonActivity(true);
    }
}