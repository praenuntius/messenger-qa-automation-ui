package org.foxtrot.pages.faq;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import io.qameta.allure.Step;
import org.foxtrot.pages.faq.elements.FaqPage;
import org.openqa.selenium.TimeoutException;

import java.time.Duration;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class FaqPageSteps {
    private final FaqPage elements = new FaqPage();

    @Step("Проверить отображение заголовка FAQ")
    public FaqPageSteps verifyFaqTitleHeaderVisible() {
        $(elements.getFaqTitleHeader())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }
}
