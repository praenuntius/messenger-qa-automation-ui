package org.foxtrot.pages.chat.input_bar;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.input_bar.elements.ContextMenu;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ContextMenuSteps {

    private final ContextMenu elements = new ContextMenu();
    private final InputBarSteps inputBarSteps = new InputBarSteps();

    @Step("Кликнуть по пункту 'Вставить' в контекстном меню поля ввода")
    public ContextMenuSteps clickPasteButton() {
        $(elements.getPasteButton())
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        return this;
    }

    @Step("Кликнуть по пункту 'Копировать' в контекстном меню поля ввода")
    public ContextMenuSteps clickCopyButton() {
        $(elements.getCopyButton())
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        return this;
    }

    @Step("Кликнуть по пункту 'Вырезать' в контекстном меню поля ввода")
    public ContextMenuSteps clickCutButton() {
        $(elements.getCutButton())
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        return this;
    }

    @Step("Кликнуть по пункту 'Форматирование' в контекстном меню поля ввода")
    public ContextMenuSteps clickFormattingButton() {
        $(elements.getFormattingButton())
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        return this;
    }

    @Step("Кликнуть по пункту 'Добавить ссылку' в контекстном меню поля ввода")
    public ContextMenuSteps clickAddLinkButton() {
        $(elements.getAddLinkButton())
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        return this;
    }

    @Step("Кликнуть по пункту 'Жирный' в контекстном меню поля ввода")
    public ContextMenuSteps clickBoldButton() {
        $(elements.getBoldButton())
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        return this;
    }

    @Step("Кликнуть по пункту 'Курсив' в контекстном меню поля ввода")
    public ContextMenuSteps clickItalicButton() {
        $(elements.getItalicButton())
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        return this;
    }

    @Step("Кликнуть по пункту 'Подчеркнутый' в контекстном меню поля ввода")
    public ContextMenuSteps clickUnderlineButton() {
        $(elements.getUnderlineButton())
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        return this;
    }

    @Step("Кликнуть по пункту 'Зачеркнутый' в контекстном меню поля ввода")
    public ContextMenuSteps clickStrikethroughButton() {
        $(elements.getStrikethroughButton())
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        return this;
    }

    @Step("Кликнуть по пункту 'Моноширинный' в контекстном меню поля ввода")
    public ContextMenuSteps clickMonospaceButton() {
        $(elements.getMonospaceButton())
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        return this;
    }

    @Step("Кликнуть по пункту 'Отменить все изменения' в контекстном меню поля ввода")
    public ContextMenuSteps clickUndoAllChangesButton() {
        $(elements.getUndoAllChangesButton())
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        return this;
    }

    @Step("Проверить, что отображается меню выбора стилей форматирования")
    public ContextMenuSteps verifyFormattingSubMenuVisible() {
        $(elements.getBoldButton()).shouldBe(visible, Duration.ofSeconds(5));
        $(elements.getItalicButton()).shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить, что меню выбора стилей форматирования закрылось")
    public ContextMenuSteps verifyFormattingSubMenuClosed() {
        $(elements.getBoldButton()).shouldNotBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Проверить, что у стиля 'Жирный' отображается галочка")
    public ContextMenuSteps verifyBoldStyleHasCheck() {
        verifyStyleSelected(elements.getBoldButton());
        return this;
    }

    @Step("Применить все стили к выделенному тексту через контекстное меню поля ввода")
    public ContextMenuSteps applyAllStylesToSelectedText() {
        applyStyle(this::clickBoldButton);
        applyStyle(this::clickUnderlineButton);
        applyStyle(this::clickItalicButton);
        applyStyle(this::clickStrikethroughButton);
        applyStyle(this::clickMonospaceButton);
        return this;
    }

    @Step("Проверить, что у всех выбранных стилей отображаются галочки")
    public ContextMenuSteps verifyAllSelectedStylesHaveChecks() {
        verifyStyleSelected(elements.getBoldButton());
        verifyStyleSelected(elements.getItalicButton());
        verifyStyleSelected(elements.getUnderlineButton());
        verifyStyleSelected(elements.getStrikethroughButton());
        verifyStyleSelected(elements.getMonospaceButton());
        return this;
    }

    private void applyStyle(Runnable clickStyleButton) {
        inputBarSteps.rightClickMessageInputField();
        clickFormattingButton();
        clickStyleButton.run();
    }

    private void verifyStyleSelected(By styleButton) {
        $(styleButton)
                .shouldBe(visible, Duration.ofSeconds(5))
                .$(elements.getSelectedStyleIndicator())
                .should(exist);
    }
}
