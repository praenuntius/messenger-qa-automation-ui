package org.foxtrot.pages.spaces.homespace.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class NewGroupModal {

    private final By itemNameInput = cssSelector("[data-cy='createItemNameInput']");
    private final By itemDescriptionInput = cssSelector("[data-cy='createItemDescriptionInput']");
    private final By createItemNextButton = cssSelector("[data-cy='createItemNextButton']");
    private final By publicGroupRadio = cssSelector("label[for='publicGroup']");
    private final By privateGroupRadio = cssSelector("label[for='privateGroup']");
    private final By uploadImage = xpath("//span[text() = 'Загрузить']");
    private final By fileInput = cssSelector("input[type='file']");
    private final By allowReactionToggle = cssSelector("[data-cy='createChannelReactionsRadioButton']");
    private final By allowCommentsToggle = cssSelector("[data-cy='createGroupCommentsSwitchButton']");
}
