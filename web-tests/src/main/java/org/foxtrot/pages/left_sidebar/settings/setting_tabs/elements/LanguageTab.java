package org.foxtrot.pages.left_sidebar.settings.setting_tabs.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class LanguageTab {

    private final By globalSettingsLanguageRuRadiobuttonSelector = cssSelector("[data-cy='globalSettingsLanguageRu']");
    private final By globalSettingsLanguageEnRadiobuttonSelector = cssSelector("[data-cy='globalSettingsLanguageEn']");
}
