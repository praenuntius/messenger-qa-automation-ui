package org.foxtrot.pages.chat.right_sidebar_menu.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.right_sidebar_menu.modals.elements.ChannelInviteByLinkModal;
import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ChannelInviteByLinkModalSteps {

    private final ChannelInviteByLinkModal elements = new ChannelInviteByLinkModal();

    @Step("Скопировать ссылку-приглашение в группу")
    public ChannelInviteByLinkModalSteps copyChannelLink() {
        $(elements.getCopyLinkButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
        verifyCopyToastAppears();
        return this;
    }

    @Step("Убедиться, что появилось сообщение 'Скопировано'")
    public ChannelInviteByLinkModalSteps verifyCopyToastAppears() {
        $(elements.getCopySuccessMessage()).shouldBe(visible);
        return this;
    }

    @Step("Закрыть модальное окно 'Invite By Link' ")
    public ChannelInviteByLinkModalSteps closeModal() {
        $(elements.getCloseButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }
}
