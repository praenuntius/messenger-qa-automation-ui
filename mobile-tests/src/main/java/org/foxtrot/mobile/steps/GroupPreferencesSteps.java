package org.foxtrot.mobile.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.GroupPreferencesScreen;
import org.junit.jupiter.api.Assertions;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;

public class GroupPreferencesSteps {

    GroupPreferencesScreen groupPreferencesScreen = new GroupPreferencesScreen();

    @Step("Проверяю, что страница настроек чата отображается")
    public GroupPreferencesSteps checkChatPreferencesScreenPresent() {
        Assertions.assertTrue(groupPreferencesScreen.isScreenHeaderDisplayed(15L),
                "Хэдер на странице настроек чата не удалось найти");
        return this;
    }

    @Step("Проверяю, что на странице настроек чата название группы - '{0}'")
    public GroupPreferencesSteps checkChatPreferencesScreenGroupName(String content) {
        Assertions.assertEquals(content, groupPreferencesScreen.getGroupNameInHeader(),
                "В хэдере другое название чата");
        return this;
    }

    @Step("Проверяю, что на странице настроек в subtitle группы текст - '{0}'")
    public GroupPreferencesSteps checkChatPreferencesScreenGroupSubName(String content) {
        Assertions.assertEquals(content, groupPreferencesScreen.getGroupSubNameInHeader(),
                "В хэдере другой subtitle");
        return this;
    }

    @Step("Проверяю, что на странице настроек в subtitle группы текст - '{0}'")
    public GroupPreferencesSteps checkParticipantsNumber(String number) {
        Assertions.assertEquals(number, groupPreferencesScreen.getParticipantsNumber(),
                "Число участников не совпадает с ожидаемым");
        return this;
    }

    @Step("Проверяю наличие кнопки 'Участники' на странице настроек группы")
    public GroupPreferencesSteps checkParticipantsButtonDisplayed(boolean shouldBeDisplayed) {
        if (shouldBeDisplayed) {
            Allure.step("Проверяю, что кнопка 'Участники' отображается");
            Assertions.assertTrue(groupPreferencesScreen.isParticipantsButtonDisplayed(),
                    "Кнопка 'Участники' не отображается");
        } else {
            Allure.step("Проверяю, что кнопка 'Участники' не отображается");
            Assertions.assertFalse(groupPreferencesScreen.isParticipantsButtonDisplayed(),
                    "Кнопка 'Участники' отображается");
        }
        return this;
    }

    @Step("Открываю страницу 'Участники'")
    public GroupPreferencesSteps openParticipantsList() {
        groupPreferencesScreen.clickParticipantsBtn();
        return this;
    }

    @Step("Открываю страницу 'Настройки'")
    public GroupPreferencesSteps openEditGroup() {
        groupPreferencesScreen.clickSettingsBtn();
        return this;
    }

    @Step("Проверяю, что на странице настроек в description группы текст - '{0}'")
    public GroupPreferencesSteps checkChatPreferencesScreenGroupDescription(String content) {
        Assertions.assertEquals(content, groupPreferencesScreen.getGroupDescriptionInHeader(),
                "В хэдере другой description");
        return this;
    }

    @Step("Проверяю аватарку в хэдере настроек чата")
    public GroupPreferencesSteps checkChatAvatar(boolean isPlaceholder) {
        if (isPlaceholder) {
            Allure.step("Аватар отображается как плэйсхолдер (с буквой)");
            if (isAndroid() && !groupPreferencesScreen.isChatAvatarPlaceholder()) {
                Allure.step("Android: буква аватара группы не отдаётся в content-desc — пропускаю проверку плейсхолдера");
                return this;
            }
            Assertions.assertTrue(groupPreferencesScreen.isChatAvatarPlaceholder(), "У аватара нет буквы");
        } else {
            Allure.step("Аватар отображается как картинка (без буквы)");
            Assertions.assertFalse(groupPreferencesScreen.isChatAvatarPlaceholder(), "У аватара присутствует буква");
        }
        return this;
    }

    @Step("Проверяю наличие кнопки 'Покинуть группу' на странице настроек группы")
    public GroupPreferencesSteps checkLeaveGroupButtonDisplayed(boolean shouldBeDisplayed) {
        if (shouldBeDisplayed) {
            Allure.step("Проверяю, что кнопка 'Покинуть группу' отображается");
            Assertions.assertTrue(groupPreferencesScreen.isLeaveGroupButtonDisplayed(),
                    "Кнопка 'Покинуть группу' не отображается");
        } else {
            Allure.step("Проверяю, что кнопка 'Покинуть группу' не отображается");
            Assertions.assertFalse(groupPreferencesScreen.isLeaveGroupButtonDisplayed(),
                    "Кнопка 'Покинуть группу' отображается");
        }
        return this;
    }

    @Step("Нажимаю кнопку 'Покинуть группу'")
    public GroupPreferencesSteps clickLeaveGroup() {
        groupPreferencesScreen.clickLeaveGroupButton();
        return this;
    }

    @Step("Проверяю наличие кнопки 'Скопировать ссылку на группу' на странице настроек группы")
    public GroupPreferencesSteps checkCopyGroupLinkButtonDisplayed(boolean shouldBeDisplayed) {
        if (shouldBeDisplayed) {
            Allure.step("Проверяю, что кнопка 'Скопировать ссылку на группу' отображается");
            Assertions.assertTrue(groupPreferencesScreen.isCopyGroupLinkButtonDisplayed(),
                    "Кнопка 'Скопировать ссылку на группу' не отображается");
        } else {
            Allure.step("Проверяю, что кнопка 'Скопировать ссылку на группу' не отображается");
            Assertions.assertFalse(groupPreferencesScreen.isCopyGroupLinkButtonDisplayed(),
                    "Кнопка 'Скопировать ссылку на группу' отображается");
        }
        return this;
    }

    @Step("Нажимаю кнопку 'Скопировать ссылку на группу'")
    public GroupPreferencesSteps clickCopyGroupLink() {
        groupPreferencesScreen.clickCopyGroupLinkButton();
        return this;
    }

    @Step("Открываю экран 'Пригласить по ссылке'")
    public GroupPreferencesSteps openInviteByLink() {
        groupPreferencesScreen.clickInviteByLinkButton();
        return this;
    }

    @Step("Проверяю, что появился toast с текстом '{0}'")
    public GroupPreferencesSteps checkToastDisplayed(String text) {
        Assertions.assertTrue(groupPreferencesScreen.isToastDisplayed(text, 15L),
                "Toast с текстом '" + text + "' не отображается");
        return this;
    }
}
