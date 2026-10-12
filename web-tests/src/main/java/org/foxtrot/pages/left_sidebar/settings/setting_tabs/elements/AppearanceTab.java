package org.foxtrot.pages.left_sidebar.settings.setting_tabs.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class AppearanceTab {

    private final By themeDarkRadiobutton = cssSelector("input[id='theme-dark']");
    private final By themeLightRadiobutton = cssSelector("input[id='theme-light']");
    private final By themeFirstRadiobutton = cssSelector("[data-cy='globalSettingsDesignVariantThemeFirst']");
    private final By themeSecondRadiobutton = cssSelector("[data-cy='globalSettingsDesignVariantThemeSecond']");
    private final By themeThirdRadiobutton = cssSelector("[data-cy='globalSettingsDesignVariantThemeThird']");
    private final By textSizeScale = cssSelector("[data-cy='globalSettingsDesignTextSize']");
    private final By asContactRadiobutton = cssSelector("[data-cy='globalSettingsDesignAsContact']");
    private final By nonContactRadiobutton = cssSelector("[data-cy='globalSettingsDesignNonContact']");
    private final By wallpaperSettingsThemeIcon = cssSelector("div[class*='ButtonWrapper'] svg");
    private final By wallpaperSettingsBlockButton = xpath("//div[text()='Выбрать обои и узор']");
    private final By resetWallpaperButton = xpath("//div[text()='Сбросить']");
    private final By settingsPreviewPatternImage = cssSelector("div[class*='SettingsPanel'] div[class*='PatternImage']"
    );
}
