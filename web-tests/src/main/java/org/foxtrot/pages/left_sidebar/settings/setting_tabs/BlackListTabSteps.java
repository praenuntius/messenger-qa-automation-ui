package org.foxtrot.pages.left_sidebar.settings.setting_tabs;

import io.qameta.allure.Step;
import org.foxtrot.pages.left_sidebar.settings.setting_tabs.elements.BlackListTab;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.xpath;

public class BlackListTabSteps {

    BlackListTab elements = new BlackListTab();

    @Step("Проверить наличие заглушки с инициалами у пользователя {userName} в черном списке")
    public void verifyUserInBlacklistHasInitialsAvatarPlaceholder(String userName, String phone) {
        String photo = elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        checkUserPresenceInBlackList(userName);
        $(xpath(photo)).shouldNotBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
    }

    @Step("Проверить наличие серой заглушки у пользователя {userName} в списке пользователей для форварда")
    public void verifyUserInBlacklistHasAvatarPlaceholder(String userName, String phone, boolean phoneVisible) {
        $(xpath(elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName))).shouldNotBe(visible);

        String selector = phoneVisible
                ? elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone)
                : elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", userName);

        $(xpath(selector)).shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Проверить наличие аватарки у пользователя {userName} в черном списке")
    public void verifyUserInBlacklistHasAvatar(String userName, String phone) {
        String photo = elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        checkUserPresenceInBlackList(userName);
        $(xpath(photo)).shouldBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
    }

    @Step("Проверить наличие пользователя {userName} в черном списке")
    public void checkUserPresenceInBlackList(String userName) {
        $(By.xpath(elements.getDefinedUserButton().replace("{username}", userName)))
                .shouldBe(visible);
    }
}
