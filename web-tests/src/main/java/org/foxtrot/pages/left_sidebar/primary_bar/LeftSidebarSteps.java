package org.foxtrot.pages.left_sidebar.primary_bar;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.foxtrot.config.RunChannelResolver;
import org.foxtrot.pages.left_sidebar.primary_bar.elements.LeftSidebar;
import org.foxtrot.pages.modals.WebOnboardingOverlay;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static java.time.Duration.ofSeconds;

public class LeftSidebarSteps {

    private final LeftSidebar elements = new LeftSidebar();

    @Step("Открыть меню профиля")
    public LeftSidebarSteps openProfileMenu() {
        sleep(2000);
        $(elements.getOpenProfileMenuButton())
                .shouldBe(visible)
                .shouldBe(enabled)
                .click();
        return this;
    }

    @Step("Открыть настройки")
    public LeftSidebarSteps openSettings() {
        $(elements.getOpenSettingsButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Открыть экран FAQ")
    public LeftSidebarSteps openFaqMenu() {
        $(elements.getHelpButton())
                .shouldBe(visible)
                .click();
        return this;
    }

    @Step("Открыть окно доступных рабочих пространств")
    public LeftSidebarSteps enterOrCreateSpaceButton() {
        SelenideElement spaceButton = $(elements.getEnterOrCreateSpaceButton());

        if (!spaceButton.isDisplayed()) {
            spaceButton.scrollTo();
        }
        spaceButton.shouldBe(visible, ofSeconds(10)).click();
        return this;
    }

    @Step("Клик кнопку [Чаты] в левом сайдбаре")
    public LeftSidebarSteps clickChatsButton() {
        SelenideElement chatsButton = $(elements.getWorkspaceChatsButton());

        if (!chatsButton.isDisplayed()) {
            chatsButton.scrollTo();
        }
        chatsButton.shouldBe(visible, ofSeconds(10)).click();
        return this;
    }

    @Step("Открыть пространство '{spaceName}'")
    public LeftSidebarSteps clickSpaceByName(String spaceName) {
        WebOnboardingOverlay.dismissIfPresent();
        SelenideElement spaceButton = $(elements.getDefinedSpaceButton(spaceName))
                .scrollTo()
                .shouldBe(visible, ofSeconds(20))
                .shouldBe(clickable, ofSeconds(10));
        spaceButton.click();
        if (RunChannelResolver.isDesktop()) {
            sleep(500);
        }
        return this;
    }

    @Step("Проверить бейдж непрочитанных на пространстве '{spaceName}'")
    public LeftSidebarSteps verifyUnreadBadgeOnSpace(String spaceName) {
        $(elements.getNewMessagesBadgeOnSpace(spaceName))
                .shouldBe(visible);
        return this;
    }

    @Step("Проверить muted-бейдж на пространстве '{spaceName}' (уведомления выключены)")
    public LeftSidebarSteps verifyUnreadBadgeIsMutedOnSpace(String spaceName) {
        $(elements.getNewMessagesBadgeOnSpace(spaceName))
                .shouldBe(visible)
                .shouldHave(attribute("muted", "true"), ofSeconds(10));
        return this;
    }

    @Step("Проверить бейдж непрочитанных '{count}' на пространстве '{spaceName}'")
    public LeftSidebarSteps verifyUnreadBadgeCountOnSpace(String spaceName, String count) {
        $(elements.getNewMessagesBadgeOnSpace(spaceName))
                .shouldBe(visible)
                .shouldHave(attribute("muted", ""))
                .shouldHave(text(count));
        return this;
    }

    @Step("Проверить бейдж непрочитанных '{count}' у p2p с пользователем '{userId}'")
    public LeftSidebarSteps verifyUnreadBadgeCountOnP2p(Long userId, String count) {
        $(elements.getNewMessagesBadgeOnP2p(userId))
                .shouldBe(visible)
                .shouldHave(text(count));
        return this;
    }

    @Step("Проверить отсутствие бейджа непрочитанных p2p у p2p с пользователем '{userId}")
    public LeftSidebarSteps verifyP2pUnreadBadgeIsNotPresent(Long userId) {
        $(elements.getNewMessagesBadgeOnP2p(userId))
                .shouldNotBe(visible);
        return this;
    }

    @Step("Проверить отсутствие бейджа непрочитанных на пространстве '{spaceName}'")
    public LeftSidebarSteps verifyUnreadBadgeIsNotPresent(String spaceName) {
        $(elements.getNewMessagesBadgeOnSpace(spaceName))
                .shouldNotBe(visible);
        return this;
    }

    @Step("Открыть контекстное меню пространства '{spaceName}'")
    public LeftSidebarSteps openSpaceContextMenu(String spaceName) {
        WebOnboardingOverlay.dismissIfPresent();
        $(elements.getDefinedSpaceButton(spaceName))
                .shouldBe(visible, ofSeconds(20))
                .shouldBe(clickable, ofSeconds(10))
                .contextClick();
        return this;
    }

    @Step("Проверить наличие пространства '{spaceName}'")
    public LeftSidebarSteps isSpaceAvailableByName(String spaceName) {
        $(elements.getDefinedSpaceButton(spaceName)).scrollTo().shouldBe(visible);
        return this;
    }

    @Step("Проверить отсутствие пространства '{spaceName}'")
    public LeftSidebarSteps verifySpaceNotAvailableByName(String spaceName) {
        $(elements.getDefinedSpaceButton(spaceName)).shouldNotBe(visible);
        return this;
    }

    @Step("Проверить что пространство {spaceName} является активным")
    public LeftSidebarSteps verifySpaceIsActive(String spaceName) {
        $(elements.getActiveWorkspaceIndicator(spaceName)).shouldBe(visible);
        return this;
    }
}