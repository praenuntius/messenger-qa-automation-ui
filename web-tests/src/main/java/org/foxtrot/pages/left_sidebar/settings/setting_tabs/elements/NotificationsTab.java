package org.foxtrot.pages.left_sidebar.settings.setting_tabs.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class NotificationsTab {

    private final By globalSettingsNotificationsEnableAllToggleSelector = cssSelector("[data-cy='globalSettingsNotificationsEnableAll']");
    private final By globalSettingsNotificationsPushToggleSelector = cssSelector("[data-cy='globalSettingsNotificationsPush']");
    private final By globalSettingsNotificationsSoundToggleSelector = cssSelector("[data-cy='globalSettingsNotificationsSound']");
    private final By unreadMessagesCounterModeButton = cssSelector("[data-cy='globalSettingsNotificationsMessages']");
    private final By unreadChatsCounterModeButton = cssSelector("[data-cy='globalSettingsNotificationsGroups']");
}
