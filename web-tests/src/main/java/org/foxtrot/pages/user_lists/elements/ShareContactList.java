package org.foxtrot.pages.user_lists.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class ShareContactList {
    private final By doneButton = cssSelector("[data-cy='shareContactsSendButton']");
    private final By closeModalButton = cssSelector("[data-cy='createGroupAddPeopleCloseButton']");
    private final By backModalButton = cssSelector("[data-cy='createGroupAddPeopleBackButton']");
    private final By allContactNames =cssSelector("div[class*='PersonListItem']");
    private final By searchInput = cssSelector("div[class *='InputWrapper'] [data-cy *='SearchUserInput']");
    private final By emptySearchResultMessage = cssSelector("div[class*= 'NotFoundContainer']");
    private final By emptyContactResultMessage = cssSelector("div[class*= 'NoContactWrapper']");
    private final By selectedCheckbox = cssSelector("svg[class*='CheckIcon']");
    private final By selectedContactChip = cssSelector("div[class *='ChipsWrapper']");
    private final String DefinedUserButton = "//div[contains(@class, 'PersonListItem')][.//div[text()='{username}']]";

}
