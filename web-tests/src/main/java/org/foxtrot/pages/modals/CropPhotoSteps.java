package org.foxtrot.pages.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.elements.CropPhoto;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class CropPhotoSteps {

    CropPhoto elements = new CropPhoto();

    @Step("Кликнуть по кнопку 'Сохранить'")
    public CropPhotoSteps clickSaveButton() {
        $(elements.getSaveButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопку 'Отмена'")
    public CropPhotoSteps clickCancelButton() {
        $(elements.getCancelButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }
}
