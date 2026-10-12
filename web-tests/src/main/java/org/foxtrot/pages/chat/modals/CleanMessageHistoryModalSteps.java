package org.foxtrot.pages.chat.modals;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.modals.elements.CleanMessageHistoryModal;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class CleanMessageHistoryModalSteps {

    CleanMessageHistoryModal elements = new CleanMessageHistoryModal();


    @Step("Очистить историю сообщений у себя")
    public CleanMessageHistoryModalSteps clickClearForSelfButton() {
        $(elements.getChatClearForSelfButton()).shouldBe(visible, Duration.ofSeconds(5)).click();
        return this;
    }

    @Step("Очистить историю сообщений у всех")
    public CleanMessageHistoryModalSteps clickClearForAllButton() {
        $(elements.getClearHistoryForAllButton()).shouldBe(visible, Duration.ofSeconds(5)).click();
        return this;
    }

    @Step("Проверить, что кнопка 'Очистить у всех' {0}")
    public void verifyClearHistoryForAllButtonVisibility(boolean expectedVisible) {
        SelenideElement button = $(elements.getClearHistoryForAllButton());

        if (expectedVisible) {
            button.shouldBe(visible, Duration.ofSeconds(3));
        } else {
            $(elements.getCleanHistoryModal()).shouldBe(visible);
            button.shouldNotBe(visible, Duration.ofSeconds(3));
        }
    }

}
