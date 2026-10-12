package org.foxtrot.pages.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class FormatTextWithLinkModal {
    private final By modalContainer = By.cssSelector("[data-cy='formatLinkModal']");
    private final By doneButton = By.cssSelector("[data-cy='formatLinkModalSubmitButton']");
    private final By cancelButton = By.cssSelector("[data-cy='createGroupCancelButton']");
    private final By urlInputField = By.cssSelector("[data-cy='formatLinkModalInput']");
    private final By outsideModalArea = By.cssSelector("[data-cy='dialogOverlay']");
    private final By messageTextToBeFormatted = By.cssSelector("[data-cy='formatLinkModalLinkView']");
}
