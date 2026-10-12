package org.foxtrot.test;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.foxtrot.allure.ChannelAllureExtension;
import org.foxtrot.config.RunChannelResolver;
import org.foxtrot.uiCore.DesktopChannelExtension;
import org.foxtrot.uiCore.PageObjectManager;
import org.foxtrot.uiCore.SelenideConfig;
import org.foxtrot.utils.BrowserLogCapture;
import org.foxtrot.utils.BrowserLogExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Базовый класс web-тестов.
 * <p>
 * Web (по умолчанию): Chrome / Selenoid.<br>
 * Desktop: {@code -Drun.channel=desktop} — те же page objects, Electron + Selenide CDP.
 */
@ExtendWith({DesktopChannelExtension.class, ChannelAllureExtension.class, BrowserLogExtension.class})
public class BaseTest {

    protected PageObjectManager pages;

    @BeforeAll
    static void globalSetup() {
        SelenideConfig.configure();
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().includeSelenideSteps(false));
    }

    @BeforeEach
    void setUp() {
        pages = new PageObjectManager();
        pages.baseSteps.openStartPage();
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        String className = testInfo.getTestClass().map(Class::getSimpleName).orElse("Unknown");
        String methodName = testInfo.getTestMethod().map(m -> m.getName()).orElse("unknown");
        BrowserLogCapture.saveBrowserLogs(className, methodName);

        if (RunChannelResolver.isWeb()) {
            try {
                Selenide.closeWebDriver();
            } catch (Exception ignored) {
            }
        }
        // desktop: WebDriver и Electron закрывает DesktopChannelExtension
    }

    @AfterAll
    static void afterAll() {
        SelenideLogger.removeListener("AllureSelenide");
    }
}
