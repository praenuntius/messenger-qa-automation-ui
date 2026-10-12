package org.foxtrot.pages.chat.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.modals.elements.ReactionsAndViewsModal;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.xpath;

public class ReactionsAndViewsModalSteps {

    ReactionsAndViewsModal elements = new ReactionsAndViewsModal();

    @Step("Проверить наличие заглушки с инициалами у пользователя {userName} в модальном окне реакций и просмотров")
            public void verifyUserHasInitialsAvatarPlaceholderInReactionsAndViewsModal(String userName, String phone) {
        String photo = elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        String user = elements.getUserName().replace("{username}", userName);
        $(xpath(user)).shouldBe(visible);
        $(xpath(photo)).shouldNotBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
    }

    @Step("Проверить наличие серой заглушки у пользователя {userName} в списке пользователей для форварда")
    public void verifyUserHasAvatarPlaceholderInReactionsAndViewsModal(String userName, String phone, boolean phoneVisible) {
        $(xpath(elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName))).shouldNotBe(visible);

        String selector = phoneVisible
                ? elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone)
                : elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", userName);

        $(xpath(selector)).shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Проверить наличие аватарки у пользователя {userName} в модальном окне реакций и просмотров")
    public void verifyUserHasAvatarInReactionsAndViewsModal(String userName, String phone) {
        String photo = elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        String user = elements.getUserName().replace("{username}", userName);
        $(xpath(user)).shouldBe(visible);
        $(xpath(photo)).shouldBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
    }
}
