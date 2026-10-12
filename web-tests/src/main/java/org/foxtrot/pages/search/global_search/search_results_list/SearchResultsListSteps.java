package org.foxtrot.pages.search.global_search.search_results_list;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.foxtrot.pages.search.global_search.search_results_list.elements.SearchResultsList;
import org.openqa.selenium.JavascriptExecutor;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInRelativeOrder;


public class SearchResultsListSteps {
    private final SearchResultsList elements = new SearchResultsList();

    @Step("Получить список текстов всех найденных сообщений")
    public List<String> getMessageTextsFromResults() {
        ElementsCollection textElements = $$(elements.getMessageText());
        textElements.shouldHave(sizeGreaterThan(0));

        return textElements.stream()
                .map(SelenideElement::getText)
                .map(text -> text.replaceFirst("^Вы:\\s*", ""))
                .toList();
    }

    @Step("Проверить подсветку '{highlightPart}' в сообщении '{fullWord}'")
    public SearchResultsListSteps verifyHighlightInMessage(String fullWord, String highlightPart) {
        $$(elements.getMessageItems()).shouldHave(sizeGreaterThan(0));

        ElementsCollection searchResults = $$(elements.getMessageItems());

        SelenideElement messageItem = searchResults.stream()
                .filter(item -> item.getText().replaceAll("\\s+", "").contains(fullWord))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Сообщение с текстом '" + fullWord + "' не найдено."
                ));

        messageItem.find("span[class*='HighlightText']").shouldHave(text(highlightPart));

        return this;
    }

    @Step("Клик по сущности {name} в контейнере результатов")
    public void clickEntityByGroupName(String name) {
        $(elements.getEntityByGroupName(name)).shouldBe(visible).click();
    }

    @Step("Открыть контекстное меню у ссылку в контейнере результатов")
    public void clickLinkContextMenuButton() {
        $(elements.getLinkContextMenuButton()).shouldBe(visible).click();
    }

    @Step("Клик по сущности {message} в контейнере результатов")
    public SearchResultsListSteps clickEntityByMessageText(String message) {
        $(elements.getEntityByMessageText(message)).shouldBe(visible).click();
        return this;
    }

    @Step("Проверить, что в результатах глобального поиска найдено сообщение: {text}")
    public SearchResultsListSteps verifyMessageFound(String text) {
        String selector = elements.getDefinedMessageItemByText().replace("{text}", text);
        $x(selector)
                .shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить, что в результатах глобального поиска НЕ найдено сообщение: {text}")
    public SearchResultsListSteps verifyMessageNotFound(String text) {
        $$(elements.getMessageItems())
                .filterBy(com.codeborne.selenide.Condition.text(text))
                .shouldBe(empty, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить наличие сущности '{resultText}' в секции '{sectionTitle}'")
    public void verifyResultInSection(String sectionTitle, String resultText) {
        String containerLocator = elements.getSectionContainerLocator(sectionTitle);
        String fullXPath = String.format("(%s)/following-sibling::div//div[text()='%s']", containerLocator, resultText);

        $x(fullXPath)
                .scrollTo()
                .shouldBe(visible, Duration.ofSeconds(10));
    }


    @Step("Проверить отсутствие сущности '{resultText}' в секции '{sectionTitle}'")
    public void verifyResultNotInSection(String sectionTitle, String resultText) {
        String containerLocator = elements.getSectionContainerLocator(sectionTitle);
        String fullXPath = String.format("(%s)/following-sibling::div//div[text()='%s']",
                containerLocator, resultText);

        $x(fullXPath).shouldNotBe(exist);
    }

    @Step("Кликнуть по сущности '{resultText}' в секции '{sectionTitle}'")
    public void clickResultInSection(String sectionTitle, String resultText) {
        String containerLocator = elements.getSectionContainerLocator(sectionTitle);
        String fullXPath = String.format("(%s)/following-sibling::div//div[text()='%s']",
                containerLocator, resultText);

        $x(fullXPath).shouldBe(visible).click();
    }

    @Step("Проверить что у группы \"{groupName}\" отображается бейдж HomeSpace")
    public SearchResultsListSteps verifyGroupHasHomeIcon(String groupName) {
        $(elements.getGroupInHomeSpace(groupName)).shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить что у  группы \"{groupName}\" отображается бейдж спейса \"{workspaceName}\"")
    public SearchResultsListSteps verifyGroupInCustomWorkspaceIcon(String workspaceName, String groupName) {
        $(elements.getGroupInWorkspace(workspaceName, groupName)).shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Проверить наличие системного сообщения 'Ничего не найдено'")
    public SearchResultsListSteps verifyNoResultsFound() {
        $(elements.getNoResultsFoundMessage()).shouldBe(visible);
        return this;
    }

    @Step("Проверить наличие аватарки у группы '{groupName}'")
    public void verifyGroupHasAvatar(String groupName) {
        $x(elements.getGroupItemByName(groupName))
                .$(elements.getGroupAvatarWrapper())
                .shouldBe(visible);
    }

    @Step("Получить список названий найденных сущностей")
    public List<String> getSearchResultNames() {
        return collectSearchResultNames();
    }

    @Step("Проверить относительный порядок результатов поиска: {expectedNames}")
    public SearchResultsListSteps verifySearchResultsRelativeOrder(String... expectedNames) {
        List<String> allResults = collectSearchResultNames();
        assertThat(allResults, containsInRelativeOrder(expectedNames));
        return this;
    }

    private List<String> collectSearchResultNames() {
        $(elements.getSearchResultNameText()).shouldBe(visible);
        return $$(elements.getSearchResultNameText())
                .stream()
                .map(SelenideElement::getText)
                .collect(Collectors.toList());
    }

    @Step("Наведение курсора на сущность {name}")
    public SearchResultsListSteps hoverOverEntity(String name) {
        SelenideElement textElement = $(elements.getEntityByGroupName(name));
        SelenideElement cardElement = textElement.parent();

        cardElement.hover();
        sleep(300);

        return this;
    }

    @Step("Проверка наличия подсветки у сущности {name}")
    public SearchResultsListSteps verifyEntityHighlight(String name) {
        SelenideElement textElement = $(elements.getEntityByGroupName(name));
        SelenideElement cardElement = textElement.$(elements.getGroupItemWrapperAncestor());

        String expectedHighlightColor = "rgba(255, 255, 255, 0.04)";
        actions().moveToElement(cardElement).perform();

        String currentColor = getBackgroundColor(cardElement);

        if (!currentColor.equals(expectedHighlightColor)) {
            throw new AssertionError("Подсветка не найдена! Ожидался цвет '" + expectedHighlightColor +
                    "', но получен: '" + currentColor + "'");
        }
        return this;
    }

    private String getBackgroundColor(SelenideElement element) {
        return (String) ((JavascriptExecutor) WebDriverRunner.getWebDriver())
                .executeScript("return window.getComputedStyle(arguments[0]).backgroundColor;", element.getWrappedElement());
    }
}