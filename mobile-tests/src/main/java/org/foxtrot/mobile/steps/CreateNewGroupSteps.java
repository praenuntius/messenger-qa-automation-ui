package org.foxtrot.mobile.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.AvatarAdjustScreen;
import org.foxtrot.mobile.screens.CreateNewGroupScreen;
import org.foxtrot.mobile.screens.bottomSheet.SelectImageTypeBottomSheet;
import org.foxtrot.mobile.screens.bottomSheet.SelectMediaBottomSheet;
import org.foxtrot.mobile.steps.modals.ModalSteps;
import org.foxtrot.mobile.testdata.dictionary.AddMediaDictionary;
import org.junit.jupiter.api.Assertions;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public class CreateNewGroupSteps {

    CreateNewGroupScreen createNewGroupScreen = new CreateNewGroupScreen();
    SelectImageTypeBottomSheet selectImageTypeBottomSheet = new SelectImageTypeBottomSheet();
    SelectMediaBottomSheet selectMediaBottomSheet = new SelectMediaBottomSheet();
    AvatarAdjustScreen avatarAdjustScreen = new AvatarAdjustScreen();
    ModalSteps modalSteps = new ModalSteps();

    @Step("Проверяю, что заголовок страницы - '{0}'")
    public CreateNewGroupSteps checkCreateNewGroupScreenTitle(String title) {
        Assertions.assertTrue(createNewGroupScreen.isScreenTitleDisplayed(title, 15L),
                "Заголовок страницы '" + title + "' не отображается");
        return this;
    }

    @Step("Нажимаю на кнопку 'Далее'")
    public CreateNewGroupSteps clickNextBtn() {
        createNewGroupScreen.clickNextBtn();
        return this;
    }

    @Step("Нажимаю на кнопку 'Сохранить'")
    public CreateNewGroupSteps clickSaveBtn() {
        createNewGroupScreen.clickSaveBtn();
        return this;
    }

    @Step("В поле 'Название' ввожу текст '{0}'")
    public CreateNewGroupSteps fillGroupName(String groupName) {
        createNewGroupScreen.fillGroupName(groupName);
        return this;
    }

    @Step("В поле 'Описание' ввожу текст '{0}'")
    public CreateNewGroupSteps fillGroupDescription(String groupDescription) {
        createNewGroupScreen.fillGroupDescription(groupDescription);
        return this;
    }

    @Step("Выбираю аватар для группы")
    public CreateNewGroupSteps selectAvatarForGroup() {
        createNewGroupScreen.clickUploadAvatarBtn();
        Assertions.assertTrue(selectImageTypeBottomSheet.isBottomSheetDisplayed(15L),
                "Bottom sheet выбора типа изображения не отображается");
        selectImageTypeBottomSheet.clickBottomSheetBtn(AddMediaDictionary.CHOOSE_FROM_GALLERY);
        if (isIos()) {
            modalSteps.acceptSystemAlert(AddMediaDictionary.ACCEPT_FULL);
        }
        Assertions.assertTrue(selectMediaBottomSheet.isBottomSheetDisplayed(15L),
                "Bottom sheet выбора изображения не отображается");
        selectMediaBottomSheet.clickBottomSheetBtn(1);
        Assertions.assertTrue(avatarAdjustScreen.isImageToAdjustDisplayed(15L),
                "Страница редактирования аватара не отображается");
        avatarAdjustScreen.clickDoneBtn();
        return this;
    }

    @Step("Проверяю, какой выбран тип группы:")
    public CreateNewGroupSteps checkTypeOfGroupSelected(boolean isPublicGroup) {
        if (isPublicGroup) {
            Allure.step("Проверяю, что тип группы выбран 'Открытый'");
            Assertions.assertTrue(createNewGroupScreen.isPublicGroupButtonChecked());
            Assertions.assertFalse(createNewGroupScreen.isPrivateGroupButtonChecked());
        } else {
            Allure.step("Проверяю, что тип группы выбран 'Закрытый'");
            Assertions.assertTrue(createNewGroupScreen.isPrivateGroupButtonChecked());
            Assertions.assertFalse(createNewGroupScreen.isPublicGroupButtonChecked());
        }
        return this;
    }

    @Step("Выбираю тип группы:")
    public CreateNewGroupSteps selectTypeOfGroup(boolean isPublicGroup) {
        if (isPublicGroup) {
            Allure.step("Выбираю тип группы 'Открытый'");
            createNewGroupScreen.clickPublicGroupButton();
        } else {
            Allure.step("Выбираю тип группы 'Закрытый'");
            createNewGroupScreen.clickPrivateGroupButton();
        }
        checkTypeOfGroupSelected(isPublicGroup);
        return this;
    }

    @Step("Удаляю аватар для группы")
    public CreateNewGroupSteps deleteAvatarForGroup() {
        createNewGroupScreen.clickUploadAvatarBtn();
        Assertions.assertTrue(selectImageTypeBottomSheet.isBottomSheetDisplayed(15L),
                "Bottom sheet выбора типа изображения не отображается");
        selectImageTypeBottomSheet.clickBottomSheetBtn(AddMediaDictionary.DELETE);
        return this;
    }
}
