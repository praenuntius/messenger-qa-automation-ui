package org.foxtrot.pages.chat.right_sidebar_menu.modals.elements;

import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

public class ShareContactModal {
    private final By closeModalButton = cssSelector("[data-cy='shareContactsCloseButton']");
    private final By searchContactField = cssSelector("[data-cy='homeSpaceSearchUserInput']");
    private final By contactListItem = cssSelector("[data-cy='shareContactsListItem']"); //add userid
    private final By removeSpecifiedContactButton = cssSelector("[data-cy='shareContactsSelectedCloseButton']"); //add userid
    private final By sendSpecifiedButton = cssSelector("[data-cy='shareContactsSendButton']");
}
