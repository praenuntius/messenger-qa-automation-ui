package org.foxtrot.test.contact_sharing;

import io.qameta.allure.Allure;
import org.foxtrot.allure.ChannelAllureIds;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.factories.contexts.GroupContext;
import org.foxtrot.utils.factories.core.GroupFactory;
import org.foxtrot.utils.factories.core.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShareContactTest extends BaseTest {

    private final LoginHelper loginHelper = new LoginHelper();
    private final GroupFactory groupFactory = new GroupFactory();
    private final UserFactory userFactory = new UserFactory();

    private GroupContext context;

    @AfterEach
    public void tearDown() {
        groupFactory.cleanUp();
    }

    @Test
    @ChannelAllureIds(web = "57842", desktop = "61847")
    @DisplayName("Отменить выбор контакта")
    public void deselectContact() {
        context = groupFactory.privateGroupWithParticipant();
        context.getOwnerBuilder().addContacts(context.getParticipant());

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickOpenAttachMenuButton();
        pages.attachContextMenuSteps.clickShareContactsButton();

        pages.shareContactListSteps
                .checkShareButtonEnabledStatus(false)
                .selectContact(context.getParticipant().getName())
                .checkShareButtonEnabledStatus(true)
                .selectContact(context.getParticipant().getName())
                .checkShareButtonEnabledStatus(false);
    }

    @Test
    @ChannelAllureIds(web = "57853", desktop = "61856")
    @DisplayName("Отображение экрана, когда в результатах поиска ничего не найдено")
    public void emptySearchResultsScreenIsDisplayed() {
        context = groupFactory.privateGroupWithParticipant();
        context.getOwnerBuilder().addContacts(context.getParticipant());

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickOpenAttachMenuButton();
        pages.attachContextMenuSteps.clickShareContactsButton();
        pages.shareContactListSteps.enterSearchText("!@#$%");
        pages.shareContactListSteps.checkEmptySearchResultMessageVisible();
    }

    @Test
    @ChannelAllureIds(web = "57854", desktop = "61858")
    @DisplayName("Отображение экрана, когда нет контактов в контактной книге")
    public void emptyContactsScreenIsDisplayed() {
        context = groupFactory.privateGroupWithParticipant();

        loginHelper.performUILogin(context.getOwner());
        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickOpenAttachMenuButton();
        pages.attachContextMenuSteps.clickShareContactsButton();
        pages.shareContactListSteps.checkEmptyContactsSMessageVisible();
    }

    @Test
    @ChannelAllureIds(web = "57840", desktop = "61846")
    @DisplayName("Отправить карточку контакта при набранном тексте в поле ввода")
    public void contactCardSentPreservesInputText() {
        context = groupFactory.privateGroupWithParticipant();
        context.getOwnerBuilder().addContacts(context.getParticipant());

        loginHelper.performUILogin(context.getOwner());

        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.enterMessage("test");
        pages.inputBarSteps.clickOpenAttachMenuButton();
        pages.attachContextMenuSteps.clickShareContactsButton();
        pages.shareContactListSteps.sendContact(context.getParticipant().getName());

        pages.chatVerify.verifyContactSent(context.getParticipant().getName());
        pages.inputBarSteps.checkMessageFieldContainsText("test");
    }


    @Test
    @ChannelAllureIds(web = "57839", desktop = "61859")
    @DisplayName("Проверить вывод в алфавитном порядке контактов в окне 'Поделиться контактом Tango'")
    public void sharedContactsAreSortedAlphabetically() {
        context = groupFactory.p2pChat();
        var user = userFactory.takeUser();
        context.getOwnerBuilder().addContacts(context.getParticipant(), user);

        openShareContactMenu(context, true);

        pages.shareContactListSteps.checkContactsAreSortedAlphabetically();
    }


    @Test
    @ChannelAllureIds(web = "57847", desktop = "61851")
    @DisplayName("Проверить последовательность отправки выбранных карточек контактов")
    public void contactCardsSentInSelectionOrder() {
        context = groupFactory.p2pChat();
        var user = userFactory.takeUser();
        context.getOwnerBuilder().addContacts(context.getParticipant(), user);

        openShareContactMenu(context, true);

        pages.shareContactListSteps
                .selectContact(context.getParticipant().getName())
                .selectContact(user.getName())
                .clickShareContactButton();

        pages.chatVerify.verifyContactCardsOrder(context.getParticipant().getName(), user.getName());
    }


    @Test
    @ChannelAllureIds(web = "57841", desktop = "61850")
    @DisplayName("Отправить в чат 15 выбранных контактов")
    public void sendFifteenContactsInOneMessage() {
        context = groupFactory.privateGroup();
        var users = new HashMap<String, UsersEnum>();

        IntStream.rangeClosed(1, 15).forEach(i -> {
            var user = userFactory.takeUser();
            users.put("User" + i, user);
            context.getOwnerBuilder().addContacts(user);
        });

        loginHelper.performUILogin(context.getOwner());

        pages.chatListSteps.clickChatByName(context.getGroupName());
        pages.inputBarSteps.clickOpenAttachMenuButton();
        pages.attachContextMenuSteps.clickShareContactsButton();

        users.values().forEach(user -> pages.shareContactListSteps.selectContact(user.getName()));
        pages.shareContactListSteps.clickShareContactButton();

        users.values().forEach(user -> pages.chatVerify.verifyContactCardVisible(user.getName()));
    }

    @Test
    @ChannelAllureIds(web = "57848", desktop = "61854")
    @DisplayName("Совершить поиск контакта по имени")
    public void searchContactByName() {
        context = groupFactory.p2pChat();
        String name = context.getParticipant().getName();
        context.getOwnerBuilder().addContacts(context.getParticipant());

        openShareContactMenu(context, true);

        pages.shareContactListSteps.enterSearchText(name)
                .verifyContactExists(name)
                .selectContact(name)
                .verifyContactChipExists(name)
                .verifyContactIsSelected(name);
    }


    @Test
    @ChannelAllureIds(web = "57852", desktop = "61852")
    @DisplayName(" Совершить поиск контакта по номеру телефона")
    public void searchContactByPhoneNumberFormats() {
        context = groupFactory.p2pChat();
        String baseNumber = context.getParticipant().getFormattedPhone();
        String name = context.getParticipant().getName();
        context.getOwnerBuilder().addContacts(context.getParticipant());

        openShareContactMenu(context, true);

        phoneNumberFormats(baseNumber).forEach(format -> {
            Allure.step("Проверка поиска для формата: " + format, () -> {
                pages.shareContactListSteps.enterSearchText(format)
                        .verifyContactExists(name)
                        .selectContact(name)
                        .verifyContactChipExists(name)
                        .verifyContactIsSelected(name)
                        .selectContact(name);
            });
        });
    }

    @Test
    @ChannelAllureIds(web = "57850", desktop = "61860")
    @DisplayName("Совершить поиск контакта по фамилии")
    public void searchContactByRegisteredLastName() {
        context = groupFactory.p2pChat();
        String name = context.getParticipant().getName();
        String lastName = context.getParticipant().getLastName();
        context.getOwnerBuilder().addContacts(context.getParticipant());

        openShareContactMenu(context, true);

        pages.shareContactListSteps.enterSearchText(lastName)
                .verifyContactExists(name)
                .selectContact(name)
                .verifyContactChipExists(name)
                .verifyContactIsSelected(name);
    }


    private void openShareContactMenu(GroupContext ct, boolean isSavedChat) {
        loginHelper.performUILogin(ct.getOwner());
        if (isSavedChat) {
            pages.chatListSteps.clickSavedChat();
        } else {
            pages.chatListSteps.clickChatByName(ct.getGroupName());
        }
        pages.inputBarSteps.clickOpenAttachMenuButton();
        pages.attachContextMenuSteps.clickShareContactsButton();
    }

    private static Stream<String> phoneNumberFormats(String baseNumber) {
        String raw = baseNumber.replaceAll("\\D", "");

        return Stream.of(
                baseNumber,
                baseNumber.replace("(", "").replace(")", ""),
                "+" + raw,
                raw,
                raw.length() >= 5 ? raw.substring(raw.length() - 5) : raw
        );
    }
}
