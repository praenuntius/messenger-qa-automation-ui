package org.foxtrot.pages.chat.chat.tags.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class TagOnMessageContextMenu {
    public final By tagContextMenuHeader = By.xpath("//*[@id=\"radix-_r_1e_\"]/div/div/div/span"); //need a method to find the text message in the elenment
    public final By tagGlobalSearchButton = cssSelector("[id='tag_global_search']");
    public final By tagLocalSearchButton = cssSelector("[id='tag_group_search']");
    public final By tagCopyButton = cssSelector("[id='tag_copy']");
    public final By tagRemoveButton = cssSelector("[id='tag_remove']");
}
