package org.foxtrot.assertions.verification;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.pages.chat.right_sidebar_menu.primary_bar.elements.P2pChatMenu;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.checked;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class P2pMenuVerifier {

    private final P2pChatMenu p2pChatMenu = new P2pChatMenu();

    @Step("Проверить элементы сайдбар-меню P2P: тип={type}, уведомления={notificationStatus}, имя={userName}")
    public P2pMenuVerifier verifyP2pSidebarMenuElements(String type, String notificationStatus, String userName) {
        verifyUserName(userName);
        verifyCloseSidebarMenuButton();

        String typeNorm = type.toLowerCase();
        String notifNorm = notificationStatus.toLowerCase();
        boolean isContact = "contact".equals(typeNorm);
        boolean isNonContact = "noncontact".equals(typeNorm);
        boolean isBlocked = "blocked".equals(typeNorm) || "blockedme".equals(typeNorm);
        boolean notifEnabled = "enabled".equals(notifNorm);
        boolean notifDisabled = "disabled".equals(notifNorm);

        // Валидация входных параметров
        if (!isContact && !isNonContact && !isBlocked) {
            throw new IllegalArgumentException("Недопустимый тип пользователя: " + type);
        }
        if ((isContact || isNonContact) && !notifEnabled && !notifDisabled) {
            throw new IllegalArgumentException("Недопустимый статус уведомлений: " + notificationStatus);
        }

        // Общие элементы для всех типов (кроме заблокированных)
        if (!isBlocked) {
            verifyAttachmentsButton();
            verifyPinnedMessagesButton();
            verifyAppearanceButton();
            verifyMessageButton();
            verifyClearChatHistoryButton();
        }

        // Проверка уведомлений
        if (isContact || isNonContact) {
            if (notifEnabled) {
                verifyNotificationToggleOn();
            } else {
                verifyNotificationToggleOff();
            }
        }

        // Кнопка звонка
        verifyCallButtonState(isBlocked, isContact, isNonContact, notifEnabled);

        // Специфичные кнопки
        if (isContact && notifDisabled) {
            verifyDeleteContactButton();
        }

        if (isNonContact) {
            if (notifEnabled) {
                verifyCopyProfileLinkButton();
                verifyAutodeleteMessagesButton();
                verifyBlockUserButton();
            } else {
                verifyAddContactButton();
                verifyBlockUserButton();
            }
        }

        if (isBlocked) {
            verifyUnblockUserButton();
        }

        return this;
    }

    @Step("Проверить элементы сайдбар-меню заблокированного-P2P: имя={userName}")
    public P2pMenuVerifier verifySidebarMenuElementsForBlockedUser(String userName) {
        verifyUnblockUserButton();
        //.verifyPhoneButton()
        //.verifyShowMoreButton()
        return this;
    }

    @Step("Проверить элементы сайдбар-меню P2P (заблокировал меня): имя={userName}")
    //добавить методы проверки атрибутов
    public P2pMenuVerifier verifySidebarMenuElementsForUserBlockedMe(String userName) {
        verifyUnblockUserButton();
        //.verifyPhoneButton()
        //.verifyShowMoreButton()
        return this;
    }

    @Step("Проверить имя пользователя: {expectedName}")
    public P2pMenuVerifier verifyUserName(String expectedName) {
        $(p2pChatMenu.getHeaderBlock())
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text(expectedName));
        return this;
    }

    @Step("Проверить наличие номера пользователя {expectedName} в шапке профиля")
    public P2pMenuVerifier verifyUserPhone(UsersEnum user) {
        $(p2pChatMenu.getHeaderBlock())
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text(user.getFormattedPhone()));
        return this;
    }

    @Step("Проверить наличие кнопки [X] (Закрыть сайдбар-меню P2P-чата)")
    public P2pMenuVerifier verifyCloseSidebarMenuButton() {
        $(p2pChatMenu.getCloseMenuButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки [Написать]")
    public P2pMenuVerifier verifyMessageButton() {
        $(p2pChatMenu.getMessageButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки [Позвонить]")
    public P2pMenuVerifier verifyCallButton() {
        $(p2pChatMenu.getCallButton()).shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие тоггла 'Уведомления: ВКЛ'")
    public P2pMenuVerifier verifyNotificationToggleOn() {
        $(p2pChatMenu.getNotificationsToggle())
                .should(exist, Duration.ofSeconds(5))
                .shouldBe(checked);
        return this;
    }

    @Step("Проверить наличие тоггла 'Уведомления: ВЫКЛ'")
    public P2pMenuVerifier verifyNotificationToggleOff() {
        $(p2pChatMenu.getNotificationsToggle())
                .should(exist, Duration.ofSeconds(5))
                .shouldNotBe(checked);
        return this;
    }

    @Step("Проверить наличие кнопки [Вложения]")
    public P2pMenuVerifier verifyAttachmentsButton() {
        $(p2pChatMenu.getAttachmentsButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки [Закрепленные сообщения]")
    public P2pMenuVerifier verifyPinnedMessagesButton() {
        $(p2pChatMenu.getPinnedMessagesButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки [Оформление чата]")
    public P2pMenuVerifier verifyAppearanceButton() {
        $(p2pChatMenu.getAppearanceButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки [Скопировать ссылку на профиль]")
    public P2pMenuVerifier verifyCopyProfileLinkButton() {
        $(p2pChatMenu.getCopyProfileLinkButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки [Автоудаление сообщений]")
    public P2pMenuVerifier verifyAutodeleteMessagesButton() {
        $(p2pChatMenu.getAutodeleteMessagesButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить, что автоудаление сообщений включено")
    public P2pMenuVerifier verifyAutodeleteIsActive() {
        $(p2pChatMenu.getAutodeleteMessagesButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldNotHave(text("Откл"));
        return this;
    }

    @Step("Проверить наличие кнопки [Добавить в контакты]")
    public P2pMenuVerifier verifyAddContactButton() {
        $(p2pChatMenu.getAddContactButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки [Удалить контакт]")
    public P2pMenuVerifier verifyDeleteContactButton() {
        $(p2pChatMenu.getDeleteContactButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки [Заблокировать пользователя]")
    public P2pMenuVerifier verifyBlockUserButton() {
        $(p2pChatMenu.getBlockUserButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки [Разблокировать пользователя]")
    public P2pMenuVerifier verifyUnblockUserButton() {
        $(p2pChatMenu.getUnblockUserButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие кнопки [Очистить историю сообщений]")
    public P2pMenuVerifier verifyClearChatHistoryButton() {
        $(p2pChatMenu.getClearChatHistoryButton())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить отсутствие номера {unexpectedPhone} в шапке профиля не контакта")
    public P2pMenuVerifier verifyNonContactHeaderHasNoPhone(String unexpectedPhone) {
        $(p2pChatMenu.getMessageButton()).shouldBe(visible);
        $(p2pChatMenu.getUserName()).shouldNotHave(Condition.text(unexpectedPhone));
        return this;
    }

    @Step("Проверить наличие номера {unexpectedPhone} в шапке профиля не контакта")
    public P2pMenuVerifier verifyNonContactHeaderHasPhone(String unexpectedPhone) {
        $(p2pChatMenu.getMessageButton()).shouldBe(visible);
        $(p2pChatMenu.getUserName()).shouldHave(Condition.text(unexpectedPhone));
        return this;
    }

    @Step("Проверить наличие заглушки с инициалами в шапке профиля")
    public P2pMenuVerifier verifyProfileHeaderHasInitialsAvatarPlaceholder() {
        $(p2pChatMenu.getMessageButton()).shouldBe(visible);
        $(p2pChatMenu.getUserPhoto()).shouldNotBe(visible);
        $(p2pChatMenu.getUserAvatarPlaceholder()).shouldNotBe(visible);
        return this;
    }

    @Step("Проверить наличие серой заглушки в шапке профиля")
    public P2pMenuVerifier verify2pHasUserSilhouettePlaceholder() {
        $(p2pChatMenu.getMessageButton()).shouldBe(visible);
        $(p2pChatMenu.getUserPhoto()).shouldNotBe(visible);
        $(p2pChatMenu.getUserAvatarPlaceholder()).shouldBe(visible);
        return this;
    }

    @Step("Проверить наличие аватарки в шапке профиля")
    public P2pMenuVerifier verifyProfileHeaderHasAvatar() {
        $(p2pChatMenu.getMessageButton()).shouldBe(visible);
        $(p2pChatMenu.getUserPhoto()).shouldBe(visible);
        $(p2pChatMenu.getUserAvatarPlaceholder()).shouldNotBe(visible);
        return this;
    }

    private void verifyCallButtonState(boolean isBlocked, boolean isContact, boolean isNonContact, boolean notifEnabled) {
        SelenideElement callButton = $(p2pChatMenu.getCallButton());
        if (!callButton.isDisplayed()) {
            return;
        }

        boolean isDisabled = callButton.getAttribute("disabled") != null;
        boolean shouldBeActive = !isBlocked && ((isContact && notifEnabled) || (isNonContact && notifEnabled));

        if (shouldBeActive && isDisabled) {
            throw new AssertionError("Кнопка 'Позвонить' должна быть активна для " +
                    (isContact ? "контакта" : "неконтакта") + " с включёнными уведомлениями");
        }

        if (isBlocked && !isDisabled) {
            throw new AssertionError("Кнопка 'Позвонить' не должна быть активна для заблокированного пользователя");
        }
    }
}