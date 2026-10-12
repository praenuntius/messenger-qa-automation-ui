package org.foxtrot.mobile.steps.bottomSheet;

import io.qameta.allure.Step;
import org.foxtrot.mobile.screens.bottomSheet.JoinGroupBottomSheet;
import org.junit.jupiter.api.Assertions;

public class JoinGroupBottomSheetSteps {

    private final JoinGroupBottomSheet joinGroupBottomSheet = new JoinGroupBottomSheet();

    @Step("Проверяю, что bottom sheet вступления в группу отображается")
    public JoinGroupBottomSheetSteps checkBottomSheetDisplayed() {
        Assertions.assertTrue(joinGroupBottomSheet.isBottomSheetDisplayed(15L),
                "Bottom sheet вступления в группу не отображается");
        return this;
    }

    @Step("Проверяю, что в bottom sheet отображается название пространства '{0}'")
    public JoinGroupBottomSheetSteps checkWorkspaceName(String workspaceName) {
        Assertions.assertTrue(joinGroupBottomSheet.isWorkspaceNameDisplayed(workspaceName, 15L),
                "Название пространства '" + workspaceName + "' не отображается в bottom sheet");
        return this;
    }

    @Step("Проверяю, что в bottom sheet отображается название группы '{0}'")
    public JoinGroupBottomSheetSteps checkGroupName(String groupName) {
        Assertions.assertTrue(joinGroupBottomSheet.isGroupNameDisplayed(groupName, 15L),
                "Название группы '" + groupName + "' не отображается в bottom sheet");
        return this;
    }

    @Step("Нажимаю 'Вступить в группу' в bottom sheet")
    public JoinGroupBottomSheetSteps clickJoinGroupButton() {
        joinGroupBottomSheet.clickJoinGroupButton();
        return this;
    }
}
