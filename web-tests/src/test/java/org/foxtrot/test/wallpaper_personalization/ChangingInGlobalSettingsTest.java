package org.foxtrot.test.wallpaper_personalization;

import io.qameta.allure.AllureId;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ChangingInGlobalSettingsTest extends BaseTest {

    private static final int CUSTOM_WALLPAPER_INDEX = 2;
    private static final int CUSTOM_PATTERN_INDEX = 1;

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();

    @AfterEach
    void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @AllureId("62927")
    @DisplayName("Общая настройка: Применение кастомных обоев рисунок+цвет при цвете оформления Бирюза и темной теме (тогл выкл)")
    public void applyCustomWallpaperWithPatternOnTurquoiseDarkThemeToggleOff() {
        var ct = groupFactory.privateGroup();
        loginHelper.performUILogin(ct.getOwner());

        pages.localStorageJsSteps.setTurquoiseThemeVariant()
                .setDarkThemeMode();
        pages.leftSidebarSteps.openSettings();
        pages.settingsMenuSteps.openDesignTab();
        pages.appearanceTabSteps.verifyDarkThemeSelected()
                .verifyTurquoiseThemeVariantSelected()
                .verifyWallpaperSettingsBlockVisible()
                .clickChooseWallpaperButton();

        String previewColorBeforeWallpaper = pages.appearanceModalSteps
                .verifyModalOpened()
                .ensureApplyToAllChatsToggleOff()
                .getPreviewColor();

        String previewClassAfterWallpaper = pages.appearanceModalSteps
                .selectWallpaper(CUSTOM_WALLPAPER_INDEX)
                .verifyWallpaperAppliedToPreview(CUSTOM_WALLPAPER_INDEX, previewColorBeforeWallpaper)
                .getPreviewClass();

        String previewColorAfterWallpaper = pages.appearanceModalSteps.getPreviewColor();

        pages.appearanceModalSteps
                .selectPattern(CUSTOM_PATTERN_INDEX)
                .verifyPatternAppliedToPreview(
                        CUSTOM_PATTERN_INDEX,
                        previewClassAfterWallpaper,
                        previewColorAfterWallpaper
                );

        String expectedWallpaperColor = pages.appearanceModalSteps.getPreviewColor();
        String expectedPreviewClass = pages.appearanceModalSteps.getPreviewClass();

        pages.appearanceModalSteps.clickApply().verifyModalClosed();
        pages.appearanceTabSteps
                .verifySettingsPreviewMatches(expectedWallpaperColor, expectedPreviewClass)
                .verifyResetWallpaperButtonVisible();
        pages.settingsMenuSteps.closeSettingsMenu();
        pages.chatListSteps.clickSavedChat();
        pages.chatVerify.verifyCustomWallpaperApplied(expectedWallpaperColor, expectedPreviewClass);
        pages.chatListSteps.clickChatByName(ct.getGroupName());
        pages.chatVerify.verifyCustomWallpaperApplied(expectedWallpaperColor, expectedPreviewClass);
    }
}
