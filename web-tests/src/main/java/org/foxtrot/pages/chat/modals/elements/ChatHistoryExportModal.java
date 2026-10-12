package org.foxtrot.pages.chat.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class ChatHistoryExportModal {
    private final By modalContainer = By.cssSelector("[aria-label='Настройка экспорта истории чата']");
    private final By exportButton = By.cssSelector("[data-cy='exportChatExportButton']");
}