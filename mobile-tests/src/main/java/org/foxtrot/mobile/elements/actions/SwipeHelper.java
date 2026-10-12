package org.foxtrot.mobile.elements.actions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.foxtrot.mobile.core.driver.DriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Collections;

public final class SwipeHelper {

    private SwipeHelper() {
    }

    /**
     * Выполняет свайп по экрану, начиная из центра дисплея.
     * Позволяет задать направление, длину свайпа и длительность.
     *
     */
    public static void swipeOnScreen(Direction direction, double percent, Duration duration) {
        Dimension screen = DriverManager.getDriver().manage().window().getSize();
        int centerX = screen.getWidth() / 2;
        int centerY = screen.getHeight() / 2;
        int startX = centerX, startY = centerY, endX = centerX, endY = centerY;
        switch (direction) {
            case UP:
                endY = centerY - (int) (screen.getHeight() * percent);
                break;
            case DOWN:
                endY = centerY + (int) (screen.getHeight() * percent);
                break;
            case LEFT:
                endX = centerX - (int) (screen.getWidth() * percent);
                break;
            case RIGHT:
                endX = centerX + (int) (screen.getWidth() * percent);
                break;
        }
        performSwipe(clipX(startX), clipY(startY), clipX(endX), clipY(endY), duration);
    }

    /**
     * Выполняет свайп по экрану с использованием значений по умолчанию.
     * Использует процент свайпа 0.4 и длительность 500 мс.
     *
     */
    public static void swipeOnScreen(Direction direction) {
        swipeOnScreen(direction, 0.4d, Duration.ofMillis(500));
    }

    /**
     * Выполняет свайп, начиная из центра переданного элемента.
     * Длина свайпа считается от размера экрана.
     */
    public static void swipeFromElement(SelenideElement element, Direction direction, double percent,
            Duration duration) {
        SelenideElement visibleElement = element.shouldBe(Condition.visible);
        Rectangle rect = visibleElement.toWebElement().getRect();
        Dimension screen = DriverManager.getDriver().manage().window().getSize();
        int startX = rect.getX() + rect.getWidth() / 2;
        int startY = rect.getY() + rect.getHeight() / 2;
        int endX = startX;
        int endY = startY;
        switch (direction) {
            case UP:
                endY = startY - (int) (screen.getHeight() * percent);
                break;
            case DOWN:
                endY = startY + (int) (screen.getHeight() * percent);
                break;
            case LEFT:
                endX = startX - (int) (screen.getWidth() * percent);
                break;
            case RIGHT:
                endX = startX + (int) (screen.getWidth() * percent);
                break;
        }
        performSwipe(clipX(startX), clipY(startY), clipX(endX), clipY(endY), duration
        );
    }

    /**
     * Выполняет свайп от элемента со значениями по умолчанию.
     * Использует процент свайпа 0.4 и длительность 500 мс.
     */
    public static void swipeFromElement(SelenideElement element, Direction direction) {
        swipeFromElement(element, direction, 0.4d, Duration.ofMillis(500));
    }

    /**
     * Выполняет низкоуровневый свайп от начальной точки к конечной.
     * Использует W3C Actions API для эмуляции жеста.
     *
     */
    private static void performSwipe(int startX, int startY, int endX, int endY, Duration duration) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(50)));
        swipe.addAction(finger.createPointerMove(duration, PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        try {
            DriverManager.getDriver().perform(Collections.singletonList(swipe));
            try {
                Thread.sleep(120);
            } catch (InterruptedException ignored) {
            }
        } catch (Exception e) {
            throw new RuntimeException("Свайп не выполнился из-за ошибки: " + e.getMessage(), e);
        }
    }

    /**
     * Ограничивает координату X в пределах ширины экрана.
     *
     */
    private static int clipX(int x) {
        int w = DriverManager.getDriver().manage().window().getSize().getWidth();
        return Math.max(0, Math.min(w - 1, x));
    }

    /**
     * Ограничивает координату Y в пределах высоты экрана.
     *
     */
    private static int clipY(int y) {
        int h = DriverManager.getDriver().manage().window().getSize().getHeight();
        return Math.max(0, Math.min(h - 1, y));
    }
}
