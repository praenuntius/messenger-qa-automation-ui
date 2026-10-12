package org.foxtrot.pages.spaces.homespace.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class NewChannelModal {
    private final By itemNameInput = cssSelector("[data-cy='createItemNameInput']");
    private final By itemDescriptionInput = cssSelector("[data-cy='createItemDescriptionInput']");
    private final By createItemNextButton = cssSelector("[data-cy='createItemNextButton']");
    private final By publicChannelRadio = cssSelector("label[for='publicChannel']");
    private final By privateChannelRadio = cssSelector("[data-cy='createClosedItemRadioButton']");
    private final By allowCommentsToggle = cssSelector("[data-cy='createChannelCommentsRadioButton']");
    private final By allowReactionToggle = cssSelector("[data-cy='createChannelReactionsRadioButton']");
}
