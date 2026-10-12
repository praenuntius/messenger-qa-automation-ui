package org.foxtrot.pages.chat.forward.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class ForwardMenu {
    private final By forwardMessageHeader = By.cssSelector("[data-cy='forwardsHeaderTitle']");
    private final By cancelButton = By.cssSelector("[data-cy='forwardsHeaderCancelButton']");
    private final By messageAuthorSettingsButton = By.cssSelector("[data-cy='forwardSettingsButton']");
    private final By showMessageAuthorButton = By.cssSelector("[data-cy='forwardMessageShowAuthorButton']");
    private final By showMessageAuthorTooltipHoverButton = By.cssSelector("[data-cy='forwardMessageShowAuthorButtonTooltip']");
    private final By hideMessageAuthorButton = By.cssSelector("[data-cy='forwardMessageHideAuthorButton']");
    private final By authorNameShouldBeHiddenText = By.cssSelector("[data-cy='forwardMessageShowAuthorButtonTooltipText']");
    private final By checkMarkElement = By.cssSelector("[data-cy='chatListItemCheckIcon']");
    private final By searchForRecipientsInputField = By.cssSelector("[data-cy='chatsForwardSearchInput']");
    private final By recipientsCounter = By.cssSelector("[data-cy='chatsForwardMessageSelection']"); //chatsForwardMessageSelection_{recipients_count}
    private final By specifiedRecipient = By.cssSelector("[data-cy='selectedChatTag']"); //selectedChatTag_{chat_name}
    private final By removeRecipientsButton = By.cssSelector("[data-cy='selectedChatTagRemoveButton']"); //selectedChatTagRemoveButton_{chat_name}
    private final By openForwardSpacesListButton = By.cssSelector("[data-cy='workspaceSelect']");

    private final By selectRecipientsMenu = By.cssSelector("div[class*='List']:has(div[data-cy='chatListItem_Сохраненные'])");
    private final By forwardRecipient = By.cssSelector("[data-forward-recipient='true']");
    private final By forwardMenuContainer = By.xpath(
            "//*[@data-cy='forwardsHeaderTitle']/ancestor::*[.//*[@data-cy='chatsForwardSearchInput']][1]"
    );

    private final By forwardMessageText = By.xpath(
            "//*[@data-cy='forwardsHeaderTitle']/ancestor::*[.//*[@data-cy='chatsForwardSearchInput']][1]" +
                    "//*[@data-message-text='true']/span[1]"
    );

    private final String DefinedUserWithNameAndAvatar = ("//div[@data-forward-recipient='true'][.//div[contains(@style, 'background')] and .//div[contains(text(), '{username}')]]");
    private final String DefinedUserWithAvatarPlaceholder = ("//div[@data-forward-recipient='true']//div[.//*[contains(@fill, 'rgba')] and .//div[text()='{username}']]");
    private final String DefinedUserButton = ("//div[@data-forward-recipient='true']//div[text() = '{username}']");

    public By getDefinedSpace(String spaceName) {
        return By.cssSelector(
                String.format("div[class*='selector__menu'] [data-cy='select%s']",
                        spaceName)
        );
    }

    public By getDefinedChat(String chatName) {
        return By.cssSelector(
                String.format("[data-cy='chatListItem_%s']",
                        chatName)
        );
    }

}
