package org.foxtrot.pages.user_lists;

import io.qameta.allure.Step;
import org.foxtrot.pages.user_lists.elements.UsersToMentionList;

import java.time.Duration;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class UsersToMentionListSteps {
    private final UsersToMentionList elements = new UsersToMentionList();

    @Step("Проверить, что список пользователей для mention отображается")
    public UsersToMentionListSteps verifyMentionListVisible() {
        $(elements.getMentionListContainer())
                .shouldBe(visible, Duration.ofSeconds(10));
        $(elements.getMentionList())
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что список пользователей для mention не отображается")
    public UsersToMentionListSteps verifyMentionListNotVisible() {
        $(elements.getMentionListContainer())
                .shouldBe(hidden, Duration.ofSeconds(10));
        return this;
    }

    @Step("Кликнуть по пользователю '{userName}' в списке mention")
    public UsersToMentionListSteps clickMentionListItemByName(String userName) {
        $$(elements.getMentionListItem())
                .findBy(text(userName))
                .shouldBe(visible, Duration.ofSeconds(10))
                .click();
        return this;
    }
}
