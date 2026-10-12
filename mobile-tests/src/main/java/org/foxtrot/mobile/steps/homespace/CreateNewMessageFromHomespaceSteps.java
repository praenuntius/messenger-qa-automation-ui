package org.foxtrot.mobile.steps.homespace;

import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.homespace.CreateNewMessageFromHomespaceScreen;
import org.junit.jupiter.api.Assertions;

public class CreateNewMessageFromHomespaceSteps {

    CreateNewMessageFromHomespaceScreen createNewMessageScreen = new CreateNewMessageFromHomespaceScreen();

    @Step("Проверяю, что заголовок страницы - '{0}'")
    public CreateNewMessageFromHomespaceSteps checkCreateNewMessageScreenTitle(String title) {
        Assertions.assertTrue(createNewMessageScreen.isScreenTitleDisplayed(title, 15L),
                "Заголовок страницы '" + title + "' не отображается");
        return this;
    }

    @Step("Проверяю, что подзаголовок страницы - '{0}'")
    public CreateNewMessageFromHomespaceSteps checkCreateNewMessageScreenSubtitle(String subTitle) {
        Assertions.assertTrue(createNewMessageScreen.isScreenSubtitleDisplayed(subTitle, 15L),
                "Подзаголовок страницы '" + subTitle + "' не отображается");
        return this;
    }

    @Step("Проверяю, что на странице создания сообщения отображается контакт '{1}' под номером '{0}'")
    public CreateNewMessageFromHomespaceSteps checkContactNameDisplayed(int number, String contactName) {
        String actual = createNewMessageScreen.getContactNameText(number, 15L);
        String expected = contactName.replace(" Отчество ", " ").trim();
        if (expected.equals(actual.replace(" Отчество ", " ").trim())) {
            return this;
        }
        if (createNewMessageScreen.isContactByTextDisplayed(contactName, 5L)) {
            return this;
        }
        Assertions.assertEquals(expected, actual.replace(" Отчество ", " ").trim(),
                "Контакт не отображается");
        return this;
    }

    @Step("Нажимаю на контакт под номером '{0}'")
    public CreateNewMessageFromHomespaceSteps clickContactByNumber(int number) {
        createNewMessageScreen.clickContact(number);
        return this;
    }

    @Step("Проверяю, что на странице создания сообщения есть кнопки - '{0}', '{1}', '{2}'")
    public CreateNewMessageFromHomespaceSteps checkBtnTextsOnCreateNewMessageScreen(String btn1, String btn2,
            String btn3) {
        Assertions.assertEquals(btn1, createNewMessageScreen.getCreateGroupButtonText(),
                "У кнопки 1 отображается текст не соответствующий ожидаемому");
        Assertions.assertEquals(btn2, createNewMessageScreen.getCreateChannelButtonText(),
                "У кнопки 2 отображается текст не соответствующий ожидаемому");
        Assertions.assertEquals(btn3, createNewMessageScreen.getInviteToAppButtonText(),
                "У кнопки 3 отображается текст не соответствующий ожидаемому");
        return this;
    }

    @Step("Нажимаю на кнопку 'Создать группу'")
    public CreateNewMessageFromHomespaceSteps clickCreateGroupBtn() {
        createNewMessageScreen.clickCreateGroupButton();
        return this;
    }

    @Step("Нажимаю на кнопку 'Создать канал'")
    public CreateNewMessageFromHomespaceSteps clickCreateChannelBtn() {
        createNewMessageScreen.clickCreateChannelButton();
        return this;
    }

    @Step("Нажимаю на кнопку 'Пригласить в приложение'")
    public CreateNewMessageFromHomespaceSteps clickInviteToAppBtn() {
        createNewMessageScreen.clickInviteToAppButton();
        return this;
    }
}
