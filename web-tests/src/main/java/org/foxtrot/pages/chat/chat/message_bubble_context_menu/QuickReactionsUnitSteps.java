package org.foxtrot.pages.chat.chat.message_bubble_context_menu;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.chat.message_bubble_context_menu.elements.QuickReactionsUnit;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class QuickReactionsUnitSteps {
    private final QuickReactionsUnit elements = new QuickReactionsUnit();
    private String lastClickedReactionEmoji;

    @Step("Кликнуть по кнопке [+], открыть полный список реакций")
    public QuickReactionsUnitSteps clickQuickReactionAddButton() {
        $$(elements.getOpenFullReactionListButton())
                .filter(Condition.visible)
                .first()
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по реакции '{emoji}' в быстрых реакциях")
    public QuickReactionsUnitSteps clickReactionByEmoji(String emoji) {
        var emojiButton = $$(elements.getEmojiButtons())
                .filterBy(visible)
                .findBy(text(emoji))
                .scrollIntoView(true);
        lastClickedReactionEmoji = emoji;
        executeJavaScript("arguments[0].click();", emojiButton.toWebElement());
        return this;
    }

    @Step("Кликнуть по реакции 'thumb-up'")
    public QuickReactionsUnitSteps clickThumbUpReactionButton() {
        return clickReactionByEmoji("👍");
    }

    @Step("Кликнуть по первой доступной реакции в контекстном меню")
    public QuickReactionsUnitSteps clickFirstAvailableReactionButton() {
        var reactionButtons = $$(elements.getEmojiButtons())
                .filterBy(visible);

        if (reactionButtons.size() > 0) {
            var firstReactionButton = reactionButtons.first()
                    .scrollIntoView(true);
            lastClickedReactionEmoji = firstReactionButton.getText();
            executeJavaScript("arguments[0].click();", firstReactionButton.toWebElement());
        } else {
            throw new RuntimeException("No available reaction buttons found in context menu");
        }
        return this;
    }

    public String getLastClickedReactionEmoji() {
        return lastClickedReactionEmoji;
    }
}
