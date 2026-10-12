package org.foxtrot.pages.chat.right_sidebar_menu.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.xpath;

@Getter
public class LeaveGroupAndChannelModal {
    private final By leaveButton = xpath("//span[text() = 'Покинуть']");
    private final By cancelButton = xpath("//span[text() = 'Отмена']");
}