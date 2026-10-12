package org.foxtrot.pages.chat.input_bar;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.input_bar.elements.AttachContextMenu;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class AttachContextMenuSteps {

    private final AttachContextMenu attachContextMenu = new AttachContextMenu();

    @Step("Клик кнопку Прикрепить в контекстном меню")
    public AttachContextMenuSteps clickAttachButton() {
        $(attachContextMenu.getAttachButton()).click();
        return this;
    }

    @Step("Клик кнопку Поделиться контактом в контекстном меню ")
    public AttachContextMenuSteps clickShareContactsButton() {
        $(attachContextMenu.getShareContactsButton()).click();
        return this;
    }

    @Step("Кликнуть по кнопке 'Создать опрос' в контекстном меню")
    public AttachContextMenuSteps clickCreatePollButton() {
        $(attachContextMenu.getCreatePollButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }
}
