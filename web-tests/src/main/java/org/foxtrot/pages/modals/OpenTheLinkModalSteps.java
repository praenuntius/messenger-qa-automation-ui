package org.foxtrot.pages.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.elements.OpenTheLinkModal;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class OpenTheLinkModalSteps {

    OpenTheLinkModal elements = new OpenTheLinkModal();

    @Step("Нажать кнопку 'Открыть'")
    public OpenTheLinkModalSteps clickOpenButton() {
        $(elements.getOpenButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }


}
