package org.foxtrot.pages.chat.system_messages.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class SystemMessages {
    private final By pinnedSystemMessage = cssSelector("[data-cy='systemMessage_messagePin']");
    private final By unpinnedSystemMessage = cssSelector("[data-cy='systemMessage_messageUnpin']");
    private final By deletedSystemMessage = cssSelector("[data-cy='systemMessage_messageSystemDeleted']");
    private final By autoDeletedSystemMessage = cssSelector("[data-cy='systemMessage_messageAutoDeletion']");

    public By getSystemMessageById(String messageId) {
        return cssSelector("#system-message-" + messageId);
    }
}
