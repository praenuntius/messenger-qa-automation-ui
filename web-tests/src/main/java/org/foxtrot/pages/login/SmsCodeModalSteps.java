package org.foxtrot.pages.login;

import io.qameta.allure.AllureId;
import io.qameta.allure.Step;
import org.foxtrot.config.RunChannelResolver;
import org.foxtrot.pages.login.elements.SmsCodeModal;

import java.time.Duration;

import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class SmsCodeModalSteps {

    private final SmsCodeModal elements = new SmsCodeModal();

    @AllureId("50154") //Обновить!
    @Step("Переход к меню ввода СМС-кода")
    public SmsCodeModalSteps checkPageIsLoaded() {
        $(elements.getSmsCodeFieldButton()).shouldBe(visible, smsScreenWaitTimeout());
        return this;
    }

    /** Для desktop-retry: не бросает исключение. */
    public boolean waitSmsCodeScreen(Duration timeout) {
        try {
            $(elements.getSmsCodeFieldButton()).shouldBe(visible, timeout);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    @AllureId("50154") //Обновить!
    @Step("Ввод СМС-кода")
    public SmsCodeModalSteps enterSmsCode(String code) {
        var field = $(elements.getSmsCodeFieldButton());
        if (RunChannelResolver.isDesktop()) {
            if (!field.is(interactable)) {
                field.shouldBe(interactable, smsScreenWaitTimeout());
            }
        } else {
            field.shouldBe(visible, smsScreenWaitTimeout());
        }
        field.setValue(code);
        return this;
    }

    private static Duration smsScreenWaitTimeout() {
        return Duration.ofSeconds(60);
    }
}
