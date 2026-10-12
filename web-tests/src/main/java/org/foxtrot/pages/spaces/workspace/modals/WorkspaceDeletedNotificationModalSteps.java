package org.foxtrot.pages.spaces.workspace.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.WebOnboardingOverlay;
import org.foxtrot.pages.spaces.workspace.modals.elements.WorkspaceDeletedNotificationModal;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class WorkspaceDeletedNotificationModalSteps {

    WorkspaceDeletedNotificationModal elements = new WorkspaceDeletedNotificationModal();

    @Step("Проверить, что модалка об удалении пространства отображается")
    public WorkspaceDeletedNotificationModalSteps verifyWorkspaceDeletedModalIsVisible() {
        WebOnboardingOverlay.dismissIfPresent();
        $(elements.getWorkspaceDeletedTitle()).shouldBe(visible, Duration.ofSeconds(30));
        $(elements.getWorkspaceDeletedConfirmButton()).shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Нажать кнопку 'Понятно'")
    public WorkspaceDeletedNotificationModalSteps clickConfirmButton() {
        $(elements.getWorkspaceDeletedConfirmButton()).shouldBe(visible).click();
        return this;
    }
}
