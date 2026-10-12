package org.foxtrot.mobile.test.login;

import org.foxtrot.mobile.allure.AllureIds;
import org.foxtrot.mobile.test.BaseMobileTest;
import org.foxtrot.mobile.testdata.dictionary.Login2FADictionary;
import org.foxtrot.mobile.testdata.dictionary.LoginVerificationCodeDictionary;
import org.foxtrot.mobile.testdata.dictionary.LoginWelcomeDictionary;
import org.foxtrot.utils.TestData;
import org.foxtrot.utils.factories.core.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.foxtrot.api.constants.TestConstants.SMS_CODE;
import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;
import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public class LoginMobileTest extends BaseMobileTest {
    private final UserFactory userFactory = new UserFactory();

    @AfterEach
    public void tearDown() {
        userFactory.releaseAll();
    }

    @Test
    @DisplayName("Ввести номер добавленный в WL")
    @AllureIds(ios = "51271", android = "48741")
    public void enterWLNumber() {
        var user = userFactory.takeUser();
        if (isIos()) {
            pages.loginWelcome().switchToPreprodServerIos();
        }
        pages.loginWelcome().checkLoginWelcomeTitle(LoginWelcomeDictionary.SCREEN_TITLE)
                .fillPhoneNumber(user)
                .clickContinueButton();
        pages.loginVerificationCode().checkLoginVerificationCodeTitle(LoginVerificationCodeDictionary.SCREEN_TITLE);
        pages.loginVerificationCode().fillCode(SMS_CODE);
        if (isAndroid()) {
            pages.loginVerificationCode().checkEnableContinueButton(true)
                    .clickContinueButton();
        }
        pages.login2FA().checkLogin2FATitle(Login2FADictionary.SCREEN_TITLE);
    }

    @Test
    @DisplayName("Ввести неверный пароль от TFA при авторизации")
    @AllureIds(ios = "51277", android = "48747")
    public void enterInvalid2FAOnLogin() {
        var user = userFactory.takeUser();
        if (isIos()) {
            pages.loginWelcome().switchToPreprodServerIos();
        }
        pages.loginWelcome().checkLoginWelcomeTitle(LoginWelcomeDictionary.SCREEN_TITLE)
                .fillPhoneNumber(user)
                .clickContinueButton();
        pages.loginVerificationCode().checkLoginVerificationCodeTitle(LoginVerificationCodeDictionary.SCREEN_TITLE);
        pages.loginVerificationCode().fillCode(SMS_CODE);
        if (isAndroid()) {
            pages.loginVerificationCode().checkEnableContinueButton(true)
                    .clickContinueButton();
        }
        pages.login2FA().checkLogin2FATitle(Login2FADictionary.SCREEN_TITLE)
                .fillCode(TestData.randomCode())
                .clickContinueButton()
                .checkValidationText(Login2FADictionary.VALIDATION_TEXT_ANDROID,
                        Login2FADictionary.VALIDATION_TEXT_IOS);
    }
}
