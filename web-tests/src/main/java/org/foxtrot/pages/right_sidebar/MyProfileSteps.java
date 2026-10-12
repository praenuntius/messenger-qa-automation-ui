package org.foxtrot.pages.right_sidebar;

import io.qameta.allure.Step;
import org.foxtrot.pages.right_sidebar.elements.MyProfile;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MyProfileSteps {

    MyProfile elements = new MyProfile();

    @Step("Открыть редактирование профиля")
    public MyProfileSteps openProfileMenu() {
        $(elements.getEditProfile()).shouldBe(visible).click();
        return this;
    }

    @Step("Копировать ссылку на профиль")
    public MyProfileSteps copyProfileLink() {
        $(elements.getCopyProfileLinkButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Открыть чат 'Сохраненные'")
    public MyProfileSteps openSavedChat() {
        $(elements.getSavedChat()).shouldBe(visible).click();
        return this;
    }

    @Step("Нажать кнопку 'Показать еще'")
    public MyProfileSteps clickShowMoreButton() {
        $(elements.getShowMoreButton()).shouldBe(visible).click();
        return this;
    }

    @Step("Проверить, что данные профиля отредактированы")
    public void verifyEditProfile(String name, String middleName, String lastName, String jobPost, String number, String email) {
        verifyChatName(name, middleName, lastName);
        verifyJobPost(jobPost);
        verifyNumber(number);
        verifyEmail(email);
    }

    @Step("Проверить, что имя  равно {name} {middleName} {lastName}")
    public void verifyChatName(String name, String middleName, String lastName) {
        $(elements.getMyName()).shouldHave(text(name));
        $(elements.getMyName()).shouldHave(text(middleName));
        $(elements.getMyName()).shouldHave(text(lastName));
    }

    @Step("Проверить, что поле должность равно {jobPost}")
    public void verifyJobPost(String jobPost) {
        $(elements.getJobTitleText()).shouldHave(text(jobPost));
    }

    @Step("Проверить, что поле номер равно {number}")
    public void verifyNumber(String number) {
        $(elements.getContactInfoBlock()).shouldHave(text(number));
    }

    @Step("Проверить, что поле почта равно {email}")
    public void verifyEmail(String email) {
        $(elements.getContactInfoBlock()).shouldHave(text(email));
    }
}
