package org.foxtrot.assertions;

import io.qameta.allure.Step;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class AssertionsSteps {

    @Step("Проверить, что ожидаемое значение '{0}' соответствует актуальному '{1}'")
    public void assertStringEquals(String expected, String actual) {
        assertThat(
                "Значения не совпадают",
                actual,
                equalTo(expected)
        );
    }

    @Step("Проверить, что ожидаемое число '{0}' соответствует актуальному '{1}'")
    public void assertIntegerEquals(int expected, int actual) {
        assertThat(
                "Значения не совпадают",
                actual,
                equalTo(expected)
        );
    }

    @Step("Проверить, что положение чата в списке не изменилось")
    public void assertChatPositionNotChanged(int positionBefore, int positionAfter) {
        assertIntegerEquals(positionBefore, positionAfter);
    }

    @Step("Проверить, что строка '{0}' содержит значение '{1}'")
    public void assertStringContains(String str, String contains) {
        assertThat(
                "Строка не содержит ожидаемое значение",
                str,
                containsString(contains)
        );
    }
}