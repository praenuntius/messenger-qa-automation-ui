package org.foxtrot.pages.chat.right_sidebar_menu.tabs.subscribers_menu.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class SubscribersMenu {

    private final By memberCard = cssSelector("[data-cy='member-card']");
    private final By contextMenuButton = cssSelector("div[class *= 'List'] div[class *= 'MenuContent']");
    private final By makeAdminButton = xpath("//div[text() = 'Сделать администратором']");
    private final By deleteFromGroupButton = cssSelector("[data-cy='delete-from-group']");
    private final By subscribersSearchInput = cssSelector("div[class *='SearchPanel'] input[class*='SearchInput']");
    private final By closeSubscribersMenuButton = cssSelector("div[class*= 'SearchPanel'] button[class*='Close']");
    private final String DefinedUserButton = ("//div[contains(@class, 'ParticipantsListContainer')]//div[text() = '{username}']");
    private final String DefinedUserWithNameAndAvatar = ("//div[contains(@class, 'ParticipantsListContainer')]//div[.//div[contains(@style, 'background')] and .//div[text()='{username}']]");
    private final String DefinedUserWithAvatarPlaceholder = ("//div[contains(@class, 'ParticipantsListContainer')]//div[.//*[contains(@fill, 'rgba')] and .//div[text()='{username}']]");
    private final By addSubscribersButton = xpath("//span[text() = 'Добавить подписчиков']");
    private final By addUserInput = cssSelector("[data-cy='addUserInput']");
}
