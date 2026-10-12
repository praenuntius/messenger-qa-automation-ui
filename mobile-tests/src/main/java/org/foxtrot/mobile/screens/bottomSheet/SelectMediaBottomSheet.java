package org.foxtrot.mobile.screens.bottomSheet;

import io.appium.java_client.AppiumBy;
import org.foxtrot.mobile.elements.Block;
import org.foxtrot.mobile.elements.Button;

import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class SelectMediaBottomSheet {

    private final Block bottomSheetBg = new Block(
            byResourceId("design_bottom_sheet"),
            AppiumBy.iOSNsPredicateString(
                    "name == 'ImagePickerCategory.container' OR name CONTAINS 'PHPicker'"
                            + " OR name CONTAINS 'Photos' OR name == 'Photos'"
            )
    );

    private final Block photosCollection = new Block(
            byResourceId("design_bottom_sheet"),
            AppiumBy.iOSClassChain("**/XCUIElementTypeCollectionView")
    );

    private Button mediaButton(int number) {
        return new Button(
                byResourceId("imPreview"), number - 1,
                AppiumBy.iOSNsPredicateString(
                        "name == 'ImagePickerPhotoCell.container' OR type == 'XCUIElementTypeImage'"),
                number - 1
        );
    }

    private final Button sendMediaBtn = new Button(
            byResourceId("btnSendAttachMedia"),
            byContainsText("Прикрепить")
    );

    private final Button bottomSheetMarker = new Button(
            byResourceId("dialogHeader")
    );

    public boolean isBottomSheetDisplayed(long second) {
        return bottomSheetBg.isDisplayed(second) || photosCollection.isDisplayed(Math.min(5L, second));
    }

    public void clickBottomSheetBtn(int number) {
        mediaButton(number).click();
    }

    public void clickSendMediaBtn() {
        sendMediaBtn.click();
    }
}
