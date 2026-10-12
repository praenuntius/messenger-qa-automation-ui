package org.foxtrot.mobile.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.AvatarAdjustScreen;
import org.foxtrot.mobile.screens.CreateNewChannelScreen;
import org.foxtrot.mobile.screens.bottomSheet.SelectImageTypeBottomSheet;
import org.foxtrot.mobile.screens.bottomSheet.SelectMediaBottomSheet;
import org.foxtrot.mobile.steps.modals.ModalSteps;
import org.foxtrot.mobile.testdata.dictionary.AddMediaDictionary;
import org.junit.jupiter.api.Assertions;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public class CreateNewChannelSteps {

    CreateNewChannelScreen createNewChannelScreen = new CreateNewChannelScreen();
    SelectImageTypeBottomSheet selectImageTypeBottomSheet = new SelectImageTypeBottomSheet();
    SelectMediaBottomSheet selectMediaBottomSheet = new SelectMediaBottomSheet();
    AvatarAdjustScreen avatarAdjustScreen = new AvatarAdjustScreen();
    ModalSteps modalSteps = new ModalSteps();

    @Step("Проверяю, что заголовок страницы - '{0}'")
    public CreateNewChannelSteps checkCreateNewChannelScreenTitle(String title) {
        Assertions.assertTrue(createNewChannelScreen.isScreenTitleDisplayed(title, 15L),
                "Заголовок страницы '" + title + "' не отображается");
        return this;
    }

    @Step("Нажимаю на кнопку 'Далее'")
    public CreateNewChannelSteps clickNextBtn() {
        createNewChannelScreen.clickNextBtn();
        return this;
    }

    @Step("Нажимаю на кнопку 'Сохранить'")
    public CreateNewChannelSteps clickDoneBtn() {
        createNewChannelScreen.clickDoneBtn();
        return this;
    }

    @Step("В поле 'Название' ввожу текст '{0}'")
    public CreateNewChannelSteps fillChannelName(String channelName) {
        createNewChannelScreen.fillChannelName(channelName);
        return this;
    }

    @Step("В поле 'Описание' ввожу текст '{0}'")
    public CreateNewChannelSteps fillChannelDescription(String channelDescription) {
        createNewChannelScreen.fillChannelDescription(channelDescription);
        return this;
    }

    @Step("Проверяю, какой выбран тип канала:")
    public CreateNewChannelSteps checkTypeOfChannelSelected(boolean isPublicChannel) {
        if (isPublicChannel) {
            Allure.step("Проверяю, что тип канала выбран 'Открытый'");
            Assertions.assertTrue(createNewChannelScreen.isPublicChannelButtonChecked());
            Assertions.assertFalse(createNewChannelScreen.isPrivateChannelButtonChecked());
        } else {
            Allure.step("Проверяю, что тип канала выбран 'Закрытый'");
            Assertions.assertTrue(createNewChannelScreen.isPrivateChannelButtonChecked());
            Assertions.assertFalse(createNewChannelScreen.isPublicChannelButtonChecked());
        }
        return this;
    }

    @Step("Выбираю тип канала:")
    public CreateNewChannelSteps selectTypeOfChannel(boolean isPublicChannel) {
        if (isPublicChannel) {
            Allure.step("Выбираю тип канала 'Открытый'");
            createNewChannelScreen.clickPublicChannelButton();
        } else {
            Allure.step("Выбираю тип канала 'Закрытый'");
            createNewChannelScreen.clickPrivateChannelButton();
        }
        checkTypeOfChannelSelected(isPublicChannel);
        return this;
    }

    @Step("Выбираю аватар для канала")
    public CreateNewChannelSteps selectAvatarForChannel() {
        createNewChannelScreen.clickUploadAvatarBtn();
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

    @Step("Удаляю аватар для канала")
    public CreateNewChannelSteps deleteAvatarForChannel() {
        createNewChannelScreen.clickUploadAvatarBtn();
        Assertions.assertTrue(selectImageTypeBottomSheet.isBottomSheetDisplayed(15L),
                "Bottom sheet выбора типа изображения не отображается");
        selectImageTypeBottomSheet.clickBottomSheetBtn(AddMediaDictionary.DELETE);
        return this;
    }

}
