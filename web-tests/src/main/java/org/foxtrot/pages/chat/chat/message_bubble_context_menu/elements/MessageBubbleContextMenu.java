package org.foxtrot.pages.chat.chat.message_bubble_context_menu.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class MessageBubbleContextMenu {

    private final By messageReplyButton = By.xpath("//*[@role='menuitem'][.//*[normalize-space()='Ответить' or normalize-space()='Reply']]");
    private final By messageEditButton = By.cssSelector("[data-cy='menuAction_message-edit']");
    private final By messageCopyTextButton = By.cssSelector("[data-cy='menuAction_message-copy']");
    private final By messageForwardButton = By.xpath("//div[@role='menuitem' and .//span[text()='Переслать']]");
    private final By messageForwardButtonByDataCy = By.cssSelector("[data-cy='menuAction_message-forward']");
    private final By messageAddTagButton = By.xpath("//*[@role='menuitem'][.//*[normalize-space()='Метки' or normalize-space()='Теги' or normalize-space()='Add tag' or normalize-space()='Tag']]");
    private final By messagePinButton = By.cssSelector("[data-cy='menuAction_message-pin']");
    private final By messageIntoSavedButton = By.cssSelector("[data-cy='menuAction_message-intoSaved']");
    private final By messageDeleteButton = By.cssSelector("[data-cy='menuAction_message-delete']");
    private final By messageCommentButton = By.cssSelector("[data-cy='menuAction_group-comments']");
    private final By messageViewedButton = By.cssSelector("[data-cy='menuAction_viewed-info']");
    private final By messageReactionsButton = By.cssSelector("[data-cy='menuAction_message-reactions']");
    private final By messageSelectButton = By.cssSelector("[data-cy='menuAction_message-select']");
    private final By messageAutodeleteUnit = By.cssSelector("[data-cy='autoDeleteMessage']"); //timer in the element body
    private final By messageAutodeleteTimerHasNotStarted = By.cssSelector("[data-cy='autoDeleteMessageNotStarted']");

    private final By pollCancelVoteButton = By.cssSelector("[data-cy='menuAction_cancel_vote']");
    private final By finishPollButton = By.cssSelector("[data-cy='menuAction_finished_vote']");
}
