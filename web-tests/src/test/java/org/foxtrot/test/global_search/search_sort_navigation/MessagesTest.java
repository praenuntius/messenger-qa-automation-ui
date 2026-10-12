package org.foxtrot.test.global_search.search_sort_navigation;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.foxtrot.utils.factories.scenarios.SearchScenarioFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MessagesTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final SearchScenarioFactory scenarioFactory = new SearchScenarioFactory();
    private final GroupFactory groupFactory = new GroupFactory();

    private GroupContext context;

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
        try {
            Selenide.closeWebDriver();} catch (Exception ignored) {
        }
    }

    @Test
    @AllureId("56039")
    @DisplayName("Сортировка поисковых результатов по количеству вхождений слов")
    public void shouldSortSearchResultsByWordOccurrenceCount() {
        String id = String.format("%04d", new Random().nextInt(10000));
        List<String> words = List.of("задача" + id, "срочно" + id, "сделай" + id);

        List<String> expected = IntStream.range(0, 3)
                .mapToObj(i -> "игнор ".repeat(i) + String.join(" ", words.subList(i, 3)))
                .toList();

        context = groupFactory.privateGroup();
        expected.forEach(msg -> context.getOwnerBuilder().sendTextMessage(context.getGroupId(), msg));

        loginHelper.performUILogin(context.getOwner());

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(String.join(" ", words));
        pages.searchUnitSteps.clickMessagesTab();

        List<String> actual = pages.searchResultsListSteps.getMessageTextsFromResults();

        Assertions.assertEquals(expected, actual, "Неверный порядок результатов поиска");
    }

    @Test
    @AllureId("56036")
    @DisplayName("Отсутствие найденных сущностей по сообщениям")
    public void noEntitiesFoundByMessages() {
        String searchRequest = "$#^@*345!@#(^%^&";
        UsersEnum user = scenarioFactory.prepareUserForTest().getUser();
            loginHelper.performUILogin(user);

            pages.groupAndChannelMenuSteps.clickSearchButton();
            pages.searchUnitSteps.enterSearchText(searchRequest);
            pages.searchUnitSteps.waitForSearchCompletion();

            pages.searchResultsListSteps.verifyNoResultsFound();
    }

    @Test
    @AllureId("TODO")
    @DisplayName("Подсветка однокоренных слов без окончаний в результатах поисковой выдачи.")
    public void highlightStemmedWordsInSearchResults() {
        List<String> words = List.of("красивый", "красиво", "красивая");
        String searchQuery = "крас";

        context = groupFactory.privateGroup();
        context.getOwnerBuilder().sendTextMessages(context.getGroupId(), words.toArray(new String[0]));

        loginHelper.performUILogin(context.getOwner());

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(searchQuery);
        pages.searchUnitSteps.waitForSearchCompletion();

        pages.searchUnitSteps.clickMessagesTab();

        for (String word : words) {
            pages.searchResultsListSteps.verifyHighlightInMessage(word, searchQuery);
        }
    }


    @ParameterizedTest()
    @MethodSource("allSearchScenarios")
    @AllureId("56019")
    @DisplayName("Поиск/переход по публикациям из групп и каналов (Workspace и Homespace)")
    public void searchMessagesUniversalTest(String groupType) {
        context = scenarioFactory.prepareByString(groupType);

        loginHelper.performUILogin(context.getParticipant());

        if (context.getWorkspaceName() != null) {
            pages.leftSidebarSteps.clickSpaceByName(context.getWorkspaceName());
            pages.leftSidebarSteps.clickChatsButton();
        }

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(context.getMessageText());
        pages.searchUnitSteps.verifyGlobalSearchElementsVisible();
        pages.searchResultsListSteps.verifyMessageFound(context.getMessageText());
        pages.searchUnitSteps.clickMessagesTab();

        if (context.getWorkspaceName() != null) {
            pages.searchResultsListSteps.verifyGroupInCustomWorkspaceIcon(context.getWorkspaceName(), context.getGroupName());
        } else {
            pages.searchResultsListSteps.verifyGroupHasHomeIcon(context.getGroupName());
        }

        pages.searchResultsListSteps.clickEntityByMessageText(context.getMessageText());

        pages.topbarSteps.verifyChatName(context.getGroupName());
        String expectedSpace = (context.getWorkspaceName() != null) ? context.getWorkspaceName() : "Чаты";
        pages.leftSidebarSteps.verifySpaceIsActive(expectedSpace);
        pages.searchUnitSteps.verifyGlobalSearchElementsVisible();
        pages.chatVerify.verifyMessageText(context.getMessageText());
    }


    static Stream<String> allSearchScenarios() {
        return Stream.of(
                "wsCloseChannel", "wsCloseGroup", "wsOpenChannel", "wsOpenGroup",
                "homeCloseChannel", "homeCloseGroup", "homeOpenChannel"
        );
    }

    @ParameterizedTest()
    @MethodSource("negativeSearchScenarios")
    @AllureId("56034")
    @DisplayName("Поиск публикаций в (Воркспейс/Хомспейс), в котором НЕ состою -> Не найдено")
    public void searchMessagesNegativeTest(String groupType) {
        context = scenarioFactory.prepareByString(groupType);
        UsersEnum user = scenarioFactory.prepareUserForTest().getUser();

        loginHelper.performUILogin(user);

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(context.getMessageText());

        pages.searchResultsListSteps.verifyNoResultsFound();
    }

    static Stream<String> negativeSearchScenarios() {
        return Stream.of(
                "neg_wsCloseChannel", "neg_wsCloseGroup", "neg_wsOpenChannel", "neg_wsOpenGroup",
                "neg_homeCloseChannel", "neg_homeCloseGroup", "neg_homeOpenChannel"
        );
    }

    //BUG
    @Test
    @AllureId("56037")
    @DisplayName("Поиск удаленных сообщений -> Не найдено")
    public void searchDeletedMessagesNotFound() {
        context = scenarioFactory.prepareByString("homeCloseGroup");

        loginHelper.performUILogin(context.getOwner());

        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.topbarSteps.clickGroupHeader();
        pages.groupMenuSteps.clickClearHistoryButton();
        pages.cleanMessageHistoryModalSteps.clickClearForAllButton();

        pages.groupAndChannelMenuSteps.clickSearchButton();
        pages.searchUnitSteps.enterSearchText(context.getMessageText());
        pages.searchUnitSteps.waitForSearchCompletion();

        pages.searchResultsListSteps.verifyNoResultsFound();

        pages.searchUnitSteps.clickMessagesTab();
        pages.searchResultsListSteps.verifyNoResultsFound();
    }
}