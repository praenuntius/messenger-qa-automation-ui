package org.foxtrot.pages.left_sidebar.settings.primary.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class SettingsMenu {
    private final By NotificationsButton = cssSelector("[data-cy='globalSettingsnotifications']");
    private final By DesignButton = cssSelector("[data-cy='globalSettingsdesign']");
    private final By LanguageButton = cssSelector("[data-cy='globalSettingslanguage']");
    private final By PrivacyButton = cssSelector("[data-cy='globalSettingsprivacy']");
    private final By StorageButton = cssSelector("[data-cy='globalSettingsstorage']");
    private final By AuthenticationButton = cssSelector("[data-cy='globalSettingsauthentication']");
    private final By DevicesButton = cssSelector("[data-cy='globalSettingsdevices']");
    private final By BlackListButton = cssSelector("[data-cy='globalSettingsblackList']");
    private final By NewsButton = cssSelector("[data-cy='globalSettingsnews']");
    private final By ExitButton = cssSelector("[data-cy='globalSettingsexit']");
    private final By settingsTitleHeader = xpath("//span[text() = 'Настройки приложения']");
    private final By closeSettingsButton = cssSelector("div[class*='PanelHeader'] button");
}
