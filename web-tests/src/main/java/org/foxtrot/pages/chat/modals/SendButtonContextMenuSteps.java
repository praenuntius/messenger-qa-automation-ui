package org.foxtrot.pages.chat.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.modals.elements.SendButtonContextMenu;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class SendButtonContextMenuSteps {

    SendButtonContextMenu elements = new SendButtonContextMenu();

    @Step("Отложить отправку")
    public void clickDeferSending() {
        $(elements.getDeferSending()).shouldBe(visible).click();
    }
}
