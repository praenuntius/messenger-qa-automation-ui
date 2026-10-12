package org.foxtrot.pages.chat.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class ScheduledMessageContextMenu {
    private final By sendNowButton = By.cssSelector("[data-cy='menuAction_goToMessage']");
    private final By changTime = By.cssSelector("[data-cy='menuAction_editTime']");
    private final By copyText = By.cssSelector("[data-cy='menuAction_message-copy']");
    private final By deleteButton = By.cssSelector("[data-cy='menuAction_message-delete']");
    private final By downloadButton = By.cssSelector("[data-cy='menuAction_message-download-media']");
}
