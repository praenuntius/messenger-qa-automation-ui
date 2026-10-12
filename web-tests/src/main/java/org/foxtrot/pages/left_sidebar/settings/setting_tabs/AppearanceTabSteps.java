package org.foxtrot.pages.left_sidebar.settings.setting_tabs;

import io.qameta.allure.Step;
import org.foxtrot.assertions.ToggleAssertions;
import org.foxtrot.pages.left_sidebar.settings.setting_tabs.elements.AppearanceTab;

import java.time.Duration;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class AppearanceTabSteps {

    private static final String CLASSIC_THEME_VARIANT_FILL = "rgba(113, 173, 242, 1.00)";
    private static final String TURQUOISE_THEME_VARIANT_FILL = "rgba(68, 180, 195, 1.00)";
    private static final String PURPLE_THEME_VARIANT_FILL = "rgba(144, 142, 243, 1.00)";

    private final AppearanceTab elements = new AppearanceTab();

    @Step("Проверить, что экран настроек оформления содержит блок настройки обоев и узоров")
    public AppearanceTabSteps verifyWallpaperSettingsBlockVisible() {
        $(elements.getWallpaperSettingsBlockButton()).shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Нажать на кнопку 'Выбрать обои и узор'")
    public AppearanceTabSteps clickChooseWallpaperButton() {
        $(elements.getWallpaperSettingsBlockButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Проверить, что на экране настроек отображается кнопка 'Сбросить'")
    public AppearanceTabSteps verifyResetWallpaperButtonVisible() {
        $(elements.getResetWallpaperButton()).shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что превью на экране настроек соответствует выбранным обоям и узору")
    public AppearanceTabSteps verifySettingsPreviewMatches(String expectedColor, String expectedClass) {
        $(elements.getSettingsPreviewPatternImage())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(attribute("color", expectedColor))
                .shouldHave(attribute("class", expectedClass));
        return this;
    }

    @Step("Проверить, что выбрана тёмная тема")
    public AppearanceTabSteps verifyDarkThemeSelected() {
        ToggleAssertions.assertChecked(
                elements.getThemeDarkRadiobutton(),
                true, Duration.ofSeconds(10), false);
        return this;
    }

    @Step("Проверить, что выбран цвет оформления 'Бирюза'")
    public AppearanceTabSteps verifyTurquoiseThemeVariantSelected() {
        $(elements.getWallpaperSettingsThemeIcon())
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(attribute("fill", TURQUOISE_THEME_VARIANT_FILL));
        return this;
    }
}