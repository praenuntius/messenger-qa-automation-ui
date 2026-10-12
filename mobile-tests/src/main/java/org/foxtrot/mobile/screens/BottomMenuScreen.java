package org.foxtrot.mobile.screens;

import org.foxtrot.mobile.config.MobilePlatformResolver;
import org.foxtrot.mobile.core.driver.DriverManager;
import org.foxtrot.mobile.elements.Block;
import org.foxtrot.mobile.elements.Button;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

import static org.foxtrot.mobile.elements.base.BaseElement.byAccessibilityId;
import static org.foxtrot.mobile.elements.base.BaseElement.byResourceId;

public class BottomMenuScreen {

    private static final Logger log = LoggerFactory.getLogger(BottomMenuScreen.class);
    private static final int TAB_COUNT = 4;

    private final Block menuBlock = new Block(
            byResourceId("msvMain"),
            byAccessibilityId("TabBar.tabBar")
    );
    private final Button chatsButton = new Button(
            byResourceId("buttonGlobalSpace"),
            byAccessibilityId("TabBar.chatsItem")
    );
    private final Button workspacesButton = new Button(
            byResourceId("buttonWorkspaces"),
            byAccessibilityId("TabBar.workspacesItem")
    );
    private final Button videoCallsButton = new Button(
            byResourceId("buttonVideoCalls"),
            byAccessibilityId("TabBar.callItem")
    );
    private final Button accountButton = new Button(
            byResourceId("buttonAccount"),
            byAccessibilityId("TabBar.profileItem")
    );

    public void clickChatsBtn() {
        chatsButton.click();
    }

    public void clickWorkspacesBtn() {
        workspacesButton.click();
    }

    public void clickVideoCallsBtn() {
        videoCallsButton.click();
    }

    public void clickAccountBtn() {
        accountButton.click();
    }

    public boolean checkMenuBlockDisplayed() {
        return menuBlock.isDisplayed();
    }

    /** TabBar в дереве, даже если XCUITest ставит {@code visible=false} из‑за оверлея. */
    public boolean isTabBarPresentInTree() {
        return !findTabElements(chatsLocator()).isEmpty()
                || !findTabElements(workspacesLocator()).isEmpty()
                || !findTabElements(tabBarLocator()).isEmpty();
    }

    public boolean tapChatsIgnoringVisibility() {
        return tapTabIgnoringVisibility(chatsLocator(), 0);
    }

    public boolean tapWorkspacesIgnoringVisibility() {
        return tapTabIgnoringVisibility(workspacesLocator(), 1);
    }

    private By chatsLocator() {
        return MobilePlatformResolver.isIos()
                ? byAccessibilityId("TabBar.chatsItem")
                : byResourceId("buttonGlobalSpace");
    }

    private By workspacesLocator() {
        return MobilePlatformResolver.isIos()
                ? byAccessibilityId("TabBar.workspacesItem")
                : byResourceId("buttonWorkspaces");
    }

    private By tabBarLocator() {
        return MobilePlatformResolver.isIos()
                ? byAccessibilityId("TabBar.tabBar")
                : byResourceId("msvMain");
    }

    private boolean tapTabIgnoringVisibility(By locator, int tabIndexFromLeft) {
        var driver = DriverManager.getDriver();
        Duration previousWait = driver.manage().timeouts().getImplicitWaitTimeout();
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(400));
            List<WebElement> found = driver.findElements(locator);
            int x;
            int y;
            var size = driver.manage().window().getSize();
            if (!found.isEmpty()) {
                Rectangle rect = found.get(0).getRect();
                if (rect.getWidth() > 0 && rect.getHeight() > 0 && rect.getY() > 0) {
                    x = rect.getX() + rect.getWidth() / 2;
                    y = rect.getY() + rect.getHeight() / 2;
                } else {
                    x = size.getWidth() * (2 * tabIndexFromLeft + 1) / (2 * TAB_COUNT);
                    y = size.getHeight() - 22;
                }
            } else {
                x = size.getWidth() * (2 * tabIndexFromLeft + 1) / (2 * TAB_COUNT);
                y = size.getHeight() - 22;
            }
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence sequence = new Sequence(finger, 1);
            sequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
            sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            sequence.addAction(new Pause(finger, Duration.ofMillis(80)));
            sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(List.of(sequence));
            return true;
        } catch (Exception e) {
            log.warn("Force tap tab failed ({}): {}", locator, e.toString());
            return false;
        } finally {
            driver.manage().timeouts().implicitlyWait(previousWait);
        }
    }

    private List<WebElement> findTabElements(By locator) {
        var driver = DriverManager.getDriver();
        Duration previousWait = driver.manage().timeouts().getImplicitWaitTimeout();
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(400));
            return driver.findElements(locator);
        } catch (Exception e) {
            return List.of();
        } finally {
            driver.manage().timeouts().implicitlyWait(previousWait);
        }
    }
}
