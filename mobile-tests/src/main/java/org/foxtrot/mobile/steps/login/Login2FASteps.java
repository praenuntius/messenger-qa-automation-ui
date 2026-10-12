package org.foxtrot.mobile.steps.login;

import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.login.Login2FAScreen;
import org.foxtrot.mobile.utils.LoaderWaiter;
import org.junit.jupiter.api.Assertions;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;

public class Login2FASteps {

    Login2FAScreen login2FAScreen = new Login2FAScreen();

    @Step("Проверяю, что страница логина имеет заголовок '{0}'")
    public Login2FASteps checkLogin2FATitle(String titleText) {
        return checkLogin2FATitle(titleText, 30L);
    }

    @Step("Проверяю, что страница логина имеет заголовок '{0}' (timeout={1}s)")
    public Login2FASteps checkLogin2FATitle(String titleText, long timeoutSeconds) {
        LoaderWaiter.waitForNoLoader();
        Assertions.assertTrue(login2FAScreen.is2FAScreenTitleDisplayed(timeoutSeconds),
                "Заголовок страницы '" + titleText + "' не отображается");
        Assertions.assertEquals(titleText, login2FAScreen.get2FAScreenTitleText(),
                "Текст в заголовке страницы не совпадает с ожидаемым");
        return this;
    }

    @Step("Проверяю, что текст валидации '{0}' (Android) или '{1}' (iOS)")
    public Login2FASteps checkValidationText(String validationTextAndroid, String validationTextIos) {
        String expectedText = isAndroid() ? validationTextAndroid : validationTextIos;
        Assertions.assertEquals(expectedText, login2FAScreen.getValidationText(),
                "Текст в валидации на странице 2FA не совпадает с ожидаемым");
        return this;
    }

    @Step("Ввожу 2FA код '{0}'")
    public Login2FASteps fillCode(String code) {
        login2FAScreen.fillCode(code);
        return this;
    }

    @Step("Нажимаю 'Далее' на странице ввода кода 2FA для логина")
    public Login2FASteps clickContinueButton() {
        login2FAScreen.clickContinueBtn();
        return this;
    }
}
