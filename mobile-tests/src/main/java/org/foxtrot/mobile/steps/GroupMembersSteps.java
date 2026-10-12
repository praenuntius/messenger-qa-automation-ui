package org.foxtrot.mobile.steps;

import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.GroupMembersScreen;
import org.junit.jupiter.api.Assertions;

public class GroupMembersSteps {

    GroupMembersScreen groupMembersScreen = new GroupMembersScreen();

    @Step("Проверяю, что на странице участников группы заголовок - '{0}'")
    public GroupMembersSteps checkScreenTitle(String title) {
        Assertions.assertEquals(title, groupMembersScreen.getScreenHeaderText(), "Заголовок на странице не " +
                "соответствует ожидаемому");
        return this;
    }

    @Step("Нажимаю на кнопку 'Назад'")
    public GroupMembersSteps clickBackBtn() {
        groupMembersScreen.clickBackBtn();
        return this;
    }

    @Step("Нажимаю на кнопку добавления участников")
    public GroupMembersSteps clickAddParticipants() {
        groupMembersScreen.clickAddParticipantsBtn();
        return this;
    }

    @Step("Нажимаю на иконку 'Поиск'")
    public GroupMembersSteps clickSearchBtn() {
        groupMembersScreen.clickSearchBtn();
        return this;
    }

    @Step("Делаю поиск по тексту '{0}'")
    public GroupMembersSteps searchByText(String text) {
        groupMembersScreen.clearSearchInput();
        groupMembersScreen.fillSearchInput(text);
        return this;
    }

    @Step("Проверяю, что subtitle страницы участников - '{0}'")
    public GroupMembersSteps checkSubTitle(String text) {
        Assertions.assertEquals(text, groupMembersScreen.getSubTitleText(),
                "Subtitle на странице участников не совпадает с ожидаемым");
        return this;
    }

    @Step("Проверяю, что участник под номером '{0}' имеет имя - '{1}'")
    public GroupMembersSteps checkContactInfoByNumber(int number, String contactName) {
        String expected = contactName.replace(" Отчество ", " ").trim();
        Assertions.assertEquals(expected, groupMembersScreen.getContactNameText(number),
                "Участник не найден");
        return this;
    }

    @Step("Проверяю, что на странице присутствует участник с именем '{0}'")
    public GroupMembersSteps checkContactInfoByName(String contactName) {
        Assertions.assertTrue(groupMembersScreen.isContactByNameDisplayed(contactName),
                "Участник не найден");
        return this;
    }

    @Step("Нажимаю на участника с именем - {0}")
    public GroupMembersSteps clickContactByName(String contactName) {
        groupMembersScreen.clickContactByName(contactName);
        return this;
    }
}
