package org.foxtrot.mobile.elements;

import org.foxtrot.mobile.elements.base.BaseElement;
import org.openqa.selenium.By;

import java.util.List;

public class RadioButton extends BaseElement {

    public RadioButton(By loc) {
        super(loc);
    }

    public RadioButton(By loc, int index) {
        super(loc, index);
    }

    public RadioButton(By androidLocator, By iosLocator) {
        super(androidLocator, iosLocator);
    }

    public RadioButton(By androidLocator, int androidIndex, By iosLocator, int iosIndex) {
        super(androidLocator, androidIndex, iosLocator, iosIndex);
    }

    public RadioButton(BaseElement parent, By locator) {
        super(parent, locator);
    }

    public RadioButton(BaseElement parent, By androidLocator, By iosLocator) {
        super(parent, androidLocator, iosLocator);
    }

    public RadioButton(BaseElement parent, By androidLocator, int androidIndex, By iosLocator, int iosIndex) {
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

    public List<RadioButton> getRadioButtons() {
        return super.findElements(locator, RadioButton::new);
    }
}
