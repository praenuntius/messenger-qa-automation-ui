package org.foxtrot.pages.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.modals.elements.DiveIntoChatViaUrlLink;
import org.foxtrot.pages.modals.elements.DiveIntoWorkSpaceViaUrlLink;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class DiveIntoWorkSpaceViaUrlLinkSteps {

    DiveIntoWorkSpaceViaUrlLink elements = new DiveIntoWorkSpaceViaUrlLink();

    @Step("Проверить, что отображается сообщение об ограничении доступа")
    public DiveIntoWorkSpaceViaUrlLinkSteps verifyAccessRestrictionMessageVisible() {
        $(elements.getNoAccessMessage()).shouldBe(visible);
        return this;
    }
}
