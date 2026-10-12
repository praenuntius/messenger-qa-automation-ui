package org.foxtrot.pages.search.local_search;

import io.qameta.allure.Step;
import org.foxtrot.pages.search.local_search.elements.LocalSearchUnit;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.*;

public class LocalSearchUnitSteps {

    private final LocalSearchUnit elements = new LocalSearchUnit();

    @Step("Кликнуть по полю ввода в меню локального поиска")
    public LocalSearchUnitSteps clickInputField() {
        org.foxtrot.pages.modals.WebOnboardingOverlay.dismissIfPresent();
        $(elements.getInputField()).click();
        return this;
    }

    @Step("Ввести текст '{searchText}' в поле локального поиска")
    public LocalSearchUnitSteps enterSearchText(String searchText) {
        $(elements.getInputField())
                .shouldBe(visible, Duration.ofSeconds(10))
                .setValue(searchText);
        return this;
    }

    @Step("Проверить, что в результатах локального поиска найдено сообщение-опрос: {pollTitle}")
    public LocalSearchUnitSteps verifyPollMessageFound(String pollTitle) {
        $$(elements.getSearchResultMessageText())
                .shouldBe(sizeGreaterThan(0), Duration.ofSeconds(10))
                .findBy(text(pollTitle))
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что в результатах локального поиска НЕ найдено сообщение: {text}")
    public LocalSearchUnitSteps verifyMessageNotFound(String Text) {
        $$(elements.getSearchResultMessageText())
                .filterBy(text(Text))
                .shouldBe(empty, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить отображение всех элементов меню локального поиска")
    public LocalSearchUnitSteps verifyLocalSearchElementsVisible() {
        $(elements.getSearchHeader())
                .shouldBe(visible, Duration.ofSeconds(10));
        $(elements.getInputField())
                .shouldBe(visible, Duration.ofSeconds(10));
//        $(elements.getFilterButton())
//                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }
}
