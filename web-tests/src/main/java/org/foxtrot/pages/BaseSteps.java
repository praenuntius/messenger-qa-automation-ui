package org.foxtrot.pages;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.config.RunChannelResolver;
import org.foxtrot.config.WebConfig;
import org.foxtrot.pages.login.PhoneInputPageSteps;
import org.openqa.selenium.TimeoutException;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.open;

public class BaseSteps {

    private static final WebConfig config = ConfigFactory.create(WebConfig.class);
    private static final Duration WEB_APP_READY_TIMEOUT = Duration.ofSeconds(60);

    @Step("Открыть стартовую страницу")
    public void openStartPage() {
        openWithRetry(config.baseUrl(), 2, true);
    }

    @Step("Открыть страницу по ссылке: {url}")
    public void openCustomPage(String url) {
        openWithRetry(url, 2, false);
    }

    private static void openWithRetry(String url, int maxAttempts, boolean waitForWebApp) {
        PhoneInputPageSteps phoneSteps = new PhoneInputPageSteps();
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                open(url);
                if (waitForWebApp && RunChannelResolver.isWeb()) {
                    phoneSteps.waitForLoginOrHomespace(WEB_APP_READY_TIMEOUT);
                }
                return;
            } catch (TimeoutException e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
                Selenide.sleep(1000);
            } catch (Throwable e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
                Selenide.sleep(1000);
            }
        }
    }
}
