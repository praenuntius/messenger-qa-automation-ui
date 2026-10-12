package org.foxtrot.mobile.screens;

import io.appium.java_client.AppiumBy;
import org.foxtrot.mobile.elements.Block;
import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.TextBox;
import org.foxtrot.mobile.elements.TextView;

import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class AddSubscribersScreen {

    private final Button nextButton = new Button(
            byResourceId("next"),
            byAccessibilityId("AddUser.navigationNextButton")
    );

    private final Button backButton = new Button(
            byResourceId("buttonBack"),
            byAccessibilityId("BackButton")
    );

    private final Button searchButton = new Button(
            byResourceId("buttonSearch"),
            AppiumBy.iOSNsPredicateString(
                    "name == 'Icon 16/ic-search' OR name CONTAINS 'search' OR name CONTAINS 'Search'"
                            + " OR label == 'Поиск'"
            )
    );

    private final TextBox searchInput = new TextBox(
            byResourceId("editSearchQuery"),
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeSearchField' AND (name == 'Common.textField'"
                            + " OR label CONTAINS 'Поиск')"
            )
    );

    private final Button cancelButton = new Button(
            byResourceId("buttonCancel"),
            byAccessibilityId("Common.searchBarClearButton")
    );

    private final Block contactsBlock = new Block(
            byResourceId("contacts"),
            byAccessibilityId("AddUser.container")
    );

    private final Block addedUsersContainer = new Block(
            byResourceId("selectedContacts"),
            byAccessibilityId("AddUser.addedUsersContainer")
    );

    private TextView screenTitle(String text) {
        return new TextView(
                byResourceIdAndText("textTitle", text),
                byAccessibilityIdAndText("AddUser.navigationTitle", text)
        );
    }

    private TextView contactNameByNumber(int numberOfBlock) {
        return new TextView(
                byContactsListText(numberOfBlock), 0,
                byAccessibilityId("CommonCell.title"), numberOfBlock - 1
        );
    }

    private TextView contactNameByText(String contactName) {
        return new TextView(contactsBlock,
                byContactsListText(contactName),
                byAccessibilityIdAndText("CommonCell.title", contactName)
        );
    }

    private TextView contactNameContains(String contactName) {
        return new TextView(contactsBlock,
                byListItemName(contactName),
                byAccessibilityIdAndContainsText("CommonCell.title", contactName)
        );
    }

    private TextView addedUserTitleByText(String contactName) {
        return new TextView(addedUsersContainer,
                byResourceIdAndText("tvContactName", contactName),
                byAccessibilityIdAndText("AddUser.addedUserTitle", contactName)
        );
    }

    public boolean isScreenTitleDisplayed(String text, long seconds) {
        return screenTitle(text).isDisplayed(seconds);
    }

    public String getContactNameText(int numberOfBlock) {
        return normalizeDisplayName(contactNameByNumber(numberOfBlock).getText(15L));
    }

    public boolean hasContactRows(long seconds) {
        return contactNameByNumber(1).isDisplayed(seconds);
    }

    public boolean isContactByTextDisplayed(String contactName) {
        for (String needle : displayNameNeedles(contactName)) {
            if (contactNameByText(needle).isDisplayed(5L) || contactNameContains(needle).isDisplayed(3L)) {
                return true;
            }
        }
        return false;
    }

    public void selectContactByName(String contactName) {
        for (String needle : displayNameNeedles(contactName)) {
            if (contactNameByText(needle).isDisplayed(3L)) {
                contactNameByText(needle).click();
                return;
            }
            if (contactNameContains(needle).isDisplayed(2L)) {
                contactNameContains(needle).click();
                return;
            }
        }
        contactNameByText(contactName).click();
    }

    public void clickNextBtn() {
        nextButton.click();
    }

    public void clickSearchBtn() {
        if (searchInput.isDisplayed(1L)) {
            return;
        }
        if (searchButton.isDisplayed(2L)) {
            searchButton.click();
        }
    }

    public void fillSearchInput(String text) {
        ensureSearchFieldVisible();
        searchInput.sendKeys(text);
        try {
            Thread.sleep(1_500L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void clearSearchInput() {
        if (!searchInput.isDisplayed(1L)) {
            ensureSearchFieldVisible();
        }
        if (searchInput.isDisplayed(2L)) {
            searchInput.clear();
        }
    }

    private void ensureSearchFieldVisible() {
        if (searchInput.isDisplayed(2L)) {
            searchInput.click();
            return;
        }
        if (searchButton.isDisplayed(2L)) {
            searchButton.click();
        }
    }

    public boolean isAddedUserBubbleDisplayed(String contactName) {
        for (String needle : displayNameNeedles(contactName)) {
            if (addedUserTitleByText(needle).isDisplayed(5L)) {
                return true;
            }
        }
        return false;
    }

    private static String normalizeDisplayName(String text) {
        return text.replaceAll("\\s+", " ").replace(" Отчество ", " ").trim();
    }

    private static String[] displayNameNeedles(String name) {
        return org.foxtrot.enums.UsersEnum.uiNameNeedles(name).toArray(String[]::new);
    }
}
