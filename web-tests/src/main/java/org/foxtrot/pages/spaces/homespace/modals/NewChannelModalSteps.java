package org.foxtrot.pages.spaces.homespace.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.spaces.homespace.modals.elements.NewChannelModal;
import org.foxtrot.pages.user_lists.AddSubscribersListSteps;
import org.foxtrot.utils.TestData;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class NewChannelModalSteps {

    private String channelName;
    private final NewChannelModal elements = new NewChannelModal();

    @Step("Создать канал в {spaceType}")
    public NewChannelModalSteps createChannel(String spaceType, String channelType) {
        switch (spaceType.toLowerCase()) {
            case "homespace" -> {
                this.channelName = TestData.generateHomespaceChannelName();
                createChannel(this.channelName);
            }
            case "workspace" -> {
                this.channelName = "private".equalsIgnoreCase(channelType)
                        ? TestData.generateWorkspacePrivateChannelName()
                        : TestData.generateWorkspacePublicChannelName();
                if ("public".equalsIgnoreCase(channelType)) {
                    createPublicChannel(this.channelName);
                } else {
                    createChannel(this.channelName);
                }
            }
            default -> throw new IllegalArgumentException("Unknown space type: " + spaceType);
        }
        return this;
    }

    @Step("Создать канал в {spaceType} с участниками {participantNames}")
    public NewChannelModalSteps createChannelWithParticipants(String spaceType, String channelType, String channelName, String... participantNames) {
        switch (spaceType.toLowerCase()) {
            case "homespace", "workspace" -> {
                switch (channelType.toLowerCase()) {
                    case "public" -> createPublicChannel(channelName);
                    case "private" -> createChannel(channelName);
                    default -> throw new IllegalArgumentException("Unknown channel type: " + channelType);
                }
            }
            default -> throw new IllegalArgumentException("Unknown space type: " + spaceType);
        }

        AddSubscribersListSteps addSubscribersListSteps = new AddSubscribersListSteps();
        if (participantNames != null && participantNames.length > 0) {
            for (String participantName : participantNames) {
                addSubscribersListSteps.selectSubscriber(participantName);
            }
        }
        addSubscribersListSteps.clickAddSubscribersButton();
        return this;
    }


    @Step("Создать приватный (по-умолчанию) канал: {channelName}")
    public NewChannelModalSteps createChannel(String channelName) {
        enterChannelName(channelName);
        enterChannelDescription();
        clickNextButton();
        return this;
    }

    @Step("Создать публичный канал: {channelName}")
    public NewChannelModalSteps createPublicChannel(String channelName) {
        enterChannelName(channelName);
        enterChannelDescription();
        selectPublicChannel();
        clickNextButton();
        return this;
    }

    @Step("Ввести название канала: {channelName}")
    public NewChannelModalSteps enterChannelName(String channelName) {
        $(elements.getItemNameInput()).shouldBe(visible, Duration.ofSeconds(15)).setValue(channelName);
        return this;
    }

    @Step("Ввести описание канала")
    public NewChannelModalSteps enterChannelDescription() {
        $(elements.getItemDescriptionInput()).shouldBe(visible, Duration.ofSeconds(15))
                .setValue("channel created via autotest by gansta");
        return this;
    }

    @Step("Нажать на кнопку 'Далее'")
    public NewChannelModalSteps clickNextButton() {
        $(elements.getCreateItemNextButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }

    @Step("Выбрать публичный канал")
    public NewChannelModalSteps selectPublicChannel() {
        $(elements.getPublicChannelRadio()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }

    @Step("Выбрать приватный канал")
    public NewChannelModalSteps selectPrivateChannel() {
        $(elements.getPrivateChannelRadio()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }

    @Step("Проверить, что тоггл 'Allow Reactions' включен")
    public NewChannelModalSteps verifyAllowReactionsToggleEnabled() {
        $(elements.getAllowReactionToggle())
                .scrollIntoView(true)
                .should(exist, Duration.ofSeconds(10))
                .shouldHave(attribute("checked"));
        return this;
    }

    @Step("Кликнуть по тогглу 'Allow Reactions' для отключения")
    public NewChannelModalSteps clickAllowReactionsToggle() {
        var toggleElement = $(elements.getAllowReactionToggle())
                .scrollIntoView(true)
                .should(exist, Duration.ofSeconds(10));
        executeJavaScript("arguments[0].click();", toggleElement.toWebElement());
        
        return this;
    }

    @Step("Создать канал без участников и аватара")
    public NewChannelModalSteps createChannelWithoutParticipants(String spaceType, String channelType, String channelName) {
        switch (spaceType.toLowerCase()) {
            case "homespace", "workspace" -> {
                switch (channelType.toLowerCase()) {
                    case "public" -> createPublicChannel(channelName);
                    case "private" -> createChannel(channelName);
                    default -> throw new IllegalArgumentException("Unknown channel type: " + channelType);
                }
            }
            default -> throw new IllegalArgumentException("Unknown space type: " + spaceType);
        }
        AddSubscribersListSteps addSubscribersListSteps = new AddSubscribersListSteps();
        addSubscribersListSteps.clickAddSubscribersButton();
        
        return this;
    }

    public String getChannelName() {
        return channelName;
    }
}
