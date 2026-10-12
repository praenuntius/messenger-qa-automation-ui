package org.foxtrot.pages.left_sidebar.settings.setting_tabs.elements;

import lombok.Getter;

@Getter
public class BlackListTab {
    private final String definedUserWithNameAndAvatar = ("//div[contains(@class, 'BlackListItem')]//div[.//div[contains(@style, 'background: url')] and .//div[text()='{username}']]");
    private final String definedUserWithAvatarPlaceholder = ("//div[contains(@class, 'BlackListItem')]//div[.//*[contains(@fill, 'rgba')] and .//div[text()='{username}']]");
    private final String definedUserButton = ("//div[contains(@class, 'SettingsBlackList')]//div[text() = '{username}']");

}
