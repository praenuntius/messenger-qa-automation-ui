package org.foxtrot.mobile.steps;

import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.BottomMenuScreen;
import org.foxtrot.mobile.utils.LoaderWaiter;
import org.junit.jupiter.api.Assertions;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public class BottomMenuSteps {

    BottomMenuScreen bottomMenuScreen = new BottomMenuScreen();

    @Step("Проверяю, что меню внизу экрана отображается")
    public BottomMenuSteps checkBottomMenuPresent() {
        Assertions.assertTrue(bottomMenuScreen.checkMenuBlockDisplayed(), "Меню внизу экрана не отображается");
        return this;
    }

    public boolean isTabBarDisplayed() {
        return bottomMenuScreen.checkMenuBlockDisplayed();
    }

    public boolean isTabBarPresentInTree() {
        return bottomMenuScreen.isTabBarPresentInTree();
    }

    @Step("Тап [Чаты] в таббаре без ожидания лоадера")
    public BottomMenuSteps tapChatsTabSoft() {
        bottomMenuScreen.clickChatsBtn();
        return this;
    }

    @Step("Тап [Пространства] в таббаре без ожидания лоадера")
    public BottomMenuSteps tapWorkspacesTabSoft() {
        bottomMenuScreen.clickWorkspacesBtn();
        return this;
    }

    @Step("Тап [Чаты] по координатам, даже если visible=false")
    public boolean tapChatsTabForce() {
        return bottomMenuScreen.tapChatsIgnoringVisibility();
    }

    @Step("Тап [Пространства] по координатам, даже если visible=false")
    public boolean tapWorkspacesTabForce() {
        return bottomMenuScreen.tapWorkspacesIgnoringVisibility();
    }

    @Step("Нажимаю 'Чаты' в меню внизу экрана")
    public BottomMenuSteps clickChatsButton() {
        if (isIos()) {
            bottomMenuScreen.tapChatsIgnoringVisibility();
        } else {
            bottomMenuScreen.clickChatsBtn();
        }
        LoaderWaiter.waitForNoLoader();
        return this;
    }

    @Step("Нажимаю 'Пространства' в меню внизу экрана")
    public BottomMenuSteps clickWorkspacesButton() {
        if (isIos()) {
            bottomMenuScreen.tapWorkspacesIgnoringVisibility();
        } else {
            bottomMenuScreen.clickWorkspacesBtn();
        }
        LoaderWaiter.waitForNoLoader();
        return this;
    }

    @Step("Нажимаю 'Видеозвонки' в меню внизу экрана")
    public BottomMenuSteps clickVideoCallsButton() {
        bottomMenuScreen.clickVideoCallsBtn();
        LoaderWaiter.waitForNoLoader();
        return this;
    }

    @Step("Нажимаю 'Аккаунт' в меню внизу экрана")
    public BottomMenuSteps clickAccountButton() {
        bottomMenuScreen.clickAccountBtn();
        LoaderWaiter.waitForNoLoader();
        return this;
    }
}
