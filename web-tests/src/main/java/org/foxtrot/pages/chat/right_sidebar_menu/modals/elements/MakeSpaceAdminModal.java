package org.foxtrot.pages.chat.right_sidebar_menu.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.xpath;

@Getter
public class MakeSpaceAdminModal {

    private final By makeAdminButton = xpath("//span[text() = 'Сделать администратором']");
    private final By removeUserFromAdminsButton = xpath("//span[text() = 'Убрать']");
    private final By cancelButton = xpath("//span[text() = 'Отмена']");


}
