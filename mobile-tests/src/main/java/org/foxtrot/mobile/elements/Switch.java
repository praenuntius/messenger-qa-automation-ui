package org.foxtrot.mobile.elements;

import org.foxtrot.mobile.elements.base.BaseElement;
import org.openqa.selenium.By;

import java.util.List;

public class Switch extends BaseElement {

    public Switch(By loc) {
        super(loc);
    }

    public Switch(By loc, int index) {
        super(loc, index);
    }

    public Switch(By androidLocator, By iosLocator) {
        super(androidLocator, iosLocator);
    }

    public Switch(By androidLocator, int androidIndex, By iosLocator, int iosIndex) {
        super(androidLocator, androidIndex, iosLocator, iosIndex);
    }

    public Switch(BaseElement parent, By locator) {
        super(parent, locator);
    }

    public Switch(BaseElement parent, By androidLocator, By iosLocator) {
        super(parent, androidLocator, iosLocator);
    }

    public Switch(BaseElement parent, By androidLocator, int androidIndex, By iosLocator, int iosIndex) {
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
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public boolean isChecked() {
        return super.isChecked();
    }

    @Override
    public boolean isDisplayed() {
        return super.isDisplayed();
    }

    public List<Switch> getSwitches() {
        return super.findElements(locator, Switch::new);
    }
}
