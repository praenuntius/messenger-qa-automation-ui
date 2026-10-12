package org.foxtrot.pages.right_sidebar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class MyProfile {
    private final By editProfile = By.xpath("//span[text() = 'Редактировать профиль']");
    private final By myName = cssSelector("[data-cy='userTitleText']");
    private final By jobTitleText = cssSelector("[data-cy='jobTitleText']");
    private final By contactInfoBlock = cssSelector("div[class*='ContactInfoBlock']");
    private final By showMoreButton = xpath("//span[text() = 'Показать еще']");
    private final By savedChat = cssSelector("[data-cy='openSavedChatButton']");
    private final By copyProfileLinkButton = cssSelector("[data-cy='copyProfileButton']");
}
