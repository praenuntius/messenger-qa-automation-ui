package org.foxtrot.pages.left_sidebar.settings.primary.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class LogoutModal {

    private final By title = xpath("//span[text()='Выйти из приложения?']");
    private final By cancelButton = cssSelector("[data-cy='cancelLogoutButton']");
    private final By confirmButton = cssSelector("[data-cy='confirmLogoutButton']");
}
