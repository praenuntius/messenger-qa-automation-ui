package org.foxtrot.mobile.steps.modals;

import com.codeborne.selenide.ex.UIAssertionError;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.modals.OnboardingScreen;
import org.openqa.selenium.WebDriverException;

public class OnboardingSteps {

    OnboardingScreen onboardingScreen = new OnboardingScreen();

    @Step("Нажимаю в онбординге кнопку 'Понятно' (если он появился)")
    public OnboardingSteps clickOnboardingActionButtonIfNeeded() {
        try {
            if (onboardingScreen.dismissOverlays()) {
                Allure.step("Закрыл оверлей чата (тултип голоса / Понятно)");
                return this;
            }
            onboardingScreen.clickOnboardingActionBtn();
        } catch (UIAssertionError | WebDriverException e) {
            Allure.step("Онбординг не появился за 6 сек");
        }
        return this;
    }
}
