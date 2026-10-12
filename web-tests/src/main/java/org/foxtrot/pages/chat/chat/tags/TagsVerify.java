package org.foxtrot.pages.chat.chat.tags;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.chat.tags.elements.Tags;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class TagsVerify {
    private final Tags elements = new Tags();

    @Step("Проверить, что счетчик добавленных тегов равен '{expectedCount}'")
    public TagsVerify verifyAddedTagsCounter(int expectedCount) {
        $(elements.getAddedTagsCounter())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(String.valueOf(expectedCount)));
        return this;
    }
}
