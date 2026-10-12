package org.foxtrot.pages.chat.right_sidebar_menu.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class GroupAndChannelEditModal {

    private final By nameInput = cssSelector("[data-cy='updateItemNameInput']");
    private final By descriptionInput = cssSelector("[data-cy='updateItemDescriptionInput']");
    private final By uploadPhotoButton = By.cssSelector("button[class*='NoAvatarIconWrapper']");
    private final By saveButton = By.cssSelector("[data-cy='updateItemSaveButton']");
    private final By fileInput = cssSelector("input[accept='image/*']");
    private final By publicChannelCheckbox  = cssSelector("label[for='publicChannel']");
    private final By publicGroupCheckbox  = xpath("//div[text() = 'Открытая группа']");
    private final By privateChannelCheckbox  = cssSelector("label[for='privateChannel']");
    private final By privateGroupCheckbox  = xpath("//div[text() = 'Закрытая группа']");
    private final By allowChannelCommentsToggle = xpath(
            "//*[@data-cy='createChannelCommentsRadioButton' or @data-cy='createGroupCommentsSwitchButton']" +
                    " | " +
                    "//div[normalize-space()='Разрешить комментарии' or normalize-space()='Allow comments']" +
                    "/following-sibling::label//input[@type='checkbox']"
    );
    private final By allowGroupCommentsToggle = xpath(
            "//*[@data-cy='createGroupCommentsSwitchButton' or @data-cy='createChannelCommentsRadioButton']" +
                    " | " +
                    "//div[normalize-space()='Разрешить комментарии' or normalize-space()='Allow comments']" +
                    "/following-sibling::label//input[@type='checkbox']"
    );
    private final By allowThreadsRadioButton = xpath(
            "//*[@data-cy='createGroupCommentsSwitchButton' or @data-cy='createChannelCommentsRadioButton']" +
                    " | " +
                    "//div[normalize-space()='Разрешить комментарии' or normalize-space()='Allow comments']" +
                    "/following-sibling::label//input[@type='checkbox']"
    );
    private final By allowReactionsRadioButton  = cssSelector("[data-cy='createChannelReactionsRadioButton']");
    private final By notShowChannelNameRadioButton = xpath("//div[contains(., 'название канала')]/following::label");
    private final By groupEditModalHeder = cssSelector("div[aria-label='Настройки группы'] div[class*='HeaderWrapper']");
    private final By channelEditModalHeder = cssSelector("div[aria-label='Настройки канала'] div[class*='HeaderWrapper']");
}
