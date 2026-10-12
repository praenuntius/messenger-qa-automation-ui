package org.foxtrot.pages.chat.chat.multiselect_messages_menu.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class MultiselectMessages {
    private final By selectedMessagesCounter = By.cssSelector("[data-cy='selectedMessagesCounter']");
    private final By cancelButton = By.cssSelector("[data-cy='selectedMessagesCancelButton']");
    private final By deleteButton = By.cssSelector("[data-cy='selectedMessagesActionDelete']");
    private final By forwardButton = By.cssSelector("[data-cy='selectedMessagesActionForward']");
    private final By saveButton = By.cssSelector("[data-cy='selectedMessagesActionSave']");
    private final By pinButton = By.cssSelector("[data-cy='selectedMessagesActionPin']");
    private final By multiselectMessagesMarker = By.cssSelector("[data-cy='messageSelectMarker']");
    private final By selectedMessageRow = By.cssSelector("[class*='BaseMessageView']:has([data-selected='true'])");
}
