package org.foxtrot.pages;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.refresh;

public class LocalStorageJsSteps {

    @Step("Переключить режим счётчика непрочитанных на 'Группы'")
    public LocalStorageJsSteps switchToGroupsCounterMode() {
        executeJavaScript("localStorage.setItem('unreadCounterMode', 'GROUPS')");
        refresh();
        return this;
    }

    @Step("Установить цвет оформления 'Классика'")
    public LocalStorageJsSteps setClassicThemeVariant() {
        setThemeVariant("themeFirst");
        return this;
    }

    @Step("Установить цвет оформления 'Бирюза'")
    public LocalStorageJsSteps setTurquoiseThemeVariant() {
        setThemeVariant("themeSecond");
        return this;
    }

    @Step("Установить цвет оформления 'Пурпур'")
    public LocalStorageJsSteps setPurpleThemeVariant() {
        setThemeVariant("themeThird");
        return this;
    }

    @Step("Установить светлую тему")
    public LocalStorageJsSteps setLightThemeMode() {
        setThemeMode("light");
        return this;
    }

    @Step("Установить тёмную тему")
    public LocalStorageJsSteps setDarkThemeMode() {
        setThemeMode("dark");
        return this;
    }

    private void setThemeVariant(String themeVariant) {
        setConfigStoreProperty("themeVariant", themeVariant);
    }

    private void setThemeMode(String themeMode) {
        setConfigStoreProperty("themeMode", themeMode);
    }

    private void setConfigStoreProperty(String property, String value) {
        executeJavaScript("""
                var store = JSON.parse(localStorage.getItem('configStore') || '{}');
                store[arguments[0]] = arguments[1];
                localStorage.setItem('configStore', JSON.stringify(store));
                """, property, value);
        refresh();
    }
}