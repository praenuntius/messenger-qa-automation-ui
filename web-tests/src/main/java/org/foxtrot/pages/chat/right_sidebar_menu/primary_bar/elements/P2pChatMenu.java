package org.foxtrot.pages.chat.right_sidebar_menu.primary_bar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class P2pChatMenu {

    private final By closeMenuButton = xpath("//button[contains(@class, 'CloseButtonWrapper')]");
    private final By userName = cssSelector("[data-cy='userTitleText']");
    private final By userPhoto = cssSelector("div[class*='HeaderBlock'] div[style*='background']");
    private final By userAvatarPlaceholder = cssSelector("div[class*='HeaderBlock'] div[class*='Avatar'] svg");
    private final By headerBlock = cssSelector("div[class*='HeaderBlock']");
    private final By userJobTitle = cssSelector("[data-cy='jobTitleText']");
    private final By messageButton = cssSelector("[data-cy='openP2PChatButton']");
    private final By callButton = cssSelector("[data-cy='startVideoConferenceProfileButton']");
    private final By copyPhoneNumberButton = cssSelector("[data-cy='copyPhoneButton']");
    private final By notificationsToggle = cssSelector("[data-cy='switchNotificationsRadioButton']");
    private final By attachmentsButton = cssSelector("[data-cy='openGalleryButton']");
    private final By pinnedMessagesButton = cssSelector("[data-cy='showPinnedMessagesButton']");
    private final By appearanceButton = xpath("//span[text() = 'Оформление чата']");
    private final By copyProfileLinkButton = cssSelector("[data-cy='profileLinkCopyButton']");
    private final By autodeleteMessagesButton = cssSelector("[data-cy='expandAutodeleteSettingsButton']");
    private final By addContactButton = cssSelector("[data-cy='addToContactsUserButton']");
    private final By deleteContactButton = cssSelector("[data-cy='removeFromContactsButton']");
    private final By blockUserButton = cssSelector("[data-cy='blockUserButton']");
    private final By unblockUserButton = cssSelector("[data-cy='unblockUserButton']");
    private final By clearChatHistoryButton = cssSelector("[data-cy='clearP2PChatHistoryButton']");
    private final By copyPhoneButton = cssSelector("[data-cy='copyPhoneButton']");
    private final By hideButton = cssSelector("[data-cy='hideButton']");
    private final By showMoreButton = cssSelector("[data-cy='showMoreButton']");
}