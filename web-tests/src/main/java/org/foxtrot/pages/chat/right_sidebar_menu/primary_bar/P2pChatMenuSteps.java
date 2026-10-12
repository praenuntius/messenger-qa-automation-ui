package org.foxtrot.pages.chat.right_sidebar_menu.primary_bar;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.right_sidebar_menu.primary_bar.elements.P2pChatMenu;
import org.foxtrot.pages.chat.topbar.TopbarSteps;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class P2pChatMenuSteps {

    private final P2pChatMenu elements = new P2pChatMenu();
    private final TopbarSteps topbarSteps = new TopbarSteps();

    @Step("Открыть меню чата")
    public P2pChatMenuSteps openP2pChatMenu() {
        topbarSteps.clickP2pHeader();
        $(elements.getCloseMenuButton()).shouldBe(visible);
        return this;
    }

    @Step("Закрыть меню чата")
    public void closeP2pChatMenu() {
        $(elements.getCloseMenuButton()).click();
    }

    @Step("Отправить сообщение")
    public void messageToP2pChat() {
        $(elements.getMessageButton()).click();
    }

    @Step("Начать P2P-звонок")
    public void startP2pCall() {
        $(elements.getCallButton()).click();
    }

    @Step("Скопировать номер телефона пользователя")
    public void copyPhoneNumber() {
        $(elements.getCopyPhoneNumberButton()).click();
    }

    @Step("Изменить настройку уведомлений в чате")
    public void switchNotificationsSetting() {
        $(elements.getNotificationsToggle()).parent().click();
    }

    @Step("Открыть меню вложений")
    public void openAttachmentsMenu() {
        $(elements.getAppearanceButton()).click();
    }

    @Step("Открыть меню закрепленных сообщений")
    public void openPinnedMessagesMenu() {
        $(elements.getPinnedMessagesButton()).click();
    }

    @Step("Скопировать ссылку на профиль пользователя")
    public void copyProfileLink() {
        $(elements.getCopyProfileLinkButton()).click();
    }

    @Step("Открыть меню автоудаления сообщений ")
    public void openAutodeleteMessagesMenu() {
        $(elements.getAutodeleteMessagesButton()).click();
    }

    @Step("Добавить в контакты")
    public void clickAddToContactsButton() {
        $(elements.getAddContactButton()).click();
    }

    @Step("Проверить, что контакт успешно добавлен")
    public void checkContactAdded(){
        $(elements.getDeleteContactButton()).shouldBe(visible);
    }

    @Step("Проверить что контакт удален")
    public void checkContactDelete() {
        $(elements.getAddContactButton()).shouldBe(visible);
    }

    @Step("Удалить контакт")
    public void clickDeleteContactButton() {
        $(elements.getDeleteContactButton()).shouldBe(visible).click();
    }

    @Step("Заблокировать пользователя")
    public void blockUserButton() {
        $(elements.getBlockUserButton()).click();
    }

    @Step("Разблокировать пользователя")
    public void unblockUserButton() {
        $(elements.getUnblockUserButton()).click();
    }

    @Step("Очистить историю сообщений")
    public void clearChatHistoryButton() {
        org.foxtrot.pages.modals.WebOnboardingOverlay.dismissIfPresent();
        $(elements.getClearChatHistoryButton()).click();
    }

}