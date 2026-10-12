package org.foxtrot.pages.left_sidebar.primary_bar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.xpath;

@Getter
public class SpaceContextMenu {
    private final By muteNotificationsButton = xpath("//div[text() = 'Отключить уведомления']");
    private final By markAsReadButton = xpath(
            "//div[normalize-space()='Прочитать все' or normalize-space()='Пометить прочитанным']");
    private final By markAsReadButtonDisabled = xpath(
            "//div[normalize-space()='Прочитать все' or normalize-space()='Пометить прочитанным']"
                    + "/ancestor::div[contains(@class, 'MenuItemDisabled')]");
    private final By leaveSpaceButton = xpath("//div[text() = 'Выйти']");
}