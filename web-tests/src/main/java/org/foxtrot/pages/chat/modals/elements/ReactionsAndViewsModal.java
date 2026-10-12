package org.foxtrot.pages.chat.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class ReactionsAndViewsModal {
    private final By closeButton = By.xpath("div[aria-label*='Реакции'] button[class*='IconButtonNone']");
    private final String userName = "//div[contains(@class, 'VerticalScroll')] //div[text() = '{username}']";
    private final String definedUserWithNameAndAvatar = ("//div[contains(@class, 'Avatar') and .//div[contains(@style, 'background')] and .//div[text()='{username}']]");
    private final String definedUserWithAvatarPlaceholder = ("//div[contains(@class, 'Avatar') and .//*[contains(@fill, 'rgba')] and .//*[text()[contains(., '{username}')]]]");
}
