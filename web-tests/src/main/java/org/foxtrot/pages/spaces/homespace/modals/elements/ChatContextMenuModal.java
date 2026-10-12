package org.foxtrot.pages.spaces.homespace.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class ChatContextMenuModal {

    private final By deleteChatButton = By.xpath("//div[text() = 'Удалить чат']");
    private final By markAsReadButton = By.xpath(
            "//div[normalize-space()='Прочитать все' or normalize-space()='Пометить прочитанным']");
    private final By markAsUnreadButton = By.xpath("//div[text() = 'Пометить непрочитанным']");
    private final By pinedChatButton = By.xpath("//div[text() = 'Закрепить чат']");
    private final By notificationOffButton = By.xpath("//div[text() = 'Отключить уведомления']");
}
