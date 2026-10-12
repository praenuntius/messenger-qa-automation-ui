package org.foxtrot.pages.chat.right_sidebar_menu.tabs.members_menu;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.right_sidebar_menu.modals.MakeGroupAdminModalSteps;
import org.foxtrot.pages.chat.right_sidebar_menu.tabs.members_menu.elements.MembersMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class MembersMenuSteps {

    private Actions actions = null;
    private final MembersMenu elements = new MembersMenu();
    private final MakeGroupAdminModalSteps makeGroupAdminModalSteps = new MakeGroupAdminModalSteps();

    @Step("Проверить наличия баннер об отсутствии владельца в группе")
    public void verifyGroupWithoutOwnerBanner() {
        $(elements.getGroupWithoutOwnerBanner()).shouldBe(visible);
    }

    @Step("Кликнуть по пиктограмме 'i'  и проверить наличие тултипа с информацией")
    public void verifyInfoTulTip() {
        $(elements.getInformationButton()).shouldBe(visible).click();
        $(elements.getInfoTooltip()).shouldBe(visible);
    }



    @Step("Проверить наличие пользователя {userName} в пространстве")
    public void checkUserPresenceInSpace(String userName) {
        $(By.xpath(elements.getDefinedUserButton().replace("{username}", userName)))
                .shouldBe(visible);
    }

    @Step("Назначить пользователя Automation Host 2 администратором")
    public MembersMenuSteps appointAutomationAdmin() {
        SelenideElement secondAdminCard = $$(elements.getMemberCard())
                .filterBy(text("Automation Host 2"))
                .get(1);
        Actions actions = new Actions(getWebDriver());
        actions.moveToElement(secondAdminCard).perform();
        secondAdminCard.$(elements.getContextMenuButton()).shouldBe(visible).click();
        secondAdminCard.$(elements.getMakeAdminButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Удалить пользователя Automation Admin из группы")
    public MembersMenuSteps deleteAutomationAdminFromGroup() {
        SelenideElement secondAdminCard = $$(elements.getMemberCard())
                .filterBy(text("Automation Admin"))
                .get(1);
        Actions actions = new Actions(getWebDriver());
        actions.moveToElement(secondAdminCard).perform();
        secondAdminCard.$(elements.getContextMenuButton()).shouldBe(visible).click();
        secondAdminCard.$(elements.getDeleteFromGroupButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Назначить пользователя {userName} администратором")
    public MembersMenuSteps makeUserAdministrator(String userName) {
        actions = new Actions(getWebDriver());
        String dynamicSelector = elements.getDefinedUserButton().replace("{username}", userName);
        actions.moveToElement($x(dynamicSelector)).perform();
        $(elements.getContextMenuButton()).click();
        $(elements.getMakeAdminButton()).click();
        makeGroupAdminModalSteps.appointAsAdmin();
        $(elements.getCloseMembersMenu()).click();
        return this;
    }

    @Step("Проверить, что пользователь {userName} является администратором")
    public MembersMenuSteps verifyUserIsAdministrator(String userName) {
        actions = new Actions(getWebDriver());
        String dynamicSelector = elements.getDefinedUserButton().replace("{username}", userName);
        actions.moveToElement($x(dynamicSelector)).perform();
        $(elements.getContextMenuButton()).click();
        makeGroupAdminModalSteps.verifyRemoveAdminButtonVisible();
        return this;
    }

    @Step("Клик по кнопке добавить участников")
    public MembersMenuSteps clickAddMembersButton() {
        SelenideElement addMembersButton = $(elements.getAddMembersButton());
        addMembersButton.shouldBe(visible, Duration.ofSeconds(10)).click();
        return this;
    }

    @Step("Выйти из меню участников")
    public MembersMenuSteps closeMembersMenu() {
        $(elements.getCloseMembersMenu()).click();
        return this;
    }
}
