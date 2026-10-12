package org.foxtrot.pages.spaces.homespace.primary;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.pages.left_sidebar.primary_bar.LeftSidebarSteps;
import org.foxtrot.pages.spaces.homespace.primary.elements.InviteModal;
import org.foxtrot.pages.spaces.workspace.left_sidebar.SpaceMenuSteps;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class InviteModalSteps {

    private final InviteModal elements = new InviteModal();
    private final SpaceMenuSteps spaceMenuSteps = new SpaceMenuSteps();
    private final LeftSidebarSteps leftSidebarSteps = new LeftSidebarSteps();

    @Step("Скопировать ссылку на пространство")
    public InviteModalSteps copyWorkspaceLink() {
        switchToInviteLinkTab();
        sleep(300);
        clickCopyWorkspaceLink();
        verifyCopyToastAppears();
        clickCloseInviteModal();
        return this;
    }

    @Step("Перейти на вкладку ссылка")
    public InviteModalSteps switchToInviteLinkTab() {
        $(elements.getInviteLinkTab()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return null;
    }

    @Step("Скопировать ссылку на пространство кнопкой в модальном окне приглашения")
    public InviteModalSteps clickCopyWorkspaceLink() {
        SelenideElement copyButton = $(elements.getCopyLinkButton()).shouldBe(visible, Duration.ofSeconds(15));
        copyButton.click();
        return this;
    }

    @Step("Убедиться, что появилось сообщение 'Скопировано'")
    public InviteModalSteps verifyCopyToastAppears() {
        $(elements.getCopySuccessMessage()).shouldBe(visible);
        return this;
    }

    @Step("Закрыть модальное окно 'Пригласить в пространство'")
    public InviteModalSteps clickCloseInviteModal() {
        $(elements.getCloseButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
        return this;
    }
}



