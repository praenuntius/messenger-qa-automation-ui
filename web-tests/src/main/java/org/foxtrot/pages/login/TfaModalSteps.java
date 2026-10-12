package org.foxtrot.pages.login;

import io.qameta.allure.AllureId;
import io.qameta.allure.Step;
import org.foxtrot.pages.login.elements.TfaModal;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class TfaModalSteps {

    private final TfaModal elements = new TfaModal();

    @AllureId("50154") //Обновить!
    @Step("Переход к меню ввода 2FA")
    public TfaModalSteps checkInputTFACode() {
        $(elements.getTfaInput()).shouldBe(visible, Duration.ofSeconds(15));
        return this;
    }

    @AllureId("50154") //Обновить!
    @Step("Ввод 2FA-кода")
    public TfaModalSteps enterTFACode(String code) {
        $(elements.getTfaInput()).setValue(code);
        return this;
    }

    @AllureId("50154") //Обновить!
    @Step("Нажать физическую кнопку Enter")
    public TfaModalSteps pressEnter() {
        $(elements.getTfaInput()).pressEnter();
        return this;
    }
}
