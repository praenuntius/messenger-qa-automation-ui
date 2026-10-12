package org.foxtrot.pages.search.global_search.search_results_list.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.xpath;

@Getter
public class LinksContextMenu {
    private final By openInBrowserButton = xpath("//div[text()='Открыть в браузере']");
    private final By goToMessageButton = xpath("//div[text()='Перейти к сообщению']");
    private final By copyButton = xpath("//div[text()='Копировать']");
    private final By forwardButton = xpath("//div[text()='Переслать']");
}