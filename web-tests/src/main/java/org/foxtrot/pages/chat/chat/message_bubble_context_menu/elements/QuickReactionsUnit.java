package org.foxtrot.pages.chat.chat.message_bubble_context_menu.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class QuickReactionsUnit {
    private final By quickEmojiContainer = By.cssSelector("[data-cy='emojisContainer']");
    private final By openFullReactionListButton = By.cssSelector("[data-cy='emojiPlusButton']");
    private final By emojiButtons = By.cssSelector("[data-cy='emojiButton']");
}
