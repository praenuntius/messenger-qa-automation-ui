package org.foxtrot.mobile.steps.native_screens;

import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.native_screens.ContactCreateScreen;

public class ContactCreateSteps {

    ContactCreateScreen contactCreateScreen = new ContactCreateScreen();

    @Step("Нажимаю Сохранить в карточке создания контакта (native)")
    public ContactCreateSteps clickSaveBtn() {
        contactCreateScreen.clickSaveBtn();
        return this;
    }
}
