package org.foxtrot.mobile.steps;

import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.AccountScreen;
import org.junit.jupiter.api.Assertions;

public class AccountSteps {

    AccountScreen accountScreen = new AccountScreen();

    @Step("Проверяю, что страница аккаунта отображается")
    public AccountSteps checkAccountScreenPresent(String titleText) {
        Assertions.assertTrue(accountScreen.isAccountScreenSubtitleDisplayed(titleText, 15L),
                "Страница аккаунта не отображается");
        return this;
    }

    @Step("Проверяю, что страница аккаунта отображается")
    public AccountSteps checkAccountName(String accountName) {
        String actual = accountScreen.getAccountName();
        String normalized = actual.replace(" Отчество ", " ").trim();
        Assertions.assertEquals(accountName, normalized, "В поле имени аккаунта отображается не " +
                "ожидаемое имя");
        return this;
    }

    @Step("Нажимаю 'Выйти из приложения'")
    public AccountSteps clickExitButton() {
        accountScreen.clickExitBtn();
        return this;
    }
}

