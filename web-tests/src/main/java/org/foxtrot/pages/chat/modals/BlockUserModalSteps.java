package org.foxtrot.pages.chat.modals;

import io.qameta.allure.Step;
import org.foxtrot.pages.chat.modals.elements.BlockUserModal;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class BlockUserModalSteps {

    BlockUserModal elements = new BlockUserModal();

    @Step("Заблокировать пользователя")
    public BlockUserModalSteps clickBlockUserButton() {
        $(elements.getBlockButton()).shouldBe(visible, Duration.ofSeconds(5)).click();
        return this;
    }

    @Step("Нажать кнопку 'Отмена'")
    public BlockUserModalSteps clickCancelButton() {
        $(elements.getCancelButton()).shouldBe(visible, Duration.ofSeconds(5)).click();
        return this;
    }

    @Step("Проверить отображение имени пользователя {name} в диалоговом окне блокировки")
    public BlockUserModalSteps verifyUserAppearanceInUserBlockingDialog(String name) {
            $(By.xpath(elements.getUserName().replace("{username}", name)))
                    .shouldBe(visible);
            return this;
    }
}
