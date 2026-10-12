package org.foxtrot.pages.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.elements.CreatePollModal;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CreatePollModalSteps {

    private final CreatePollModal elements = new CreatePollModal();

    @Step("Проверить, что селектор времени завершения опроса скрыт")
    public CreatePollModalSteps verifyExpirationTimerSelectIsHidden() {
        $(elements.getExpirationTimerSelect())
                .shouldNotBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Включить таймер завершения опроса и проверить, что по умолчанию выбран 1 день")
    public CreatePollModalSteps enableExpirationTimer() {
        $(elements.getExpirationTimerToggle())
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        return this;
    }

    @Step("Включить таймер завершения опроса и проверить, что по умолчанию выбран 1 день")
    public CreatePollModalSteps verifyDefaultExpirationPeriodOneDay() {
        $(elements.getExpirationTimerSelect())
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("1 день"));
        return this;
    }

    @Step("Ввести тему опроса: {theme}")
    public CreatePollModalSteps insertPollTheme(String theme) {
        $(elements.getPollNameField())
                .shouldBe(visible, Duration.ofSeconds(5))
                .setValue(theme);
        return this;
    }

    @Step("Ввести первую опцию опроса: {option}")
    public CreatePollModalSteps insertFirstOption(String option) {
        $$(elements.getPollOptionInput())
                .get(0)
                .shouldBe(visible, Duration.ofSeconds(5))
                .setValue(option);
        return this;
    }

    @Step("Ввести вторую опцию опроса: {option}")
    public CreatePollModalSteps insertSecondOption(String option) {
        $$(elements.getPollOptionInput())
                .get(1)
                .shouldBe(visible, Duration.ofSeconds(5))
                .setValue(option);
        return this;
    }

    @Step("Ввести опцию #{index} опроса: {option}")
    public CreatePollModalSteps insertOptionByIndex(int index, String option) {
        $$(elements.getPollOptionInput())
                .get(index)
                .shouldBe(visible, Duration.ofSeconds(5))
                .setValue(option);
        return this;
    }

    @Step("Клик кнопку 'Далее'")
    public CreatePollModalSteps clickNextButton() {
        $(elements.getNextButton())
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        return this;
    }

    @Step("Валидационное сообщение отображено")
    public CreatePollModalSteps validationMessagePresented() {
        $(elements.getPollErrorMessage())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Клик тумблер 'Выбор нескольких ответов'")
    public CreatePollModalSteps clickMultiOptionToggle() {
        $(elements.getMultiAnswersPollToggle())
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        return this;
    }

    @Step("Клик кнопку 'Создать'")
    public CreatePollModalSteps clickCreatePollButton() {
        $(elements.getCreatePollButton())
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        return this;
    }

//    @Step("Опрос с мультивыбором представлен")
//    public CreatePollSteps verifyMultiOptionPollPresented() {
//        $(elements. )
//                .shouldBe(visible, Duration.ofSeconds(10));
//        return this;
//    }
}