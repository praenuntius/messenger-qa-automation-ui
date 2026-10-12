package org.foxtrot.pages.spaces.workspace.left_sidebar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class GroupAndChannelMenu {
    public final String DefinedGroupName = "//div[contains(@class, 'VerticalScroll')]//div[text() = '{GroupName}']";
    public final By searchButton = By.cssSelector("div[class*='HeaderWrapper'] > button[class*='HeaderIconButton']");
}