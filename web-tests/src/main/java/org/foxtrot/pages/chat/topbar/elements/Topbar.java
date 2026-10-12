package org.foxtrot.pages.chat.topbar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class Topbar {
    private final By searchButton = cssSelector("[data-cy='channelHeaderSearch']");
    private final By StartCallButton = cssSelector("[data-cy='channelHeaderStartCall']");
    private final By openVideoConferenceChat = cssSelector("[data-cy='openVideoConferenceChatButton']");
    private final By preferencesMenuButton = cssSelector("[data-cy='channelHeaderMenu']");
    private final By groupHeader = cssSelector("[data-cy='group_chatHeader']");
    private final By channelHeader = cssSelector("[data-cy='info_channel_chatHeader']");
    private final By p2pChatHeader = cssSelector("[data-cy='p2p_chatHeader']");
    private final By chatTitle = cssSelector(
            "header[data-cy$='_chatHeader'] div[class*='TitleWrapper'] div[class*='Body1'], " +
                    "header[data-cy$='_chatHeader'] div[class*='Info'] div[class*='Body1']"
    );
    private final By savedChatTitle = xpath("//div[contains(@class, 'TitleWrapper')]//div[contains(@class, 'Body1') and contains(text(), 'Сохраненные')]");
    private final By savedChatTitleFallback = xpath("//div[contains(@class, 'Body1') and contains(text(), 'Сохраненные')]");
    private final By PinnedMessagesMenuButton = cssSelector("[data-cy='channelHeaderPin']");
    private final By pinnedMessagesUnreadBadge = cssSelector(
            "[data-cy='channelHeaderPin'] div[class*='NewPinnedMessageBadge'], "
                    + "[data-cy='showPinnedMessagesButton'] div[class*='NewPinnedMessageBadge']");
    private final By userPhoto = cssSelector("header[class*='HeaderView'] div[style*='background']");
    private final By userAvatarPlaceholder = cssSelector("header[class*='HeaderView'] div[class*='Avatar'] svg");
    private String definedTypingIndicator = "//div[contains(@class, 'TitleWrapper')]//span[text() = '{username}']";
}
