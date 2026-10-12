package org.foxtrot.pages.chat.topbar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class ChatHeaderPreferencesMenu {

    private final By menuContainer = cssSelector("div[class*='DropdownMenuContent']");
    private final By exportHistoryButton = xpath("//div[contains(text(),'Экспорт истории чата')]");
}
