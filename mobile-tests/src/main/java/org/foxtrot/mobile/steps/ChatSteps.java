package org.foxtrot.mobile.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.foxtrot.enums.DocumentFileExtension;
import org.foxtrot.mobile.screens.ChatScreen;
import org.foxtrot.mobile.screens.SelectFileScreen;
import org.foxtrot.mobile.screens.bottomSheet.SelectImageTypeBottomSheet;
import org.foxtrot.mobile.screens.bottomSheet.SelectMediaBottomSheet;
import org.foxtrot.mobile.steps.modals.ModalSteps;
import org.foxtrot.mobile.steps.modals.OnboardingSteps;
import org.foxtrot.mobile.testdata.dictionary.AddMediaDictionary;
import org.foxtrot.mobile.utils.AttachmentUtils;
import org.junit.jupiter.api.Assertions;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public class ChatSteps {

    ChatScreen chatScreen = new ChatScreen();
    SelectImageTypeBottomSheet selectImageTypeBottomSheet = new SelectImageTypeBottomSheet();
    SelectMediaBottomSheet selectMediaBottomSheet = new SelectMediaBottomSheet();
    SelectFileScreen selectFileScreen = new SelectFileScreen();
    ModalSteps modalSteps = new ModalSteps();
    OnboardingSteps onboardingSteps = new OnboardingSteps();

    @Step("Проверяю, что название чата - '{0}'")
    public ChatSteps checkChatTitle(String title) {
        Assertions.assertEquals(title, chatScreen.getChatNameFromHeader(),
                "Текст в заголовке страницы не совпадает с ожидаемым");
        return this;
    }

    @Step("Проверяю, что системное сообщение под номером '{0}' - '{1}'")
    public ChatSteps checkSystemMessage(int number, String content) {
        Assertions.assertEquals(content, chatScreen.getSystemMessageText(number),
                "Текст в системном сообщении не совпадает с ожидаемым");
        return this;
    }

    @Step("Проверяю, что системное сообщение под номером '{0}' имеет слова: '{1}'")
    public ChatSteps checkSystemMessageContainsText(int number, String... contents) {
        String actualText = chatScreen.getSystemMessageText(number);
        for (String content : contents) {
            Assertions.assertTrue(actualText.contains(content),
                    "Текст в системном сообщении не совпадает с ожидаемым");
        }
        return this;
    }

    @Step("Проверяю, что в поле статуса в хэдере отображается - '{0}'")
    public ChatSteps checkStatusContentInHeader(String content) {
        Assertions.assertEquals(content, chatScreen.getChatStatusFromHeader(),
                "Текст в поле статуса в хэдере не совпадает с ожидаемым");
        return this;
    }

    @Step("Проверяю, что на странице отсутствуют блоки с сообщениями")
    public ChatSteps checkNoMessageViewsOnChatScreen() {
        Assertions.assertFalse(chatScreen.isMessageContentViewDisplayed(),
                "На странице присутствуют блоки с сообщениями, а не должны");
        return this;
    }

    @Step("Проверяю, что страница чата отображается")
    public ChatSteps checkChatScreenDisplayed() {
        return checkChatScreenDisplayed(null);
    }

    @Step("Проверяю, что страница чата '{0}' отображается")
    public ChatSteps checkChatScreenDisplayed(String expectedChatName) {
        onboardingSteps.clickOnboardingActionButtonIfNeeded();
        if (chatScreen.isChatScreenDisplayed(expectedChatName, 8L)) {
            return this;
        }
        onboardingSteps.clickOnboardingActionButtonIfNeeded();
        if (chatScreen.isChatScreenDisplayed(expectedChatName, 15L)) {
            return this;
        }
        AttachmentUtils.dumpUiState("chat-check-failed");
        Assertions.fail(expectedChatName == null
                ? "Страница чата не отображается"
                : "Страница чата '" + expectedChatName + "' не отображается");
        return this;
    }

    public boolean isChatScreenDisplayedSoft(String expectedChatName, long seconds) {
        return chatScreen.isChatScreenDisplayed(expectedChatName, seconds);
    }

    @Step("Нажимаю на название чата в хэдере")
    public ChatSteps clickChatNameInHeader() {
        chatScreen.clickChatName();
        return this;
    }

    @Step("Отправляю текстовое сообщение '{0}'")
    public ChatSteps sendTextMessage(String text) {
        chatScreen.enterMessage(text);
        chatScreen.clickSend();
        return this;
    }

    @Step("Проверяю, что в чате отображается текст '{0}'")
    public ChatSteps checkMessageDisplayed(String text) {
        Assertions.assertTrue(chatScreen.isChatMessageTextDisplayed(text, 15L),
                "Сообщение с текстом '" + text + "' не найдено в чате");
        return this;
    }

    @Step("Проверяю аватарку в хэдере чата")
    public ChatSteps checkChatAvatar(boolean isPlaceholder) {
        if (isPlaceholder) {
            Allure.step("Аватар отображается как плэйсхолдер (с буквой)");
            Assertions.assertTrue(chatScreen.isChatAvatarPlaceholder(), "У аватара нет буквы");
        } else {
            Allure.step("Аватар отображается как картинка (без буквы)");
            Assertions.assertFalse(chatScreen.isChatAvatarPlaceholder(), "У аватара присутствует буква");
        }
        return this;
    }

    @Step("Нажимаю на сообщение под номером '{0}'")
    public ChatSteps clickChatMessage(int number) {
        chatScreen.clickChatMessage(number);
        return this;
    }

    @Step("Проверяю, что появился алерт с текстом - '{0}'")
    public ChatSteps checkAlertDisplayedOnChatScreen(String text) {
        Assertions.assertTrue(chatScreen.isAlertMessageDisplayed(text, 15L),
                "Алерт с текстом %s не появился".formatted(text));
        return this;
    }

    @Step("Добавляю медиа в сообщение")
    public ChatSteps attachMediaToMessage() {
        chatScreen.clickAttachBtn();
        Assertions.assertTrue(selectImageTypeBottomSheet.isBottomSheetDisplayed(15L),
                "Bottom sheet выбора типа изображения не отображается");
        selectImageTypeBottomSheet.clickBottomSheetBtn(AddMediaDictionary.CHOOSE_FROM_GALLERY);
        if (isIos()) {
            modalSteps.acceptSystemAlert(AddMediaDictionary.ACCEPT_FULL);
        }
        Assertions.assertTrue(selectMediaBottomSheet.isBottomSheetDisplayed(15L),
                "Bottom sheet выбора изображения не отображается");
        selectMediaBottomSheet.clickBottomSheetBtn(1);
        selectMediaBottomSheet.clickSendMediaBtn();
        if (isIos()) { // todo нет локатора
            Assertions.assertTrue(chatScreen.isChatScreenDisplayed(15L),
                    "Страница чата не отображается");
        } else {
            Assertions.assertTrue(chatScreen.isMediaImageDisplayed(1, 15L),
                    "Добавленный медиа на страница чата не отображается");
        }
        return this;
    }

    @Step("Добавляю файл в сообщение")
    public ChatSteps attachFileToMessage(DocumentFileExtension extension) {
        String fileName = extension.getFileName();
        chatScreen.clickAttachBtn();
        Assertions.assertTrue(selectImageTypeBottomSheet.isBottomSheetDisplayed(15L),
                "Bottom sheet выбора типа вложения не отображается");
        selectImageTypeBottomSheet.clickBottomSheetBtn(AddMediaDictionary.ATTACH_FILE);
        Assertions.assertTrue(selectFileScreen.isBottomSheetDisplayed(15L),
                "Bottom sheet выбора файла не отображается");
        selectFileScreen.clickFileBtn(fileName);
        Assertions.assertTrue(chatScreen.isChatScreenDisplayed(15L),
                "Страница чата не отображается");
        return this;
    }

    @Step("Проверяю состояние кнопки '{0}' на странице чата")
    public ChatSteps checkJoinBtnDisplayed(String buttonText, boolean shouldBeDisplayed) {
        if (shouldBeDisplayed) {
            Allure.step("Проверяю, что кнопка '" + buttonText + "' отображается");
            Assertions.assertTrue(chatScreen.isJoinBtnDisplayed(buttonText),
                    "Кнопка '" + buttonText + "' не отображается");
        } else {
            Allure.step("Проверяю, что кнопка '" + buttonText + "' не отображается");
            Assertions.assertFalse(chatScreen.isJoinBtnDisplayed(buttonText),
                    "Кнопка '" + buttonText + "' отображается");
        }
        return this;
    }

    @Step("Нажимаю кнопку '{0}' на странице чата")
    public ChatSteps clickJoinBtn(String buttonText) {
        chatScreen.clickJoinBtn(buttonText);
        return this;
    }

    @Step("Проверяю состояние кнопки '{0}' на странице чата")
    public ChatSteps checkSubscribeBtnDisplayed(String buttonText, boolean shouldBeDisplayed) {
        if (shouldBeDisplayed) {
            Allure.step("Проверяю, что кнопка '" + buttonText + "' отображается");
            Assertions.assertTrue(chatScreen.isSubscribeBtnDisplayed(buttonText),
                    "Кнопка '" + buttonText + "' не отображается");
        } else {
            Allure.step("Проверяю, что кнопка '" + buttonText + "' не отображается");
            Assertions.assertFalse(chatScreen.isSubscribeBtnDisplayed(buttonText),
                    "Кнопка '" + buttonText + "' отображается");
        }
        return this;
    }

    @Step("Нажимаю кнопку '{0}' на странице чата")
    public ChatSteps clickSubscribeBtn(String buttonText) {
        chatScreen.clickSubscribeBtn(buttonText);
        return this;
    }
}
