package org.foxtrot.mobile.steps.modals;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.modals.ModalScreen;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class ModalSteps {

    private static final Logger logger = LoggerFactory.getLogger(ModalSteps.class);

    ModalScreen modalScreen = new ModalScreen();

    @Step("Проверяю, что в модальном окне имеется текст '{0}', '{1}', '{2}'")
    public ModalSteps checkModalContent(String titleText, String cancelBtnText, String actionBtnText) {
        Assertions.assertEquals(titleText, modalScreen.getModalHeaderText(),
                "Текст в модальном окне не совпадает с ожидаемым");
        Assertions.assertEquals(cancelBtnText, modalScreen.getCancelBtnText(),
                "Текст в модальном окне не совпадает с ожидаемым");
        Assertions.assertEquals(actionBtnText, modalScreen.getActionBtnText(),
                "Текст в модальном окне не совпадает с ожидаемым");
        return this;
    }

    @Step("Проверяю, что в модальном окне имеется текст '{0}' в заголовке")
    public ModalSteps checkModalTitle(String titleText) {
        Assertions.assertEquals(titleText, modalScreen.getModalHeaderText(),
                "Текст в модальном окне не совпадает с ожидаемым");
        return this;
    }

    @Step("Проверяю, что в модальном окне имеется текст '{0}'")
    public ModalSteps checkModalText(String text) {
        Assertions.assertEquals(text, modalScreen.getModalText(),
                "Текст в модальном окне не совпадает с ожидаемым");
        return this;
    }

    @Step("Нажимаю кнопку действия в модальном окне")
    public ModalSteps clickActionButton() {
        modalScreen.clickActionBtn();
        return this;
    }

    @Step("Нажимаю кнопку 'Сгенерировать' в модальном окне")
    public ModalSteps clickInviteButton() {
        modalScreen.clickInviteBtn();
        return this;
    }

    @Step("Нажимаю кнопку 'Покинуть' в модальном окне")
    public ModalSteps clickLeaveButton() {
        modalScreen.clickLeaveBtn();
        return this;
    }

    @Step("Нажимаю кнопку 'Войти' в модальном окне")
    public ModalSteps clickEnterButton() {
        modalScreen.clickEnterBtn();
        return this;
    }

    @Step("Нажимаю кнопку отмены в модальном окне")
    public ModalSteps clickCancelButton() {
        modalScreen.clickCancelBtn();
        return this;
    }

    @Step("Нажимаю 'Разрешить' в системном alert (best-effort, max 1s)")
    public ModalSteps acceptSystemAlert() {
        try {
            new WebDriverWait(WebDriverRunner.getWebDriver(), Duration.ofSeconds(1))
                    .until(ExpectedConditions.alertIsPresent())
                    .accept();
        } catch (Exception ignore) {
            logger.warn("Системный alert не найден — продолжаем");
        }
        return this;
    }

    @Step("Нажимаю '{0}' в появившемся модальном окне")
    public ModalSteps acceptSystemAlert(String modalText) {
        try {
            modalScreen.clickActionBtnByText(modalText);
        } catch (Exception ignore) {
            logger.warn("Не удалось кликнуть '{}' в модальном окне", modalText);
        }
        return this;
    }

    /** Текст in-app алерта, если он уже на экране (без ожидания). */
    public String readVisibleAlertText() {
        try {
            if (modalScreen.isMessageDisplayed()) {
                return modalScreen.getModalText();
            }
            if (modalScreen.isHeaderDisplayed()) {
                return modalScreen.getModalHeaderText();
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }
}
