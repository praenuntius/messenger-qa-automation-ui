package org.foxtrot.assertions;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.Objects;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public final class ToggleAssertions {

    private ToggleAssertions() {
    }

    public static void assertChecked(By toggleLocator, boolean expectedChecked) {
        assertChecked(toggleLocator, expectedChecked, Duration.ofSeconds(5), true);
    }

    public static void assertChecked(By toggleLocator, boolean expectedChecked, Duration timeout) {
        assertChecked(toggleLocator, expectedChecked, timeout, true);
    }

    /**
     * @param requireVisible false — для скрытых input (displayed:false), например кастомные чекбоксы
     */
    public static void assertChecked(By toggleLocator, boolean expectedChecked, Duration timeout, boolean requireVisible) {
        SelenideElement toggle = requireVisible
                ? $(toggleLocator).shouldBe(visible, timeout)
                : $(toggleLocator).should(exist, timeout);
        Boolean checked = executeJavaScript("return arguments[0] && arguments[0].checked === true;", toggle);

        boolean actualChecked = Boolean.TRUE.equals(checked);
        if (!Objects.equals(actualChecked, expectedChecked)) {
            throw new AssertionError(
                    "Toggle checked state mismatch. Expected checked=" + expectedChecked + ", actual checked=" + actualChecked +
                            ", locator=" + toggleLocator
            );
        }
    }
}

