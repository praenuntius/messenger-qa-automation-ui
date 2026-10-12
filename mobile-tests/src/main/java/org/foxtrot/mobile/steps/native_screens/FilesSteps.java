package org.foxtrot.mobile.steps.native_screens;

import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.native_screens.FilesScreen;

public class FilesSteps {

    FilesScreen filesScreen = new FilesScreen();

    @Step("Нажимаю Сохранить на странице сохранения файла (native)")
    public FilesSteps clickSaveBtn() {
        filesScreen.clickSaveBtn();
        return this;
    }
}
