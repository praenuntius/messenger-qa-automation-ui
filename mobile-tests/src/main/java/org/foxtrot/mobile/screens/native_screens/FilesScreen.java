package org.foxtrot.mobile.screens.native_screens;

import org.foxtrot.mobile.elements.Button;

import static org.foxtrot.mobile.elements.base.BaseElement.byAccessibilityId;

public class FilesScreen {

    private final Button saveButton = new Button(byAccessibilityId("Сохранить"));

    public void clickSaveBtn() {
        saveButton.click();
    }
}
