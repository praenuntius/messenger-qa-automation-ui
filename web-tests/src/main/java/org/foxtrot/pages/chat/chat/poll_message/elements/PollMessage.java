package org.foxtrot.pages.chat.chat.poll_message.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.xpath;

@Getter
public class PollMessage {

    private final By pollEndedLabel = xpath(".//span[normalize-space(.)='Опрос завершен']");
    private final By voteButton = xpath(".//button[.//*[normalize-space(.)='Проголосовать']]");
    private final By resultsButton = xpath(".//button[.//*[normalize-space(.)='Результаты']]");
    private final By pollEndTimeLabel = xpath(".//span[starts-with(normalize-space(.), 'до ')]");
    private final By yourVoteAcceptedLabel = xpath(".//span[normalize-space(.)='Ваш голос учтен']");
    private final By chooseOptionLabel = xpath(".//span[normalize-space(.)='Выберите один из вариантов']");

    public By pollTitleByText(String title) {
        return xpath(".//*[@data-message-title='true' and normalize-space(.)=" + toXpathLiteral(title) + "]");
    }

    public By pollBubbleByTitle(String title) {
        return xpath(".//div[contains(@data-cy,'messageBubble')]" +
                "[.//*[@data-message-title='true' and normalize-space(.)=" + toXpathLiteral(title) + "]]");
    }

    /**
     * Строка варианта: блок с checkbox/radio и текстом {@code data-message-text} (соседние Select + Description).
     */
    public By pollVoteOptionByIndex(int index) {
        return xpath("(.//span[@data-message-text='true']/ancestor::div[.//input[@type='checkbox' or @type='radio']][1]"
                + "//label[.//input[@type='checkbox' or @type='radio']])[" + index + "]");
    }

    public By pollVoteOptionByText(String optionText) {
        return xpath(".//span[@data-message-text='true' and normalize-space(.)=" + toXpathLiteral(optionText)
                + "]/ancestor::div[.//input[@type='checkbox' or @type='radio']][1]"
                + "//label[.//input[@type='checkbox' or @type='radio']]");
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

