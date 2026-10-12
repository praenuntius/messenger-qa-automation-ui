package org.foxtrot.pages.chat.modals.elements;

import lombok.Getter;

@Getter
public class MentionModal {
    private String DefinedUserButton = "//div[contains(@class, 'mention')]//span[text() = '{userName}']";
    private String DefinedUserWithNameAndAvatar = "//ul[contains(@class, 'mention')]//li[.//div[contains(@style, 'background: url')] and .//div[contains(., '{username}')]]";
    private final String DefinedUserWithAvatarPlaceholder = ("//ul[contains(@class, 'mention')]//li[.//div[contains(@style, 'non_contact_avatar')] and .//div[contains(., '{username}')]]");
}