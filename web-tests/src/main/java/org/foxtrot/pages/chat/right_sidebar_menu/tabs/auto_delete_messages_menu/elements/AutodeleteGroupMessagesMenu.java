package org.foxtrot.pages.chat.right_sidebar_menu.tabs.auto_delete_messages_menu.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class AutodeleteGroupMessagesMenu {

    private final By autodeleteMessagesGroupDisabledRadioButtonSelector = cssSelector("[data-cy='autodeleteGroupsItem-1']");
    private final By autodeleteMessagesGroup1dRadioButtonSelector = cssSelector("[data-cy='autodeleteGroupsItem86400']");
    private final By autodeleteMessagesGroup2dRadioButtonSelector = cssSelector("[data-cy='autodeleteGroupsItem172800']");
    private final By autodeleteMessagesGroup3dRadioButtonSelector = cssSelector("[data-cy='autodeleteGroupsItem259200']");
    private final By autodeleteMessagesGroup1wRadioButtonSelector = cssSelector("[data-cy='autodeleteGroupsItem604800']");
    private final By autodeleteMessagesGroup2wRadioButtonSelector = cssSelector("[data-cy='autodeleteGroupsItem1209600']");
    private final By autodeleteMessagesGroup1mntRadioButtonSelector = cssSelector("[data-cy='autodeleteGroupsItem2592000']");
    private final By autodeleteMessagesGroup3mntRadioButtonSelector = cssSelector("[data-cy='autodeleteGroupsItem7776000']");
    private final By autodeleteMessagesGroup6mntRadioButtonSelector = cssSelector("[data-cy='autodeleteGroupsItem15552000']");

}
