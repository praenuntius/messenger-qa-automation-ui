package org.foxtrot.pages.links;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class LinksInBrowser {

    private String originalWindow;

    public LinksInBrowser() {}

    @Step("Проверить новую вкладку с URL '{expectedUrl}'")
    public LinksInBrowser verifyLinkOpensInNewTab(String expectedUrl) {
        if (originalWindow == null) {
            originalWindow = WebDriverRunner.getWebDriver().getWindowHandle();
        }

        Selenide.Wait().until(driver ->
                driver.getWindowHandles().size() > 1
        );

        String newTabHandle = WebDriverRunner.getWebDriver()
                .getWindowHandles()
                .stream()
                .filter(handle -> !handle.equals(originalWindow))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Новая вкладка не найдена")
                );

        WebDriverRunner.getWebDriver().switchTo().window(newTabHandle);

        String actualUrl = getCurrentUrl();
        System.out.println("ACTUAL URL = " + actualUrl);

        assertThat(
                "Ожидался URL, содержащий: " + expectedUrl,
                actualUrl,
                containsString(expectedUrl)
        );

        WebDriverRunner.getWebDriver().switchTo().window(originalWindow);
        return this;
    }

    private String getCurrentUrl() {
        return WebDriverRunner.getWebDriver().getCurrentUrl();
    }
}