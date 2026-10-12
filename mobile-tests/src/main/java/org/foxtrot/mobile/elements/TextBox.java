package org.foxtrot.mobile.elements;

import org.foxtrot.mobile.elements.base.BaseElement;
import org.openqa.selenium.By;

import java.util.List;

public class TextBox extends BaseElement {

    public TextBox(By loc) {
        super(loc);
    }

    public TextBox(By loc, int index) {
        super(loc, index);
    }

    public TextBox(By androidLocator, By iosLocator) {
        super(androidLocator, iosLocator);
    }

    public TextBox(By androidLocator, int androidIndex, By iosLocator, int iosIndex) {
        super(androidLocator, androidIndex, iosLocator, iosIndex);
    }

    public TextBox(BaseElement parent, By locator) {
        super(parent, locator);
    }

    public TextBox(BaseElement parent, By androidLocator, By iosLocator) {
        super(parent, androidLocator, iosLocator);
    }

    public TextBox(BaseElement parent, By androidLocator, int androidIndex, By iosLocator, int iosIndex) {
        super(parent, androidLocator, androidIndex, iosLocator, iosIndex);
    }

    @Override
    public void click() {
        super.click();
    }

    @Override
    public void sendKeys(String text) {
        super.sendKeys(text);
    }

    @Override
    public void clear() {
        super.clear();
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
    public boolean isDisplayed() {
        return super.isDisplayed();
    }

    @Override
    public boolean isDisplayed(long seconds) {
        return super.isDisplayed(seconds);
    }

    public List<TextBox> getTextBoxes() {
        return super.findElements(locator, TextBox::new);
    }
}
