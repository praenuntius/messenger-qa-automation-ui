package org.foxtrot.mobile.screens.bottomSheet;

import org.foxtrot.mobile.elements.Block;
import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.TextView;
import org.foxtrot.mobile.testdata.dictionary.ChatDictionary;

import static org.foxtrot.mobile.elements.base.BaseElement.byEqualsText;
import static org.foxtrot.mobile.elements.base.BaseElement.byResourceId;

public class JoinChannelBottomSheet {

    private final Block bottomSheet = new Block(
            byResourceId("design_bottom_sheet"),
            byEqualsText(ChatDictionary.SUBSCRIBE_TO_CHANNEL)
    );

    private TextView workspaceName(String text) {
        return new TextView(
                byResourceId("workspaceName"),
                byEqualsText(text)
        );
    }

    private TextView channelName(String text) {
        return new TextView(
                byResourceId("name"),
                byEqualsText(text)
        );
    }

    private final Button subscribeButton = new Button(
            byResourceId("subscribe"),
            byEqualsText(ChatDictionary.SUBSCRIBE_TO_CHANNEL)
    );

    public boolean isBottomSheetDisplayed(long seconds) {
        return bottomSheet.isDisplayed(seconds);
    }

    public boolean isWorkspaceNameDisplayed(String workspaceName, long seconds) {
        return workspaceName(workspaceName).isDisplayed(seconds);
    }

    public boolean isChannelNameDisplayed(String channelName, long seconds) {
        return channelName(channelName).isDisplayed(seconds);
    }

    public void clickSubscribeButton() {
        subscribeButton.click();
    }
}
