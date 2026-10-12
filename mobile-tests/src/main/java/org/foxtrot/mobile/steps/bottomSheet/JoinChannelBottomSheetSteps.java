package org.foxtrot.mobile.steps.bottomSheet;

import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.bottomSheet.JoinChannelBottomSheet;
import org.junit.jupiter.api.Assertions;

public class JoinChannelBottomSheetSteps {

    private final JoinChannelBottomSheet joinChannelBottomSheet = new JoinChannelBottomSheet();

    @Step("Проверяю, что bottom sheet подписки на канал отображается")
    public JoinChannelBottomSheetSteps checkBottomSheetDisplayed() {
        Assertions.assertTrue(joinChannelBottomSheet.isBottomSheetDisplayed(15L),
                "Bottom sheet подписки на канал не отображается");
        return this;
    }

    @Step("Проверяю, что в bottom sheet отображается название пространства '{0}'")
    public JoinChannelBottomSheetSteps checkWorkspaceName(String workspaceName) {
        Assertions.assertTrue(joinChannelBottomSheet.isWorkspaceNameDisplayed(workspaceName, 15L),
                "Название пространства '" + workspaceName + "' не отображается в bottom sheet");
        return this;
    }

    @Step("Проверяю, что в bottom sheet отображается название канала '{0}'")
    public JoinChannelBottomSheetSteps checkChannelName(String channelName) {
        Assertions.assertTrue(joinChannelBottomSheet.isChannelNameDisplayed(channelName, 15L),
                "Название канала '" + channelName + "' не отображается в bottom sheet");
        return this;
    }

    @Step("Нажимаю 'Подписаться на канал' в bottom sheet")
    public JoinChannelBottomSheetSteps clickSubscribeButton() {
        joinChannelBottomSheet.clickSubscribeButton();
        return this;
    }
}
