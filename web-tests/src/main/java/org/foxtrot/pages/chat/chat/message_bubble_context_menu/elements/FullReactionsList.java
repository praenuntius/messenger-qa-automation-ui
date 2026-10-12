package org.foxtrot.pages.chat.chat.message_bubble_context_menu.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class FullReactionsList {

    private final By reactionsListContainer = cssSelector("[class*='Items-sc-jrw7r1-8']");
    private final By reactionsListContainerByRole = cssSelector("[role='menu'][data-radix-menu-content]");
    private final By reactionsListContainerByClass = cssSelector("[class*='ContextMenuContent']");
    private final By reactionsListContainerById = cssSelector("[id='radix-_r_g_']");
    private final By reactionsListContainerByContainerClass = cssSelector("[class*='Container-sc-jrw7r1-0']");
    private final By reactionsListCloseButton = cssSelector("[data-cy='reactionsListCloseButton']");

    public By getReactionByName(String reactionName) {
        return cssSelector("[data-cy='reaction-" + reactionName + "']");
    }

    public By getReactionByEmoji(String emoji) {
        return xpath(".//button[normalize-space(.)='" + emoji + "']");
    }
}

