package org.foxtrot.pages.snackbars;

import io.qameta.allure.Step;
import org.foxtrot.pages.snackbars.elements.Snackbars;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class SnackbarsSteps {

    private final Snackbars elements = new Snackbars();

    @Step("Проверить отображение снекбара 'Текст скопирован'")
    public SnackbarsSteps verifyTextCopiedSnackbarVisible() {
        sleep(600);
        $(elements.getTextCopied())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить отображение снекбара 'Скопировано'")
    public SnackbarsSteps verifyCopiedSnackbarVisible() {
        sleep(600);
        $(elements.getCopied())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить отображение снекбара 'Ссылка скопированна'")
    public SnackbarsSteps verifyCopiedLinkSnackbarVisible() {
        sleep(600);
        $(elements.getLinkCopied())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить отображение снекбара 'Сообщение отправлено в Сохраненные'")
    public SnackbarsSteps verifySentIntoSaveSnackbarVisible() {
        sleep(600);
        $(elements.getSentToSaved())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить отображение снекбара-тултипа 'Не применяется при скрытом имени'")
    public SnackbarsSteps verifyHideMessageAuthorTooltipVisible() {
        sleep(600);
        $(elements.getShowMessageAuthor())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить наличие снекбара с сообщением о удалении чата")
    public SnackbarsSteps verifyChatDeletedSnackBarIsVisible() {
        $(elements.getChatDeleteSnackBar()).shouldBe(visible);
        return this;
    }

    @Step("Проверить отображение снекбара 'История сообщений очищена'")
    public SnackbarsSteps verifyClearHistoryMessageIsVisible() {
        $(elements.getClearP2pHistoryForSelf())
                .shouldBe(exist, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить отображение снекбара 'Ссылка больше не активна'")
    public SnackbarsSteps verifyInviteLinkInactive() {
        sleep(600);
        $(elements.getInviteLinkInactive())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить отображение снекбара 'Пространство было удалено или вы были удалены из него'")
    public SnackbarsSteps verifyWorkSpaceDeleted() {
        sleep(600);
        $(elements.getWorkSpaceDeleted())
                .shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }  //возникает при попытке входа в удаленное пространстно из окна доступных пространств

    @Step("Проверить появление уведомления об успешном удалении пространств")
    public void verifyDeleteSuccessNotificationIsDisplayed(String text) {
        $(elements.getWorkspacesDeletedNotification())
                .shouldBe(visible)
                .shouldHave(text(text));
    }

    @Step("Проверить отображение снекбара 'Начало экспорта истории чата «Tango»'")
    public SnackbarsSteps verifyChatHistoryExportIsVisible() {
        $(elements.getExportChatHistory())
                .shouldBe(visible);
        return this;
    }
}
