package org.foxtrot.pages.chat.right_sidebar_menu.tabs.members_menu.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class MembersMenu {

    private final By closeMembersMenu = cssSelector("div[class*= 'SearchPanel'] button");
    private final By memberCard = cssSelector("[data-cy='member-card']");
    private final By contextMenuButton = cssSelector("div[class *= 'List'] div[class *= 'MenuContent']");
    private final By makeAdminButton = xpath("//div[text() = 'Сделать администратором группы']");
    private final By deleteFromGroupButton = cssSelector("[data-cy='delete-from-group']");
    private final By addMembersButton = xpath("//span[text() = 'Добавить участников']");
    private final By contactsSearchInput = cssSelector("div[class *= 'SearchPanelWrapper'] span [class*= 'SearchInput']");
    private final String definedUserButton = ("//div[contains(@class, 'ParticipantsListContainer')]//div[text() = '{username}']");
    private final By groupWithoutOwnerBanner = xpath("//span[contains(text(), 'без владельца')]");
    private final By informationButton = cssSelector("div[class*= 'InfoIconButton']");
    private final By infoTooltip = cssSelector("div[class*= 'InfoCardWrapper']");
    private final By addUserInput = cssSelector("[data-cy='addUserInput']");
}
