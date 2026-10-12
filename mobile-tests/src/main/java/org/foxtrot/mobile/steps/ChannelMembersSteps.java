package org.foxtrot.mobile.steps;

import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.ChannelMembersScreen;
import org.junit.jupiter.api.Assertions;

public class ChannelMembersSteps {

    ChannelMembersScreen channelMembersScreen = new ChannelMembersScreen();

    @Step("Проверяю, что на странице подписчиков канала заголовок - '{0}'")
    public ChannelMembersSteps checkScreenTitle(String title) {
        Assertions.assertEquals(title, channelMembersScreen.getScreenHeaderText(), "Заголовок на странице не " +
                "соответствует ожидаемому");
        return this;
    }

    @Step("Нажимаю на кнопку 'Назад'")
    public ChannelMembersSteps clickBackBtn() {
        channelMembersScreen.clickBackBtn();
        return this;
    }

    @Step("Нажимаю на кнопку добавления подписчиков")
    public ChannelMembersSteps clickAddParticipants() {
        channelMembersScreen.clickAddParticipantsBtn();
        return this;
    }

    @Step("Нажимаю на иконку 'Поиск'")
    public ChannelMembersSteps clickSearchBtn() {
        channelMembersScreen.clickSearchBtn();
        return this;
    }

    @Step("Делаю поиск по тексту '{0}'")
    public ChannelMembersSteps searchByText(String text) {
        channelMembersScreen.clearSearchInput();
        channelMembersScreen.fillSearchInput(text);
        return this;
    }

    @Step("Проверяю, что subtitle страницы подписчиков - '{0}'")
    public ChannelMembersSteps checkSubTitle(String text) {
        Assertions.assertEquals(text, channelMembersScreen.getSubTitleText(),
                "Subtitle на странице подписчиков не совпадает с ожидаемым");
        return this;
    }

    @Step("Проверяю, что подписчик под номером '{0}' имеет имя - '{1}'")
    public ChannelMembersSteps checkContactInfoByNumber(int number, String contactName) {
        String expected = contactName.replace(" Отчество ", " ").trim();
        Assertions.assertEquals(expected, channelMembersScreen.getContactNameText(number),
                "Подписчик не найден");
        return this;
    }

    @Step("Проверяю, что на странице присутствует подписчик с именем '{0}'")
    public ChannelMembersSteps checkContactInfoByName(String contactName) {
        Assertions.assertTrue(channelMembersScreen.isContactByNameDisplayed(contactName),
                "Подписчик не найден");
        return this;
    }

    @Step("Нажимаю на подписчика с именем - {0}")
    public ChannelMembersSteps clickContactByName(String contactName) {
        channelMembersScreen.clickContactByName(contactName);
        return this;
    }
}
