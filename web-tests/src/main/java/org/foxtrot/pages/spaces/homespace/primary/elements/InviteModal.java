package org.foxtrot.pages.spaces.homespace.primary.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class InviteModal {
    private final By inviteLinkTab = cssSelector("div[data-cy='workspaceInviteUserTabLink']");
    private final By copyLinkButton = cssSelector("div[data-cy='workspaceInviteUserTabLinkCopy']");
    private final By copySuccessMessage = xpath("//div[text() = 'Скопировано']");
    private final By closeButton = cssSelector("button[data-cy*='CloseButton']");
}
