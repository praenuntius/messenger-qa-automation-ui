package org.foxtrot.pages.left_sidebar.settings.setting_tabs.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class StorageTab {

    private final By globalSettingsPrivacyHistoryClearModalButtonSelector = cssSelector("[data-cy='globalSettingsPrivacyHistoryClear']");
}
