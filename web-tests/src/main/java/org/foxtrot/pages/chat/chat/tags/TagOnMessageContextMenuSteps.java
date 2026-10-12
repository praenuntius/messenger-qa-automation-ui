package org.foxtrot.pages.chat.chat.tags;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.chat.tags.elements.TagOnMessageContextMenu;

import java.time.Duration;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class TagOnMessageContextMenuSteps {
    private final TagOnMessageContextMenu elements = new TagOnMessageContextMenu();

    @Step("Кликнуть по кнопке 'Копировать текст' в контекстном меню тега")
    public TagOnMessageContextMenuSteps clickCopyButton() {
        $(elements.getTagCopyButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(enabled, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке 'Убрать' в контекстном меню тега")
    public TagOnMessageContextMenuSteps clickRemoveButton() {
        $(elements.getTagRemoveButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(enabled, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке 'Искать глобально' в контекстном меню тега")
    public TagOnMessageContextMenuSteps clickGlobalSearchButton() {
        $(elements.getTagGlobalSearchButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(enabled, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке 'Искать в чате' в контекстном меню тега")
    public TagOnMessageContextMenuSteps clickLocalSearchButton() {
        $(elements.getTagLocalSearchButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(enabled, Duration.ofSeconds(10))
                .click();
        return this;
    }
}
