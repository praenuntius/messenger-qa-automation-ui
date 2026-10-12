package org.foxtrot.mobile.elements;

import org.foxtrot.mobile.elements.base.BaseElement;
import org.openqa.selenium.By;

import java.util.List;

public class Image extends BaseElement {

    public Image(By loc) {
        super(loc);
    }

    public Image(By loc, int index) {
        super(loc, index);
    }

    public Image(By androidLocator, By iosLocator) {
        super(androidLocator, iosLocator);
    }

    public Image(By androidLocator, int androidIndex, By iosLocator, int iosIndex) {
        super(androidLocator, androidIndex, iosLocator, iosIndex);
    }

    public Image(BaseElement parent, By locator) {
        super(parent, locator);
    }

    public Image(BaseElement parent, By androidLocator, By iosLocator) {
        super(parent, androidLocator, iosLocator);
    }

    public Image(BaseElement parent, By androidLocator, int androidIndex, By iosLocator, int iosIndex) {
        super(parent, androidLocator, androidIndex, iosLocator, iosIndex);
    }

    @Override
    public boolean isDisplayed() {
        return super.isDisplayed();
    }

    @Override
    public boolean isDisplayed(long seconds) {
        return super.isDisplayed(seconds);
    }

    @Override
    public boolean isPlaceholder() {
        return super.isPlaceholder();
    }

    public List<Image> getImages() {
        return super.findElements(locator, Image::new);
    }
}
