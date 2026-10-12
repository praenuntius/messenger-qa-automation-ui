package org.foxtrot.pages.spaces.workspace.modals;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.pages.spaces.workspace.modals.elements.InviteMembersToWorkspaceModal;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.Arrays;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class InviteMembersToWorkspaceModalSteps {

    private final InviteMembersToWorkspaceModal elements = new InviteMembersToWorkspaceModal();

    @Step("Добавить контакт {contactName} в пространство")
    public InviteMembersToWorkspaceModalSteps addContactToWorkspace(String contactName) {
        ElementsCollection contactCards = $$(elements.getContactCard())
                .filterBy(visible);

        SelenideElement targetContact = null;
        for (SelenideElement contact : contactCards) {
            SelenideElement nameElement = contact.$(elements.getContactName()).shouldBe(visible, Duration.ofSeconds(5));
            if (nameElement.getText().contains(contactName)) {
                targetContact = contact;
                break;
            }
        }

        if (targetContact == null) {
            throw new RuntimeException("Contact '" + contactName + "' not found");
        }

        targetContact.click();

        return this;
    }

    @Step("Проверить отсутствие пользователя {userName} в списке")
    public InviteMembersToWorkspaceModalSteps verifyUserNotInList(String userName) {
        String dynamicSelector = elements.getTabContactDefinedUserButton().replace("{username}", userName);
        if ($(By.xpath(dynamicSelector)).is(visible)) {
            throw new AssertionError("User '" + userName + "' should not be in the list");
        }
        return this;
    }

    @Step("Добавить участника '{name}'")
    public InviteMembersToWorkspaceModalSteps selectPerson(String name) {
        String dynamicSelector = elements.getTabContactDefinedUserButton().replace("{username}", name);
        SelenideElement chatButton = $(By.xpath(dynamicSelector)).shouldBe(visible, Duration.ofSeconds(10));
        chatButton.click();
        submitInvite();
        return this;
    }

    @Step("Отправить приглашение")
    public InviteMembersToWorkspaceModalSteps submitInvite() {
        SelenideElement submitButton = $(elements.getWorkspaceInviteUserTabContactInviteButton());
        submitButton.shouldBe(visible, Duration.ofSeconds(10)).click();
        sleep(500);
        return this;
    }

    @Step("Перейти во вкладку 'Email' в модальном окне приглашения")
    public InviteMembersToWorkspaceModalSteps switchTabToEmail() {
        SelenideElement tabEmail = $(elements.getWorkspaceInviteUserTabEmailButton());
        tabEmail.shouldBe(visible, Duration.ofSeconds(10)).click();
        return this;
    }

    @Step("Ввести email {email} для приглашения")
    public InviteMembersToWorkspaceModalSteps enterEmail(String email) {
        SelenideElement input = $(elements.getWorkspaceInviteUserTabEmailInputField());
        input.click();
        Arrays.stream(email.split("")).forEach(c -> { input.sendKeys(c); Selenide.sleep(30); });
        return this;
    }


    @Step("Отправить приглашение на email")
    public InviteMembersToWorkspaceModalSteps submitEmailInvite() {
        $(elements.getWorkspaceInviteUserTabEmailInviteButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Перейти во вкладку 'ссылка' в модальном окне приглашения")
    public InviteMembersToWorkspaceModalSteps switchTabToLink() {
        SelenideElement tabLink = $(elements.getWorkspaceInviteUserTabLinkButton());
        tabLink.shouldBe(visible, Duration.ofSeconds(10)).click();
        return this;
    }

    @Step("Скопировать ссылку на рабочее пространство")
    public InviteMembersToWorkspaceModalSteps copyWorkspaceLink() {
        SelenideElement copyButton = $(elements.getWorkspaceInviteUserTabLinkCopyButton());
        copyButton.shouldBe(visible, Duration.ofSeconds(10)).click();
        return this;
    }

    @Step("Закрыть модальное окно приглашения участников кликом по кнопке [X]")
    public InviteMembersToWorkspaceModalSteps closeModal() {
        SelenideElement closeButton = $(elements.getWorkspaceInviteUserModalCloseButton());
        closeButton.shouldBe(visible, Duration.ofSeconds(10)).click();
        return this;
    }
}
