package org.foxtrot.mobile.steps.workspace;

import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.workspace.CreateNewMessageFromWorkspaceScreen;
import org.junit.jupiter.api.Assertions;

public class CreateNewMessageFromWorkspaceSteps {

    CreateNewMessageFromWorkspaceScreen createNewMessageFromWorkspaceScreen = new CreateNewMessageFromWorkspaceScreen();

    @Step("Проверяю, что заголовок страницы - '{0}'")
    public CreateNewMessageFromWorkspaceSteps checkCreateNewMessageScreenTitle(String title) {
        Assertions.assertTrue(createNewMessageFromWorkspaceScreen.isScreenTitleDisplayed(title, 15L),
                "Заголовок страницы '" + title + "' не отображается");
        return this;
    }

    @Step("Проверяю, что подзаголовок страницы - '{0}'")
    public CreateNewMessageFromWorkspaceSteps checkCreateNewMessageScreenSubtitle(String subTitle) {
        Assertions.assertTrue(createNewMessageFromWorkspaceScreen.isScreenSubtitleDisplayed(subTitle, 15L),
                "Подзаголовок страницы '" + subTitle + "' не отображается");
        return this;
    }

    @Step("Проверяю, что на странице создания сообщения отображается чат '{0}'")
    public CreateNewMessageFromWorkspaceSteps checkChatNameDisplayed(String chatName) {
        Assertions.assertTrue(createNewMessageFromWorkspaceScreen.isChatNameByTextDisplayed(chatName, 15L),
                "Чат не отображается");
        return this;
    }

    @Step("Проверяю, что на странице создания сообщения есть кнопки - '{0}', '{1}'")
    public CreateNewMessageFromWorkspaceSteps checkBtnTextsOnCreateNewMessageScreen(String btn1, String btn2) {
        Assertions.assertEquals(btn1, createNewMessageFromWorkspaceScreen.getCreateGroupButtonText(),
                "У кнопки 1 отображается текст не соответствующий ожидаемому");
        Assertions.assertEquals(btn2, createNewMessageFromWorkspaceScreen.getCreateChannelButtonText(),
                "У кнопки 2 отображается текст не соответствующий ожидаемому");
        return this;
    }

    @Step("Нажимаю на кнопку 'Создать группу'")
    public CreateNewMessageFromWorkspaceSteps clickCreateGroupBtn() {
        createNewMessageFromWorkspaceScreen.clickCreateGroupButton();
        return this;
    }

    @Step("Нажимаю на кнопку 'Создать канал'")
    public CreateNewMessageFromWorkspaceSteps clickCreateChannelBtn() {
        createNewMessageFromWorkspaceScreen.clickCreateChannelButton();
        return this;
    }
}
