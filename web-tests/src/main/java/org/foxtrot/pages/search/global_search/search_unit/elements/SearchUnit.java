package org.foxtrot.pages.search.global_search.search_unit.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class SearchUnit {
    private final By backButton = xpath("//*[@id=\"root\"]/div[1]/div[2]/div/section[1]/div/button");
    private final By searchFilterButton = cssSelector("[data-cy='showGlobalSearchFilters']");
    private final By searchInputField = cssSelector("input[name='search-input']");
    private final By scrollableTagList = cssSelector("div[class*= 'SearchContainer'] div[class *= 'TabsWrapper']");
    private final By allEntitiesTab = xpath("//div[contains(@class, 'TabsScroll')]//div[contains(text(), 'Все')]");
    private final By peopleTab = xpath("//div[contains(@class, 'TabsScroll')]//div[contains(text(), 'Люди')]");
    private final By groupsTab = xpath("//div[contains(@class, 'TabsScroll')]//div[contains(text(), 'Группы')]");
    private final By channelsTab = xpath("//div[contains(@class, 'TabsScroll')]//div[contains(text(), 'Каналы')]");
    private final By messagesTab = xpath("//div[contains(@class, 'TabsScroll')]//div[contains(text(), 'Сообщения')]");
    private final By mediaTab = xpath("//div[contains(@class, 'TabsScroll')]//div[contains(text(), 'Медиа')]");
    private final By filesTab = cssSelector("//div[contains(@class, 'TabsScroll')]//div[contains(text(), 'Файлы')]");
    private final By linksTab = xpath("//div[contains(@class, 'TabsScroll')]//div[contains(text(), 'Ссылки')]");
    private final By defaultChatsIconInFilter = By.cssSelector("section[class*='SearchHead'] div[class*='OneAvatarWrapper']");
    private final By clearInputButton = By.cssSelector("div[class*='ClearButton']");


    public By getSpaceIconInFilter(String spaceName) {
        return By.cssSelector(
                String.format("[data-cy='sidebarWorkspaceButton%s']", spaceName)
        );
    }
}
