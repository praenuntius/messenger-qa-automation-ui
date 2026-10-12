package org.foxtrot.mobile.screens;

import org.foxtrot.mobile.elements.Block;
import org.foxtrot.mobile.elements.Button;

import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class SelectFileScreen {

    private final Block bottomSheetBg = new Block(
            byExternalResourceId("com.google.android.documentsui:id/dir_list"),
            byAccessibilityId("FullDocumentManagerViewControllerNavigationBar")
    );

    private Button fileButton(String fileName) {
        return new Button(
                byExternalResourceIdAndText("android:id/title", fileName),
                byAccessibilityId(fileName.replace(".", ", "))
        );
    }

    public boolean isBottomSheetDisplayed(long second) {
        return bottomSheetBg.isDisplayed(second);
    }

    public void clickFileBtn(String fileName) {
        fileButton(fileName).click();
    }
}
