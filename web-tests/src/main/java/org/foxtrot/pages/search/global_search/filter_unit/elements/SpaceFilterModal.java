package org.foxtrot.pages.search.global_search.filter_unit.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class SpaceFilterModal {

    private final By applyButton = By.xpath("//span[text()='Применить']/ancestor::button ");
    private final By listOfWorkspace = By.cssSelector("div[class*='ListOfWorkspaces']");
    private final By selectAllWrapper = By.cssSelector("div[class*='SelectAllWrapper'] label");
    private final By selectAllCheckboxIconUse = By.cssSelector("div[class*='SelectAllWrapper'] use");

    public By getWorkspaceRowByName(String workspaceName) {
        return By.xpath(
                String.format(
                        "//div[contains(@class, 'WorkspaceItem')][.//span[contains(text(), '%s')]]", workspaceName)
        );
    }

    public By getWorkspaceCheckmark() {
        return By.cssSelector("svg[class*='CheckIcon']");
    }

    public By getSpaceNameItemLocator() {
        return By.xpath("//div[contains(@class, 'Workspace')]//span[contains(@class, 'Subtitle')]");
    }
}
