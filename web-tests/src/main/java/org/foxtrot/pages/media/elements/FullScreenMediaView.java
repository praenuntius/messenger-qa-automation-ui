package org.foxtrot.pages.media.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class FullScreenMediaView {
    private final By fullscreenDialog = By.cssSelector("[data-cy='dialogOverlay']");
    private final By videoController = By.cssSelector("div[class*='VideoControllerContainer']");
    private final String definedUserWithNameAndAvatar = ("//div[contains(@class, 'MiddleSection')]//div[.//div[contains(@style, 'background: url')] and .//div[text()='{username}']]");
    private final String definedUserWithAvatarPlaceholder = ("//div[contains(@class, 'MiddleSection')]//div[.//*[contains(@fill, 'rgba')] and .//div[text()='{username}']]");
    private final String userName = ("//div[contains(@class, 'MiddleSection')] //div[text()='{username}']");
}
