package org.foxtrot.mobile.steps.login;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.login.LoginVerificationCodeScreen;
import org.foxtrot.mobile.steps.modals.ModalSteps;
import org.foxtrot.mobile.utils.LoaderWaiter;
import org.junit.jupiter.api.Assertions;

public class LoginVerificationCodeSteps {

    LoginVerificationCodeScreen loginVerificationCodeScreen = new LoginVerificationCodeScreen();
    ModalSteps modalSteps = new ModalSteps();

    @Step("Проверяю, что страница логина имеет заголовок '{0}'")
    public LoginVerificationCodeSteps checkLoginVerificationCodeTitle(String titleText) {
        LoaderWaiter.waitForNoLoader();
        long deadline = System.currentTimeMillis() + 30_000;
        while (System.currentTimeMillis() < deadline) {
            if (loginVerificationCodeScreen.isVerificationCodeScreenTitleDisplayed(titleText, 2L)) {
                return this;
            }
            String alert = modalSteps.readVisibleAlertText();
            if (alert != null && !alert.isBlank()) {
                Assertions.fail("Экран SMS не открылся: приложение показало алерт — " + alert);
            }
        }
        Assertions.fail("Заголовок страницы '" + titleText + "' не отображается");
        return this;
    }

    @Step("Проверяю состояние кнопки 'Далее' на странице логина - Проверочный код")
    public LoginVerificationCodeSteps checkEnableContinueButton(boolean shouldBeEnabled) {
        if (shouldBeEnabled) {
            Allure.step("Проверяю, что кнопка 'Продолжить' доступна для клика");
            Assertions.assertTrue(loginVerificationCodeScreen.checkContinueBtnEnabled(),
                    "Кнопка 'Далее' недоступна для клика. Ожидали обратное");
        } else {
            Allure.step("Проверяю, что кнопка 'Продолжить' недоступна для клика");
            Assertions.assertFalse(loginVerificationCodeScreen.checkContinueBtnEnabled(),
                    "Кнопка 'Далее' доступна для клика. Ожидали обратное");
        }
        return this;
    }

    @Step("Ввожу проверочный код '{0}'")
    public LoginVerificationCodeSteps fillCode(String code) {
        loginVerificationCodeScreen.fillCode(code);
        return this;
    }

    @Step("Нажимаю 'Далее' на странице ввода проверочного кода для логина")
    public LoginVerificationCodeSteps clickContinueButton() {
        loginVerificationCodeScreen.clickContinueBtn();
        return this;
    }
}
