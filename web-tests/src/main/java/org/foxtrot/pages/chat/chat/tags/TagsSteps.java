package org.foxtrot.pages.chat.chat.tags;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.chat.tags.elements.Tags;

import java.time.Duration;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class TagsSteps {
    private final Tags elements = new Tags();

    @Step("Кликнуть по тегу Important в панели тегов")
    public TagsSteps clickImportantTag() {
        try {
            $(elements.getImportantTag())
                    .shouldBe(visible, Duration.ofSeconds(10))
                    .click();
        } catch (Exception e) {
            $(elements.getTagElementContainer())
                    .$$(elements.getTagLabel())
                    .findBy(text("Important"))
                    .shouldBe(visible, Duration.ofSeconds(10))
                    .click();
        }
        return this;
    }

    @Step("Кликнуть по тегу Discuss в панели тегов")
    public TagsSteps clickDiscussTag() {
        try {
            $(elements.getDiscussTag())
                    .shouldBe(visible, Duration.ofSeconds(10))
                    .click();
        } catch (Exception e) {
            $(elements.getTagElementContainer())
                    .$$(elements.getTagLabel())
                    .findBy(text("Discuss"))
                    .shouldBe(visible, Duration.ofSeconds(10))
                    .click();
        }
        return this;
    }

    @Step("Кликнуть по тегу Done в панели тегов")
    public TagsSteps clickDoneTag() {
        try {
            $(elements.getDoneTag())
                    .shouldBe(visible, Duration.ofSeconds(10))
                    .click();
        } catch (Exception e) {
            $(elements.getTagElementContainer())
                    .$$(elements.getTagLabel())
                    .findBy(text("Done"))
                    .shouldBe(visible, Duration.ofSeconds(10))
                    .click();
        }
        return this;
    }

    @Step("Кликнуть по кнопке 'Закрыть' в панели тегов")
    public TagsSteps clickCloseTagMenuButton() {
        try {
            $(elements.getTagElementContainer())
                    .$$(elements.getTagLabel())
                    .findBy(text("Закрыть"))
                    .shouldBe(visible, Duration.ofSeconds(10))
                    .shouldBe(enabled, Duration.ofSeconds(10))
                    .click();
        } catch (Exception e) {
            $(elements.getTagMenuCloseButton())
                    .shouldBe(visible, Duration.ofSeconds(10))
                    .shouldBe(enabled, Duration.ofSeconds(10))
                    .click();
        }
        return this;
    }
}
