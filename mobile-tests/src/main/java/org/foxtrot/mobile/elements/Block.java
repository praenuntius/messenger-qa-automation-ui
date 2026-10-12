package org.foxtrot.mobile.elements;

import org.foxtrot.mobile.elements.base.BaseElement;
import org.openqa.selenium.By;

import java.util.List;

public class Block extends BaseElement {

    public Block(By locator) {
        super(locator);
    }

    public Block(By locator, int index) {
        super(locator, index);
    }

    public Block(By androidLocator, By iosLocator) {
        super(androidLocator, iosLocator);
    }

    public Block(By androidLocator, int androidIndex, By iosLocator, int iosIndex) {
        super(androidLocator, androidIndex, iosLocator, iosIndex);
    }

    public Block(BaseElement parent, By locator) {
        super(parent, locator);
    }

    public Block(BaseElement parent, By locator, int index) {
        super(parent, locator, index);
    }

    public Block(BaseElement parent, By androidLocator, By iosLocator) {
        super(parent, androidLocator, iosLocator);
    }

    public Block(BaseElement parent, By androidLocator, int androidIndex, By iosLocator, int iosIndex) {
        super(parent, androidLocator, androidIndex, iosLocator, iosIndex);
    }

    @Override
    public void click() {
        super.click();
    }

    public void shortTap() {
        super.shortTap();
    }

    @Override
    public boolean isDisplayed() {
        return super.isDisplayed();
    }

    @Override
    public boolean isDisplayed(long seconds) {
        return super.isDisplayed(seconds);
    }

    public List<Block> getBlocks() {
        return super.findElements(locator, Block::new);
    }

}
