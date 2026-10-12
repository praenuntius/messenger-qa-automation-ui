package org.foxtrot.mobile.steps.workspace;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.foxtrot.mobile.core.application.ApplicationUtils;
import org.foxtrot.mobile.elements.actions.Direction;
import org.foxtrot.mobile.elements.actions.SwipeHelper;
import org.foxtrot.mobile.screens.workspace.WorkspaceScreen;
import org.foxtrot.mobile.steps.BottomMenuSteps;
import org.foxtrot.mobile.utils.AttachmentUtils;
import org.foxtrot.mobile.utils.LoaderWaiter;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;

public class WorkspaceSteps {

    WorkspaceScreen workspaceScreen = new WorkspaceScreen();
    private final BottomMenuSteps bottomMenu = new BottomMenuSteps();

    @Step("Проверяю, что страница Пространств имеет заголовок '{0}'")
    public WorkspaceSteps checkWorkspaceScreenTitle(String titleText) {
        if (workspaceScreen.isWorkspaceScreenTitleDisplayed(titleText, 5L)) {
            return this;
        }
        enterWorkspaceFromListIfNeeded(titleText);
        Assertions.assertTrue(workspaceScreen.isWorkspaceScreenTitleDisplayed(titleText, 15L),
                "Заголовок страницы '" + titleText + "' не отображается");
        return this;
    }

    @Step("Открываю пространство '{0}' из списка, если показан экран 'Мои пространства'")
    public WorkspaceSteps enterWorkspaceFromListIfNeeded(String workspaceName) {
        dismissDeletedWorkspaceOverlay();
        if (workspaceScreen.isWorkspaceScreenTitleDisplayed(workspaceName, 2L)) {
            return this;
        }
        openWorkspacesTab();
        if (workspaceScreen.isWorkspaceScreenTitleDisplayed(workspaceName, 3L)) {
            return this;
        }
        if (!workspaceScreen.isMySpacesListDisplayed(5L) && !workspaceScreen.isEmptySpacesListDisplayed(1L)) {
            dismissDeletedWorkspaceOverlay();
            if (workspaceScreen.isWorkspaceScreenTitleDisplayed(workspaceName, 2L)) {
                return this;
            }
            if (workspaceScreen.isAnyWorkspaceOpen(1L)) {
                Allure.step("Открыто другое пространство — возвращаюсь к списку 'Мои пространства'");
                workspaceScreen.clickWorkspaceTitleToOpenList();
                LoaderWaiter.waitForNoLoader();
                dismissDeletedWorkspaceOverlay();
            }
        }
        long deadline = System.currentTimeMillis() + 40_000L;
        int attempt = 0;
        boolean relaunched = false;
        while (System.currentTimeMillis() < deadline) {
            dismissDeletedWorkspaceOverlay();
            if (workspaceScreen.isWorkspaceScreenTitleDisplayed(workspaceName, 1L)) {
                return this;
            }
            if (workspaceScreen.isWorkspaceInListDisplayed(workspaceName, 2L)) {
                workspaceScreen.clickWorkspaceInList(workspaceName);
                LoaderWaiter.waitForNoLoader();
                dismissDeletedWorkspaceOverlay();
                if (workspaceScreen.isWorkspaceScreenTitleDisplayed(workspaceName, 5L)) {
                    return this;
                }
            }
            attempt++;
            Allure.step("Пространство '" + workspaceName + "' не открылось — refresh (попытка " + attempt + ")");
            if (attempt % 2 == 1) {
                SwipeHelper.swipeOnScreen(Direction.DOWN, 0.35d, Duration.ofMillis(350));
            } else if (workspaceScreen.isAnyWorkspaceOpen(1L) && !workspaceScreen.isMySpacesListDisplayed(1L)) {
                workspaceScreen.clickWorkspaceTitleToOpenList();
                LoaderWaiter.waitForNoLoader();
            } else {
                openWorkspacesTab();
            }
            if (!relaunched && attempt >= 4 && workspaceScreen.isEmptySpacesListDisplayed(1L)) {
                Allure.step("Список пространств пуст — перезапускаю приложение без wipe");
                ApplicationUtils.relaunchApplicationKeepingData();
                openWorkspacesTab();
                relaunched = true;
            }
        }
        AttachmentUtils.dumpUiState("workspace-list-failed");
        Assertions.fail("Пространство '" + workspaceName + "' нет в списке 'Мои пространства'");
        return this;
    }

    private void openWorkspacesTab() {
        bottomMenu.clickWorkspacesButton();
        dismissDeletedWorkspaceOverlay();
    }

    private void dismissDeletedWorkspaceOverlay() {
        if (workspaceScreen.dismissDeletedWorkspaceIfShown()) {
            Allure.step("Закрыл экран «пространство удалено»");
            LoaderWaiter.waitForNoLoader();
        }
    }

    @Step("Нажимаю на кнопку 'Плюс'")
    public WorkspaceSteps clickPlusBtn() {
        workspaceScreen.clickPlusBtn();
        return this;
    }
}
