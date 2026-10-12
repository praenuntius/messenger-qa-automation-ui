package org.foxtrot.mobile.screens.bottomSheet;

import org.foxtrot.mobile.elements.Block;
import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.TextView;
import org.foxtrot.mobile.testdata.dictionary.ChatDictionary;

import static org.foxtrot.mobile.elements.base.BaseElement.byEqualsText;
import static org.foxtrot.mobile.elements.base.BaseElement.byResourceId;

public class JoinGroupBottomSheet {

    private final Block bottomSheet = new Block(
            byResourceId("design_bottom_sheet"),
            byEqualsText(ChatDictionary.JOIN_GROUP)
    );

    private TextView workspaceName(String text) {
        return new TextView(
                byResourceId("workspaceName"),
                byEqualsText(text)
        );
    }

    private TextView groupName(String text) {
        return new TextView(
                byResourceId("name"),
                byEqualsText(text)
        );
    }

    private final Button joinGroupButton = new Button(
            byResourceId("subscribe"),
            byEqualsText(ChatDictionary.JOIN_GROUP)
    );

    public boolean isBottomSheetDisplayed(long seconds) {
        return bottomSheet.isDisplayed(seconds);
    }

    public boolean isWorkspaceNameDisplayed(String workspaceName, long seconds) {
        return workspaceName(workspaceName).isDisplayed(seconds);
    }

    public boolean isGroupNameDisplayed(String groupName, long seconds) {
        return groupName(groupName).isDisplayed(seconds);
    }

    public void clickJoinGroupButton() {
        joinGroupButton.click();
    }
}
