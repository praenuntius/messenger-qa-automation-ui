package org.foxtrot.pages.chat.input_bar;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.chat.elements.Chat;
import org.foxtrot.pages.chat.input_bar.elements.AttachContextMenu;
import org.foxtrot.pages.chat.input_bar.elements.InputBar;
import org.foxtrot.pages.key_interactions.PressKeys;
import org.foxtrot.pages.modals.elements.AttachImagesModal;
import org.foxtrot.utils.FileUploadHelper;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.foxtrot.pages.key_interactions.PressKeys.pressEscape;

public class InputBarSteps {

    private final InputBar inputBar = new InputBar();
    private final Chat chatElements = new Chat();
    private final AttachContextMenu attachContextMenu = new AttachContextMenu();
    private final AttachImagesModal attachImagesModal = new AttachImagesModal();
    private final FileUploadHelper fileUploadHelper = new FileUploadHelper();

    @Step("Вставить скопированный текст при помощи комбинации клавиш 'Ctrl+V'")
    public InputBarSteps pasteTextIntoInput() {
        PressKeys.pressCmdV();
        return this;
    }

    @Step("Вырезать текст при помощи комбинации клавиш 'Ctrl+X'")
    public InputBarSteps cuteTextIntoInput() {
        PressKeys.pressCmdX();
        return this;
    }

    @Step("Скопировать изображение '{filePath}' в буфер обмена")
    public InputBarSteps copyImageToClipboard(String filePath) {
        fileUploadHelper.copyImageToBrowserClipboard(filePath);
        return this;
    }

    @Step("Вставить изображение '{filePath}' в поле ввода через Ctrl+V")
    public InputBarSteps pasteImageIntoInput(String filePath) {
        copyImageToClipboard(filePath);
        clickMessageInputField();
        return pasteTextIntoInput();
    }

    @Step("Отформатировать текст комбинацией клавиш 'Ctrl+B' (жирный)")
    public InputBarSteps pressCmdB() {
        PressKeys.pressCmdB();
        return this;
    }

    @Step("Отформатировать текст комбинацией клавиш 'CMD+U' (подчеркнутый)")
    public InputBarSteps pressCmdU() {
        PressKeys.pressCmdU();
        return this;
    }

    @Step("Отформатировать текст комбинацией клавиш 'CMD+I' (подчеркнутый)")
    public InputBarSteps pressCmdI() {
        PressKeys.pressCmdI();
        return this;
    }

    @Step("Отформатировать текст комбинацией клавиш 'CMD+S' (зачеркнутый)")
    public InputBarSteps pressCmdS() {
        PressKeys.pressCmdS();
        return this;
    }

    @Step("Отформатировать текст комбинацией клавиш 'CMD+Shift+M' (моноширный)")
    public InputBarSteps pressCmdShiftM() {
        PressKeys.pressCmdShiftM();
        return this;
    }

    @Step("Отформатировать текст комбинацией клавиш 'CMD+K' (Создание гиперссылки)")
    public InputBarSteps pressCmdК() {
        PressKeys.pressCmdK();
        return this;
    }

    @Step("Кликнуть в поле ввода")
    public InputBarSteps clickMessageInputField() {
        $(inputBar.getMessageField()).shouldBe(visible, Duration.ofSeconds(10)).click();
        sleep(600);
        return this;
    }

    @Step("Кликнуть правой кнопкой мыши в поле ввода")
    public InputBarSteps rightClickMessageInputField() {
        $(inputBar.getMessageField()).shouldBe(visible, Duration.ofSeconds(10)).contextClick();
        sleep(600);
        return this;
    }

    @Step("Отправить сообщение")
    public InputBarSteps clickSendMessageButton() {
        sleep(600);
        SelenideElement send = $(inputBar.getMessageSendButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .scrollIntoView("{block: 'center'}");
        try {
            send.click();
        } catch (Throwable clickBlocked) {
            // overlay / attach preview / SKR — обычный click часто ElementClickIntercepted
            send.click(ClickOptions.usingJavaScript());
        }
        return this;
    }

    @Step("Вызвать контекстное меню у кнопки отправить")
    public InputBarSteps contextClickSendMessageButton() {
        sleep(600);
        $(inputBar.getMessageSendButton()).shouldBe(visible, Duration.ofSeconds(10)).contextClick();
        sleep(600);
        return this;
    }

    @Step("Выделить весь текст в поле ввода тройным кликом")
    public InputBarSteps selectAllTextByTripleClick() {
        SelenideElement inputField = $(inputBar.getMessageField())
                .shouldBe(visible, Duration.ofSeconds(10));
        Actions actions = new Actions(WebDriverRunner.getWebDriver());
        actions.moveToElement(inputField)
                .click()
                .click()
                .click()
                .perform();
        sleep(200);
        return this;
    }

    @Step("Поставить курсор в середину текста '{text}' в поле ввода")
    public InputBarSteps putCursorInMiddleOfText(String text) {
        int offset = text.length() / 2;

        actions().sendKeys(Keys.END).perform();

        for (int i = 0; i < text.length() - offset; i++) {
            actions().sendKeys(Keys.ARROW_LEFT).perform();
        }
        return this;
    }

    @Step("Поставить курсор в начало строки")
    public InputBarSteps moveToLineStart() {
        PressKeys.pressHome();
        return this;
    }

    @Step("Удалить {charactersCount} символов перед курсором")
    public InputBarSteps deleteCharactersBeforeCursor(int charactersCount) {
        for (int i = 0; i < charactersCount; i++) {
            PressKeys.pressBackSpace();
        }
        return this;
    }

    @Step("Выделить строку '{lineText}' в поле ввода тройным кликом")
    public InputBarSteps selectLineByTextWithTripleClick(String lineText) {
        SelenideElement line = $(inputBar.getMessageField())
                .shouldBe(visible, Duration.ofSeconds(10))
                .$(inputBar.getMessageEditor())
                .$$(inputBar.getEditorLine())
                .findBy(exactText(lineText))
                .shouldBe(visible);
        Actions actions = new Actions(WebDriverRunner.getWebDriver());
        actions.moveToElement(line)
                .click()
                .click()
                .click()
                .perform();
        return this;
    }

    @Step("Выделить текст в поле ввода при помощи курсора (от начала до конца)")
    public InputBarSteps selectTextByCursor() {
        SelenideElement inputField = $(inputBar.getMessageField())
                .shouldBe(visible, Duration.ofSeconds(10));
        Actions actions = new Actions(WebDriverRunner.getWebDriver());
        actions.moveToElement(inputField, -inputField.getSize().getWidth() / 2, 0)
                .click()
                .perform();
        sleep(100);
        actions.clickAndHold()
                .moveToElement(inputField, inputField.getSize().getWidth() / 2, 0)
                .release()
                .perform();
        sleep(200);
        return this;
    }

    @Step("Сделать перенос строки комбинацией клавиш 'Shift+Enter'")
    public InputBarSteps pressShiftEnter() {
        PressKeys.pressShiftEnter();
        return this;
    }

    @Step("Ввести текст в поле ввода сообщения")
    public InputBarSteps enterMessage(String messageText) {
        SelenideElement inputField = $(inputBar.getMessageField());
        new Actions(WebDriverRunner.getWebDriver())
                .moveToElement(inputField)
                .click()
                .sendKeys(messageText)
                .perform();
        sleep(1000);
        return this;
    }

    @Step("Вставить текст в поле ввода сообщения")
    public InputBarSteps pasteMessage(String messageText) {
        Selenide.clipboard().setText(messageText);
        SelenideElement inputField = $(inputBar.getMessageField());
        pasteTextIntoInput();
        sleep(500);
        return this;
    }

    @Step("Открыть меню Прикрепить")
    public InputBarSteps clickOpenAttachMenuButton() {
        $(inputBar.getOpenAttachContextMenuButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Проверить наличие кнопки отложенных сообщений")
    public InputBarSteps verifyScheduledMessagesButtonVisible() {
        $(inputBar.getScheduledMessagesButton())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить отсутствие кнопки отложенных сообщений")
    public InputBarSteps verifyScheduledMessagesButtonNotVisible() {
        $(inputBar.getScheduledMessagesButton())
                .shouldNotBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить наличие кнопки 'Вступить в группу'")
    public InputBarSteps verifyJoinToGroupButtonVisible() {
        $(inputBar.getJoinToGroupButton())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Кликнуть по кнопке 'Вступить в группу'")
    public InputBarSteps clickJoinToGroupButton() {
        $(inputBar.getJoinToGroupButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Проверить наличие кнопки 'Подписаться на канал'")
    public InputBarSteps verifySubscribeToChannelButtonVisible() {
        $(inputBar.getSubscribeToChannelButton())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Кликнуть по кнопке 'Подписаться на канал'")
    public InputBarSteps clickSubscribeToChannelButton() {
        $(inputBar.getSubscribeToChannelButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Открыть контекстное меню поля ввода сообщения")
    public InputBarSteps openInputFieldContextMenu() {
        SelenideElement inputField = $(inputBar.getMessageField())
                .shouldBe(visible, Duration.ofSeconds(10));
        new Actions(WebDriverRunner.getWebDriver())
                .moveToElement(inputField)
                .contextClick(inputField)
                .perform();
        return this;
    }

    @Step("Загрузить медиафайл '{filePath}' через attach меню")
    public InputBarSteps uploadMediaFileViaAttachMenu(String filePath) {
        clickOpenAttachMenuButton();
        SelenideElement fileInput = $(attachContextMenu.getFileInput())
                .should(Condition.exist);
        fileUploadHelper.uploadFileViaSelenideElement(fileInput, filePath);
        pressEscape();
        return this;
    }

    /**
     * Отправка картинки после upload: без Escape (он сбрасывает attach).
     * Сначала пробуем Send в модалке, иначе Send в input bar.
     */
    @Step("Загрузить и отправить медиафайл '{filePath}' через attach (без Escape)")
    public InputBarSteps uploadAndSendMediaViaAttachModal(String filePath) {
        clickOpenAttachMenuButton();
        SelenideElement fileInput = $(attachContextMenu.getFileInput())
                .should(Condition.exist);
        fileUploadHelper.uploadFileViaSelenideElement(fileInput, filePath);

        SelenideElement modalSend = $(attachImagesModal.getAttachImageModalSendButtonSelector());
        try {
            modalSend.shouldBe(visible, Duration.ofSeconds(5))
                    .click(ClickOptions.usingJavaScript());
            return this;
        } catch (Throwable ignored) {
            // Модалки нет — файл уже в composer; Escape НЕ жмём (сбрасывает вложение)
            clickSendMessageButton();
            return this;
        }
    }

    @Step("Кликнуть по кнопке меню тегов")
    public InputBarSteps clickTagMenuButton() {
        SelenideElement tagButton = $(inputBar.getTagMenuButton())
                .shouldBe(visible, Duration.ofSeconds(10));
        tagButton.parent().click();
        return this;
    }

    @Step("Проверить, что счетчик тегов в поле ввода равен '{expectedCount}'")
    public InputBarSteps verifyTagCounterInInputBar(int expectedCount) {
        $(inputBar.getTagCounterInInputBar())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(String.valueOf(expectedCount)));
        return this;
    }

    @Step("Проверить, что счетчик тегов в поле ввода отсутствует")
    public InputBarSteps verifyNoTagCounterInInputBar() {
        $(inputBar.getTagCounterInInputBar())
                .shouldNotBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить видимость поля ввода")
    public InputBarSteps verifyInputBarIsVisible() {
        $(inputBar.getMessageField())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что упоминание пользователя '{userName}' отображается в поле ввода")
    public InputBarSteps verifyMentionInInputField(String userName) {
        $(inputBar.getMessageField())
                .$(inputBar.getMentionInInputField())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(userName));
        return this;
    }

    @Step("Проверить, что в поле сообщения отображается текст: {expectedText}")
    public InputBarSteps checkMessageFieldContainsText(String expectedText) {
        $(inputBar.getMessageField())
                .shouldBe(visible)
                .shouldHave(text(expectedText));
        return this;
    }

    @Step("Проверить, что в поле ввода есть зачёркнутый текст: {text}")
    public InputBarSteps verifyStrikethroughInInput(String text) {
        $(inputBar.getMessageField())
                .$(chatElements.getStrikethroughText())
                .shouldHave(text(text))
                .shouldBe(visible);
        return this;
    }

    @Step("Проверить, что в поле ввода есть жирный текст: {text}")
    public InputBarSteps verifyBoldInInput(String text) {
        $(inputBar.getMessageField())
                .$(inputBar.getMessageEditor())
                .$$(chatElements.getBoldText())
                .findBy(text(text))
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что в поле ввода есть курсивный текст: {text}")
    public InputBarSteps verifyItalicInInput(String text) {
        $(inputBar.getMessageField())
                .$(inputBar.getMessageEditor())
                .$$(chatElements.getItalicText())
                .findBy(text(text))
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что в поле ввода есть подчеркнутый текст: {text}")
    public InputBarSteps verifyUnderlinedInInput(String text) {
        $(inputBar.getMessageField())
                .$(inputBar.getMessageEditor())
                .$$(chatElements.getUnderlinedText())
                .findBy(text(text))
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что в поле ввода есть моноширинный текст: {text}")
    public InputBarSteps verifyMonospaceInInput(String text) {
        $(inputBar.getMessageField())
                .$(inputBar.getMessageEditor())
                .$$(chatElements.getMonospaceText())
                .findBy(text(text))
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что в поле ввода нет жирного текста: {text}")
    public InputBarSteps verifyBoldAbsentInInput(String text) {
        $(inputBar.getMessageField())
                .$(inputBar.getMessageEditor())
                .$$(chatElements.getBoldText())
                .filterBy(text(text))
                .shouldHave(size(0));
        return this;
    }

    @Step("Проверить, что в поле ввода нет курсивного текста: {text}")
    public InputBarSteps verifyItalicAbsentInInput(String text) {
        $(inputBar.getMessageField())
                .$(inputBar.getMessageEditor())
                .$$(chatElements.getItalicText())
                .filterBy(text(text))
                .shouldHave(size(0));
        return this;
    }

    @Step("Проверить, что в поле ввода нет подчеркнутого текста: {text}")
    public InputBarSteps verifyUnderlinedAbsentInInput(String text) {
        $(inputBar.getMessageField())
                .$(inputBar.getMessageEditor())
                .$$(chatElements.getUnderlinedText())
                .filterBy(text(text))
                .shouldHave(size(0));
        return this;
    }

    @Step("Проверить, что в поле ввода нет зачёркнутого текста: {text}")
    public InputBarSteps verifyStrikethroughAbsentInInput(String text) {
        $(inputBar.getMessageField())
                .$(inputBar.getMessageEditor())
                .$$(chatElements.getStrikethroughText())
                .filterBy(text(text))
                .shouldHave(size(0));
        return this;
    }

    @Step("Проверить, что в поле ввода нет моноширинного текста: {text}")
    public InputBarSteps verifyMonospaceAbsentInInput(String text) {
        $(inputBar.getMessageField())
                .$(inputBar.getMessageEditor())
                .$$(chatElements.getMonospaceText())
                .filterBy(text(text))
                .shouldHave(size(0));
        return this;
    }

    @Step("Проверить, что текст '{expectedText}' содержит обычную часть '{plainText}' и зачёркнутую часть '{strikethroughText}' в поле ввода")
    public InputBarSteps verifyPlainAndStrikethroughTextInInput(String expectedText, String plainText, String strikethroughText) {
        SelenideElement editor = $(inputBar.getMessageField())
                .$(inputBar.getMessageEditor())
                .shouldBe(visible, Duration.ofSeconds(10));

        editor.shouldHave(text(expectedText));

        editor.$$(chatElements.getStrikethroughText())
                .findBy(exactText(strikethroughText))
                .shouldBe(visible);

        editor.$$(chatElements.getStrikethroughText())
                .filterBy(exactText(plainText))
                .shouldHave(size(0));

        return this;
    }

    @Step("Проверить, что к тексту '{expectedText}' в поле ввода применены все стили")
    public InputBarSteps verifyAllStylesAppliedToTextInInput(String expectedText) {
        SelenideElement editor = $(inputBar.getMessageField())
                .$(inputBar.getMessageEditor())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(expectedText));

        verifyAllStylesApplied(editor, expectedText);

        return this;
    }

    @Step("Проверить, что текст '{expectedText}' в поле ввода является ссылкой")
    public InputBarSteps verifyTextIsLinkInInput(String expectedText) {
        $(inputBar.getMessageField())
                .$(inputBar.getMessageEditor())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text(expectedText))
                .$$(inputBar.getEditorLink())
                .findBy(text(expectedText))
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    private void verifyAllStylesApplied(SelenideElement container, String expectedText) {
        Duration timeout = Duration.ofSeconds(10);

        container.$$(chatElements.getBoldText())
                .findBy(text(expectedText))
                .shouldBe(visible, timeout);
        container.$$(chatElements.getItalicText())
                .findBy(text(expectedText))
                .shouldBe(visible, timeout);
        container.$$(chatElements.getUnderlinedText())
                .findBy(text(expectedText))
                .shouldBe(visible, timeout);
        container.$$(chatElements.getStrikethroughText())
                .findBy(text(expectedText))
                .shouldBe(visible, timeout);
        container.$$(chatElements.getMonospaceText())
                .findBy(text(expectedText))
                .shouldBe(visible, timeout);
    }

    @Step("Проверить, что поле ввода сообщения пустое")
    public InputBarSteps verifyMessageFieldIsEmpty() {
        $(inputBar.getMessageField())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(empty);
        return this;
    }

    @Step("Проверить, что индикатор автоудаления сообщений отображается")
    public void verifyAutoDeleteIndicatorIsVisible() {
        $(inputBar.getAutoDeleteStatus()).shouldBe(visible);
    }

    @Step("Проверить, что индикатор автоудаления сообщений не отображается")
    public void verifyAutoDeleteIndicatorIsNotVisible() {
        $(inputBar.getAutoDeleteStatus()).shouldNotBe(visible);
    }

    @Step("Проверить, что реплай панель отображается над полем ввода")
    public InputBarSteps verifyReplyFieldIsVisible() {
        $(inputBar.getReplyFieldView())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что панель ссылки отображается над полем ввода")
    public InputBarSteps verifyAttachedLinkPreviewIsVisible() {
        $(inputBar.getAttachedLinkPreview())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что в поле ввода отображается {expectedImages} фото и {expectedVideos} видео")
    public InputBarSteps verifyAttachedMediaPreviews(int expectedImages, int expectedVideos) {
        Duration timeout = Duration.ofSeconds(30);

        // Проверяем количество и корректность превью картинок
        $$(inputBar.getAttachedMediaImagePreview())
                .shouldHave(size(expectedImages), timeout)
                .forEach(this::verifyRenderedPreview);

        // Проверяем количество и корректность превью видео
        $$(inputBar.getAttachedMediaVideoPreview())
                .shouldHave(size(expectedVideos), timeout)
                .forEach(this::verifyRenderedPreview);

        return this;
    }

    private void verifyRenderedPreview(SelenideElement preview) {
        preview.shouldBe(visible).should(match("rendered", el -> {
            String src = el.getAttribute("src");
            var previewSize = el.getSize();
            return src != null && !src.isBlank() && previewSize.getWidth() > 0 && previewSize.getHeight() > 0;
        }));
    }
}