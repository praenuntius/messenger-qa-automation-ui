package org.foxtrot.mobile.screens.homespace;

import io.appium.java_client.AppiumBy;
import org.foxtrot.mobile.core.driver.DriverManager;
import org.foxtrot.mobile.elements.Block;
import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.Image;
import org.foxtrot.mobile.elements.TextView;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;
import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;
import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class ChatsScreen {
    private final TextView screenTitle = new TextView(
            byResourceId("textTitle"),
            byAccessibilityId("ChatList.NavigationBar.title")
    );
    private final Button plusButton = new Button(
            byResourceId("buttonAdd"),
            byAccessibilityId("ChatList.NavigationBar.plus")
    );
    private final TextView chatContextMenu = new TextView(
            byEqualsText("Отключить уведомления")
    );

    private TextView chatName(int number) {
        return new TextView(
                byResourceId("chatTitle"), number - 1,
                byAccessibilityId("ChatList.chatName"), number - 1
        );
    }

    private TextView chatName(String text) {
        return new TextView(
                byResourceIdAndText("chatTitle", text),
                byAccessibilityIdAndText("ChatList.chatName", text)
        );
    }

    private TextView chatNameContains(String text) {
        return new TextView(
                // Android foxtrot: chatTitle может отсутствовать
                byContainsText(text),
                byAccessibilityIdAndContainsText("ChatList.chatName", text)
        );
    }

    private Block chatCell(String text, boolean contains) {
        return new Block(
                contains ? byContainsText(text) : byResourceIdAndText("chatTitle", text),
                iosChatCellLocator(text, contains)
        );
    }

    private static By iosChatCellLocator(String text, boolean contains) {
        String safe = text.replace("\\", "\\\\").replace("'", "\\'").replace("`", "'").replace("$", "");
        String descendant = contains
                ? "name == 'ChatList.chatName' AND (label CONTAINS '%s' OR value CONTAINS '%s')"
                : "name == 'ChatList.chatName' AND (label == '%s' OR value == '%s')";
        return AppiumBy.iOSClassChain(
                "**/XCUIElementTypeCell[`name == 'ChatList.chatCell'`][$"
                        + descendant.formatted(safe, safe) + "$]");
    }

    private Block chatBlock(int number) {
        return new Block(
                byResourceId("chatRow"), number - 1,
                byAccessibilityId("ChatList.chatCell"), number - 1
        );
    }

    private final Image chatAvatar = new Image(
            byResourceId("123"),
            byAccessibilityId("ChatList.chatAvatar")
    );

    private TextView screenTitleByText(String text) {
        return new TextView(
                byResourceIdAndText("textTitle", text),
                byAccessibilityIdAndText("ChatList.NavigationBar.title", text)
        );
    }

    public boolean isChatsScreenTitleDisplayed(String text, long seconds) {
        return screenTitleByText(text).isDisplayed(seconds);
    }

    public boolean isChatsListDisplayed(long seconds) {
        return screenTitle.isDisplayed(seconds) || plusButton.isDisplayed(seconds);
    }

    public void clickPlusBtn() {
        plusButton.click();
    }

    public void clickChatByName(String chatName) {
        if (isIos()) {
            chatCell(chatName, false).shortTap();
            return;
        }
        chatName(chatName).click();
    }

    public void clickChatByNameContains(String text) {
        if (isIos()) {
            chatCell(text, true).shortTap();
            return;
        }
        chatNameContains(text).click();
    }

    public boolean isChatContextMenuDisplayed() {
        return chatContextMenu.isDisplayed(1L);
    }

    public void dismissChatContextMenu() {
        if (!isChatContextMenuDisplayed()) {
            return;
        }
        try {
            DriverManager.getDriver().navigate().back();
        } catch (RuntimeException ignored) {
            // sheet мог закрыться сам
        }
        if (!isChatContextMenuDisplayed()) {
            return;
        }
        try {
            var size = DriverManager.getDriver().manage().window().getSize();
            int x = size.getWidth() / 2;
            int y = Math.max(40, size.getHeight() / 10);
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence sequence = new Sequence(finger, 1);
            sequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
            sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            sequence.addAction(new Pause(finger, Duration.ofMillis(30)));
            sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            DriverManager.getDriver().perform(List.of(sequence));
        } catch (RuntimeException ignored) {
            // координатный тап мог не пройти
        }
    }

    public boolean isChatNameContainsDisplayed(String text, long seconds) {
        return chatNameContains(text).isDisplayed(seconds);
    }

    public boolean isChatNameDisplayed(String chatName) {
        return chatName(chatName).isDisplayed();
    }

    /**
     * Android: {@code UiScrollable.scrollIntoView(textContains)} — надёжнее жестового swipe
     * для RecyclerView (элемент вне viewport отсутствует в hierarchy).
     *
     * @return true если элемент найден/проскроллен в видимую область
     */
    public boolean scrollChatNameIntoView(String textContains) {
        if (isAndroid()) {
            String safe = textContains.replace("\\", "\\\\").replace("\"", "\\\"");
            String expr = "new UiScrollable(new UiSelector().scrollable(true)).setAsVerticalList()"
                    + ".setMaxSearchSwipes(30)"
                    + ".scrollIntoView(new UiSelector().textContains(\"" + safe + "\"))";
            try {
                WebElement el = DriverManager.getDriver().findElement(AppiumBy.androidUIAutomator(expr));
                return el != null;
            } catch (Exception e) {
                return false;
            }
        }
        if (!isIos()) {
            return false;
        }
        String safe = textContains.replace("\\", "\\\\").replace("'", "\\'");
        By pred = AppiumBy.iOSNsPredicateString(
                "name == 'ChatList.chatName' AND (label CONTAINS '%s' OR value CONTAINS '%s')"
                        .formatted(safe, safe));
        try {
            WebElement el = DriverManager.getDriver().findElement(pred);
            DriverManager.getDriver().executeScript("mobile: scroll",
                    Map.of("element", el, "toVisible", true));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickChatByNumber(int number) {
        if (isIos()) {
            chatBlock(number).shortTap();
            return;
        }
        chatBlock(number).click();
    }

    public boolean isChatRowDisplayed(int number, long seconds) {
        return chatBlock(number).isDisplayed(seconds);
    }

    public boolean isChatAvatarPlaceholder() {
        return chatAvatar.isPlaceholder();
    }
}
