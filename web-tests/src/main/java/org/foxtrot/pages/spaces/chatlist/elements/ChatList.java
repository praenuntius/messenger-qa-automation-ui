package org.foxtrot.pages.spaces.chatlist.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class ChatList {
    private final By previewTextMessage = cssSelector("[data-cy='chatPreviewMessageText']");
    private final By previewSystemMessage = cssSelector("[data-cy='previewSystemMessage']");
    private final By chatCounter = cssSelector("[data-cy='unreadMessagesCount']");
    private final By savedChatButton = cssSelector("[data-cy='openChatСохраненные']");
    private final By tdmChatButton = cssSelector("[data-cy='openChatTDM']");
    private final By chatPreviewName = cssSelector("[data-cy='chatPreviewName'], [data-cy='channelPreviewName']");
    private final String openChatCardCss = "[data-cy^='openChat']";

    public By chatCardByName(String chatName) {
        String nameLiteral = toXpathLiteral(chatName);
        return xpath(
                "//div[(@data-cy=concat('openChat', " + nameLiteral + ")) or "
                        + "(starts-with(@data-cy,'openChat') and .//*[@data-cy='chatPreviewName' or @data-cy='channelPreviewName']"
                        + "[contains(normalize-space(.), " + nameLiteral + ")])]"
        );
    }

    public By userWithNameAndAvatar(String username) {
        return xpath("//div[@data-active and .//div[contains(@style, 'background')] and .//div[contains(text(), "
                + toXpathLiteral(username) + ")]]");
    }

    public By userWithAvatarPlaceholder(String username) {
        return xpath("//div[contains(@class, 'VerticalScroll') and .//*[local-name()='svg' and contains(@fill, 'rgba')] and .//div[contains(text(), "
                + toXpathLiteral(username) + ")]]");
    }

    private static String toXpathLiteral(String value) {
        if (value == null) {
            return "''";
        }
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        // concat('foo', "'", 'bar')
        String[] parts = value.split("'", -1);
        StringBuilder sb = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(", \"'\", ");
            sb.append("'").append(parts[i]).append("'");
        }
        sb.append(")");
        return sb.toString();
    }
}