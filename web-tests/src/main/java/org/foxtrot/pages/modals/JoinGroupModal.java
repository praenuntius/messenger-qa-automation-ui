package org.foxtrot.pages.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.elements.JoinGroupModalElements;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class JoinGroupModal {

    JoinGroupModalElements elements = new JoinGroupModalElements();

    @Step("Нажать на кнопку 'Вступить в группу'")
    public void clickDoneButton() {
        $(elements.getJoinGroupButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
    }
}