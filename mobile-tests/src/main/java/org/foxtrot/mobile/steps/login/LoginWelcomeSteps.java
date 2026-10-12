package org.foxtrot.mobile.steps.login;

import io.qameta.allure.Step;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.mobile.screens.login.LoginWelcomeScreen;
import org.foxtrot.mobile.testdata.dictionary.LoginWelcomeDictionary;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public class LoginWelcomeSteps {

    LoginWelcomeScreen loginWelcomeScreen = new LoginWelcomeScreen();

    public boolean isWelcomeDisplayedSoft() {
        return loginWelcomeScreen.isAnyWelcomeScreenTitleDisplayed(
                LoginWelcomeDictionary.SCREEN_TITLE_CANDIDATES, 2L);
    }

    @Step("Проверяю, что начальная страница логина имеет заголовок '{0}'")
    public LoginWelcomeSteps checkLoginWelcomeTitle(String titleText) {
        String[] candidates = welcomeCandidatesFor(titleText);
        Assertions.assertTrue(
                loginWelcomeScreen.isAnyWelcomeScreenTitleDisplayed(candidates, 15L),
                "Welcome не отображается (ожидали один из: " + Arrays.toString(candidates) + ")");
        return this;
    }

    @Step("Ввожу номер телефона от пользователя '{0}'")
    public LoginWelcomeSteps fillPhoneNumber(UsersEnum usersEnum) {
        loginWelcomeScreen.fillPhone(usersEnum.getPhoneNumber());
        return this;
    }

    @Step("Нажимаю 'Далее' на странице ввода номера телефона для логина")
    public LoginWelcomeSteps clickContinueButton() {
        loginWelcomeScreen.clickContinueBtn();
        return this;
    }

    @Step("Меняю сервер на Ios")
    public LoginWelcomeSteps switchToPreprodServerIos() {
        if (isIos()) {
            loginWelcomeScreen.clickOnceServerSwitcherBtn();
            loginWelcomeScreen.clickServerSwitcherBtn();
            Assertions.assertTrue(loginWelcomeScreen.checkAlertChangeServerDisplayed(),
                    "Алерт для смены сервера не появился");
            loginWelcomeScreen.clickServerOptionPreprodBtn();
        }
        return this;
    }

    @Step("Меняю сервер на Ios")
    public LoginWelcomeSteps switchToStageServerIos(int stageNumber) {
        if (isIos()) {
            loginWelcomeScreen.clickOnceServerSwitcherBtn();
            loginWelcomeScreen.clickServerSwitcherBtn();
            Assertions.assertTrue(loginWelcomeScreen.checkAlertChangeServerDisplayed(),
                    "Алерт для смены сервера не появился");
            loginWelcomeScreen.swipeInOptionContainer(10);
            loginWelcomeScreen.clickServerOptionStageBtn(stageNumber);
        }
        return this;
    }

    @Step("Нажимаю 'Server config'")
    public LoginWelcomeSteps clickServerConfig() {
        loginWelcomeScreen.clickServerConfigBtn();

        return this;
    }

    private static String[] welcomeCandidatesFor(String titleText) {
        if (titleText == null
                || LoginWelcomeDictionary.SCREEN_TITLE.equals(titleText)
                || LoginWelcomeDictionary.SCREEN_TITLE_STAGE.equals(titleText)
                || Arrays.asList(LoginWelcomeDictionary.SCREEN_TITLE_CANDIDATES).contains(titleText)) {
            return LoginWelcomeDictionary.SCREEN_TITLE_CANDIDATES;
        }
        return new String[] { titleText };
    }
}
