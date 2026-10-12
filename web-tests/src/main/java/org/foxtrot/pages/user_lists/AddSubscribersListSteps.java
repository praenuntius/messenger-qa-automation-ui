package org.foxtrot.pages.user_lists;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.pages.user_lists.elements.AddSubscribersList;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

public class AddSubscribersListSteps {

    private final AddSubscribersList elements = new AddSubscribersList();

    @Step("Нажать кнопку добавления подписчиков")
    public void clickAddSubscribersButton() {
        SelenideElement addSubscribersButton = $(elements.getDoneButtonSelector()).shouldBe(visible, Duration.ofSeconds(15));
        addSubscribersButton.click();
    }

    @Step("Добавить подписчика {memberName}")
    public void clickAddSubscriber(String memberName) {
        String dynamicSelector = elements.getUserCardButtonSelector() + memberName.replace(" ", "-").toLowerCase() + "']";
        $(cssSelector(dynamicSelector)).shouldBe(visible).click();
        $(elements.getDoneButtonSelector()).click();
    }

    @Step("Добавить подписчика '{name}'")
    public void selectSubscriber(String name) {
        $(xpath(UsersEnum.xpathWithUiNames(elements.getDefinedUserButton(), name))).shouldBe(visible).click();
    }

    @Step("Проверить наличие заглушки с инициалами у пользователя {userName} в списке на добавление в канал")
    public void verifyChannelSubscribersHasInitialsAvatarPlaceholder(String userName, String phone) {
        String photo = UsersEnum.xpathWithUiNames(elements.getDefinedUserWithNameAndAvatar(), userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        String user = UsersEnum.xpathWithUiNames(elements.getDefinedUserButton(), userName);
        $(xpath(user)).shouldBe(visible);
        $(xpath(photo)).shouldNotBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
    }

    @Step("Проверить наличие серой заглушки у пользователя {userName} в списке на добавление в канал")
    public void verifyChannelSubscribersHasAvatarPlaceholder(String userName, String phone, boolean phoneVisible ) {
        $(xpath(UsersEnum.xpathWithUiNames(elements.getDefinedUserWithNameAndAvatar(), userName))).shouldNotBe(visible);

        String selector = phoneVisible
                ? elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone)
                : UsersEnum.xpathWithUiNames(elements.getDefinedUserWithAvatarPlaceholder(), userName);

        $(xpath(selector)).shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Проверить наличие аватарки у пользователя {userName} в списке на добавление в канал")
    public void verifyChannelSubscribersHasAvatar(String userName, String phone) {
        String photo = UsersEnum.xpathWithUiNames(elements.getDefinedUserWithNameAndAvatar(), userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        String user = UsersEnum.xpathWithUiNames(elements.getDefinedUserButton(), userName);
        $(xpath(user)).shouldBe(visible);
        $(xpath(photo)).shouldBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
    }
}
