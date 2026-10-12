package org.foxtrot.pages.spaces.homespace.primary;

import io.qameta.allure.Step;
import org.foxtrot.pages.spaces.homespace.modals.NewChannelModalSteps;
import org.foxtrot.pages.spaces.homespace.modals.NewGroupModalSteps;
import org.foxtrot.pages.spaces.homespace.primary.elements.NewMessageModal;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class NewMessageModalSteps {

    private final NewMessageModal elements = new NewMessageModal();

    @Step("Убедиться, что модальное окно создания нового сообщения отображается")
    private void ensureModalIsDisplayed() {
        $(elements.getCreateNewGroupButton()).shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Нажать на кнопку создания группы")
    public NewGroupModalSteps clickCreateGroupButton() {
        $(elements.getCreateNewGroupButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return new NewGroupModalSteps();
    }

    @Step("Нажать на кнопку создания канала")
    public NewChannelModalSteps clickCreateChannelButton() {
        $(elements.getCreateNewChannelButton()).shouldBe(visible, Duration.ofSeconds(20)).click();
        return new NewChannelModalSteps();
    }
}
