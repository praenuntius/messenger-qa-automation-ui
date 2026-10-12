package org.foxtrot.mobile.screens;

import org.foxtrot.mobile.elements.Block;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;
import static org.foxtrot.mobile.elements.base.BaseElement.byAccessibilityId;
import static org.foxtrot.mobile.elements.base.BaseElement.byResourceId;

public class LoaderScreen {

    private final Block progressLoader = new Block(
            byResourceId("vIndeterminateProgress"),
            byAccessibilityId("Common.activityIndicator")
    );

    private final Block shimmerLoader = new Block(
            byResourceId("shimmer_view_container")
    );

    public boolean isLoaderDisplayed() {
        return progressLoader.isDisplayed() || (isAndroid() && shimmerLoader.isDisplayed());
    }
}
