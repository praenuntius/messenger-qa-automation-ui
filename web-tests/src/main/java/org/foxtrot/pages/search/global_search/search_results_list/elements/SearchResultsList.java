package org.foxtrot.pages.search.global_search.search_results_list.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class SearchResultsList {
    private final By noEntitiesFoundText = xpath("//*[@id=\"root\"]/div[1]/div[2]/div/div[2]/div[2]/div/div/div/div/div");
    private final By itemFound = xpath("//*[@id=\"root\"]/div[1]/div[2]/div/div[2]/div[2]/div/div/div[2]/div[2]/div[1]/div/div");
    private final By messageItems = cssSelector("div[class*='MessageItemWrapper']");
    private final By messageText = cssSelector("div[class*='MessageItemWrapper'] div[class*='MessageInfoText']");
    private final String definedMessageItemByText = "//div[contains(@class,'MessageItemWrapper') and .//*[contains(normalize-space(.), '{text}')]]";
    private final By NoResultsFoundMessage = xpath("//div[text() = 'Ничего не найдено']");
    private final By groupItemByName = xpath("//div[contains(@class, 'GroupItemWrapper') and .//div[text()='%s']]");
    private final By searchResultNameText= xpath("//div[contains(@class, 'GroupItemWrapper')]//div[contains(@class, 'Name')]");
    private final By linkContextMenuButton= cssSelector("div[class *='SearchContainer'] div[aria-haspopup='menu']");
    private final By groupAvatarWrapper = xpath(".//div[contains(@class, 'GroupAvatarWrapper')]");
    private final By groupItemWrapperAncestor = xpath("ancestor::*[contains(@class, 'GroupItemWrapper')]");

    public String getGroupItemByName(String groupName) {
        return String.format("//div[contains(@class, 'GroupItemWrapper') and .//div[text()='%s']]", groupName);
    }

    public String getSectionContainerLocator(String sectionTitle) {
        return String.format("//div[text()='%s']/ancestor::div[contains(@class, 'TitleOfSearchResult')]",
                sectionTitle);
    }

    public By getEntityByGroupName(String groupName) {
        return xpath(
                String.format("//div[contains(@class,'SearchContainer')]//div[text() = '%s']",
                        groupName)
        );
    }

    public By getEntityByMessageText(String message) {
        return xpath(
                String.format("//div[contains(@class,'MessageItemWrapper')]//span[text() = '%s']",
                        message)
        );
    }

    public By getGroupInHomeSpace(String groupName) {
        return xpath(
                String.format("//div[contains(@class,'ItemWrapper') and .//div[contains(@class,'HomeAvatarWrapper')] and contains(.,'%s')]",
                groupName));
    }

    public By getGroupInWorkspace(String workspaceName, String groupName) {
        return xpath(
                String.format("//div[contains(@class,'ItemWrapper') and .//*[@data-cy='sidebarWorkspaceButton%s'] and contains(.,'%s')]",
                workspaceName, groupName));
    }
}
