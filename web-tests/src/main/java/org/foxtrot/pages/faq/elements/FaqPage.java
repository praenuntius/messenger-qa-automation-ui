package org.foxtrot.pages.faq.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class FaqPage {
    private final By faqTitleHeader = By.cssSelector("div[class*='ListTitle']");
}
