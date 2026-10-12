package org.foxtrot.pages.chat.right_sidebar_menu.modals;

import io.qameta.allure.Step;
import org.foxtrot.assertions.ToggleAssertions;
import org.foxtrot.pages.chat.right_sidebar_menu.modals.elements.GroupAndChannelEditModal;
import org.foxtrot.pages.key_interactions.PressKeys;
import org.foxtrot.pages.modals.CropPhotoSteps;
import org.foxtrot.utils.FileUploadHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class GroupAndChannelEditModalSteps {

    GroupAndChannelEditModal elements = new GroupAndChannelEditModal();
    FileUploadHelper fileUploadHelper = new FileUploadHelper();
    CropPhotoSteps cropPhotoSteps = new CropPhotoSteps();

    @Step("Проверить, отображение хедера модального окна 'Настройки группы'")
    public void verifyGroupSettingsHeaderIsVisible() {
        $(elements.getGroupEditModalHeder()).shouldBe(visible);
    }

    @Step("Проверить, отображение хедера модального окна 'Настройки канала'")
    public void verifyChannelSettingsHeaderIsVisible() {
        $(elements.getChannelEditModalHeder()).shouldBe(visible);
    }

    @Step("Загрузить фото")
    public GroupAndChannelEditModalSteps uploadPhoto(String filePath) {
        $(elements.getUploadPhotoButton()).shouldBe(visible).click();
        fileUploadHelper.uploadFileViaInput(elements.getFileInput(),filePath);
        cropPhotoSteps.clickSaveButton();
        return this;
    }

    @Step("Изменить имя")
    public GroupAndChannelEditModalSteps editName(String name) {
        $(elements.getNameInput()).click();
        PressKeys.pressCmdA();
        PressKeys.pressBackSpace();
        $(elements.getNameInput()).setValue(name);
        return this;
    }

    @Step("Изменить описание")
    public GroupAndChannelEditModalSteps editDescription(String description) {
        $(elements.getDescriptionInput()).click();
        PressKeys.pressCmdA();
        PressKeys.pressBackSpace();
        $(elements.getDescriptionInput()).setValue(description);
        return this;
    }

    @Step("Сделать канал приватным")
    public GroupAndChannelEditModalSteps clickPrivateChannelCheckbox() {
        $(elements.getPrivateChannelCheckbox()).shouldBe(visible).click();
        return this;
    }

    @Step("Сделать группу приватной")
    public GroupAndChannelEditModalSteps clickPrivateGroupCheckbox() {
        $(elements.getPrivateGroupCheckbox()).shouldBe(visible).click();
        return this;
    }

    @Step("Сделать канал публичным")
    public GroupAndChannelEditModalSteps clickPublicChannelCheckbox() {
        $(elements.getPublicChannelCheckbox()).shouldBe(visible).click();
        return this;
    }

    @Step("Сделать группу публичной")
    public GroupAndChannelEditModalSteps clickPublicGroupCheckbox() {
        $(elements.getPublicGroupCheckbox()).shouldBe(visible).click();
        return this;
    }

    @Step("Нажать кнопку 'Сохранить'")
    public GroupAndChannelEditModalSteps saveButton() {
        $(elements.getSaveButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Проверить, что тоггл 'Allow Reactions' включен")
    public GroupAndChannelEditModalSteps verifyAllowReactionsToggleEnabled() {
        $(elements.getAllowReactionsRadioButton())
                .scrollIntoView(true)
                .should(exist, Duration.ofSeconds(10))
                .shouldHave(attribute("checked"));
        return this;
    }

    @Step("Проверить, что тоггл 'Allow Reactions' выключен")
    public GroupAndChannelEditModalSteps verifyAllowReactionsToggleDisabled() {
        $(elements.getAllowReactionsRadioButton())
                .scrollIntoView(true)
                .should(exist, Duration.ofSeconds(10))
                .shouldNotHave(attribute("checked"));
        return this;
    }

    @Step("Проверить наличие элемента 'Разрешить комментарии' в настройках канала")
    public GroupAndChannelEditModalSteps verifyAllowCommentsOptionPresentInChannel() {
        $(elements.getAllowChannelCommentsToggle())
                .scrollIntoView(true)
                .should(exist, Duration.ofSeconds(10));
        $(elements.getAllowChannelCommentsToggle())
                .closest("label")
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить наличие элемента 'Разрешить комментарии' в настройках группы")
    public GroupAndChannelEditModalSteps verifyAllowCommentsOptionPresentInGroup() {
        $(elements.getAllowGroupCommentsToggle())
                .scrollIntoView(true)
                .should(exist, Duration.ofSeconds(10))
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что тоггл 'Разрешить комментарии' в группе выключен (checked=false)")
    public GroupAndChannelEditModalSteps verifyAllowCommentsToggleOffInGroup() {
        // input скрыт (displayed:false) — кастомный чекбокс, проверяем через exist, не visible
        ToggleAssertions.assertChecked(elements.getAllowGroupCommentsToggle(), false, Duration.ofSeconds(10), false);
        return this;
    }

    @Step("Проверить, что тоггл 'Разрешить комментарии' в группе включен (checked=true)")
    public GroupAndChannelEditModalSteps verifyAllowCommentsToggleOnInGroup() {
        // input скрыт (displayed:false) — кастомный чекбокс, проверяем через exist, не visible
        ToggleAssertions.assertChecked(elements.getAllowGroupCommentsToggle(), true, Duration.ofSeconds(10), false);
        return this;
    }

    @Step("Кликнуть по тогглу 'Разрешить комментарии' в канале")
    public GroupAndChannelEditModalSteps clickAllowCommentsRadioButton() {
        // input скрыт (displayed:false), кликаем по label
        $(elements.getAllowChannelCommentsToggle())
                .closest("label")
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Кликнуть по кнопке 'Сохранить'")
    public GroupAndChannelEditModalSteps clickSaveButton() {
        $(elements.getSaveButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }
}
