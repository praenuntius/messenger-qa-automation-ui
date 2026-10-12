package org.foxtrot.pages.chat.input_bar;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.input_bar.elements.ScheduleMessageMenu;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ScheduleMessageMenuSteps {

    ScheduleMessageMenu elements = new ScheduleMessageMenu();

    @Step("Кликнуть на пикер выбора минут")
    public void clickMinutePicker() {
       $(elements.getMinutesInput()).shouldBe(visible).click();
    }

    @Step("Кликнуть кнопку назад")
    public void clickBackButton() {
        $(elements.getBackButton()).shouldBe(visible).click();
    }

    @Step("Выбрать минуту '{minute}'")
    public void selectMinute(int minute) {
        $(elements.getMinuteOption(minute)).click();
    }

    @Step("Проверить, что клик по минуте '{minute}' не изменяет выбранное значение")
    public void verifyClickOnMinuteHasNoEffect(int minute) {
        String initialValue = $(elements.getSelectedMinuteDisplay()).text();
        $(elements.getMinuteOption(minute)).click();
        $(elements.getSelectedMinuteDisplay()).shouldHave(text(initialValue));
    }

    @Step("Открыть контекстное меню сообщения с ID '{messageId}'")
    public ScheduleMessageMenuSteps openMessageContextMenuById(String messageId) {
        SelenideElement messageBubble = $(elements.getContainerWithMessages())
                .$(elements.getMessageBubbleById(messageId))
                .shouldBe(visible, Duration.ofSeconds(10));
        new Actions(WebDriverRunner.getWebDriver())
                .moveToElement(messageBubble)
                .contextClick()
                .perform();
        return this;
    }

}
