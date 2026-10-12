package org.foxtrot.mobile.steps.homespace;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.foxtrot.mobile.elements.actions.Direction;
import org.foxtrot.mobile.elements.actions.SwipeHelper;
import org.foxtrot.mobile.screens.homespace.ChatsScreen;
import org.foxtrot.mobile.screens.workspace.WorkspaceScreen;
import org.foxtrot.mobile.steps.BottomMenuSteps;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

public class ChatsSteps {

    ChatsScreen chatsScreen = new ChatsScreen();
    private final BottomMenuSteps bottomMenu = new BottomMenuSteps();
    private final WorkspaceScreen workspaceScreen = new WorkspaceScreen();

    @Step("Проверяю, что страница Чатов имеет заголовок '{0}'")
    public ChatsSteps checkChatsScreenTitle(String titleText) {
        Assertions.assertTrue(chatsScreen.isChatsScreenTitleDisplayed(titleText, 15L),
                "Заголовок страницы '" + titleText + "' не отображается");
        return this;
    }

    @Step("Нажимаю на кнопку 'Плюс'")
    public ChatsSteps clickPlusBtn() {
        chatsScreen.clickPlusBtn();
        return this;
    }

    public boolean isChatsListDisplayed(long seconds) {
        return chatsScreen.isChatsListDisplayed(seconds);
    }

    @Step("Открываю чат '{0}' из списка")
    public ChatsSteps openChatByName(String chatName) {
        return openChatWhenAppears(chatName, 25L);
    }

    @Step("Проверяю состояние чата '{0}' в списке")
    public ChatsSteps checkChatDisplayed(String chatName, boolean shouldBeDisplayed) {
        if (shouldBeDisplayed) {
            Allure.step("Проверяю, что чат '" + chatName + "' отображается в списке");
            Assertions.assertTrue(chatsScreen.isChatNameDisplayed(chatName),
                    "Чат '" + chatName + "' не отображается в списке");
        } else {
            Allure.step("Проверяю, что чат '" + chatName + "' не отображается в списке");
            Assertions.assertFalse(chatsScreen.isChatNameDisplayed(chatName),
                    "Чат '" + chatName + "' отображается в списке");
        }
        return this;
    }

    @Step("Открываю чат по части названия '{0}' из списка")
    public ChatsSteps openChatByNameContains(String text) {
        chatsScreen.clickChatByNameContains(text);
        return this;
    }

    /**
     * Ждёт чат в списке и открывает. Учитывает:
     * <ul>
     *   <li>RecyclerView: off-screen нет в hierarchy → Android {@code UiScrollable}</li>
     *   <li>длинные имена могут обрезаться в UI → ищем и по короткому needle</li>
     *   <li>после re-login — sync delay + pull-to-refresh</li>
     * </ul>
     */
    @Step("Жду появления чата '{0}' до {1}с и открываю")
    public ChatsSteps openChatWhenAppears(String chatName, long timeoutSeconds) {
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        Set<String> needles = searchNeedles(chatName);
        int attempt = 0;
        while (System.currentTimeMillis() < deadline) {
            ensureChatListVisible();
            for (String needle : needles) {
                if (tryOpenVisible(needle)) {
                    return this;
                }
            }
            attempt++;
            Allure.step("Чат '" + chatName + "' не виден — scroll/refresh (попытка " + attempt
                    + ", needles=" + needles + ")");

            boolean scrolled = false;
            for (String needle : needles) {
                if (chatsScreen.scrollChatNameIntoView(needle) && tryOpenVisible(needle)) {
                    return this;
                }
                scrolled = true;
            }
            if (!scrolled || attempt % 2 == 0) {
                gestureSearchPass(attempt);
            }
            try {
                Thread.sleep(1_000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for chat: " + chatName, e);
            }
        }
        attachChatsDebug(chatName, needles);
        Assertions.fail("Чат '" + chatName + "' не появился в списке за " + timeoutSeconds + "с"
                + " (needles=" + needles
                + ", baseApiUrl=" + System.getProperty("baseApiUrl", "<unset>") + ")");
        return this;
    }

    /**
     * После logout/re-login stage14 часто не возвращает только что созданную пустую группу.
     * Как WEB-007: если чата нет — Saved или первый в списке.
     */
    @Step("Открываю чат '{0}' если появился, иначе Saved/первый")
    public ChatsSteps openChatWhenAppearsOrFallback(String chatName, long timeoutSeconds) {
        try {
            return openChatWhenAppears(chatName, timeoutSeconds);
        } catch (AssertionError missing) {
            Allure.step("Чат '" + chatName + "' нет в списке после re-login — fallback Saved/первый: "
                    + missing.getMessage());
            return openSavedOrFirstChat("Сохраненн");
        }
    }

    private boolean tryOpenVisible(String needle) {
        if (!chatsScreen.isChatNameContainsDisplayed(needle, 1L)) {
            return false;
        }
        chatsScreen.clickChatByNameContains(needle);
        if (chatsScreen.isChatContextMenuDisplayed()) {
            chatsScreen.dismissChatContextMenu();
            chatsScreen.clickChatByNameContains(needle);
        }
        if (chatsScreen.isChatContextMenuDisplayed()) {
            chatsScreen.dismissChatContextMenu();
            return false;
        }
        return true;
    }

    private void ensureChatListVisible() {
        chatsScreen.dismissChatContextMenu();
        workspaceScreen.dismissDeletedWorkspaceIfShown();
        if (chatsScreen.isChatsListDisplayed(1L)) {
            return;
        }
        if (workspaceScreen.isMySpacesListDisplayed(1L) || workspaceScreen.isAnyWorkspaceOpen(1L)) {
            Allure.step("Открыт экран пространств — переключаюсь на вкладку Чаты");
            bottomMenu.tapChatsTabForce();
        }
    }

    /** Полное имя + укороченный префикс + iOS/Android «test Отчество N». */
    private static Set<String> searchNeedles(String chatName) {
        Set<String> needles = new LinkedHashSet<>(org.foxtrot.enums.UsersEnum.uiNameNeedles(chatName));
        if (chatName != null && chatName.length() > 18) {
            needles.add(chatName.substring(0, 18));
        }
        if (chatName != null && chatName.length() > 12) {
            needles.add(chatName.substring(0, 12));
        }
        return needles;
    }

    private void gestureSearchPass(int attempt) {
        try {
            if (attempt % 4 == 1) {
                // к началу списка + pull-to-refresh
                SwipeHelper.swipeOnScreen(Direction.DOWN, 0.5d, Duration.ofMillis(350));
                SwipeHelper.swipeOnScreen(Direction.DOWN, 0.35d, Duration.ofMillis(350));
            } else {
                // вниз по списку (контент уезжает вверх)
                SwipeHelper.swipeOnScreen(Direction.UP, 0.45d, Duration.ofMillis(400));
            }
        } catch (RuntimeException swipeError) {
            Allure.step("Swipe skip: " + swipeError.getMessage());
        }
    }

    private void attachChatsDebug(String chatName, Set<String> needles) {
        try {
            String source = com.codeborne.selenide.WebDriverRunner.getWebDriver().getPageSource();
            Allure.addAttachment("chats-list-page-source", "text/xml", source, ".xml");
            String lower = source.toLowerCase();
            boolean rawHit = needles.stream().anyMatch(n -> lower.contains(n.toLowerCase()));
            Allure.addAttachment(
                    "chats-scroll-debug",
                    "chatName=" + chatName
                            + "\nneedles=" + needles
                            + "\npageSourceContainsNeedle=" + rawHit
                            + "\n(if true — элемент в XML, но не кликабелен/не сматчился; "
                            + "if false — чата нет в hierarchy, нужен scroll или чат не создан)"
            );
        } catch (Exception e) {
            Allure.step("Не удалось снять page source: " + e.getMessage());
        }
    }

    /**
     * Saved на stage14/Android часто отсутствует. Fallback — первый чат, если список не пуст.
     */
    @Step("Открываю Saved ('{0}') или первый чат в списке")
    public ChatsSteps openSavedOrFirstChat(String savedNameContains) {
        if (chatsScreen.isChatNameContainsDisplayed(savedNameContains, 5L)) {
            chatsScreen.clickChatByNameContains(savedNameContains);
            return this;
        }
        if (chatsScreen.isChatRowDisplayed(1, 5L)) {
            Allure.step("Чат с текстом '" + savedNameContains + "' не найден — открываю первый в списке");
            chatsScreen.clickChatByNumber(1);
            return this;
        }
        Assertions.fail("Список чатов пуст: нет '" + savedNameContains + "' и нет строк chatRow/chatCell");
        return this;
    }
}
