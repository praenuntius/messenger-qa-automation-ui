package org.foxtrot.pages.chat.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.modals.elements.ScheduledMessageContextMenu;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ScheduledMessageContextMenuSteps {
    ScheduledMessageContextMenu elements = new ScheduledMessageContextMenu();

    @Step("Нажать кнопку 'Отправить сейчас'")
    public void clickSendNowButton() {
        $(elements.getSendNowButton()).shouldBe(visible).click();
    }

    @Step("Нажать кнопку 'Изменить время'")
    public void clickChangTimeButton() {
        $(elements.getChangTime()).shouldBe(visible).click();
    }

    @Step("Нажать кнопку 'Скопировать'")
    public void clickCopyButton() {
        $(elements.getCopyText()).shouldBe(visible).click();
    }

    @Step("Нажать кнопку 'Удалить'")
    public void clickDeleteButton() {
        $(elements.getDeleteButton()).shouldBe(visible).click();
    }

    @Step("Нажать кнопку 'Скачать'")
    public void clickDownloadButton() {
        $(elements.getDownloadButton()).shouldBe(visible).click();
    }
}
