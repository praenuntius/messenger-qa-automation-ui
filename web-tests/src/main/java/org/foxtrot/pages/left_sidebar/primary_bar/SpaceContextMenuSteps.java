package org.foxtrot.pages.left_sidebar.primary_bar;

import io.qameta.allure.Step;
import lombok.Getter;
import org.foxtrot.pages.left_sidebar.primary_bar.elements.SpaceContextMenu;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class SpaceContextMenuSteps {
   SpaceContextMenu elements = new SpaceContextMenu();

    @Step("Пометить прочитанным")
    public void clickMarkAsReadButton () {
        $(elements.getMarkAsReadButton()).shouldBe(visible, java.time.Duration.ofSeconds(10)).click();
    }

    @Step("Проверить, что кнопка 'Пометить прочитанным' заблокирована")
    public void checkMarkAsReadButtonIsDisabled() {
        $(elements.getMarkAsReadButtonDisabled()).shouldBe(visible);
    }
}