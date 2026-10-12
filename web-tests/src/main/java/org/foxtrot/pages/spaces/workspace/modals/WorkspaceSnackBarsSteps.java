package org.foxtrot.pages.spaces.workspace.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.spaces.workspace.modals.elements.WorkspaceSnackBars;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class WorkspaceSnackBarsSteps {

    WorkspaceSnackBars elements = new WorkspaceSnackBars();

    @Step("Проверить наличие снекбара с сообщением о удалении пространства ")
    public WorkspaceSnackBarsSteps verifyWorkspaceDeletedSnackBarIsVisible() {
        $(elements.getSpaceDeletedSnackBar()).shouldBe(visible);
        return this;
    }
}
