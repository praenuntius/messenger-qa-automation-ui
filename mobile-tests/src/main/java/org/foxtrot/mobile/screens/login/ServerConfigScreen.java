package org.foxtrot.mobile.screens.login;

import io.appium.java_client.AppiumBy;
import org.foxtrot.mobile.core.driver.DriverManager;
import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.TextBox;
import org.foxtrot.mobile.elements.actions.Direction;
import org.foxtrot.mobile.elements.actions.SwipeHelper;

import java.time.Duration;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;
import static org.foxtrot.mobile.elements.base.BaseElement.byEqualsText;
import static org.foxtrot.mobile.elements.base.BaseElement.byXpath;

public class ServerConfigScreen {

    private static final int STAGE_LIST_MAX_SWIPES = 12;

    private final Button screenTitle = new Button(
            byEqualsText("Enter the data of the desired environment"));
    /**
     * Не {@code textContains("stage")}: last() попадает в disabled
     * {@code downloadPath} = {@code https://s3-stage.domain.net}.
     * Environment — первый enabled EditText в форме (domain = [2]).
     */
    private final Button environmentInput = new Button(
            byXpath("//android.widget.ScrollView/android.widget.EditText[@enabled='true'][1]"));

    private Button stageOption(String stageName) {
        return new Button(
                byEqualsText(stageName));
    }

    private final TextBox domainInput = new TextBox(
            byXpath("//android.widget.ScrollView/android.widget.EditText[2]")
    );
    private final Button saveBtn = new Button(
            byEqualsText("Save")
    );

    public boolean isScreenTitleDisplayed(long seconds) {
        return screenTitle.isDisplayed(seconds);
    }

    public void clickEnvironmentInput() {
        environmentInput.click();
    }

    public void clickSaveBtn() {
        saveBtn.click();
    }

    public void clickStageOption(String stageName) {
        Button option = stageOption(stageName);
        if (isAndroid() && !option.isDisplayed(1L)) {
            scrollAndroidStageOptionIntoView(stageName);
        }
        option.click();
    }

    /**
     * stage14 и ниже в dropdown вне viewport — элемента нет в hierarchy.
     * Сначала UiScrollable (ListView/RecyclerView), иначе swipe UP по списку.
     */
    private void scrollAndroidStageOptionIntoView(String stageName) {
        String safe = stageName.replace("\\", "\\\\").replace("\"", "\\\"");
        String child = "new UiSelector().text(\"" + safe + "\")";
        String[] containers = {
                "new UiSelector().className(\"android.widget.ListView\")",
                "new UiSelector().className(\"androidx.recyclerview.widget.RecyclerView\")",
                "new UiSelector().scrollable(true).instance(1)",
                "new UiSelector().scrollable(true)"
        };
        for (String container : containers) {
            String expr = "new UiScrollable(" + container + ").setAsVerticalList()"
                    + ".setMaxSearchSwipes(" + STAGE_LIST_MAX_SWIPES + ")"
                    + ".scrollIntoView(" + child + ")";
            try {
                DriverManager.getDriver().findElement(AppiumBy.androidUIAutomator(expr));
                return;
            } catch (Exception ignored) {
            }
        }
        for (int i = 0; i < STAGE_LIST_MAX_SWIPES; i++) {
            if (stageOption(stageName).isDisplayed(1L)) {
                return;
            }
            SwipeHelper.swipeOnScreen(Direction.UP, 0.35d, Duration.ofMillis(350));
        }
    }

    public void fillDomain(String domain) {
        domainInput.clear();
        domainInput.sendKeys(domain);
    }
}