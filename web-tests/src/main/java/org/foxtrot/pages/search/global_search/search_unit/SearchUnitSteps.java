package org.foxtrot.pages.search.global_search.search_unit;

import io.qameta.allure.Step;
import org.foxtrot.pages.search.global_search.search_unit.elements.SearchUnit;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class SearchUnitSteps {

    private final SearchUnit elements = new SearchUnit();

    @Step("Кликнуть по полю ввода глобального поиска")
    public SearchUnitSteps clickSearchInputField() {
        $(elements.getSearchInputField())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке 'Фильтры по пространствам'")
    public SearchUnitSteps clickSpaceFilterButton() {
        $(elements.getSearchFilterButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Проверить отображение иконок пространств {spaceNames} в фильтре: наличие={shouldBeVisible}")
    public void verifySpaceIconsInFilter(boolean shouldBeVisible, String... spaceNames) {
        var filterButton = $(elements.getSearchFilterButton());
        $(elements.getDefaultChatsIconInFilter()).shouldBe(visible);

        for (String name : spaceNames) {
            var icon = filterButton.$(elements.getSpaceIconInFilter(name));

            if (shouldBeVisible) {
                icon.shouldBe(visible, Duration.ofSeconds(5));
            } else {
                icon.shouldNotBe(visible, Duration.ofSeconds(2));
            }
        }
    }

    @Step("Ввести текст '{searchText}' в поле глобального поиска")
    public SearchUnitSteps enterSearchText(String searchText) {
        $(elements.getSearchInputField())
                .shouldBe(visible, Duration.ofSeconds(10))
                .setValue(searchText);
        return this;
    }

    @Step("Проверить отображение всех элементов меню глобального поиска")
    public SearchUnitSteps verifyGlobalSearchElementsVisible() {
        Duration timeout = Duration.ofSeconds(10);

        $(elements.getBackButton()).shouldBe(visible, timeout);

        if ($(elements.getSearchFilterButton()).is(visible)) {
            $(elements.getSearchFilterButton()).shouldBe(visible, timeout);
        } else {
            $(elements.getDefaultChatsIconInFilter()).shouldBe(visible, timeout);
        }

        $(elements.getSearchInputField()).shouldBe(visible, timeout);
        $(elements.getScrollableTagList()).shouldBe(visible, timeout);

        return this;
    }


    @Step("Дожидаемся завершения поиска после ввода значения")
    public void waitForSearchCompletion() {
        $(elements.getClearInputButton()).shouldBe(visible);
    }

    @Step("Кликнуть по вкладке 'Группы'")
    public void clickGroupsTab() {
        $(elements.getGroupsTab()).shouldBe(visible).click();
    }

    @Step("Кликнуть по вкладке 'Каналы'")
    public void clickChannelTab() {
        $(elements.getChannelsTab()).shouldBe(visible).click();
    }

    @Step("Кликнуть по вкладке 'Люди'")
    public void clickPeopleTab() {
        $(elements.getPeopleTab()).shouldBe(visible).click();
    }

    @Step("Кликнуть по вкладке 'Все'")
    public void clickALLTab() {
        $(elements.getAllEntitiesTab()).shouldBe(visible).click();
    }

    @Step("Кликнуть по вкладке 'Сообщения'")
    public void clickMessagesTab() {
        $(elements.getMessagesTab()).shouldBe(visible).click();
    }

    @Step("Кликнуть по вкладке 'Cсылки'")
    public void clickLinksTab() {
        $(elements.getLinksTab()).shouldBe(visible).click();
    }
}