package org.foxtrot.pages.spaces.homespace.primary.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class Homespace {
    private final By homespaceButton = cssSelector("[data-cy='sidebarWorkspaceButtonЧаты']");
    /**
     * Кнопка «+» / new message в homespace.
     * Absolute xpath ломается при любом сдвиге DOM — используем class (+ опечатка Creat в legacy)
     * и известные data-cy, если появятся на стенде.
     */
    private final By createNewChatButton = cssSelector("div[aria-haspopup='menu'] button[class*='HeaderIconButton']");
    private final By tdmChatButton = cssSelector("[data-cy='openChatTDM']");
    private final String definedChatButton = "[data-cy='openChat{chatname}']"; //chatname in the selector should be provided
    private final By enterOrCreateSpaceButton = cssSelector("[data-cy='enterOrCreateSpaceButton']");
    private final By savedChatButton = cssSelector("[data-cy='openChatСохраненные']");

    public By chatCardByName(String chatName) {
        String nameLiteral = toXpathLiteral(chatName);
        return xpath(
                "//div[(@data-cy=concat('openChat', " + nameLiteral + ")) or " +
                        "(starts-with(@data-cy,'openChat') and .//*[@data-cy='chatPreviewName' or @data-cy='channelPreviewName']" +
                        "[contains(normalize-space(.), " + nameLiteral + ")])]"
        );
    }

    private static String toXpathLiteral(String value) {
        if (value == null) {
            return "''";
        }
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        StringBuilder sb = new StringBuilder("concat(");
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (i > 0) sb.append(", ");
            if (ch == '\'') sb.append("\"'\"");
            else if (ch == '"') sb.append("'\"'");
            else sb.append("'").append(ch).append("'");
        }
        sb.append(")");
        return sb.toString();
    }
}
