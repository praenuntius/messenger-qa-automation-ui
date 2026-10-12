package org.foxtrot.pages.search.global_search.filter_unit;

import io.qameta.allure.Step;
import org.foxtrot.pages.search.global_search.filter_unit.elements.SpaceFilterModal;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;


public class SpaceFilterModalSteps {
    private final SpaceFilterModal elements = new SpaceFilterModal();

    @Step("Кликнуть по кнопке'Применить'")
    public void clickApplyButton() {
        $(elements.getApplyButton()).shouldBe(visible).click();
    }

    @Step("Проверить активность кнопки'Применить'")
    public void verifyApplyButtonActivity(boolean shouldBeActive) {
        if (shouldBeActive) {
            $(elements.getApplyButton()).shouldBe(enabled);
        } else {
            $(elements.getApplyButton()).shouldBe(disabled);
        }
    }

    @Step("Проверить галочки у пространств {workspaceNames}: наличие={shouldBePresent}")
    public void verifySpacesCheckmarkState(boolean shouldBePresent, String... workspaceNames) {
        for (String name : workspaceNames) {
            var row = $(elements.getWorkspaceRowByName(name));
            var checkmark = row.$(elements.getWorkspaceCheckmark());

            if (shouldBePresent) {
                checkmark.should(exist);
                checkmark.shouldHave(attribute("fill"));
            } else {
                checkmark.shouldNot(exist);
            }
        }
    }

    @Step("Кликнуть по галочке пространства '{workspaceName}'")
    public void clickSpaceCheckmark(String workspaceName) {
        $(elements.getWorkspaceRowByName(workspaceName))
                .$(elements.getWorkspaceCheckmark())
                .click();
    }

    @Step("Кликнуть по чекбоксу 'Выбрать все'")
    public void clickSelectAllCheckbox() {
        $(elements.getSelectAllWrapper()).click();
    }

    @Step("Проверить состояние чекбокса 'Выбрать все': {shouldBeChecked}")
    public void verifySelectAllCheckboxState(boolean shouldBeChecked) {
        var iconUse = $(elements.getSelectAllCheckboxIconUse());

        if (shouldBeChecked) {
            iconUse.shouldHave(attribute("href", "#icCheckboxActive"));
        } else {
            iconUse.shouldHave(attribute("href", "#icCheckboxDef"));
        }
    }


    @Step("Проверить, что пространства в фильтре отсортированы по алфавиту")
    public void verifySpacesAreSortedAlphabetically() {
        List<String> allNames = $$(elements.getSpaceNameItemLocator()).texts();
        List<String> skipHeaders = List.of("Чаты", "Пространства для поиска");

        List<String> cleanNames = allNames.stream()
                .map(String::trim)
                .filter(name -> !skipHeaders.contains(name))
                .collect(Collectors.toList());

        String first = cleanNames.get(0);
        String second = cleanNames.get(1);

        if (first.compareToIgnoreCase(second) > 0) {
            throw new RuntimeException("Порядок нарушен: '" + first + "' идет перед '" + second + "'");
        }
    }
}
