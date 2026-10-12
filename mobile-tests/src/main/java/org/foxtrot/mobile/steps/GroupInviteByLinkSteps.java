package org.foxtrot.mobile.steps;

import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.GroupInviteByLinkScreen;
import org.junit.jupiter.api.Assertions;

public class GroupInviteByLinkSteps {

    GroupInviteByLinkScreen groupInviteByLinkScreen = new GroupInviteByLinkScreen();

    @Step("Проверяю, что экран 'Пригласить в группу по ссылке' отображается")
    public GroupInviteByLinkSteps checkScreenPresent() {
        Assertions.assertTrue(groupInviteByLinkScreen.isScreenTitleDisplayed(15L),
                "Экран 'Пригласить в группу по ссылке' не отображается");
        return this;
    }

    @Step("Проверяю, что заголовок экрана - '{0}'")
    public GroupInviteByLinkSteps checkScreenTitle(String title) {
        Assertions.assertEquals(title, groupInviteByLinkScreen.getScreenTitleText(),
                "Заголовок экрана приглашения по ссылке не совпадает с ожидаемым");
        return this;
    }

    @Step("Проверяю, что в поле ссылка-приглашение - '{0}'")
    public GroupInviteByLinkSteps checkInviteLink(String expectedLink) {
        Assertions.assertEquals(expectedLink, groupInviteByLinkScreen.getInviteLinkText(),
                "Ссылка приглашение в поле не совпадает с ожидаемой");
        return this;
    }

    @Step("Нажимаю кнопку 'Скопировать'")
    public GroupInviteByLinkSteps clickCopyLink() {
        groupInviteByLinkScreen.clickCopyLinkButton();
        return this;
    }

    @Step("Нажимаю кнопку 'Сгенерировать новую ссылку'")
    public GroupInviteByLinkSteps clickGenerateNewLink() {
        groupInviteByLinkScreen.clickGenerateNewLinkButton();
        return this;
    }

    @Step("Проверяю, что ссылка-приглашение изменилась")
    public GroupInviteByLinkSteps checkInviteLinkChanged(String previousLink) {
        Assertions.assertNotEquals(previousLink, groupInviteByLinkScreen.getInviteLinkText(),
                "Ссылка приглашение не изменилась");
        return this;
    }
}
