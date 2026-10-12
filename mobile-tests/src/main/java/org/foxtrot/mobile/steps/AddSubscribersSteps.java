package org.foxtrot.mobile.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.AddSubscribersScreen;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AddSubscribersSteps {

    AddSubscribersScreen addSubscribersScreen = new AddSubscribersScreen();

    @Step("Проверяю, что заголовок страницы - '{0}'")
    public AddSubscribersSteps checkAddSubscribersScreenTitle(String title) {
        Assertions.assertTrue(addSubscribersScreen.isScreenTitleDisplayed(title, 15L),
                "Заголовок страницы '" + title + "' не отображается");
        return this;
    }

    @Step("Нажимаю на иконку 'Галочка'")
    public AddSubscribersSteps clickNextBtn() {
        addSubscribersScreen.clickNextBtn();
        return this;
    }

    @Step("Нажимаю на иконку 'Поиск' (если поле поиска ещё не открыто)")
    public AddSubscribersSteps clickSearchBtn() {
        addSubscribersScreen.clickSearchBtn();
        return this;
    }

    @Step("Проверяю, что контакт под номером '{0}' имеет имя - '{1}'")
    public AddSubscribersSteps checkContactInfoByNumber(int number, String contactName) {
        if (addSubscribersScreen.isContactByTextDisplayed(contactName)) {
            return this;
        }
        if (!addSubscribersScreen.hasContactRows(8L)) {
            Assertions.fail("Контакт не найден");
        }
        String expected = contactName.replace(" Отчество ", " ").trim();
        Assertions.assertEquals(expected, addSubscribersScreen.getContactNameText(number),
                "Контакт не найден");
        return this;
    }

    @Step("Проверяю, что на странице присутствует контакт с именем '{0}'")
    public AddSubscribersSteps checkContactInfoByName(String contactName) {
        Assertions.assertTrue(addSubscribersScreen.isContactByTextDisplayed(contactName),
                "Контакт не найден");
        return this;
    }

    @Step("Выбираю контакты с именами: {0}")
    public AddSubscribersSteps selectContactsByName(String... contactNames) {
        for (String contactName : contactNames) {
            addSubscribersScreen.selectContactByName(contactName);
        }
        return this;
    }

    @Step("Проверяю, что появились бабблы с именами: {0}")
    public AddSubscribersSteps checkBubblesByNames(String[] contactNames) {
        for (String contactName : contactNames) {
            Assertions.assertTrue(addSubscribersScreen.isAddedUserBubbleDisplayed(contactName),
                    "Баббл выбранного контакта не найден: " + contactName);
        }
        return this;
    }

    @Step("Делаю поиск по тексту '{0}'")
    public AddSubscribersSteps searchByText(String text) {
        addSubscribersScreen.clearSearchInput();
        addSubscribersScreen.fillSearchInput(text);
        return this;
    }

    @Step("Ищу участника по номеру телефона '{0}'")
    public AddSubscribersSteps searchByPhone(String phone) {
        for (String query : phoneSearchQueries(phone)) {
            Allure.step("Поиск по номеру, запрос: " + query);
            addSubscribersScreen.clearSearchInput();
            addSubscribersScreen.fillSearchInput(query);
            if (addSubscribersScreen.hasContactRows(4L)) {
                return this;
            }
        }
        return this;
    }

    private static List<String> phoneSearchQueries(String phone) {
        String digits = phone == null ? "" : phone.replaceAll("\\D", "");
        Set<String> queries = new LinkedHashSet<>();
        if (phone != null && !phone.isBlank()) {
            queries.add(phone);
        }
        if (!digits.isEmpty()) {
            queries.add(digits);
        }
        if (digits.length() == 10) {
            queries.add("7" + digits);
            queries.add("+7" + digits);
            queries.add("+7 " + digits.substring(0, 3) + " " + digits.substring(3, 6)
                    + "-" + digits.substring(6, 8) + "-" + digits.substring(8));
        }
        if (digits.length() == 11 && digits.startsWith("7")) {
            queries.add(digits.substring(1));
            queries.add("+" + digits);
        }
        return new ArrayList<>(queries);
    }
}
