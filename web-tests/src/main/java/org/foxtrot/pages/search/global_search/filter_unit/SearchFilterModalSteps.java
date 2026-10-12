package org.foxtrot.pages.search.global_search.filter_unit;

import io.qameta.allure.Step;
import org.foxtrot.pages.search.global_search.filter_unit.elements.SearchFilterModal;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class SearchFilterModalSteps {

    SearchFilterModal elements = new SearchFilterModal();

    @Step("Кликнуть по кнопке'Пространства'")
    public void clickSpaseButton() {
        $(elements.getSpacesButton()).shouldBe(visible).click();
    }

    @Step("Кликнуть по кнопке'Метки'")
    public void clickTagsButton() {
        $(elements.getTagsButton()).shouldBe(visible).click();
    }
}