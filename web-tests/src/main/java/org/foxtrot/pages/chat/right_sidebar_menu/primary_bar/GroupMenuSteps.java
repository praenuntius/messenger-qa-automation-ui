package org.foxtrot.pages.chat.right_sidebar_menu.primary_bar;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.right_sidebar_menu.primary_bar.elements.GroupMenu;
import org.foxtrot.pages.chat.right_sidebar_menu.tabs.members_menu.elements.MembersMenu;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class GroupMenuSteps {

    private final GroupMenu elements = new GroupMenu();
    private final MembersMenu membersMenu = new MembersMenu();

    @Step("Открыть меню редактирования группы")
    public void clickOpenSetingsButton() {
        $(elements.getOpenMenuButton()).click();
    }

    @Step("Закрыть сайдбар-меню группы")
    public GroupMenuSteps closeGroupMenu() {
        SelenideElement closeButton = $(elements.getMenuCloseButton()).shouldBe(visible, Duration.ofSeconds(5));
        closeButton.click();
        $(elements.getOpenMenuButton()).shouldNotBe(visible, Duration.ofSeconds(5));
        return this;
    }

    //решить вопрос с этим методом (временный костыль для тестов)
    @Step("Добавить пользователя в группу")
    public void addUserToGroup(long userId) {
        $(elements.getMembersButton()).click();
        $(membersMenu.getAddUserInput()).setValue(String.valueOf(userId)).pressEnter();
    }

    @Step("Выключить уведомления из группы")
    public void NotificationsOnToggle() {
        $(elements.getNotificationsOnToggle()).click();
    }

    @Step("Включить уведомления из группы")
    public void NotificationsOffToggle() {
        $(elements.getNotificationsOffToggle()).click();
    }

    @Step("Открыть меню 'Вложения группы'")
    public void AttachmentsButton() {
        $(elements.getAttachmentsButton()).click();
    }

    @Step("Открыть меню 'Закрепленные Сообщения'")
    public void PinnedMessagesButton() {
        $(elements.getPinnedMessagesButton()).click();
    }

    @Step("Открыть окно 'Пригласить по ссылке'")
    public void clickInviteButton() {
        $(elements.getInviteButton()).click();
    }

    @Step("Открыть меню 'Участники'")
    public void clickMembersButton() {
        $(elements.getMembersButton()).click();
    }

    @Step("Открыть меню 'Администраторы'")
    public void AdminsButton() {
        $(elements.getAdminsButton()).click();
    }

    @Step("Открыть меню 'Автоудаление сообщений'")
    public void AutodeleteButton() {
        $(elements.getOpenAutodeleteButton()).click();
    }

    @Step("Открыть модальное окно 'Передать права Владельца'")
    public void TransferOwnershipButton() {
        $(elements.getTransferOwnershipButton()).click();
    }

    @Step("Открыть модальное окно 'Настройки'")
    public void clickSettingsButton() {
        $(elements.getOpenMenuButton()).click();
    }

    @Step("Открыть модальное окно 'Очистка истории чата'")
    public void clickClearHistoryButton() {
        $(elements.getClearHistoryButton()).click();
    }

    @Step("Открыть модальное окно 'Удалить группу'")
    public void deleteGroupButton() {
        $(elements.getDeleteGroupButton()).click();
    }

    @Step("Открыть модальное окно 'Покинуть группу'")
    public void leaveButton() {
        $(elements.getLeaveButton()).click();
    }

    @Step("Проверить, что данные группы отредактированы")
    public void verifyEditGroup(String name, String description) {
        verifyAvatar();
        verifyName(name);
        verifyDescription(description);
    }

    @Step("Проверить, что данные группы отредактированы")
    public void verifyEditGroup(String name, String description, String groupType) {
        verifyAvatar();
        verifyName(name);
        verifyDescription(description);
        verifyGroupType(groupType);
    }

    @Step("Проверить, что появился аватар")
    public GroupMenuSteps verifyAvatar() {
        $(elements.getHeaderAvatar()).shouldBe(visible);
        return this;
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
    public void verifyGroupType(String channelType) {
        $(elements.getHeaderInfoBlock()).shouldHave(text(channelType));
    }
}
