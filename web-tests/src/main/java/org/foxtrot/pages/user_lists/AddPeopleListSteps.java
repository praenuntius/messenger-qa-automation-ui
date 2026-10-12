package org.foxtrot.pages.user_lists;

import io.qameta.allure.Step;
import org.foxtrot.pages.user_lists.elements.AddPeopleList;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

public class AddPeopleListSteps {

    private final AddPeopleList elements = new AddPeopleList();

    @Step("Убедиться, что модальное окно отображается")
    private void ensureModalIsDisplayed() {
        $(elements.getDoneButton()).shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Добавить участника '{name}'")
    public AddPeopleListSteps selectPerson(String name) {
        String dynamicSelector = elements.getDefinedUserButton().replace("{username}", name);
        $(xpath(dynamicSelector)).shouldBe(visible).click();
        clickAddUsersButton();
        return this;
    }

    @Step("Добавить участника")
    public void clickAddUsersButton() {
        $(elements.getDoneButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
    }

    @Step("Добавить администратора")
    public void addAdminUser() {
        selectPerson("Automation");
        clickAddUsersButton();
    }

    @Step("Добавить пользователя {memberName}")
    public void addUser(String memberName) {
        String dynamicSelector = elements.getUserListCard() + memberName.replace(" ", "-").toLowerCase() + "']";
        $(cssSelector(dynamicSelector)).shouldBe(visible).click();
        clickAddUsersButton();
    }
}

