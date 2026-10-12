package org.foxtrot.mobile.screens;

import org.foxtrot.mobile.elements.Block;
import org.foxtrot.mobile.elements.Button;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;
import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class AvatarAdjustScreen {

    private final Block imageToAdjust = new Block(
            byResourceId("image"),
            byAccessibilityId("CropImage.image")
    );

    private final Block bottomSheetContainer = new Block(
            byAccessibilityId("CropImage.container")
    );
    private final Button doneButtonAndroid = new Button(
            byResourceId("done")
    );

    private final Button doneButtonIos = new Button(
            bottomSheetContainer,
            byEqualsText("Готово")
    );

    private final Block toolbar = new Block(
            byResourceId("toolbar")
    );

    private final Button backButtonAndroid = new Button(
            toolbar,
            byClassName("android.widget.ImageButton")
    );

    private final Button backButtonIos = new Button(
            bottomSheetContainer,
            byEqualsText("Отменить")
    );

    public boolean isImageToAdjustDisplayed(long seconds) {
        return imageToAdjust.isDisplayed(seconds);
    }

    public void clickDoneBtn() {
        if (isAndroid()) {
            doneButtonAndroid.click();
        } else {
            doneButtonIos.click();
        }
    }
}
