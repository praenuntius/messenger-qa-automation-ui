package org.foxtrot.pages.chat.right_sidebar_menu.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.right_sidebar_menu.modals.elements.SpaceEditModal;
import org.foxtrot.pages.key_interactions.PressKeys;
import org.foxtrot.pages.modals.CropPhotoSteps;
import org.foxtrot.utils.FileUploadHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class SpaceEditModalSteps {

    SpaceEditModal elements = new SpaceEditModal();
    FileUploadHelper fileUploadHelper = new FileUploadHelper();
    CropPhotoSteps cropPhotoSteps = new CropPhotoSteps();

    @Step("Загрузить фото")
    public SpaceEditModalSteps uploadPhoto(String filePath) {
        $(elements.getUploadPhotoButton()).shouldBe(visible).click();
        fileUploadHelper.uploadFileViaInput(elements.getFileInput(),filePath);
        cropPhotoSteps.clickSaveButton();
        return this;
    }

    @Step("Изменить имя")
    public SpaceEditModalSteps editName(String name) {
        $(elements.getNameInput()).click();
        PressKeys.pressCmdA();
        PressKeys.pressBackSpace();
        $(elements.getNameInput()).setValue(name);
        return this;
    }

    @Step("Изменить описание")
    public SpaceEditModalSteps editDescription(String description) {
        $(elements.getDescriptionInput()).click();
        PressKeys.pressCmdA();
        PressKeys.pressBackSpace();
        $(elements.getDescriptionInput()).setValue(description);
        return this;
    }

    @Step("Нажать кнопку 'Сохранить'")
    public SpaceEditModalSteps saveButton() {
        $(elements.getSaveBotton()).shouldBe(visible).click();
        return this;
    }
}
