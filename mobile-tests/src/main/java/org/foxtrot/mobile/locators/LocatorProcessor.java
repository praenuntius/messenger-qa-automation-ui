package org.foxtrot.mobile.locators;

import org.foxtrot.mobile.core.application.capabilities.CapabilitiesCreator;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;

import java.util.Objects;

public class LocatorProcessor {

    private LocatorProcessor() {
    }

    public static By getLocator(By androidLocator, By iosLocator) {
        Platform platform = CapabilitiesCreator.getCapabilities().getPlatformName();
        return switch (Objects.requireNonNull(platform)) {
            case ANDROID -> Objects.requireNonNull(androidLocator, "Android locator не может быть null");
            case IOS -> Objects.requireNonNull(iosLocator, "iOS locator не может быть null");
            default -> throw new RuntimeException("Данный вид платформы не поддерживается");
        };
    }

    public static Integer getIndex(Integer androidIndex, Integer iosIndex) {
        Platform platform = CapabilitiesCreator.getCapabilities().getPlatformName();
        return switch (Objects.requireNonNull(platform)) {
            case ANDROID -> androidIndex;
            case IOS -> iosIndex;
            default -> throw new RuntimeException("Данный вид платформы не поддерживается");
        };
    }
}
