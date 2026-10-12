package org.foxtrot.pages.chat.chat.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class Chat {
    private final By messageBubbleUserAvatar = cssSelector("[data-cy^='messageBubbleUserAvatar']");
    private final By messageBubble = cssSelector("div[data-cy*='messageBubble']:not([data-cy*='UserAvatar'])");
    private final By commentsPostButton = cssSelector("[data-cy^='messageCommentPostButton']");
    private final By savedMessageText = cssSelector("div[class*= 'ql-editor'] div");
    private final By containerWithChat = cssSelector("div[class*='ChatContainerScrollableBlock']");
    private final By chatWallpaperPatternImage = cssSelector("div[class*='PatternImage']");
    private final By containerWithMessages = cssSelector(
            "div[class*='ChatMessagesContainer'], div[class*='ChatContainerScrollableBlock']");
    private final By contactCard = cssSelector("div[class*='CardContainer']");
    private final By emojiMessage = cssSelector("div[class*='Emoji']");
    /** Web: img[class*='Image']; desktop Electron: PromiseImgWithStubStyled + fitmode, ImageWrapper */
    private final By imagesMessage = cssSelector(
            "img[class*='Image'], img[class*='PromiseImg'], img[fitmode], div[class*='ImageWrapper'] img");
    private final By imageMessageWrapper = cssSelector("div[class*='ImageWrapper']");
    private final By videoMessage = cssSelector("div[class*='VideoDurationWrapper']");
    private final By albumImageItem = cssSelector("div[class*='MultimediaMessageView']:not(:has(div[class*='VideoDurationWrapper']))");
    private final By albumGridImage = cssSelector("div[class*='MultimediaMessageGrid'] img");
    private final By mediaUploadProgress = cssSelector("[data-test-id='CircularProgressbar']");
    private final By fileMessage = cssSelector("div[class*='FileMessage']");
    private final By mention = cssSelector("a[class*='MentionStyling']");
    private final By mentionButton = cssSelector("[data-cy='mentionButton']");
    private final By commentButtonUnderBubble = cssSelector("[data-cy='messageCommentButton'], [data-cy^='messageCommentPostButton']");
    private final By commentCounter = cssSelector("[data-cy='messageCommentCounter']");
    private final By commentButtonText = cssSelector("span[class*='ButtonText'] span");
    private final By commentsMenu = cssSelector("[data-cy='commentsMenu']");
    private final By channelCommentsBackButton = cssSelector("[data-cy='channelCommentsBackButton']");
    private final By commentChatHeader = cssSelector("[data-cy='comment_chatHeader']");
    private final By pinnedMessagePictogram = cssSelector("svg use[href='#icPin']");
    private final By replyMessageText = cssSelector("div[data-message-text='true'][mode='reply']");
    private final By replyMessageByAuthorName = cssSelector("div[class*='ReplyTextWrapper'] div[class*='Nickname']");
    private final By userPhoto = cssSelector("div[class*='ChatMessagesContainer'] div[style*='background']");
    private final By userAvatarPlaceholder = cssSelector("div[class*='ChatMessagesContainer'] div[class*='Avatar'] svg");
    private final By scheduledMessagesButton = cssSelector("[data-cy='scheduled']");
    /** Sticky header of the commented post in the comments thread (author + original post text). */
    private final By commentedPostHeader = By.xpath(
            "//button[@data-cy='channelCommentsBackButton']" +
                    "/ancestor::div[contains(@class,'ChatArea')][1]" +
                    "//div[contains(@class,'Container-sc-dg67nq')]" +
                    "[.//div[contains(@class,'Info-sc-dg67nq')]" +
                    " and .//span[contains(@class,'Title-sc-dg67nq')]" +
                    " and .//span[contains(@class,'Message-sc-dg67nq')]]"
    );
    private final By commentedPostHeaderAuthorTitle = By.cssSelector("span[class*='Title-sc-dg67nq']");
    private final By commentedPostHeaderMessageText = By.cssSelector("span[class*='Message-sc-dg67nq']");
    private final By commentedPostHeaderContainer = By.xpath(
            "//button[@data-cy='channelCommentsBackButton']" +
                    "/ancestor::div[contains(@class,'ChatArea')][1]" +
                    "//div[contains(@class,'Container-sc-dg67nq')][1]"
    );
    private final By scrollDownButton = By.xpath(
            "//div[contains(@class,'WrapperDownBtn')]//button" +
                    " | " +
                    "//button[.//*[name()='use' and @href='#icToggleDown']]"
    );
    private final By messageStatusSent = cssSelector("[data-cy='messageStatusSent']");
    private final By messageStatusSeen = cssSelector("[data-cy='messageStatusSeen']");
    private final By clearAllMessageHistory = cssSelector("[data-cy='systemMessage_messageSystemClearAllHistory']");
    private final By contactNameInCard = cssSelector("[data-cy='contactName']");
    private final By replayMessageBubbleByText = cssSelector("[data-cy='replyMessageText']");
    private final By messageText = cssSelector("[data-message-text]");
    private final By boldText = cssSelector("strong, b");
    private final By italicText = cssSelector("em, i");
    private final By underlinedText = cssSelector("u");
    private final By strikethroughText = cssSelector("s, strike, del");
    private final By monospaceText = cssSelector("code, [class*='Monospace'], [class*='monospace']");

    private final By commentedPostHeaderByXpath = By.xpath(
            "//*[@id='root']/div[1]/div[4]/div[3]/div[1]/div[1]" +
                    "/div[.//span[contains(@class,'Title-sc')] and .//span[contains(@class,'Message-sc')]]"
    );
    private final By commentedPostHeaderByTitleAndMessage = By.xpath(
            "//*[@id='root']//div[.//span[contains(@class,'Title-sc')] and .//span[contains(@class,'Message-sc')]]"
    );
    private String definedGroupInviteSystemMessage = "//div[contains(@id, 'system-message')]//div[text() = 'Вы добавил(а) {userName} ']";
    private String definedMessageSender = "//div[contains(@data-cy, 'message') and .//span[text()='{sender}'] and .//span[text()='{message}']]";
    private final By copyContactButton = cssSelector("div[id*='message'] button[class*='IconButtonNone']");
    private final By messageLink = cssSelector("a");
    private final By fileName = cssSelector("[class*='FileName']");
    private final By messageTagLabel = cssSelector(
            "span[class*='MessageTagLabel'], [data-attr='messageTagButton'] span, "
                    + "[data-attr='messageTagButton'], [class*='MessageTagButtonView']");
    private final By messageRoot = xpath("./ancestor-or-self::div[contains(@class,'Message')][1]");
    private final By formattedTextNodes = cssSelector("a, span, div");
    private final By messageReactionCount = cssSelector("[data-cy='messageReactionCount']");
    private final By reactionTooltip = xpath("//div[@role='dialog' and contains(@class, 'PopoverContent')]");


    public By getScheduledLabelByText(String label) {
        return By.xpath("//div[text() = 'Запланировано на " + label + "']");
    }

    public By getMessageBubbleById(String messageId) {
        return cssSelector("[id='message-" + messageId + "'], [data-cy*='messageBubble'][data-cy*='" + messageId + "']");
    }

    public By getMessageBubbleByIdSecond(String messageId) {
        return By.cssSelector(String.format("[id='message-%s'] div[data-cy*='messageBubblebubble']", messageId));
    }


    public By getMediaMessageBubbleById(String messageId) {
        return cssSelector("[data-cy='messageBubble" + messageId + "']");
    }

    public By getReplayMessageBubbleById(String messageId) {
        return cssSelector("[id='message-" + messageId + "'] span[class*='Reply']");
    }

    public By getMessageLinkByText(String text) {
        return By.xpath(String.format("//div[contains(@class, 'ChatMessagesContainer')]//a[contains(.,'%s')]", text));
    }

    public By getCommentsButtonByPostId(long postId) {
        return By.cssSelector(
                "[data-cy^='messageCommentPostButton" + postId + "_'], " +
                        "[data-cy='messageCommentPostButton" + postId + "']"
        );
    }

    public By getCommentsButtonByPostIdAndCount(long postId, int count) {
        return By.cssSelector("[data-cy='messageCommentPostButton" + postId + "_" + count + "']");
    }

    public String getMessageReaction(String emoji) {
        return "[data-cy='messageReaction_" + emoji + "']";
    }
}
