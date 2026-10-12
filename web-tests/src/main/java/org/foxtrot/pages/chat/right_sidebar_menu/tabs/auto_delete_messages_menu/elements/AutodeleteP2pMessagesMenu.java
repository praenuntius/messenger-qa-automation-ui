package org.foxtrot.pages.chat.right_sidebar_menu.tabs.auto_delete_messages_menu.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class AutodeleteP2pMessagesMenu {

    private final By autodeleteMessagesP2pDisabledRadioButtonSelector = cssSelector("[data-cy='autodeleteP2PItem-1']");
    private final By autodeleteMessagesP2p5mRadioButtonSelector = cssSelector("[id='5м']");
    private final By autodeleteMessagesP2p10mRadioButtonSelector = cssSelector("[data-cy='autodeleteP2PItem600']");
    private final By autodeleteMessagesP2p30mRadioButtonSelector = cssSelector("[data-cy='autodeleteP2PItem1800']");
    private final By autodeleteMessagesP2p1hRadioButtonSelector = cssSelector("[data-cy='autodeleteP2PItem3600']");
    private final By autodeleteMessagesP2p2hRadioButtonSelector = cssSelector("[data-cy='autodeleteP2PItem7200']");
    private final By autodeleteMessagesP2p4hRadioButtonSelector = cssSelector("[data-cy='autodeleteP2PItem14400']");
    private final By autodeleteMessagesP2p1dRadioButtonSelector = cssSelector("[data-cy='autodeleteP2PItem86400']");
    private final By autodeleteMessagesP2p2dRadioButtonSelector = cssSelector("[data-cy='autodeleteP2PItem172800']");
    private final By autodeleteMessagesP2p3dRadioButtonSelector = cssSelector("[data-cy='autodeleteP2PItem259200']");
    private final By autodeleteMessagesP2p1wRadioButtonSelector = cssSelector("[data-cy='autodeleteP2PItem604800']");
}
