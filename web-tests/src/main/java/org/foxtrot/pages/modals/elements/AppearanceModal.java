package org.foxtrot.pages.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class AppearanceModal {
    private final By appearanceCancelButton = cssSelector("[data-cy='customizationModalCancelButton']");
    private final By appearanceApplyButton = cssSelector("[data-cy='customizationModalApplyButton']");
    private final By modalTitle = xpath("//span[contains(text(), 'Выбор обоев и узоров')]");
    private final By applyToAllChatsToggle = cssSelector("div[class*='ModalHeaderWithContentWrapper'] input[type='checkbox']");
    private final By modalPreviewPatternImage = cssSelector("div[class*='ModalHeader'] div[class*='Preview'] div[class*='PatternImage']");
    private final By modalPreviewBackgroundContainer = cssSelector("div[class*='ModalHeader'] div[class*='Preview'] div[class*='BackgroundContainer']");

    public By getWallpaperTile(int index) {
        return cssSelector("[data-cy='wallpaperTile" + index + "']");
    }

    public By getPatternTile(int index) {
        return cssSelector("[data-cy='patternTile" + index + "']");
    }
}
