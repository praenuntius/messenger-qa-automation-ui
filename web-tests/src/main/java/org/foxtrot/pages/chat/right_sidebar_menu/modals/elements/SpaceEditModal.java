package org.foxtrot.pages.chat.right_sidebar_menu.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class SpaceEditModal {

    private final By nameInput = cssSelector("[placeholder='Имя*']");
    private final By descriptionInput = cssSelector("textarea[placeholder='Описание']");
    private final By uploadPhotoButton = By.cssSelector("button[class*='NoAvatarIconWrapper']");
    private final By saveBotton = xpath("//span[text() = 'Сохранить']");
    private final By fileInput = cssSelector("input[accept='image/*']");
}
