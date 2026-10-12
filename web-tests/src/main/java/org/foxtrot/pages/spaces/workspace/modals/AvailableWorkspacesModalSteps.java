package org.foxtrot.pages.spaces.workspace.modals;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.pages.spaces.workspace.modals.elements.AvailableWorkspacesModal;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AvailableWorkspacesModalSteps {

    private final AvailableWorkspacesModal elements = new AvailableWorkspacesModal();

    @Step("Войти в рабочее пространство с именем {workspaceName}")
    public AvailableWorkspacesModalSteps enterWorkspaceByName(String workspaceName) {
        $(elements.getWorkspaceLoginButtonByName(workspaceName)).shouldBe(visible).click();
        return this;
    }

    @Step("Выйти из рабочих пространств: {names}")
    public AvailableWorkspacesModalSteps exitingWorkspacesByNames(String... names) {
        for (String name : names) {
            $(elements.getWorkspaceExitingButton(name)).shouldBe(visible).click();
        }
        return this;
    }

    @Step("Проверить наличие активного пространства с именем {workspaceName}")
    public AvailableWorkspacesModalSteps isActiveSpaceAvailableByName(String workspaceName) {
        $(elements.getWorkspaceExitingButton(workspaceName)).shouldBe(visible);
        return this;
    }

    @Step("Проверить наличие неактивного пространства с именем {workspaceName}")
    public AvailableWorkspacesModalSteps isInactiveSpaceAvailableByName(String workspaceName) {
        $(elements.getWorkspaceLoginButtonByName(workspaceName)).shouldBe(visible);
        return this;
    }

    @Step("Проверить, что активные пространства {active} отображаются выше неактивных {inactive}")
    public AvailableWorkspacesModalSteps verifyActiveWorkspacesDisplayedBeforeInactiveWorkspaces(List<String> active, List<String> inactive) {
        List<String> actual = $$(elements.getWorkspaceNamesInList()).texts();

        int firstInactive = inactive.stream().mapToInt(actual::indexOf).filter(i -> i >= 0).min().orElse(Integer.MAX_VALUE);

        boolean isValidOrder = active.stream().mapToInt(actual::indexOf).allMatch(i -> i < firstInactive && i >= 0);

        assertTrue(isValidOrder, "Некоторые активные пространства находятся ниже неактивных или отсутствуют. Список: " + actual);
        return this;
    }

    @Step("Проверить отсутствие рабочего пространства с именем {workspaceName}")
    public AvailableWorkspacesModalSteps verifySpaceNotAvailableByName(String workspaceName) {
        $$(elements.getWorkSpaceName())
                .filterBy(Condition.exactText(workspaceName))
                .shouldBe(CollectionCondition.empty);
        Selenide.sleep(300);
        return this;
    }

    @Step("Закрыть окно доступных рабочих пространств")
    public AvailableWorkspacesModalSteps closeAvailableWorkspacesModal() {
        $(elements.getWorkspaceAvailableSpacesCloseButton())
                .shouldBe(interactable)
                .click();
        return this;
    }

    @Step("Нажать на кнопку создания нового рабочего пространства")
    public AvailableWorkspacesModalSteps clickCreateNewWorkspaceButton() {
        SelenideElement createButton = $(elements.getCreateNewWorkspaceButton());
        if (!createButton.isDisplayed()) {
            createButton.scrollIntoView(true);
        }
        createButton.shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }

    @Step("Нажать на кнопку перезода к удалению рабочих пространств")
    public AvailableWorkspacesModalSteps clickDeleteWorkspaceButton() {
        SelenideElement createButton = $(elements.getWorkspaceAvailableSpacesDeleteButton());
        if (!createButton.isDisplayed()) {
            createButton.scrollIntoView(true);
        }
        createButton.shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }
}
