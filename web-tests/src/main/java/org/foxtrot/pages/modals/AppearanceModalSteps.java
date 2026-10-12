package org.foxtrot.pages.modals;

import io.qameta.allure.Step;
import org.foxtrot.assertions.ToggleAssertions;
import org.foxtrot.pages.modals.elements.AppearanceModal;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.Objects;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class AppearanceModalSteps {

    private final AppearanceModal elements = new AppearanceModal();

    @Step("Проверить, что открыто модальное окно 'Выбор обоев и узоров'")
    public AppearanceModalSteps verifyModalOpened() {
        $(elements.getModalTitle()).shouldBe(visible, Duration.ofSeconds(10));
        $(elements.getAppearanceApplyButton()).shouldBe(visible);
        $(elements.getAppearanceCancelButton()).shouldBe(visible);
        return this;
    }

    @Step("Убедиться, что тогл 'Применить во всех чатах' выключен")
    public AppearanceModalSteps ensureApplyToAllChatsToggleOff() {
        ToggleAssertions.assertChecked(elements.getApplyToAllChatsToggle(), false, Duration.ofSeconds(10), false);
        return this;
    }

    @Step("Выбрать обои с индексом {wallpaperIndex}")
    public AppearanceModalSteps selectWallpaper(int wallpaperIndex) {
        $(elements.getWallpaperTile(wallpaperIndex)).shouldBe(visible).click();
        return this;
    }

    @Step("Выбрать узор с индексом {patternIndex}")
    public AppearanceModalSteps selectPattern(int patternIndex) {
        $(elements.getPatternTile(patternIndex)).shouldBe(visible).click();
        return this;
    }

    @Step("Проверить, что выбранные обои выделены и цвет применён к превью")
    public AppearanceModalSteps verifyWallpaperAppliedToPreview(int wallpaperIndex, String previewColorBeforeWallpaper) {
        verifyWallpaperTileSelected(wallpaperIndex);
        verifyPreviewColorChanged(previewColorBeforeWallpaper);
        return this;
    }

    @Step("Проверить, что выбранный узор выделен и применён к превью")
    public AppearanceModalSteps verifyPatternAppliedToPreview(
            int patternIndex,
            String previewClassBeforePattern,
            String previewColorBeforePattern
    ) {
        verifyPatternTileSelected(patternIndex);
        verifyPreviewColorUnchanged(previewColorBeforePattern);
        verifyPreviewClassChanged(previewClassBeforePattern);
        return this;
    }

    @Step("Получить color превью модалки")
    public String getPreviewColor() {
        return $(elements.getModalPreviewPatternImage())
                .shouldBe(visible, Duration.ofSeconds(10))
                .getAttribute("color");
    }

    @Step("Получить class превью модалки")
    public String getPreviewClass() {
        return $(elements.getModalPreviewPatternImage())
                .shouldBe(visible, Duration.ofSeconds(10))
                .getAttribute("class");
    }

    @Step("Нажать на кнопку 'Применить'")
    public AppearanceModalSteps clickApply() {
        $(elements.getAppearanceApplyButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Проверить, что модальное окно закрыто")
    public AppearanceModalSteps verifyModalClosed() {
        $(elements.getModalTitle()).shouldBe(hidden, Duration.ofSeconds(10));
        return this;
    }

    private void verifyWallpaperTileSelected(int wallpaperIndex) {
        int referenceIndex = wallpaperIndex == 0 ? 1 : 0;
        verifyTileClassDiffers(
                elements.getWallpaperTile(wallpaperIndex),
                elements.getWallpaperTile(referenceIndex)
        );
    }

    private void verifyPatternTileSelected(int patternIndex) {
        int referenceIndex = patternIndex == 0 ? 1 : 0;
        verifyTileClassDiffers(
                elements.getPatternTile(patternIndex),
                elements.getPatternTile(referenceIndex)
        );
    }

    private void verifyPreviewClassChanged(String previewClassBeforePattern) {
        String currentClass = getPreviewClass();
        if (Objects.equals(previewClassBeforePattern, currentClass)) {
            throw new AssertionError("Preview PatternImage class did not change after pattern selection");
        }
    }

    private void verifyTileClassDiffers(By tileLocator, By referenceTileLocator) {
        String selectedClass = $(tileLocator).shouldBe(visible).getAttribute("class");
        String referenceClass = $(referenceTileLocator).shouldBe(visible).getAttribute("class");
        if (Objects.equals(selectedClass, referenceClass)) {
            throw new AssertionError("Tile does not appear selected: " + tileLocator);
        }
    }

    private void verifyPreviewColorChanged(String previewColorBeforeWallpaper) {
        String currentColor = getPreviewColor();
        if (currentColor == null || currentColor.isBlank()) {
            throw new AssertionError("Preview PatternImage color attribute is empty after wallpaper selection");
        }
        if (Objects.equals(previewColorBeforeWallpaper, currentColor)) {
            throw new AssertionError("Preview PatternImage color did not change after wallpaper selection");
        }
    }

    private void verifyPreviewColorUnchanged(String previewColorBeforePattern) {
        String currentColor = getPreviewColor();
        if (!Objects.equals(previewColorBeforePattern, currentColor)) {
            throw new AssertionError("Preview PatternImage color changed after pattern selection");
        }
    }
}
