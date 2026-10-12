package org.foxtrot.pages.modals;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

/**
 * Онбординг меток/голоса ({@code OnboardingWrapper} + Body3) перехватывает клики
 * (clear history, pin, автоудаление) и прячет меню пространства.
 */
public final class WebOnboardingOverlay {

    private static final By ACK_BUTTON = By.xpath(
            "//div[contains(@class,'OnboardingWrapper')]//span[normalize-space()='Понятно']"
                    + " | //div[contains(@class,'OnboardingWrapper')]//button[.//span[normalize-space()='Понятно']]"
    );

    private WebOnboardingOverlay() {
    }

    @Step("Закрыть онбординг (метки / голосовые), если он поверх UI")
    public static void dismissIfPresent() {
        for (int i = 0; i < 5; i++) {
            SelenideElement ack = $(ACK_BUTTON);
            if (!ack.exists() || !ack.isDisplayed()) {
                SelenideElement fallback = $x(
                        "//div[contains(@class,'OnboardingBtn') or contains(@class,'OnboardingWrapper')]"
                                + "//span[normalize-space()='Понятно']");
                if (!fallback.exists() || !fallback.isDisplayed()) {
                    return;
                }
                ack = fallback;
            }
            try {
                ack.click();
            } catch (RuntimeException ignored) {
                try {
                    ack.toWebElement().click();
                } catch (RuntimeException ignoredAgain) {
                    return;
                }
            }
            Selenide.sleep(250);
        }
    }
}
