package org.foxtrot.pages.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.elements.UnpinMessageModal;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class UnpinMessageModalSteps {

    UnpinMessageModal elements = new UnpinMessageModal();


    @Step("Нажать на кнопку 'Открепить'")
    public UnpinMessageModalSteps clickUnpinButton() {
        $(elements.getUnpinButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }

    @Step("Нажать на кнопку 'Отмена'")
    public UnpinMessageModalSteps clickCancelButton() {
        $(elements.getCancelButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }
}
