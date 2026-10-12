package org.foxtrot.pages.search.global_search.filter_unit.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class FilterUnit {
 private final By spacesButton = By.cssSelector("[id='radix-_r_3q_']");
 private final By tagsButton = By.cssSelector("[id='radix-_r_3t_']");
 private final By resetFiltersButton = By.xpath("//*[@id=\"radix-_r_38_\"]/div[4]");
 private final By selectAllSpaces = By.xpath("//*[@id=\"radix-_r_3p_\"]/div/div/div[2]");
 private final By cancelButton = By.xpath("//*[@id=\"radix-_r_3p_\"]/div/div/div[4]/button[1]/span/span");
 private final By applyButton = By.xpath("//*[@id=\"radix-_r_3p_\"]/div/div/div[4]/button[2]/span/span");
 private final By spaceItemFromList = By.xpath("//*[@id=\"radix-_r_3p_\"]/div/div/div[3]/div/div/div[29]/div/span[2]");
}
