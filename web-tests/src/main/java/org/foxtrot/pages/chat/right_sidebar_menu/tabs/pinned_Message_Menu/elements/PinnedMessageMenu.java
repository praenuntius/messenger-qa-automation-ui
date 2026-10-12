package org.foxtrot.pages.chat.right_sidebar_menu.tabs.pinned_Message_Menu.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class PinnedMessageMenu {

    private final By pinnedMessagesSidebar = cssSelector("div [class*='PinsWrapper']");
    private String pinnedMessageFromUser = "//div[contains(@id, 'pinned')]//span[text() = '{username}']";
    private final String DefinedUserWithNameAndAvatar = ("//div[contains(@class, 'pinned')]//div[.//div[contains(@style, 'background')] and .//span[text()='{username}']]");
    private final String DefinedUserWithAvatarPlaceholder = ("//div[contains(@class, 'pinned')]//div[.//*[contains(@fill, 'rgba')] and .//span[text()='{username}']]");

    public By getPinnedMessageById(String messageId) {
        return cssSelector(String.format("[id='pinned-%s']", messageId));
    }

    public By getGoToMessageButtonByPinnedMessageId(String messageId) {
        return xpath(String.format(
                "//div[@id='pinned-%s']//span[contains(text(),'Перейти к сообщению')]",
                messageId
        ));
    }
}