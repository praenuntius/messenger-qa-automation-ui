package org.foxtrot.mobile.elements.base;

import org.openqa.selenium.By;

@FunctionalInterface
public interface LazyElementCreator<T extends BaseElement> {
    T create(By locator, int index);
}
