package org.foxtrot.mobile.screens.native_screens;

import io.appium.java_client.AppiumBy;
import org.foxtrot.mobile.elements.Button;

import static org.foxtrot.mobile.elements.base.BaseElement.byAccessibilityId;
import static org.foxtrot.mobile.elements.base.BaseElement.byEqualsText;

public class ContactCreateScreen {

    private final Button saveButton = new Button(
            byAccessibilityId("Сохранить"),
            AppiumBy.iOSNsPredicateString(
                    "name == 'Сохранить' OR label == 'Сохранить' OR name == 'Done' OR label == 'Done'"
                            + " OR name == 'Add' OR label == 'Add' OR name == 'Готово' OR label == 'Готово'"
                            + " OR name == 'Create' OR label == 'Create'"));

    private final Button saveByText = new Button(byEqualsText("Сохранить"));

    public void clickSaveBtn() {
        if (saveButton.isDisplayed(8L)) {
            saveButton.click();
            return;
        }
        if (saveByText.isDisplayed(2L)) {
            saveByText.click();
        }
    }
}
