package org.foxtrot.pages.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.elements.DeletePhotoModal;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class DeletePhotoModalSteps {

    DeletePhotoModal elements = new DeletePhotoModal();

    @Step("Нажать кнопку 'Удалить'")
    public DeletePhotoModalSteps clickDeleteButton() {
        $(elements.getDeleteButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Нажать кнопку 'Отмена'")
    public DeletePhotoModalSteps clickCancelButton() {
        $(elements.getCancelButton()).shouldBe(visible).click();
        return this;
    }
}
