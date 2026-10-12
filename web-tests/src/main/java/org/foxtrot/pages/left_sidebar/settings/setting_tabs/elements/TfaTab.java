package org.foxtrot.pages.left_sidebar.settings.setting_tabs.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class TfaTab {

    private final By globalSettingsAuthenticationSetPasswordButtonSelector = cssSelector("[data-cy='globalSettingsAuthenticationSetPassword']");
    private final By globalSettingsAuthenticationChangePasswordButtonSelector = cssSelector("[data-cy='globalSettingsAuthenticationChangePassword']");
    private final By globalSettingsAuthenticationChangeEmailButtonSelector = cssSelector("[data-cy='globalSettingsAuthenticationChangeEmail']");
}
