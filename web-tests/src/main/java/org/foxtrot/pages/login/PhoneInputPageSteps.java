package org.foxtrot.pages.login;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.AllureId;
import io.qameta.allure.Step;
import org.foxtrot.config.RunChannelResolver;
import org.foxtrot.pages.login.elements.PhoneInputPage;
import org.foxtrot.pages.spaces.homespace.primary.elements.Homespace;

import java.time.Duration;

import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class PhoneInputPageSteps {

    private final PhoneInputPage elements = new PhoneInputPage();
    private final Homespace homespace = new Homespace();

    /**
     * eager open() ждёт только DOMContentLoaded; React монтируется позже.
     * Ждём #root и появление формы логина или homespace.
     */
    @Step("Дождаться экрана логина или homespace")
    public void waitForLoginOrHomespace(Duration timeout) {
        if (RunChannelResolver.isDesktop()) {
            return;
        }
        waitForReactRoot(timeout);
        Selenide.Wait()
                .withTimeout(timeout)
                .pollingEvery(Duration.ofMillis(500))
                .until(driver -> isLoginScreenReady() || isHomespaceReady());
    }

    public boolean isLoginScreenReady() {
        var phone = $(elements.getPhoneNumberField());
        return phone.exists() && phone.is(visible);
    }

    private boolean isHomespaceReady() {
        var home = $(homespace.getHomespaceButton());
        return home.exists() && home.is(visible);
    }

    private static void waitForReactRoot(Duration timeout) {
        Selenide.Wait()
                .withTimeout(timeout)
                .pollingEvery(Duration.ofMillis(300))
                .until(driver -> {
                    Object count = Selenide.executeJavaScript(
                            "var r = document.getElementById('root'); return r ? r.childElementCount : 0;");
                    return count instanceof Number number && number.intValue() > 0;
                });
    }

    @AllureId("50154") //Обновить!
    @Step("Открыть базовый URL")
    public PhoneInputPageSteps checkPageIsLoaded() {
        $(elements.getPhoneNumberField()).shouldBe(visible, phoneFormWaitTimeout());
        return this;
    }

    @AllureId("50154") //Обновить!
    @Step("Ввести номер из WL")
    public PhoneInputPageSteps enterPhoneNumber(String phoneNumber) {
        var field = $(elements.getPhoneNumberField()).shouldBe(interactable, phoneFormWaitTimeout());
        if (!phonesMatch(field.getValue(), phoneNumber)) {
            field.click();
            field.setValue(phoneNumber);
            waitUntilPhoneValueMatches(field, phoneNumber);
        }
        submitPhoneFormWeb();
        return this;
    }

    @Step("Повторить сабмит номера без повторного ввода (web)")
    public PhoneInputPageSteps resubmitPhoneFormWeb() {
        $(elements.getPhoneNumberField()).shouldBe(visible, Duration.ofSeconds(5));
        submitPhoneFormWeb();
        return this;
    }

    @Step("Нажать «Далее» на экране телефона (web)")
    public PhoneInputPageSteps clickNextButton() {
        submitPhoneFormWeb();
        return this;
    }

    private void submitPhoneFormWeb() {
        $(elements.getNextButton()).shouldBe(interactable, Duration.ofSeconds(5)).click();
    }

    private static void waitUntilPhoneValueMatches(
            com.codeborne.selenide.SelenideElement field, String phoneNumber) {
        Selenide.Wait()
                .withTimeout(Duration.ofSeconds(3))
                .pollingEvery(Duration.ofMillis(100))
                .until(driver -> phonesMatch(field.getValue(), phoneNumber));
    }

    /**
     * Desktop: WL может предзаполнить номер — повторный setValue дописывает цифры
     * (9774660018 → 97746600189774660018) и ломает авторизацию.
     */
    @Step("Отправить номер телефона (desktop)")
    public PhoneInputPageSteps submitPhoneNumber(String phoneNumber, boolean forceReenter) {
        var field = $(elements.getPhoneNumberField()).shouldBe(interactable, phoneFormWaitTimeout());
        if (!forceReenter && phonesMatch(field.getValue(), phoneNumber)) {
            submitPhoneFormDesktop(field);
            return this;
        }
        field.click();
        field.clear();
        field.setValue(phoneNumber);
        submitPhoneFormDesktop(field);
        return this;
    }

    private static Duration phoneFormWaitTimeout() {
        return RunChannelResolver.isDesktop() ? Duration.ofSeconds(120) : Duration.ofSeconds(15);
    }

    private void submitPhoneFormDesktop(com.codeborne.selenide.SelenideElement field) {
        field.pressEnter();
        try {
            $(elements.getNextButton()).shouldBe(interactable, Duration.ofMillis(500)).click();
        } catch (Throwable ignored) {
            // Enter уже сработал или кнопка скрыта
        }
    }

    private static boolean phonesMatch(String current, String expected) {
        return !normalizePhone(current).isEmpty()
                && normalizePhone(current).equals(normalizePhone(expected));
    }

    private static String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return "";
        }
        return phone.replaceAll("\\D", "");
    }
}
