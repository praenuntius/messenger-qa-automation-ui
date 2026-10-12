package org.foxtrot.pages.search.global_search.search_results_list;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.pages.search.global_search.search_results_list.elements.LinksContextMenu;

import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LinksContextMenuSteps {
    private final LinksContextMenu elements = new LinksContextMenu();

    @Step("Клик по кнопке '{name}'")
    private void clickButton(SelenideElement element, String name) {
        element.shouldBe(visible).click();
    }

    @Step("Проверить видимость всех кнопок в меню")
    public void checkAllButtonsVisible() {
        Stream.of(
                $(elements.getOpenInBrowserButton()),
                $(elements.getGoToMessageButton()),
                $(elements.getCopyButton()),
                $(elements.getForwardButton())
        ).forEach(el -> el.shouldBe(visible));
    }


    public void clickOpenInBrowser() {
        clickButton($(elements.getOpenInBrowserButton()), "Открыть в браузере");
    }

    public void clickGoToMessage() {
        clickButton($(elements.getGoToMessageButton()), "Перейти к сообщению");
    }

    public void clickCopy() {
        clickButton($(elements.getCopyButton()), "Копировать");
    }

    public void clickForward() {
        clickButton($(elements.getForwardButton()), "Переслать");
    }
}