package org.foxtrot.pages.spaces.workspace.left_sidebar;

import io.qameta.allure.Step;
import org.foxtrot.pages.spaces.workspace.left_sidebar.elements.GroupAndChannelMenu;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class GroupAndChannelMenuSteps {

    GroupAndChannelMenu elements = new GroupAndChannelMenu();

    @Step("Нажать кнопку глобального поиска в меню групп и каналов")
    public GroupAndChannelMenuSteps clickSearchButton() {
        $(elements.getSearchButton())
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Проверить наличие группы '{GroupName}' в списке")
    public void verifyGroupPresentInList(String GroupName) {
        String selector = elements.getDefinedGroupName().replace("{GroupName}", GroupName);
        $x(selector).shouldBe(visible);
    }

    @Step("Проверить отсутствие группы '{GroupName}' в списке")
    public void verifyGroupNotPresentInList(String GroupName) {
        String selector = elements.getDefinedGroupName().replace("{GroupName}", GroupName);
        $x(selector).shouldNotBe(visible);
    }
}
