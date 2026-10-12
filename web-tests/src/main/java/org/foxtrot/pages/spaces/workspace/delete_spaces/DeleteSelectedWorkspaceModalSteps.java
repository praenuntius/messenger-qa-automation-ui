package org.foxtrot.pages.spaces.workspace.delete_spaces;

import io.qameta.allure.Step;
import org.foxtrot.pages.spaces.workspace.delete_spaces.elements.DeleteSelectedWorkspaceModal;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class DeleteSelectedWorkspaceModalSteps {

    DeleteSelectedWorkspaceModal elements = new DeleteSelectedWorkspaceModal();

    @Step("Нажать на чекбокс подтверждения удаления")
    public DeleteSelectedWorkspaceModalSteps clickConfirmCheckbox() {
        $(elements.getConfirmCheckboxSelector())
                .shouldBe(visible)
                .click();
        return this;
    }

    @Step("Проверить заголовок модального окна удаления: '{expectedText}'")
    public DeleteSelectedWorkspaceModalSteps verifyHeaderTitle(String expectedText) {
        $(elements.getHeaderTitleSelector())
                .shouldBe(visible)
                .shouldHave(text(expectedText));
        return this;
    }

    @Step("Проверить, что чекбокс подтверждения выбран")
    public DeleteSelectedWorkspaceModalSteps verifyConfirmCheckboxIsSelected() {
        $(elements.getConfirmCheckboxNativeInput())
                .shouldBe(selected);
        return this;
    }

    @Step("Проверить, что чекбокс подтверждения НЕ выбран")
    public DeleteSelectedWorkspaceModalSteps verifyConfirmCheckboxIsNotSelected() {
        $(elements.getConfirmCheckboxNativeInput())
                .shouldNotBe(selected);
        return this;
    }

    @Step("Проверить, что кнопка удаления заблокирована")
    public DeleteSelectedWorkspaceModalSteps verifyDeleteButtonIsDisabled() {
        $(elements.getDeleteButtonSelector())
                .shouldBe(disabled);
        return this;
    }

    @Step("Проверить, что кнопка удаления активна")
    public DeleteSelectedWorkspaceModalSteps verifyDeleteButtonIsEnabled() {
        $(elements.getDeleteButtonSelector())
                .shouldBe(enabled);
        return this;
    }

    @Step("Нажать на кнопку 'Удалить'")
    public DeleteSelectedWorkspaceModalSteps clickDeleteButton() {
        $(elements.getDeleteButtonSelector())
                .shouldBe(visible)
                .click();
        return this;
    }
}