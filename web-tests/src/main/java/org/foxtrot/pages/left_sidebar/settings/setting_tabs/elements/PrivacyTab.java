package org.foxtrot.pages.left_sidebar.settings.setting_tabs.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class PrivacyTab {

    private final By globalSettingsPrivacyPhoneVisibleToggleSelector = cssSelector("[data-cy='globalSettingsPrivacyPhoneVisible']");
    private final By globalSettingsPrivacyPhoneInVisibleToggleSelector = cssSelector("[data-cy='globalSettingsPrivacyPhoneInVisible']");
    private final By globalSettingsPrivacyHistoryClearButtonSelector = cssSelector("[data-cy='globalSettingsPrivacyHistoryClear']");
}
