package org.foxtrot.mobile.elements;

import org.foxtrot.mobile.elements.actions.Direction;
import org.foxtrot.mobile.elements.base.BaseElement;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.List;

public class Button extends BaseElement {

    public Button(By loc) {
        super(loc);
    }

    public Button(By loc, int index) {
        super(loc, index);
    }

    public Button(By androidLocator, By iosLocator) {
        super(androidLocator, iosLocator);
    }

    public Button(By androidLocator, int androidIndex, By iosLocator, int iosIndex) {
        super(androidLocator, androidIndex, iosLocator, iosIndex);
    }

    public Button(BaseElement parent, By locator) {
        super(parent, locator);
    }

    public Button(BaseElement parent, By androidLocator, By iosLocator) {
        super(parent, androidLocator, iosLocator);
    }

    public Button(BaseElement parent, By androidLocator, int androidIndex, By iosLocator, int iosIndex) {
        super(parent, androidLocator, androidIndex, iosLocator, iosIndex);
    }

    @Override
    public void click() {
        super.click();
    }

    @Override
    public void doubleTapAndLongPress() {
        super.doubleTapAndLongPress();
    }

    @Override
    public String getText() {
        return super.getText();
    }

    @Override
    public String getText(long seconds) {
        return super.getText(seconds);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public boolean isDisplayed() {
        return super.isDisplayed();
    }

    @Override
    public boolean isDisplayed(long seconds) {
        return super.isDisplayed(seconds);
    }

    public void swipe(Direction direction) {
        super.swipe(direction);
    }

    public void swipe(Direction direction, double percent, Duration duration) {
        super.swipe(direction, percent, duration);
    }

    public List<Button> getButtons() {
        return super.findElements(locator, Button::new);
    }
}
