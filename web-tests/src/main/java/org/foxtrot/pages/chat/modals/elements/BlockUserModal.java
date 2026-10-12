package org.foxtrot.pages.chat.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class BlockUserModal {
    private final By blockButton = By.xpath("//span[text() = 'Заблокировать']");
    private final By cancelButton = By.xpath("//span[text() = 'Отмена']");
    private final String userName = "//div//span[contains(., '{username}')]";
}
