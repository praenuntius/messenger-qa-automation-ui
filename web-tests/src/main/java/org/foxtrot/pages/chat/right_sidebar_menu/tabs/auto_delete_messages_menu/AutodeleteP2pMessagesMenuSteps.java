package org.foxtrot.pages.chat.right_sidebar_menu.tabs.auto_delete_messages_menu;

import io.qameta.allure.Step;
import lombok.Getter;
import org.foxtrot.pages.chat.right_sidebar_menu.tabs.auto_delete_messages_menu.elements.AutodeleteP2pMessagesMenu;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class AutodeleteP2pMessagesMenuSteps {

    private final AutodeleteP2pMessagesMenu elements = new AutodeleteP2pMessagesMenu();

    @Step("Выбрать интервал автоудаления '5 минут'")
    public void selectAutodelete5Minutes() {
        $(elements.getAutodeleteMessagesP2p5mRadioButtonSelector())
                .parent()
                .click();
    }
}