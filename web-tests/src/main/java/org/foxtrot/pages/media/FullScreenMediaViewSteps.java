package org.foxtrot.pages.media;

import io.qameta.allure.Step;
import org.foxtrot.pages.media.elements.FullScreenMediaView;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.xpath;

public class FullScreenMediaViewSteps {

    FullScreenMediaView elements = new FullScreenMediaView();

    @Step("Проверить что открыт просмотр медиа в полноэкранном режиме")
    public void verifyFullscreenMediaModeIsOpened() {
        $(elements.getFullscreenDialog()).shouldBe(visible, Duration.ofSeconds(5));
    }

    @Step("Проверить что в полноэкранном режиме отображается изображение")
    public void verifyImageIsPresentInFullscreen() {
        $(elements.getFullscreenDialog()).shouldBe(visible, Duration.ofSeconds(5));
    }

    @Step("Проверить что в полноэкранном режиме отображается видео")
    public void verifyVideoIsPresentInFullscreen() {
        $(elements.getVideoController()).shouldBe(visible, Duration.ofSeconds(5));
    }

    @Step("Проверить наличие заглушки с инициалами у пользователя {userName} при просмотре медиа")
    public void verifyUserHasInitialsAvatarPlaceholderInMediaView(String userName, String phone) {
        String photo = elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        checkUserName(userName);
        $(xpath(photo)).shouldNotBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
    }

    @Step("Проверить наличие серой заглушки у пользователя {userName} при просмотре медиа")
    public void verifyUserHasAvatarPlaceholderInMediaView(String userName, String phone, boolean phoneVisible) {
        $(xpath(elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName))).shouldNotBe(visible);

        String selector = phoneVisible
                ? elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone)
                : elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", userName);

        $(xpath(selector)).shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Проверить наличие аватарки у пользователя {userName} при просмотре медиа")
    public void verifyUserHasAvatarInMediaView(String userName, String phone) {
        String photo = elements.getDefinedUserWithNameAndAvatar().replace("{username}", userName);
        String placeholder = elements.getDefinedUserWithAvatarPlaceholder().replace("{username}", phone);
        checkUserName(userName);
        $(xpath(photo)).shouldBe(visible);
        $(xpath(placeholder)).shouldNotBe(visible);
    }

    @Step("Проверить что имя пользователя при просмотре медиа равно {userName}")
    public void checkUserName(String userName) {
        $(By.xpath(elements.getUserName().replace("{username}", userName)))
                .shouldBe(visible);
    }
}
