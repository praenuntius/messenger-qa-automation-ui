package org.foxtrot.pages.chat.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.modals.elements.ScheduledMessageEditTimeModal;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ScheduledMessageEditTimeModalSteps {

    ScheduledMessageEditTimeModal elements = new ScheduledMessageEditTimeModal();

    @Step("Установить текущую дату в поле 'Дата' модального окна")
    public void setTodayAsScheduledDate() {
        $(elements.getInputDate()).shouldBe(visible).click();

        if (!$(elements.getTodayButton()).is(visible)) {
            $(elements.getPreviousMonthButton()).shouldBe(visible).click();
        }

        $(elements.getTodayButton()).shouldBe(visible).click();
        $(elements.getApplyButton()).shouldBe(visible).click();
    }

}
