package org.foxtrot.pages.spaces.workspace.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.spaces.workspace.modals.elements.DeleteWorkspaceModal;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class DeleteWorkspaceModalSteps {

    DeleteWorkspaceModal elements = new DeleteWorkspaceModal();

    @Step("Удалить пространство навсегда")
    public DeleteWorkspaceModalSteps hardDeleteWorkspace() {
        $(elements.getHardDeleteOptions()).shouldBe(visible).click();
        clickDeleteButton();
        return this;
    }

    @Step("Удалить пространство с возможностью восстановления")
    public DeleteWorkspaceModalSteps softDeleteWorkspace() {
        $(elements.getSoftDeleteOptions()).shouldBe(visible).click();
        clickDeleteButton();
        return this;
    }

    @Step("Нажать кнопку удалить")
    public DeleteWorkspaceModalSteps clickDeleteButton() {
        $(elements.getDeleteButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Нажать кнопку отмена")
    public DeleteWorkspaceModalSteps clickCancelButton() {
        $(elements.getCancelButton()).shouldBe(visible).click();
        return this;
    }


}
