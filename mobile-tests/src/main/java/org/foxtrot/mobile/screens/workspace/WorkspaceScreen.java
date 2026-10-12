package org.foxtrot.mobile.screens.workspace;

import io.appium.java_client.AppiumBy;
import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.TextView;

import static org.foxtrot.mobile.elements.base.BaseElement.byAccessibilityId;
import static org.foxtrot.mobile.elements.base.BaseElement.byAccessibilityIdAndContainsText;
import static org.foxtrot.mobile.elements.base.BaseElement.byAccessibilityIdAndText;
import static org.foxtrot.mobile.elements.base.BaseElement.byContainsText;
import static org.foxtrot.mobile.elements.base.BaseElement.byEqualsText;
import static org.foxtrot.mobile.elements.base.BaseElement.byResourceId;
import static org.foxtrot.mobile.elements.base.BaseElement.byResourceIdAndContainsText;
import static org.foxtrot.mobile.elements.base.BaseElement.byResourceIdAndText;

public class WorkspaceScreen {

    private final Button membersButton = new Button(
            byResourceId("buttonMembers"),
            byAccessibilityId("123")
    );
    private final Button plusButton = new Button(
            byResourceId("buttonAdd"),
            byAccessibilityId("ChatList.NavigationBar.workspacePlus")
    );

    private final TextView anyWorkspaceTitle = new TextView(
            byResourceId("textTitle"),
            byAccessibilityId("ChatList.NavigationBar.workspaceTitle")
    );

    private TextView screenTitle(String text) {
        return new TextView(
                byResourceIdAndText("textTitle", text),
                byAccessibilityIdAndText("ChatList.NavigationBar.workspaceTitle", text)
        );
    }

    private TextView screenTitleContains(String text) {
        return new TextView(
                byResourceIdAndContainsText("textTitle", text),
                byAccessibilityIdAndContainsText("ChatList.NavigationBar.workspaceTitle", text)
        );
    }

    private final TextView mySpacesListTitle = new TextView(
            byResourceIdAndText("text_title", "Мои пространства"),
            byEqualsText("Мои пространства")
    );

    private final TextView emptySpacesSubtitle = new TextView(
            byResourceIdAndText("text_subtitle", "У вас пока нет пространств"),
            byEqualsText("У вас пока нет пространств")
    );

    private final Button deletedWorkspaceAckButton = new Button(
            byResourceId("buttonClose"),
            byEqualsText("Понятно")
    );

    private final TextView deletedWorkspaceTitle = new TextView(
            byResourceId("textTitleDeleted"),
            byContainsText("удалено")
    );

    private TextView workspaceInList(String name) {
        String safe = name.replace("\\", "\\\\").replace("'", "\\'");
        return new TextView(
                byEqualsText(name),
                AppiumBy.iOSNsPredicateString(
                        "(name == '%s' OR label == '%s' OR value == '%s'"
                                + " OR label CONTAINS '%s' OR value CONTAINS '%s')"
                                + " AND NOT (label CONTAINS 'удалено' OR value CONTAINS 'удалено')"
                                .formatted(safe, safe, safe, safe, safe))
        );
    }

    public boolean isWorkspaceScreenTitleDisplayed(String text, long seconds) {
        return screenTitle(text).isDisplayed(seconds) || screenTitleContains(text).isDisplayed(Math.min(2L, seconds));
    }

    public boolean isAnyWorkspaceOpen(long seconds) {
        return anyWorkspaceTitle.isDisplayed(seconds);
    }

    public boolean isMySpacesListDisplayed(long seconds) {
        return mySpacesListTitle.isDisplayed(seconds);
    }

    public boolean isWorkspaceInListDisplayed(String name, long seconds) {
        return workspaceInList(name).isDisplayed(seconds);
    }

    public void clickWorkspaceInList(String name) {
        workspaceInList(name).click();
    }

    public void clickWorkspaceTitleToOpenList() {
        if (anyWorkspaceTitle.isDisplayed(2L)) {
            anyWorkspaceTitle.click();
        }
    }

    public boolean isEmptySpacesListDisplayed(long seconds) {
        return emptySpacesSubtitle.isDisplayed(seconds);
    }

    public boolean isDeletedWorkspaceDisplayed(long seconds) {
        return deletedWorkspaceTitle.isDisplayed(seconds);
    }

    public boolean dismissDeletedWorkspaceIfShown() {
        if (!isDeletedWorkspaceDisplayed(1L) && !deletedWorkspaceAckButton.isDisplayed(1L)) {
            return false;
        }
        if (deletedWorkspaceAckButton.isDisplayed(2L)) {
            deletedWorkspaceAckButton.click();
            return true;
        }
        return false;
    }

    public void clickPlusBtn() {
        plusButton.click();
    }
}
