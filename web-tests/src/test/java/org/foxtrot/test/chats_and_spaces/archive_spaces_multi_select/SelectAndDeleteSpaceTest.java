package org.foxtrot.test.chats_and_spaces.archive_spaces_multi_select;

import org.foxtrot.allure.ChannelAllureIds;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.WorkspaceContext;
import org.foxtrot.utils.factories.core.WorkspaceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SelectAndDeleteSpaceTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final WorkspaceFactory workspaceFactory = new WorkspaceFactory();

    private List<WorkspaceContext> contexts;

    @AfterEach
    public void tearDown() {
        workspaceFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "58268", desktop = "95407")
    @DisplayName("Выбор и удаление более 1 пространства (отображение в заголовке количества удаляемых пространств)")
    public void selectAndVerifyMultipleWorkspacesDeletionCountInHeader() {
        contexts = workspaceFactory.createMultipleWorkspaces(3);
        String[] names = contexts.stream().map(WorkspaceContext::getWsName).toArray(String[]::new);

        loginHelper.performUILogin(contexts.get(0).getOwner());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.clickDeleteWorkspaceButton();
        pages.deleteSpaceModalSteps.clickWorkspacesByNames(names)
                .verifyWorkspacesAreSelected(names)
                .clickDeleteButton();
        pages.deleteSelectedWorkspaceModalSteps.verifyHeaderTitle("Удалить 3 пространства?");
    }

    @Test
    @ChannelAllureIds(web = "58270", desktop = "95408")
    @DisplayName("Выбор и удаление нескольких пространств, в которых не находится")
    public void deleteMultipleNonCurrentWorkspaces() {
        contexts = workspaceFactory.createMultipleWorkspaces(3);
        String[] names = contexts.stream().map(WorkspaceContext::getWsName).toArray(String[]::new);

        loginHelper.performUILogin(contexts.get(0).getOwner());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.clickDeleteWorkspaceButton();
        pages.deleteSpaceModalSteps.clickWorkspacesByNames(names)
                .verifyWorkspacesAreSelected(names)
                .clickDeleteButton();
        pages.deleteSelectedWorkspaceModalSteps.verifyConfirmCheckboxIsNotSelected()
                .verifyDeleteButtonIsDisabled()
                .clickConfirmCheckbox()
                .verifyConfirmCheckboxIsSelected()
                .verifyDeleteButtonIsEnabled()
                .clickDeleteButton();
        pages.snackbarsSteps.verifyDeleteSuccessNotificationIsDisplayed("Удалено 3 пространства");
        pages.leftSidebarSteps.verifySpaceIsActive("Чаты");
    }

    @Test
    @ChannelAllureIds(web = "58269", desktop = "95409")
    @DisplayName("Выбор и удаление нескольких пространств, в одном из которых находишься")
    public void deleteMultipleWorkspacesIncludingCurrentOne() {
        contexts = workspaceFactory.createMultipleWorkspaces(3);
        String[] names = contexts.stream().map(WorkspaceContext::getWsName).toArray(String[]::new);

        loginHelper.performUILogin(contexts.get(0).getOwner());
        pages.leftSidebarSteps.clickSpaceByName(contexts.get(0).getWsName());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.clickDeleteWorkspaceButton();
        pages.deleteSpaceModalSteps.clickWorkspacesByNames(names)
                .verifyWorkspacesAreSelected(names)
                .clickDeleteButton();
        pages.deleteSelectedWorkspaceModalSteps.verifyConfirmCheckboxIsNotSelected()
                .verifyDeleteButtonIsDisabled()
                .clickConfirmCheckbox()
                .verifyConfirmCheckboxIsSelected()
                .verifyDeleteButtonIsEnabled()
                .clickDeleteButton();
        pages.snackbarsSteps.verifyDeleteSuccessNotificationIsDisplayed("Удалено 3 пространства");
        pages.leftSidebarSteps.verifySpaceIsActive("Чаты");
    }

    @Test
    @ChannelAllureIds(web = "58258", desktop = "95410")
    @DisplayName("Выбор и удаление одного пространства (не удаляя пространство, в котором находишься)")
    public void deleteOneWorkspaceWhileKeepingCurrentOne() {
        contexts = workspaceFactory.createMultipleWorkspaces(2);

        loginHelper.performUILogin(contexts.get(0).getOwner());
        pages.leftSidebarSteps.clickSpaceByName(contexts.get(0).getWsName());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.clickDeleteWorkspaceButton();
        pages.deleteSpaceModalSteps.clickWorkspacesByNames(contexts.get(1).getWsName())
                .verifyWorkspacesAreSelected(contexts.get(1).getWsName())
                .clickDeleteButton();
        pages.deleteSelectedWorkspaceModalSteps.verifyConfirmCheckboxIsNotSelected()
                .verifyDeleteButtonIsDisabled()
                .clickConfirmCheckbox()
                .verifyConfirmCheckboxIsSelected()
                .verifyDeleteButtonIsEnabled()
                .clickDeleteButton();
        pages.snackbarsSteps.verifyDeleteSuccessNotificationIsDisplayed(
                "Пространство «" + contexts.get(1).getWsName() + "» удалено");
        pages.leftSidebarSteps.verifySpaceIsActive(contexts.get(0).getWsName());
    }

    @Test
    @ChannelAllureIds(web = "58260", desktop = "95411")
    @DisplayName("Выбор и удаление одного пространства (удаляя пространство в котором находишься)")
    public void deleteCurrentActiveWorkspace() {
        var context = workspaceFactory.createWorkspace();

        loginHelper.performUILogin(context.getOwner());
        pages.leftSidebarSteps.clickSpaceByName(context.getWsName());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.clickDeleteWorkspaceButton();
        pages.deleteSpaceModalSteps.clickWorkspacesByNames(context.getWsName())
                .verifyWorkspacesAreSelected(context.getWsName())
                .clickDeleteButton();
        pages.deleteSelectedWorkspaceModalSteps.verifyConfirmCheckboxIsNotSelected()
                .verifyDeleteButtonIsDisabled()
                .clickConfirmCheckbox()
                .verifyConfirmCheckboxIsSelected()
                .verifyDeleteButtonIsEnabled()
                .clickDeleteButton();
        pages.snackbarsSteps.verifyDeleteSuccessNotificationIsDisplayed("Пространство «" + context.getWsName() + "» удалено");
        pages.leftSidebarSteps.verifySpaceIsActive("Чаты");
    }

    @Test
    @ChannelAllureIds(web = "58257", desktop = "95412")
    @DisplayName("Отображение неактивных пространств для удаления")
    public void verifyInactiveWorkspacesAreAvailableForDeletion() {
        contexts = workspaceFactory.createMultipleWorkspaces(3);
        String[] names = contexts.stream().map(WorkspaceContext::getWsName).toArray(String[]::new);
        loginHelper.performUILogin(contexts.get(0).getOwner());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.exitingWorkspacesByNames(names);
        pages.availableWorkspacesModalSteps.clickDeleteWorkspaceButton();
        pages.deleteSpaceModalSteps.verifyWorkspacesAreDisplayed(names).verifyDeleteButtonIsDisabled();
    }

    @Test
    @ChannelAllureIds(web = "58256", desktop = "95413")
    @DisplayName("Отображение пространств, в которых пользователь владелец либо администратор")
    public void verifyWorkspacesWhereUserIsOwnerOrAdminAreDisplayed() {
        contexts = workspaceFactory.createMultipleWorkspaces(3);
        String[] names = contexts.stream().map(WorkspaceContext::getWsName).toArray(String[]::new);

        loginHelper.performUILogin(contexts.get(0).getOwner());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.clickDeleteWorkspaceButton();
        pages.deleteSpaceModalSteps.verifyWorkspacesAreDisplayed(names).verifyDeleteButtonIsDisabled();
    }

    @Test
    @ChannelAllureIds(web = "58267", desktop = "95414")
    @DisplayName("Отображение счетчика количества выбранных пространств для удаления")
    public void verifySelectedWorkspacesCounterUpdatesOnSelection() {
        contexts = workspaceFactory.createMultipleWorkspaces(3);
        String[] names = contexts.stream().map(WorkspaceContext::getWsName).toArray(String[]::new);
        contexts.get(0).getOwnerBuilder().deactivateAllSpaces();

        loginHelper.performUILogin(contexts.get(0).getOwner());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.clickDeleteWorkspaceButton();
        pages.deleteSpaceModalSteps
                .verifyWorkspacesAreDisplayed(names)
                .verifyDeleteButtonIsDisabled()
                .verifySelectedWorkspacesCount(0)
                .clickWorkspacesByNames(contexts.get(0).getWsName(), contexts.get(1).getWsName())
                .verifySelectedWorkspacesCount(2)
                .verifyDeleteButtonIsEnabled()
                .clickWorkspacesByNames(contexts.get(0).getWsName())
                .verifySelectedWorkspacesCount(1)
                .verifyDeleteButtonIsEnabled()
                .clickDeleteButton();
        pages.deleteSelectedWorkspaceModalSteps.verifyHeaderTitle(
                "Удалить 1 пространство?")
                .verifyDeleteButtonIsDisabled();
    }

    @Test
    @ChannelAllureIds(web = "58255", desktop = "95415")
    @DisplayName("Сортировка списка пространств в окне 'Выбора для удаления'")
    public void verifyWorkspacesSortingOrderInDeleteModal() {
        contexts = workspaceFactory.createMultipleWorkspaces(3);
        contexts.get(0).getOwnerBuilder().deactivateAllSpaces();

        loginHelper.performUILogin(contexts.get(0).getOwner());
        pages.leftSidebarSteps.enterOrCreateSpaceButton();
        pages.availableWorkspacesModalSteps.clickDeleteWorkspaceButton();
        pages.deleteSpaceModalSteps
                .verifyWorkspacesAreSortedAlphabetically();
    }
}