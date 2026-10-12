package org.foxtrot.mobile.screens.bottomSheet;

import org.foxtrot.mobile.elements.Block;
import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.actions.Direction;

import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class SelectImageTypeBottomSheet {

    private final Block bottomSheetBg = new Block(
            byResourceId("actionsListView"),
            byAccessibilityId("MessengerActionSheet.container")
    );

    private Button bottomSheetButton(String text) {
        return new Button(
                bottomSheetBg,
                byResourceIdAndText("buttonAction", text),
                byAccessibilityIdAndText("MessengerActionSheet.button", text)
        );
    }

    private Button bottomSheetButtonByText(String text) {
        return new Button(byEqualsText(text), byEqualsText(text));
    }

    private final Button bottomSheetMarker = new Button(
            byResourceId("marker")
    );

    private final Button bottomSheetCloseButton = new Button(
            byResourceId("MessengerActionSheet.cancelButton")
    );

    public boolean isBottomSheetDisplayed(long sec) {
        return bottomSheetBg.isDisplayed(sec)
                || bottomSheetButtonByText("Выбрать из галереи").isDisplayed(Math.min(3L, sec));
    }

    public void clickBottomSheetBtn(String text) {
        if (bottomSheetButton(text).isDisplayed(2L)) {
            bottomSheetButton(text).click();
            return;
        }
        bottomSheetButtonByText(text).click();
    }

    public void swipeMarkerDown() {
        bottomSheetMarker.swipe(Direction.DOWN);
    }
}
