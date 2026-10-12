package org.foxtrot.pages.chat.right_sidebar_menu.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.right_sidebar_menu.modals.elements.GroupInviteByLinkModal;
import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class GroupInviteByLinkModalSteps {

    private final GroupInviteByLinkModal elements = new GroupInviteByLinkModal();

    @Step("Перейти к 'Invite Link' табе")
    public GroupInviteByLinkModalSteps switchToInviteLinkTab() {
        $(elements.getInviteLinkTabSelector()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }

    @Step("Скопировать ссылку-приглашение в группу")
    public GroupInviteByLinkModalSteps copyGroupLink() {
        $(elements.getCopyLinkButtonSelector()).shouldBe(visible, Duration.ofSeconds(15)).click();
        verifyCopyToastAppears();
        return this;
    }

    @Step("Убедиться, что появилось сообщение 'Скопировано'")
    public GroupInviteByLinkModalSteps verifyCopyToastAppears() {
        $(elements.getCopySuccessMessage()).shouldBe(visible);
        return this;
    }

    @Step("Закрыть модальное окно 'Invite By Link' ")
    public GroupInviteByLinkModalSteps closeModal() {
        $(elements.getCloseButtonSelector()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }
}
