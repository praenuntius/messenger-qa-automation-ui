package org.foxtrot.pages.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter

public class OpenTheLinkModal {
    private final By modalContainer = By.cssSelector("[data-cy='openLinkConfirmationModal']");
    private final By outsideModalArea = By.cssSelector("[data-cy='dialogOverlay']");
    private final By cancelButton = By.cssSelector("[data-cy='openLinkConfirmationModalDeclineButton']");
    private final By openButton = By.cssSelector("[data-cy='openLinkConfirmationModalSubmitButton']");
    private final By linkToBeOpened = By.cssSelector("[data-cy='openLinkConfirmationModalLinkText']");
}
