package org.foxtrot.utils;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import org.foxtrot.pages.chat.input_bar.InputBarSteps;
import org.foxtrot.pages.chat.topbar.TopbarSteps;
import org.foxtrot.pages.spaces.chatlist.ChatListSteps;

import java.time.Duration;

/**
 * Бэкенд: сидеть в группе без действий — ключ не сменится даже через TTL.
 * Нужен reconnect group SSE (выйти из чата и зайти снова) после TTL — тогда стартует смена ключа.
 * Saved→группа одного SSE мало: клиент часто не стартует V2 (pending=null) — после reconnect
 * делаем полный refresh SPA.
 */
public final class BrowserSseReconnect {

    private BrowserSseReconnect() {
    }

    @Step("Ждём key TTL, затем Saved → группа → refresh (reconnect SSE + SKR)")
    public static void reconnectAfterKeyTtl(
            ChatListSteps chats, InputBarSteps input, TopbarSteps topbar, String groupName) {
        long ttlMinutes = Long.parseLong(System.getProperty("encryption.key.ttl.minutes", "1"));
        Selenide.sleep(Duration.ofMinutes(ttlMinutes).plusSeconds(5).toMillis());
        reenterGroupViaSaved(chats, input, topbar, groupName);
        refreshAndReopenGroup(chats, input, topbar, groupName);
    }

    @Step("Выйти в [Сохраненные] и снова открыть '{groupName}' — reconnect group SSE")
    public static void reenterGroupViaSaved(
            ChatListSteps chats, InputBarSteps input, TopbarSteps topbar, String groupName) {
        chats.clickSavedChat();
        topbar.verifySavedChatOpened();
        chats.clickChatByName(groupName);
        topbar.verifyChatName(groupName);
        input.verifyInputBarIsVisible();
    }

    @Step("Refresh страницы и снова открыть '{groupName}'")
    public static void refreshAndReopenGroup(
            ChatListSteps chats, InputBarSteps input, TopbarSteps topbar, String groupName) {
        Selenide.refresh();
        try {
            chats.verifyChatWithNameIsDisplayed(groupName, 30);
            chats.clickChatByName(groupName);
            input.verifyInputBarIsVisible();
        } catch (Throwable ignored) {
            reenterGroupViaSaved(chats, input, topbar, groupName);
        }
    }
}
