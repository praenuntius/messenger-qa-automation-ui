package org.foxtrot.mobile.elements;

import org.foxtrot.mobile.elements.base.BaseElement;
import org.openqa.selenium.By;

import java.util.List;

public class TextView extends BaseElement {

    public TextView(By locator) {
        super(locator);
    }

    public TextView(By locator, int index) {
        super(locator, index);
    }

    public TextView(By androidLocator, By iosLocator) {
        super(androidLocator, iosLocator);
    }

    public TextView(By androidLocator, int androidIndex, By iosLocator, int iosIndex) {
        super(androidLocator, androidIndex, iosLocator, iosIndex);
    }

    public TextView(BaseElement parent, By locator) {
        super(parent, locator);
    }

    public TextView(BaseElement parent, By androidLocator, By iosLocator) {
        super(parent, androidLocator, iosLocator);
    }

    public TextView(BaseElement parent, By androidLocator, int androidIndex, By iosLocator, int iosIndex) {
        super(parent, androidLocator, androidIndex, iosLocator, iosIndex);
    }

    @Override
    public void click() {
        super.click();
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
    public boolean isDisplayed() {
        return super.isDisplayed();
    }

    @Override
    public boolean isDisplayed(long seconds) {
        return super.isDisplayed(seconds);
    }

    public List<TextView> getTextViews() {
        return super.findElements(locator, TextView::new);
    }
}
