package org.foxtrot.pages.chat.comments_threads.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class Comments {
    private final By backFromCommentsButton = cssSelector("[data-cy='channelCommentsBackButton']");
    private final By commentsHeader = cssSelector("[data-cy='comment_chatHeader']");
    private final By commentAuthorName = cssSelector("span[class*='Nickname']");
    private final By pinnedSystemMessage = cssSelector("[data-cy='pinnedSystemMessage']");
    private final By unpinnedSystemMessage = cssSelector("[data-cy='unpinnedSystemMessage']");
    private final By deletedSystemMessage = By.xpath(
            ".//div[starts-with(@id,'system-message-') and " +
                    "not(starts-with(@id,'system-message-date-')) and " +
                    "not(starts-with(@id,'system-message-state-'))]"
    );
    private final By commentsChatArea = By.xpath(
            "//button[@data-cy='channelCommentsBackButton']" +
                    "/ancestor::div[contains(@class,'ChatArea')][1]" +
                    "//div[contains(@class,'ChatMessagesContainer')][1]"
    );
    private final By noCommentsYetSystemMessage = By.xpath(
            "//button[@data-cy='channelCommentsBackButton']" +
                    "/ancestor::div[contains(@class,'ChatArea')][1]" +
                    "//*[contains(normalize-space(.),'Комментариев пока нет') or contains(normalize-space(.),'No comments yet')]"
    );

    private final By threadStartSystemMessage = By.xpath(
            "//button[@data-cy='channelCommentsBackButton']" +
                    "/ancestor::div[contains(@class,'ChatArea')][1]" +
                    "//*[contains(normalize-space(.),'Начало обсуждения') or contains(normalize-space(.),'Start of discussion')]"
    );

    private final By todayDateSystemMessage = By.xpath(
            "//button[@data-cy='channelCommentsBackButton']" +
                    "/ancestor::div[contains(@class,'ChatArea')][1]" +
                    "//*[contains(normalize-space(.),'Сегодня') or contains(normalize-space(.),'Today')]"
    );
    private final By messageView = cssSelector("div[class*='BaseMessageView']");
    private final By messageViewSelfOrAncestor = By.xpath("./ancestor-or-self::div[contains(@class,'BaseMessageView')][1]");
    private final By previousMessageView = By.xpath("preceding-sibling::div[contains(@class,'BaseMessageView')][1]");
    private final By precedingMessageView = By.xpath("preceding::div[contains(@class,'BaseMessageView')][1]");
}
