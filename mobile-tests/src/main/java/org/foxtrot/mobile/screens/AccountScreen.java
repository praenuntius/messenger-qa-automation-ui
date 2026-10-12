package org.foxtrot.mobile.screens;

import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.TextView;
import org.foxtrot.mobile.elements.actions.Direction;
import org.foxtrot.mobile.elements.actions.SwipeHelper;

import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class AccountScreen {

    private final TextView userName = new TextView(
            byResourceId("user_name"),
            byAccessibilityId("CurrentUserProfile.title")
    );
    private final Button exitButton = new Button(
            byResourceId("app_logout"),
            byAccessibilityId("CurrentUserProfile.logout")
    );
    /** Stage/foxtrot: «Выход из приложения»; preprod иногда «Выйти из приложения». */
    private final Button exitButtonByTextPrimary = new Button(
            byEqualsText("Выход из приложения")
    );
    private final Button exitButtonByTextFallback = new Button(
            byEqualsText("Выйти из приложения")
    );

    private TextView screenSubtitle(String text) {
        return new TextView(
                byEqualsText(text)
        );
    }

    public void clickExitBtn() {
        for (int i = 0; i < 4; i++) {
            SwipeHelper.swipeOnScreen(Direction.UP);
            if (exitButton.isDisplayed(2L)) {
                exitButton.click();
                return;
            }
            if (exitButtonByTextPrimary.isDisplayed(2L)) {
                exitButtonByTextPrimary.click();
                return;
            }
            if (exitButtonByTextFallback.isDisplayed(2L)) {
                exitButtonByTextFallback.click();
                return;
            }
        }
        exitButtonByTextPrimary.click();
    }

    public boolean isAccountScreenSubtitleDisplayed(String text, long seconds) {
        return screenSubtitle(text).isDisplayed(seconds);
    }

    public String getAccountName() {
        return userName.getText();
    }
}
