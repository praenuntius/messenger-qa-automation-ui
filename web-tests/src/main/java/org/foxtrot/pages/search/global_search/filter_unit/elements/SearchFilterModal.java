package org.foxtrot.pages.search.global_search.filter_unit.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class SearchFilterModal {
    private final By spacesButton = By.xpath("//div[contains(@role, 'menuitem') and .//span[text()='Пространства']]");
    private final By tagsButton = By.cssSelector("//div[contains(@role, 'menuitem') and .//span[text()='Метки']]");
}