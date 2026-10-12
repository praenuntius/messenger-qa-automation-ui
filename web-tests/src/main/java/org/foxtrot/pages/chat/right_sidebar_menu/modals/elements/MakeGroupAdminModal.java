package org.foxtrot.pages.chat.right_sidebar_menu.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class MakeGroupAdminModal {

    private final By makeAdminButton = xpath("//span[text() = 'Сделать администратором']");
    private final By removeUserFromAdminsButton = xpath("//div[contains(text(), 'Убрать из администраторов')]");
    private final By cancelButton = cssSelector("div[class *= 'Content-sc'] button[class *= 'NfkDw']");
}
