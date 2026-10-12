package org.foxtrot.pages.chat.right_sidebar_menu.primary_bar;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.right_sidebar_menu.primary_bar.elements.ChannelMenu;
import org.foxtrot.pages.chat.right_sidebar_menu.tabs.subscribers_menu.elements.SubscribersMenu;
import org.foxtrot.pages.chat.topbar.TopbarSteps;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ChannelMenuSteps {

    private final ChannelMenu elements = new ChannelMenu();
    private final SubscribersMenu subscribersMenu = new SubscribersMenu();
    private final TopbarSteps topbarSteps = new TopbarSteps();

    @Step("Открыть меню канала")
    public void openChannelMenu() {
        topbarSteps.clickChannelHeader();
    }

    @Step("Открыть настройки канала")
    public void openChannelSettings() {
        $(elements.getSidebarOpenSettingsButton()).shouldBe(visible).click();
    }

    @Step("Закрыть меню канала")
    public void closeChannelSettingsButton() {
        $(elements.getSidebarCloseMenuButton()).click();
    }

    @Step("Выключить уведомления")
    public void notificationsOnButton() {
        $(elements.getNotificationsOnToggle()).click();
    }

    @Step("Включить уведомления")
    public void notificationsOffButton() {
        $(elements.getNotificationsOffToggle()).click();
    }

    @Step("Открыть меню 'вложения'")
    public void attachmentsButton() {
        $(elements.getAttachmentsButton()).click();
    }

    @Step("Открыть меню'Закрепленные сообщения'")
    public void pinnedMessagesButton() {
        $(elements.getPinnedMessagesButton()).click();
    }

    @Step("Открыть окно настроек оформления")
    public void appearanceButton() {
        $(elements.getAppearanceButton()).click();
    }

    @Step("Пригласить в закрытый канал по ссылке")
    public void clickInviteCloseChannelLinkButton() {
        $(elements.getInviteButton()).click();
    }

    @Step("Скопировать ссылку на открытый канал")
    public void clickCopyLinkOpenChannelButton() {
        $(elements.getInviteButton()).click();
    }

    @Step("Открыть меню 'Подписчики'")
    public void clickSubscribersButton() {
        $(elements.getSubscribersButton()).click();
    }

    @Step("Открыть окно передачи прав владельца")
    public void TransferOwnershipButton() {
        $(elements.getTransferOwnershipButton()).click();
    }

    @Step("Открыть окно удаления канала")
    public void DeleteChannelButton() {
        $(elements.getDeleteChannelButton()).click();
    }

    @Step("Открыть окно добавления пользователя в канал")
    public void addUserToChannel(long userId) {
        $(elements.getSubscribersButton()).click();
        $(subscribersMenu.getAddUserInput()).setValue(String.valueOf(userId)).pressEnter();
    }

    @Step("Проверить, что данные канала отредактированы")
    public void verifyEditChannel(String name, String description, String channelType) {
        verifyAvatar();
        verifyName(name);
        verifyDescription(description);
        verifyChannelType(channelType);
    }

    @Step("Проверить, что появился аватар")
    public void verifyAvatar() {
        $(elements.getHeaderAvatar()).shouldBe(visible);
    }

    @Step("Проверить, что имя равно {name}")
    public void verifyName(String name) {
        $(elements.getHeaderInfoBlock()).shouldHave(text(name));
    }

    @Step("Проверить, что описание равно {description}")
    public void verifyDescription(String description) {
        $(elements.getHeaderInfoBlock()).shouldHave(text(description));
    }

    @Step("Проверить, что тип канала равно {channelType}")
    public void verifyChannelType(String channelType) {
        $(elements.getHeaderInfoBlock()).shouldHave(text(channelType));
    }
}
