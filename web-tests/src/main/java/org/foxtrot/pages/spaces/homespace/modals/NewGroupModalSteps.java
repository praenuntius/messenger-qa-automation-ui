package org.foxtrot.pages.spaces.homespace.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.CropPhotoSteps;
import org.foxtrot.pages.spaces.homespace.modals.elements.NewGroupModal;
import org.foxtrot.pages.user_lists.AddPeopleListSteps;
import org.foxtrot.utils.FileUploadHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class NewGroupModalSteps {

    private final NewGroupModal elements = new NewGroupModal();
    private final FileUploadHelper fileUploadHelper = new FileUploadHelper();
    private final CropPhotoSteps cropPhotoSteps = new CropPhotoSteps();





    @Step("Создать группу в {spaceType} с участниками {participantNames}")
    public NewGroupModalSteps createGroupWithParticipants(String spaceType, String groupType, String groupName, String... participantNames) {
        switch (spaceType.toLowerCase()) {
            case "homespace" -> {
                createHomespaceGroup(groupName);
            }
            case "workspace" -> {
                switch (groupType.toLowerCase()) {
                    case "public" -> createPublicGroup(groupName);
                    case "private" -> createGroup(groupName);
                    default -> throw new IllegalArgumentException("Unknown group type: " + groupType);
                }
            }
            default -> throw new IllegalArgumentException("Unknown space type: " + spaceType);
        }

        AddPeopleListSteps addPeopleListSteps = new AddPeopleListSteps();
        if (participantNames != null && participantNames.length > 0) {
            for (String participantName : participantNames) {
                addPeopleListSteps.selectPerson(participantName);
            }
        }

        return this;
    }


    @Step("Загрузить аватар группы")
    public NewGroupModalSteps uploadPhoto(String filePath) {
        $(elements.getUploadImage()).shouldBe(visible).click();
        fileUploadHelper.uploadFileViaInput(elements.getFileInput(),filePath);
        cropPhotoSteps.clickSaveButton();
        return this;
    }

    @Step("Создать группу в homespace: {groupName}")
    public NewGroupModalSteps createHomespaceGroup(String groupName) {
        enterGroupName(groupName);
        enterGroupDescription();
        clickNextButton();
        return this;
    }

    @Step("Создать приватную (по-умолчанию) группу: {groupName}")
    public NewGroupModalSteps createGroup(String groupName) {
        enterGroupName(groupName);
        enterGroupDescription();
        selectPrivateGroup();
        clickNextButton();
        return this;
    }

    @Step("Создать публичную группу: {groupName}")
    public NewGroupModalSteps createPublicGroup(String groupName) {
        enterGroupName(groupName);
        enterGroupDescription();
        selectPublicGroup();
        clickNextButton();
        return this;
    }

    @Step("Ввести название группы: {groupName}")
    public NewGroupModalSteps enterGroupName(String groupName) {
        $(elements.getItemNameInput()).shouldBe(visible, Duration.ofSeconds(15)).setValue(groupName);
        return this;
    }

    @Step("Ввести описание группы")
    public NewGroupModalSteps enterGroupDescription() {
        $(elements.getItemDescriptionInput()).shouldBe(visible, Duration.ofSeconds(15))
                .setValue("group created via autotest by gansta");
        return this;
    }

    @Step("Нажать на кнопку 'Далее'")
    public AddPeopleListSteps clickNextButton() {
        $(elements.getCreateItemNextButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return new AddPeopleListSteps();
    }

    @Step("Добавить участников в группу")
    public void addPeople() {
        AddPeopleListSteps addPeopleListSteps = new AddPeopleListSteps();
        addPeopleListSteps.clickAddUsersButton();
    }

    @Step("Выбрать публичную группу")
    public NewGroupModalSteps selectPublicGroup() {
        $(elements.getPublicGroupRadio()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }

    @Step("Выбрать приватную группу")
    public NewGroupModalSteps selectPrivateGroup() {
        $(elements.getPrivateGroupRadio()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }

    @Step("Проверить, что тоггл 'Allow Reactions' включен")
    public NewGroupModalSteps verifyAllowReactionsToggleEnabled() {
        $(elements.getAllowReactionToggle())
                .scrollIntoView(true)
                .should(exist, Duration.ofSeconds(10))
                .shouldHave(attribute("checked"));
        return this;
    }

    @Step("Кликнуть по тогглу 'Allow Reactions' для отключения")
    public NewGroupModalSteps clickAllowReactionsToggle() {
        var toggleElement = $(elements.getAllowReactionToggle())
                .scrollIntoView(true)
                .should(exist, Duration.ofSeconds(10));
        executeJavaScript("arguments[0].click();", toggleElement.toWebElement());
        return this;
    }

    @Step("Проверить, что тоггл 'Allow Comments' включен")
    public NewGroupModalSteps verifyAllowCommentsToggleEnabled() {
        $(elements.getAllowCommentsToggle())
                .scrollIntoView(true)
                .should(exist, Duration.ofSeconds(10))
                .shouldHave(attribute("checked"));
        return this;
    }

    @Step("Кликнуть по тогглу 'Allow Comments' для отключения")
    public NewGroupModalSteps clickAllowCommentsToggle() {
        var toggleElement = $(elements.getAllowCommentsToggle())
                .scrollIntoView(true)
                .should(exist, Duration.ofSeconds(10));
        executeJavaScript("arguments[0].click();", toggleElement.toWebElement());
        return this;
    }

    @Step("Создать группу без участников и аватара")
    public NewGroupModalSteps createGroupWithoutParticipants(String spaceType, String groupType, String groupName) {
        switch (spaceType.toLowerCase()) {
            case "homespace", "workspace" -> {
                switch (groupType.toLowerCase()) {
                    case "public" -> createPublicGroup(groupName);
                    case "private" -> createGroup(groupName);
                    default -> throw new IllegalArgumentException("Unknown group type: " + groupType);
                }
            }
            default -> throw new IllegalArgumentException("Unknown space type: " + spaceType);
        }
        AddPeopleListSteps addPeopleListSteps = new AddPeopleListSteps();
        addPeopleListSteps.clickAddUsersButton();
        
        return this;
    }

}
