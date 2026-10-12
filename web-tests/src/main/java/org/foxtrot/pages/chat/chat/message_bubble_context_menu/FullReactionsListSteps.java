package org.foxtrot.pages.chat.chat.message_bubble_context_menu;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.chat.message_bubble_context_menu.elements.FullReactionsList;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class FullReactionsListSteps {
    private final FullReactionsList elements = new FullReactionsList();

    @Step("Проверить, что полный список реакций отображается")
    public FullReactionsListSteps verifyReactionsListVisible() {
        try {
            $(elements.getReactionsListContainer())
                    .shouldBe(visible, Duration.ofSeconds(5));
        } catch (Exception e) {
            try {
                $(elements.getReactionsListContainerByContainerClass())
                        .shouldBe(visible, Duration.ofSeconds(5));
            } catch (Exception e2) {
                $(elements.getReactionsListContainerByRole())
                        .shouldBe(visible, Duration.ofSeconds(10));
            }
        }
        return this;
    }

    private com.codeborne.selenide.SelenideElement getVisibleReactionsContainer(Duration timeout) {
        By[] candidates = new By[] {
                elements.getReactionsListContainer(),
                elements.getReactionsListContainerByContainerClass(),
                elements.getReactionsListContainerByClass(),
                elements.getReactionsListContainerByRole(),
                elements.getReactionsListContainerById()
        };

        RuntimeException lastError = null;
        for (By locator : candidates) {
            try {
                return $(locator).shouldBe(visible, timeout);
            } catch (RuntimeException e) {
                lastError = e;
            }
        }
        if (lastError != null) {
            throw lastError;
        }
        throw new AssertionError("Reactions list container not found");
    }

    @Step("Выбрать реакцию по эмодзи '{emoji}'")
    public FullReactionsListSteps selectReactionByEmoji(String emoji) {
        var container = getVisibleReactionsContainer(Duration.ofSeconds(30));
        var reactionButton = container.$(elements.getReactionByEmoji(emoji))
                .shouldBe(visible, Duration.ofSeconds(30))
                .scrollIntoView(true);

        executeJavaScript("arguments[0].click();", reactionButton.toWebElement());
        
        return this;
    }
}


