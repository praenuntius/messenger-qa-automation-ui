package org.foxtrot.pages.right_sidebar;

import io.qameta.allure.Step;
import org.foxtrot.pages.key_interactions.PressKeys;
import org.foxtrot.pages.modals.CropPhotoSteps;
import org.foxtrot.pages.modals.DeletePhotoModalSteps;
import org.foxtrot.pages.right_sidebar.elements.MyProfileMenuModal;
import org.foxtrot.utils.FileUploadHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class MyProfileMenuModalSteps {

    MyProfileMenuModal elements = new MyProfileMenuModal();
    FileUploadHelper fileUploadHelper = new FileUploadHelper();
    CropPhotoSteps cropPhotoSteps = new CropPhotoSteps();
    DeletePhotoModalSteps deletePhotoModalSteps = new DeletePhotoModalSteps();

    @Step("Кликнуть 'Сохранить'")
    public MyProfileMenuModalSteps clickSaveButton() {
        $(elements.getSaveButton()).shouldBe(visible).click();
        sleep(300);
        return this;
    }

    @Step("Загрузить фото")
    public MyProfileMenuModalSteps uploadPhoto(String filePath) {
        $(elements.getEditPhotoButton()).scrollTo().shouldBe(visible);
        $(elements.getEditPhotoButton()).click();
        fileUploadHelper.uploadFileViaInput(elements.getFileInput(), filePath);
        cropPhotoSteps.clickSaveButton();
        return this;
    }

    @Step("Удалить фото")
    public MyProfileMenuModalSteps clickDeletePhotoButton() {
        $(elements.getDeletePhotoButton()).shouldBe(visible).click();
        deletePhotoModalSteps.clickDeleteButton();
        return this;
    }

    @Step("Удалить email")
    public MyProfileMenuModalSteps clickDeleteEmailButton() {
        $(elements.getDeleteEmailButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Удалить номер")
    public MyProfileMenuModalSteps clickDeleteNumberButton() {
        $(elements.getDeletePhoneButton()).shouldBe(visible).scrollIntoView(true);
        $(elements.getDeletePhoneButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Изменить имя")
    public MyProfileMenuModalSteps editName(String name) {
        $(elements.getEditFirstNameInput()).click();
        PressKeys.pressCmdA();
        PressKeys.pressBackSpace();
        $(elements.getEditFirstNameInput()).setValue(name);
        return this;
    }

    @Step("Изменить фамилию")
    public MyProfileMenuModalSteps editLastName(String lastName) {
        $(elements.getEditLastNameInput()).click();
        PressKeys.pressCmdA();
        PressKeys.pressBackSpace();
        $(elements.getEditLastNameInput()).setValue(lastName);
        return this;
    }

    @Step("Изменить отчество")
    public MyProfileMenuModalSteps editMiddleName(String middleName) {
        $(elements.getEditMiddleNameInput()).click();
        PressKeys.pressCmdA();
        PressKeys.pressBackSpace();
        $(elements.getEditMiddleNameInput()).setValue(middleName);
        return this;
    }

    @Step("Изменить должность")
    public MyProfileMenuModalSteps editJobTitle(String jobTitle) {
        $(elements.getEditProfileJobTittleInput()).click();
        PressKeys.pressCmdA();
        PressKeys.pressBackSpace();
        $(elements.getEditProfileJobTittleInput()).setValue(jobTitle);
        return this;
    }

    @Step("Добавить почту")
    public MyProfileMenuModalSteps editEmail(String email) {
        $(elements.getEditProfileEmailInput()).click();
        PressKeys.pressCmdA();
        PressKeys.pressBackSpace();
        $(elements.getEditProfileEmailInput()).setValue(email);
        return this;
    }

    @Step("Добавить номер телефона")
    public MyProfileMenuModalSteps addAdditionalPhone(String phone) {
        $(elements.getAddPhoneButton()).shouldBe(visible).scrollIntoView(true);
        $(elements.getAddPhoneButton()).shouldBe(visible).click();
        $(elements.getAddPhoneInput0()).setValue(phone);
        return this;
    }

}
