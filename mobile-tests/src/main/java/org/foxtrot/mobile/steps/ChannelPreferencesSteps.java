package org.foxtrot.mobile.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.ChannelPreferencesScreen;
import org.junit.jupiter.api.Assertions;

public class ChannelPreferencesSteps {

    ChannelPreferencesScreen channelPreferencesScreen = new ChannelPreferencesScreen();

    @Step("Проверяю, что страница настроек чата отображается")
    public ChannelPreferencesSteps checkChatPreferencesScreenPresent() {
        Assertions.assertTrue(channelPreferencesScreen.isScreenHeaderDisplayed(15L),
                "Хэдер на странице настроек чата не удалось найти");
        return this;
    }

    @Step("Проверяю, что на странице настроек чата название группы - '{0}'")
    public ChannelPreferencesSteps checkChatPreferencesScreenChannelName(String content) {
        Assertions.assertEquals(content, channelPreferencesScreen.getChannelNameInHeader(),
                "В хэдере другое название чата");
        return this;
    }

    @Step("Проверяю, что на странице настроек в subtitle канала текст - '{0}'")
    public ChannelPreferencesSteps checkChatPreferencesScreenChannelSubName(String content) {
        Assertions.assertEquals(content, channelPreferencesScreen.getChannelSubNameInHeader(),
                "В хэдере другой subtitle");
        return this;
    }

    @Step("Проверяю, что на странице настроек в description канала текст - '{0}'")
    public ChannelPreferencesSteps checkChatPreferencesScreenChannelDescription(String content) {
        Assertions.assertEquals(content, channelPreferencesScreen.getChannelDescriptionInHeader(),
                "В хэдере другой description");
        return this;
    }

    @Step("Открываю страницу 'Подписчики'")
    public ChannelPreferencesSteps openParticipantsList() {
        channelPreferencesScreen.clickParticipantsBtn();
        return this;
    }

    @Step("Открываю страницу 'Настройки'")
    public ChannelPreferencesSteps openEditChannel() {
        channelPreferencesScreen.clickSettingsBtn();
        return this;
    }

    @Step("Проверяю аватарку в хэдере настроек чата")
    public ChannelPreferencesSteps checkChatAvatar(boolean isPlaceholder) {
        if (isPlaceholder) {
            Allure.step("Аватар отображается как плэйсхолдер (с буквой)");
            Assertions.assertTrue(channelPreferencesScreen.isChatAvatarPlaceholder(), "У аватара нет буквы");
        } else {
            Allure.step("Аватар отображается как картинка (без буквы)");
            Assertions.assertFalse(channelPreferencesScreen.isChatAvatarPlaceholder(), "У аватара присутствует буква");
        }
        return this;
    }
}
