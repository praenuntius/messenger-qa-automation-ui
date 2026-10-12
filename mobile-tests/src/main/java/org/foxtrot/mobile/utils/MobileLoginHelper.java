package org.foxtrot.mobile.utils;

import io.qameta.allure.Step;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.mobile.steps.homespace.ChatsSteps;
import org.foxtrot.mobile.steps.login.Login2FASteps;
import org.foxtrot.mobile.steps.login.LoginVerificationCodeSteps;
import org.foxtrot.mobile.steps.login.LoginWelcomeSteps;
import org.foxtrot.mobile.steps.login.ServerConfigSteps;
import org.foxtrot.mobile.steps.modals.ModalSteps;
import org.foxtrot.mobile.testdata.dictionary.ChatsDictionary;
import org.foxtrot.mobile.testdata.dictionary.Login2FADictionary;
import org.foxtrot.mobile.testdata.dictionary.LoginVerificationCodeDictionary;
import org.foxtrot.mobile.testdata.dictionary.LoginWelcomeDictionary;

import static org.foxtrot.api.constants.TestConstants.SMS_CODE;
import static org.foxtrot.api.constants.TestConstants.TFA_CODE;
import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;
import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public class MobileLoginHelper {

    private LoginWelcomeSteps loginWelcomeSteps = new LoginWelcomeSteps();
    private LoginVerificationCodeSteps loginVerificationCodeSteps = new LoginVerificationCodeSteps();
    private Login2FASteps login2FASteps = new Login2FASteps();
    private ChatsSteps chatsSteps = new ChatsSteps();
    private ModalSteps modalSteps = new ModalSteps();
    private ServerConfigSteps serverConfigSteps = new ServerConfigSteps();
    /** Android Server Config: поле domain. Stage14 API — api-stage14.domain.net. */
    private final String STAGE_DOMAIN = "domain.net";

    @Step("Произвожу UI логин в приложении под профайлом '{0}'")
    public void loginViaUi(UsersEnum user) {
        loginOnPreprod(user, true);
    }

    /**
     * stage14 ({@code baseApiUrl} содержит stage14) — Server Config на stage.
     * Иначе preprod, как мобильный регресс: {@link #loginViaUi}.
     *
     * @param reconfigureServer false — не открывать Server Config (повторный логин после logout)
     */
    @Step("UI-логин под '{0}' на текущем энвайрменте, reconfigureServer={1}")
    public void loginForCurrentEnv(UsersEnum user, boolean reconfigureServer) {
        if (isStage14()) {
            loginViaUiOnStage(user, 14, reconfigureServer);
            return;
        }
        loginOnPreprod(user, reconfigureServer);
    }

    public void loginForCurrentEnv(UsersEnum user) {
        loginForCurrentEnv(user, true);
    }

    private static boolean isStage14() {
        return System.getProperty("baseApiUrl", "").contains("stage14");
    }

    private void loginOnPreprod(UsersEnum user, boolean switchServer) {
        try {
            if (isIos() && switchServer) {
                loginWelcomeSteps.checkLoginWelcomeTitle(LoginWelcomeDictionary.SCREEN_TITLE)
                        .switchToPreprodServerIos();
            }
            loginWelcomeSteps.checkLoginWelcomeTitle(LoginWelcomeDictionary.SCREEN_TITLE)
                    .fillPhoneNumber(user)
                    .clickContinueButton();
            LoaderWaiter.waitForNoLoader();
            // todo задача https://allprojects.atlassian.net/browse/FB-20822
            loginVerificationCodeSteps.checkLoginVerificationCodeTitle(LoginVerificationCodeDictionary.SCREEN_TITLE);
            loginVerificationCodeSteps.fillCode(SMS_CODE);
            if (isAndroid()) {
                loginVerificationCodeSteps.checkEnableContinueButton(true)
                        .clickContinueButton();
            }
            LoaderWaiter.waitForNoLoader();
            login2FASteps.checkLogin2FATitle(Login2FADictionary.SCREEN_TITLE)
                    .fillCode(TFA_CODE)
                    .clickContinueButton();
            if (isIos()) {
                LoaderWaiter.waitForNoLoader();
                modalSteps.acceptSystemAlert();
            }
            LoaderWaiter.waitForNoLoader();
            chatsSteps.checkChatsScreenTitle(ChatsDictionary.SCREEN_TITLE);
        } catch (Exception e) {
            throw new RuntimeException("Логин прошел с ошибкой", e);
        }
    }

    @Step("Произвожу UI логин в приложении под профайлом '{0}' на stage{1}")
    public void loginViaUiOnStage(UsersEnum user, int stageId) {
        loginViaUiOnStage(user, stageId, true);
    }

    /**
     * @param reconfigureServer false — сервер уже stage (после logout), не открываем Server Config
     *                          (иначе клик по environment «stage» падает, если в поле уже stage14).
     */
    @Step("Произвожу UI логин на stage{1}, reconfigureServer={2}")
    public void loginViaUiOnStage(UsersEnum user, int stageId, boolean reconfigureServer) {
        try {
            loginWelcomeSteps.checkLoginWelcomeTitle(LoginWelcomeDictionary.SCREEN_TITLE_STAGE);
            if (reconfigureServer) {
                if (isAndroid()) {
                    loginWelcomeSteps.clickServerConfig();
                    serverConfigSteps.changeServerConfig("stage" + stageId, STAGE_DOMAIN);
                } else if (isIos()) {
                    loginWelcomeSteps.switchToStageServerIos(stageId);
                }
                loginWelcomeSteps.checkLoginWelcomeTitle(LoginWelcomeDictionary.SCREEN_TITLE_STAGE);
            }
            loginWelcomeSteps.fillPhoneNumber(user)
                    .clickContinueButton();
            LoaderWaiter.waitForNoLoader();
            // todo задача https://allprojects.atlassian.net/browse/FB-20822
            loginVerificationCodeSteps.checkLoginVerificationCodeTitle(LoginVerificationCodeDictionary.SCREEN_TITLE);
            loginVerificationCodeSteps.fillCode(SMS_CODE);
            if (isAndroid()) {
                loginVerificationCodeSteps.clickContinueButton();
            }
            LoaderWaiter.waitForNoLoader();
            login2FASteps.checkLogin2FATitle(Login2FADictionary.SCREEN_TITLE)
                    .fillCode(TFA_CODE)
                    .clickContinueButton();
            if (isIos()) {
                LoaderWaiter.waitForNoLoader();
                modalSteps.acceptSystemAlert();
            }
            LoaderWaiter.waitForNoLoader();
            chatsSteps.checkChatsScreenTitle(ChatsDictionary.SCREEN_TITLE);
        } catch (Exception e) {
            throw new RuntimeException("Логин прошел с ошибкой", e);
        }
    }
}
