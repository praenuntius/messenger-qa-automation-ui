package org.foxtrot.pages.user_lists.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class AddPeopleList {

    private final By doneButton = cssSelector("[data-cy='createItemAddUsersButton']");
    private final By closeModalButton = cssSelector("[data-cy='createGroupAddPeopleCloseButton']");
    private final By backModalButton = cssSelector("[data-cy='createGroupAddPeopleBackButton']");
    private final String userListCard = "[data-cy='contactlistselector']";
    private final By searchInput = cssSelector("div[class *='InputWrapper'] [data-cy *='SearchUserInput']");
    private final String DefinedUserButton = "//div[contains(@class, 'PersonList')]//div[text() = '{username}']";

}
