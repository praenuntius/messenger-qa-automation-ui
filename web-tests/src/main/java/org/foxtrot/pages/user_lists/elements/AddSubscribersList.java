package org.foxtrot.pages.user_lists.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class AddSubscribersList {

    private final By doneButtonSelector = cssSelector("[data-cy='createItemAddUsersButton']");
    private final By userCardButtonSelector = cssSelector("[data-cy='usercardButton{Admin Name}']");
    private final By addSubscribersSearchFieldSelector = cssSelector("[data-cy='homeSpaceSearchUserInput']");
    private final By addSubscribersModalCloseButtonSelector = cssSelector("[data-cy='createChannelAddSubscribersCloseButton']");
    private final By addSubscribersModalBackButtonSelector = cssSelector("[data-cy='createChannelAddSubscribersBackButton']");
    private final String definedUserButton = "//div[contains(@class, 'PersonList')]//div[text() = '{username}']";
    private final String definedUserWithNameAndAvatar = ("//div[contains(@class, 'PersonListItem')]//div[.//div[contains(@style, 'background')] and .//div[text()='{username}']]");
    private final String definedUserWithAvatarPlaceholder = ("//div[contains(@class, 'PersonListItem')]//div[.//*[contains(@fill, 'rgba')] and .//div[text()='{username}']]");
    ////div[contains(@class, 'PersonListItem')]//div[.//div[contains(@style, 'background')] and .//div[text()='test 25']]

}
