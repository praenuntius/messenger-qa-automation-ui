package org.foxtrot.pages.chat.right_sidebar_menu.tabs.admins_menu.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class AdminsMenu {

    private final By searchInput = cssSelector("div[class*='PanelWrapper'] span [class *='SearchInput']");
}
