package org.foxtrot.pages.chat.chat.tags.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class Tags {
    private final By tagElementContainer = By.xpath("//*[@id=\"sendMessageBoxWrapper\"]");
    private final By tagHelpButton = By.xpath("//*[@id=\"sendMessageBoxWrapper\"]/div/div[2]/div[1]/div");
    private final By tagHelpPopupContainer = By.xpath("//*[@id=\"sendMessageBoxWrapper\"]/div/div[2]/div[1]/div/div[2]");
    private final By tagHelpPopupCloseButton = cssSelector("[data-cy='closeMessageTagPanelHelp']");
    private final By tagMenuReplyCloseButton = By.xpath("//*[@id=\"sendMessageBoxWrapper\"]/div/div[1]/button");
    private final By tagMenuCloseButton = By.xpath("//*[@id=\"sendMessageBoxWrapper\"]/div/div[2]/div[1]/span[2]");
    private final By tagInputField = cssSelector("[data-cy='tagSearchInput']");
    private final By tagAddEmojiButton = cssSelector("[data-cy='addEmojiButton']");
    private final By importantTag = By.xpath("//*[@id=\"sendMessageBoxWrapper\"]/div/div[2]/div[3]/div/div[1]/span");
    private final By discussTag = By.xpath("//*[@id=\"sendMessageBoxWrapper\"]/div/div[2]/div[3]/div/div[2]/span");
    private final By doneTag = By.xpath("//*[@id=\"sendMessageBoxWrapper\"]/div/div[2]/div[3]/div/div[3]/span");
    private final By addedTagsCounter = By.xpath("//*[contains(@class,'TagBadgeWrapper')]//*[contains(@class,'TagCounter')]");
    private final By sendTag = cssSelector("[data-cy='sendMessageAreaSubmitMessage']");
    private final By noTagsFoundTextContainer = By.xpath("//*[@id=\"sendMessageBoxWrapper\"]/div/div[2]/div[3]/div/div[1]/span");
    private final By createPrivateTagButton = By.xpath("//*[@id=\"sendMessageBoxWrapper\"]/div/div[2]/div[3]/div/div[2]/div[1]/div/span[1]");
    private final By createPublicTagButton = By.xpath("//*[@id=\"sendMessageBoxWrapper\"]/div/div[2]/div[3]/div/div[2]/div[2]/div/span[1]");
    private final By tagLabel = cssSelector("span");
}
