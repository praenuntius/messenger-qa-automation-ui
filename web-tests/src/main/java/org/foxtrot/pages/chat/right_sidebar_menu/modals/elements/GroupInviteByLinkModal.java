package org.foxtrot.pages.chat.right_sidebar_menu.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class GroupInviteByLinkModal {

    private final By inviteLinkTabSelector = cssSelector("[data-cy='invite-link-tab']");
    private final By copyLinkButtonSelector = xpath("//div[contains(@class, 'ActionsWrapper')]//span[text() = 'Скопировать ссылку']");
    private final By closeButtonSelector = xpath(
            "//*[@id='dialog-root']" +
                    "//button[.//*[local-name()='use' and (@href='#icClose' or @*[local-name()='href']='#icClose')]]"
    );
    private final By copySuccessMessage = xpath("//div[text() = 'Скопировано']");
}
