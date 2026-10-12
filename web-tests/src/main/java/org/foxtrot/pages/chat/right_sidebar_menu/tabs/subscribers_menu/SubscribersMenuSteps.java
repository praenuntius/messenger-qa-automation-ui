package org.foxtrot.pages.chat.right_sidebar_menu.tabs.subscribers_menu;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.right_sidebar_menu.modals.MakeGroupAdminModalSteps;
import org.foxtrot.pages.chat.right_sidebar_menu.tabs.subscribers_menu.elements.SubscribersMenu;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.openqa.selenium.By.xpath;

public class SubscribersMenuSteps {

    private final SubscribersMenu elements = new SubscribersMenu();
    private Actions actions;
    MakeGroupAdminModalSteps makeGroupAdminModalSteps = new MakeGroupAdminModalSteps();


    @Step("Добавить подписчиков")
    public SubscribersMenuSteps clickAddSubscribers() {
        $((elements.getAddSubscribersButton())).shouldBe(visible).click();
        return this;
    }

    @Step("Назначить пользователя Automation Admin администратором")
    public SubscribersMenuSteps appointAutomationAdmin() {
        SelenideElement secondAdminCard = $$(elements.getMemberCard())
                .filterBy(text("Automation Admin"))
                .get(1);

        Actions actions = new Actions(getWebDriver());
        actions.moveToElement(secondAdminCard).perform();
        secondAdminCard.$(elements.getContextMenuButton()).shouldBe(visible).click();
        secondAdminCard.$(elements.getMakeAdminButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Назначить пользователя {userName} администратором")
    public SubscribersMenuSteps makeUserAdministrator(String userName) {
        actions = new Actions(getWebDriver());
        String dynamicSelector = elements.getDefinedUserButton().replace("{username}", userName);
        actions.moveToElement($x(dynamicSelector)).perform();
        $(elements.getContextMenuButton()).click();
        $(elements.getMakeAdminButton()).click();
        makeGroupAdminModalSteps.appointAsAdmin();
        $(elements.getCloseSubscribersMenuButton()).click();
        return this;
    }

    @Step("Проверить, что пользователь {userName} является администратором")
    public SubscribersMenuSteps verifyUserIsAdministrator(String userName) {
        actions = new Actions(getWebDriver());
        String dynamicSelector = elements.getDefinedUserButton().replace("{username}", userName);
        actions.moveToElement($x(dynamicSelector)).perform();
        $(elements.getContextMenuButton()).click();
        makeGroupAdminModalSteps.verifyRemoveAdminButtonVisible();
        return this;
    }

    @Step("Проверить наличие заглушки с инициалами у пользователя {userName} списке подписчиков")
    public SubscribersMenuSteps verifyChannelSubscribersHasInitialsAvatarPlaceholder(String userName, String phone) {
        String photo = elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        verifySubscriberExists(userName);
        $(xpath(photo)).shouldNotBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
        return this;
    }

    @Step("Проверить наличие серой заглушки у пользователя {userName} в списке подписчиков")
    public SubscribersMenuSteps verifyChannelSubscribersHasAvatarPlaceholder(String userName, String phone, boolean phoneVisible) {
        $(xpath(elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName))).shouldNotBe(visible);

        String selector = phoneVisible
                ? elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone)
                : elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", userName);

        $(xpath(selector)).shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить наличие аватарки у пользователя {userName} списке подписчиков")
    public SubscribersMenuSteps verifyChannelSubscribersHasAvatar(String userName, String phone) {
        String photo = elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        verifySubscriberExists(userName);
        $(xpath(photo)).shouldBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
        return this;
    }

    @Step("Удалить пользователя Automation Admin из группы")
    public SubscribersMenuSteps deleteAutomationAdminFromGroup() {
        SelenideElement secondAdminCard = $$(elements.getMemberCard())
                .filterBy(text("Automation Admin"))
                .get(1);

        Actions actions = new Actions(getWebDriver());
        actions.moveToElement(secondAdminCard).perform();
        secondAdminCard.$(elements.getContextMenuButton()).shouldBe(visible).click();
        secondAdminCard.$(elements.getDeleteFromGroupButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Проверить наличие подписчика '{name}'")
    public void verifySubscriberExists(String name) {
        String dynamicSelector = elements.getDefinedUserButton().replace("{username}", name);
        $(xpath(dynamicSelector)).shouldBe(visible, Duration.ofSeconds(10));
    }
}
