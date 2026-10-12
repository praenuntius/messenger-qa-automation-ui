package org.foxtrot.mobile.steps;

import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.ContactInformationScreen;
import org.junit.jupiter.api.Assertions;

public class ContactInformationSteps {

    ContactInformationScreen contactInformationScreen = new ContactInformationScreen();

    @Step("Проверяю, что на странице информации о контакте имя - '{0}'")
    public ContactInformationSteps checkScreenTitle(String title) {
        Assertions.assertEquals(title, contactInformationScreen.getContactName(), "Заголовок на странице не " +
                "соответствует ожидаемому");
        return this;
    }

    @Step("Нажимаю кнопку сообщения")
    public ContactInformationSteps clickMessageBtn() {
        contactInformationScreen.clickMessageBtn();
        return this;
    }
}
