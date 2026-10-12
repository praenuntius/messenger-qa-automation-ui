package org.foxtrot.pages.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.chat.message_bubble_context_menu.MessageBubbleContextMenuSteps;
import org.foxtrot.pages.modals.elements.EndPollModal;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class EndPollModalSteps {
    private final EndPollModal elements = new EndPollModal();

    @Step("Кликнуть по кнопке Cancel Vote в контекстном меню опроса")
    public EndPollModalSteps clickCancelPollEndingButton() {
        $(elements.getCancelButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке Finish Vote в контекстном меню опроса")
    public EndPollModalSteps clickFinishPollButton() {
        $(elements.getFinishPollButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке [X] в контекстном меню опроса")
    public EndPollModalSteps clickCloseModalButton() {
        $(elements.getCloseModalButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }
}
