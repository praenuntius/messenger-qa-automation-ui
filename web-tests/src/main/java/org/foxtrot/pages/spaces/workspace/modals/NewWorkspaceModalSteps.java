package org.foxtrot.pages.spaces.workspace.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.CropPhotoSteps;
import org.foxtrot.pages.spaces.workspace.modals.elements.NewWorkspaceModal;
import org.foxtrot.utils.FileUploadHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class NewWorkspaceModalSteps {

    private final NewWorkspaceModal elements = new NewWorkspaceModal();
    FileUploadHelper fileUploadHelper = new FileUploadHelper();
    CropPhotoSteps cropPhotoSteps = new CropPhotoSteps();

    @Step("Загрузить аватар группы")
    public NewWorkspaceModalSteps UploadAvatar(String filePath) {
        $(elements.getUploadAvatarButton()).shouldBe(visible).click();
        fileUploadHelper.uploadFileViaInput(elements.getFileInput(),filePath);
        cropPhotoSteps.clickSaveButton();
        return this;
    }
    @Step("Создать рабочее пространство с именем: {workspaceName}")
    public NewWorkspaceModalSteps createWorkspace(String workspaceName) {
        $(elements.getNewSpaceTitleInput()).shouldBe(visible, Duration.ofSeconds(15)).setValue(workspaceName);
        $(elements.getNewSpaceDescriptionInput()).shouldBe(visible, Duration.ofSeconds(15)).setValue("Test Space created for QA's sake!");
        clickCreateButton();
        return this;
    }

    @Step("Нажать кнопку «Создать» в модальном окне создания пространства")
    public NewWorkspaceModalSteps clickCreateButton() {
        $(elements.getNewSpaceCreateButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }

    @Step("Проверить отображение ошибки «Обязательное поле не может быть пустым»")
    public NewWorkspaceModalSteps verifyRequiredFieldErrorIsVisible() {
        $(elements.getRequiredFieldError())
                .shouldBe(visible);
        return this;
    }

}
