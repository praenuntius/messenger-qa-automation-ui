package org.foxtrot.mobile.utils;

import com.codeborne.selenide.Selenide;
import org.foxtrot.mobile.screens.LoaderScreen;
import org.foxtrot.utils.Waits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public final class LoaderWaiter {

    private static final Logger logger = LoggerFactory.getLogger(LoaderWaiter.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(60);
    private static final Duration POLLING = Duration.ofMillis(300);
    private static final LoaderScreen loaderScreen = new LoaderScreen();

    private LoaderWaiter() {
    }

    public static void waitForNoLoader() {
        long start = System.currentTimeMillis();
        Waits.sleepMillis(500);
        Selenide.Wait()
                .withTimeout(TIMEOUT)
                .pollingEvery(POLLING)
                .until(driver -> {
                    boolean visible = loaderScreen.isLoaderDisplayed();
                    if (visible) {
                        logger.info("Лоадер еще отображается {} сек...", (System.currentTimeMillis() - start) / 1000);
                    }
                    return !visible;
                });
        Waits.sleepMillis(500);
    }
}
