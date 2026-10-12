package org.foxtrot.pages.search.local_search.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class LocalSearchUnit {
    private final By searchHeader = By.cssSelector("div[class*='HeaderLine']");
    private final By inputField = By.cssSelector("[placeholder='Поиск']");
    private final By searchResultMessageText = By.cssSelector(
            "[data-cy^='chatPreviewMessageText'], [data-cy^='channelPreviewMessageText']"
    );
    //private final By filterButton = By.xpath("//*[@id=\"root\"]/div[1]/aside/div/div/section/div[2]/div/div[2]/svg");
}
