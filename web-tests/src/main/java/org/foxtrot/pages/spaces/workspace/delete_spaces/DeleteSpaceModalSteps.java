package org.foxtrot.pages.spaces.workspace.delete_spaces;

import com.codeborne.selenide.CollectionCondition;
import io.qameta.allure.Step;
import org.foxtrot.pages.spaces.workspace.delete_spaces.elements.DeleteSpaceModal;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DeleteSpaceModalSteps {

    private final DeleteSpaceModal elements = new DeleteSpaceModal();

    @Step("Кликнуть по воркспейсам: {names}")
    public DeleteSpaceModalSteps clickWorkspacesByNames(String... names) {
        for (String name : names) {
            $$(elements.getWorkspaceItem())
                    .findBy(text(name))
                    .shouldBe(visible)
                    .click();
        }
        return this;
    }

    @Step("Проверить наличие воркспейсов в списке: {names}")
    public DeleteSpaceModalSteps verifyWorkspacesAreDisplayed(String... names) {
        for (String name : names) {
            $$(elements.getWorkspaceItem())
                    .findBy(text(name))
                    .shouldBe(visible);
        }
        return this;
    }

    @Step("Проверить, что воркспейсы отсортированы по алфавиту")
    public DeleteSpaceModalSteps verifyWorkspacesAreSortedAlphabetically() {
        List<String> actualNames = $$(elements.getWorkspaceItem()).texts();

        List<String> expectedNames = new ArrayList<>(actualNames);
        expectedNames.sort(String.CASE_INSENSITIVE_ORDER);

        $$(elements.getWorkspaceItem()).shouldHave(CollectionCondition.texts(expectedNames));

        return this;
    }



    @Step("Проверить, что у воркспейсов {names} отображается галочка")
    public DeleteSpaceModalSteps verifyWorkspacesAreSelected(String... names) {
        for (String name : names) {
            $$(elements.getWorkspaceItem())
                    .findBy(text(name))
                    .$(elements.getSelectedCheckIcon())
                    .shouldBe(visible);
        }
        return this;
    }

    @Step("Проверить, что кнопка удаления активна")
    public DeleteSpaceModalSteps verifyDeleteButtonIsEnabled() {
        $(elements.getDeleteButtonSelector())
                .shouldBe(enabled);
        return this;
    }

    @Step("Проверить, что кнопка удаления заблокирована")
    public DeleteSpaceModalSteps verifyDeleteButtonIsDisabled() {
        $(elements.getDeleteButtonSelector())
                .shouldBe(disabled);
        return this;
    }

    @Step("Нажать на кнопку 'Удалить'")
    public DeleteSpaceModalSteps clickDeleteButton() {
        $(elements.getDeleteButtonSelector())
                .shouldBe(enabled)
                .click();
        return this;
    }

    @Step("Проверить, что в модалке отображается счетчик: 'Выбрано {count}'")
    public DeleteSpaceModalSteps verifySelectedWorkspacesCount(int count) {
        $(elements.getSelectedWorkspacesCounter())
                .shouldBe(visible)
                .shouldHave(text("Выбрано " + count));
        return this;
    }
}