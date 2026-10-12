package org.foxtrot.pages.user_lists;

import com.codeborne.selenide.CollectionCondition;
import io.qameta.allure.Step;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.pages.user_lists.elements.ShareContactList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.openqa.selenium.By.xpath;

public class ShareContactListSteps {

    private final ShareContactList elements = new ShareContactList();

    @Step("Отправить контакт '{name}'")
    public void sendContact(String name) {
        $(xpath(UsersEnum.xpathWithUiNames(elements.getDefinedUserButton(), name)))
                .shouldBe(visible).click();
        clickShareContactButton();
    }

    @Step("Выбрать контакт '{name}'")
    public ShareContactListSteps selectContact(String name) {
        $(xpath(UsersEnum.xpathWithUiNames(elements.getDefinedUserButton(), name)))
                .shouldBe(visible)
                .click();
        return this;
    }

    @Step("Отправить выбранный контакт")
    public void clickShareContactButton() {
        $(elements.getDoneButton()).shouldBe(visible, Duration.ofSeconds(15)).click();
    }

    @Step("Проверить состояние кнопки отправки (активна: {status})")
    public ShareContactListSteps checkShareButtonEnabledStatus(boolean status) {
        if (status) {
            $(elements.getDoneButton()).shouldBe(enabled);
        } else {
            $(elements.getDoneButton()).shouldBe(disabled);
        }
        return this;
    }

    @Step("Проверить отображение сообщения о пустом результате поиска")
    public ShareContactListSteps checkEmptySearchResultMessageVisible() {
            $(elements.getEmptySearchResultMessage()).shouldBe(visible);
        return this;
    }

    @Step("Проверить отображение сообщения о пустом результате поиска")
    public ShareContactListSteps checkEmptyContactsSMessageVisible() {
        $(elements.getEmptyContactResultMessage()).shouldBe(visible);
        return this;
    }

    @Step("Ввести текст '{text}' в поле поиска")
    public ShareContactListSteps enterSearchText(String text) {
        $(elements.getSearchInput()).shouldBe(visible).sendKeys(text);
        return this;
    }

    @Step("Проверить, что контакты отсортированы по алфавиту")
    public ShareContactListSteps checkContactsAreSortedAlphabetically() {
        List<String> actualNames = $$((elements.getAllContactNames()))
                .shouldHave(CollectionCondition.sizeGreaterThan(0))
                .texts();

        List<String> expectedNames = new ArrayList<>(actualNames);
        expectedNames.sort(String.CASE_INSENSITIVE_ORDER);

        if (!actualNames.equals(expectedNames)) {
            throw new AssertionError("Список не отсортирован! \nОжидали: " + expectedNames + "\nПолучили: " + actualNames);
        }

        return this;
    }

    @Step("Проверить наличие контакта {name} в списке")
    public ShareContactListSteps verifyContactExists(String name) {
        $(xpath(UsersEnum.xpathWithUiNames(elements.getDefinedUserButton(), name)))
                .shouldBe(visible);
        return this;
    }

    @Step("Проверить, что у контакта {name} появилась галочка")
    public ShareContactListSteps verifyContactIsSelected(String name) {
        $(xpath(UsersEnum.xpathWithUiNames(elements.getDefinedUserButton(), name)))
                .$(elements.getSelectedCheckbox())
                .shouldBe(visible);

        return this;
    }

    @Step("Проверить наличие чипсы с контактом '{contactName}'")
    public ShareContactListSteps verifyContactChipExists(String contactName) {
        $(elements.getSelectedContactChip())
                .shouldBe(visible)
                .shouldHave(text(contactName));
        return this;
    }
}
