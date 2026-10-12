package org.foxtrot.pages.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.elements.DiveIntoChatViaInviteLink;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class DiveIntoChatViaInviteLinkSteps {

    DiveIntoChatViaInviteLink elements = new DiveIntoChatViaInviteLink();

    @Step("Проверить наличие кнопки 'Вступить в группу/канал'")
    public DiveIntoChatViaInviteLinkSteps verifyJoinGroupButtonVisible() {
        $(elements.getJoinGroupButton()).shouldBe(visible);
        return this;
    }
}
