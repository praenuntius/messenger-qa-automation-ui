package org.foxtrot.pages.snackbars.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class Snackbars {
    private final By textCopied = By.cssSelector("[data-cy='snackbar-copied-success']");
    private final By showMessageAuthor = By.xpath("/html/body/div[4]/div");
    private final By sentToSaved = By.cssSelector("[data-cy='snackbar-save-chats']");
    private final By copied = By.cssSelector("[data-cy='snackbar-copied-success']");
    private final By chatDeleteSnackBar = By.xpath("//div[contains(text(), 'удален')]");
    private final By clearP2pHistoryForSelf = By.cssSelector("[data-cy='snackbar-my-history-cleared']");
    private final By linkCopied = By.xpath("//div[text()='Ссылка скопирована']");
    private final By inviteLinkInactive = By.cssSelector("[data-cy='snackbar-invite-link-inactive']");
    private final By workSpaceDeleted = By.cssSelector("[data-cy='snackbar-workspace-deleted']");
    private final By workspacesDeletedNotification = By.cssSelector("[data-cy='snackbar-deletion-success']");
    private final By exportChatHistory = By.cssSelector("[data-cy='snackbar-exported']");
}
