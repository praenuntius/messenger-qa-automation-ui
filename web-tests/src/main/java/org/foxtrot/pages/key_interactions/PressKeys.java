package org.foxtrot.pages.key_interactions;

import io.qameta.allure.Step;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.actions;
import static com.codeborne.selenide.Selenide.sleep;

public class PressKeys {

    /**
     * Определяет модификатор для комбинаций клавиш:
     * COMMAND для Mac, CONTROL для Windows/Linux
     */
    private static Keys getModifierKey() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return os.contains("mac") ? Keys.COMMAND : Keys.CONTROL;
    }

    @Step("Нажать клавишу Enter")
    public static void pressEnter() {
        actions().sendKeys(Keys.ENTER).perform();
        sleep(200);
    }

    @Step("Нажать комбинацию клавиш Shift+Enter (перенос строки)")
    public static void pressShiftEnter() {
        actions()
                .keyDown(Keys.SHIFT)
                .sendKeys(Keys.ENTER)
                .keyUp(Keys.SHIFT)
                .perform();
    }

    @Step("Нажать клавишу BackSpace")
    public static void pressBackSpace() {
        actions().sendKeys(Keys.BACK_SPACE).perform();
        sleep(200);
    }

    @Step("Нажать клавишу Escape")
    public static void pressEscape() {
        actions().sendKeys(Keys.ESCAPE).perform();
        sleep(200);
    }

    @Step("Нажать комбинацию клавиш CMD+C (копировать)")
    public static void pressCmdC() {
        Keys modifier = getModifierKey();
        actions()
                .keyDown(modifier)
                .sendKeys("c")
                .keyUp(modifier)
                .perform();
        sleep(200);
    }

    @Step("Нажать комбинацию клавиш CMD+A (Выделить все)")
    public static void pressCmdA() {
        Keys modifier = getModifierKey();
        actions()
                .keyDown(modifier)
                .sendKeys("a")
                .keyUp(modifier)
                .perform();
        sleep(200);
    }

    @Step("Нажать комбинацию клавиш CMD+V (вставить)")
    public static void pressCmdV() {
        Keys modifier = getModifierKey();
        actions()
                .keyDown(modifier)
                .sendKeys("v")
                .keyUp(modifier)
                .perform();
        sleep(200);
    }

    @Step("Нажать комбинацию клавиш CMD+X (вырезать)")
    public static void pressCmdX() {
        Keys modifier = getModifierKey();
        actions()
                .keyDown(modifier)
                .sendKeys("x")
                .keyUp(modifier)
                .perform();
        sleep(200);
    }

    @Step("Нажать комбинацию клавиш CMD+B (жирный)")
    public static void pressCmdB() {
        Keys modifier = getModifierKey();
        actions()
                .keyDown(modifier)
                .sendKeys("b")
                .keyUp(modifier)
                .perform();
        sleep(200);
    }

    @Step("Нажать комбинацию клавиш CMD+U (подчеркнутый)")
    public static void pressCmdU() {
        Keys modifier = getModifierKey();
        actions()
                .keyDown(modifier)
                .sendKeys("u")
                .keyUp(modifier)
                .perform();
        sleep(200);
    }

    @Step("Нажать комбинацию клавиш CMD+I (курсив)")
    public static void pressCmdI() {
        Keys modifier = getModifierKey();
        actions()
                .keyDown(modifier)
                .sendKeys("i")
                .keyUp(modifier)
                .perform();
        sleep(200);
    }

    @Step("Нажать комбинацию клавиш CMD+K (ссылка)")
    public static void pressCmdK() {
        Keys modifier = getModifierKey();
        actions()
                .keyDown(modifier)
                .sendKeys("k")
                .keyUp(modifier)
                .perform();
        sleep(200);
    }

    @Step("Нажать комбинацию клавиш CMD+S (перкечеркнутый)")
    public static void pressCmdS() {
        Keys modifier = getModifierKey();
        actions()
                .keyDown(modifier)
                .sendKeys("s")
                .keyUp(modifier)
                .perform();
        sleep(200);
    }

    @Step("Нажать комбинацию клавиш CMD+Shift+M (моноширный)")
    public static void pressCmdShiftM() {
        Keys modifier = getModifierKey();
        actions()
                .keyDown(modifier)
                .keyDown(Keys.SHIFT)
                .sendKeys("m")
                .keyUp(Keys.SHIFT)
                .keyUp(modifier)
                .perform();
        sleep(200);
    }

    @Step("Нажать клавишу HOME (переместить курсор в начало строки)")
    public static void pressHome() {
        actions()
                .sendKeys(Keys.HOME)
                .perform();
        sleep(200);
    }
}
