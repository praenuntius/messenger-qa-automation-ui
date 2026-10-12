package org.foxtrot.mobile.screens;

import org.foxtrot.mobile.elements.Button;
import org.foxtrot.mobile.elements.TextBox;
import org.foxtrot.mobile.elements.TextView;

import static org.foxtrot.mobile.elements.base.BaseElement.*;

public class ChannelMembersScreen {

    private final TextView screenHeader = new TextView(
            byResourceId("textTitle"),
            byAccessibilityId("MembersList.navigationTitle")
    );

    private final Button backBtn = new Button(
            byResourceId("buttonBack"),
            byAccessibilityId("BackButton")
    );

    private final Button searchBtn = new Button(
            byResourceId("buttonSearch")
    );

    private final TextBox searchInput = new TextBox(
            byResourceId("editSearchQuery"),
            byAccessibilityId("Common.textField")
    );

    private final Button addParticipantsBtn = new Button(
            byResourceId("add"),
            byAccessibilityId("MembersList.addMemberButton")
    );

    private final TextView subTitleText = new TextView(
            byResourceId("textMemberCounts"),
            byAccessibilityId("Common.tableViewSectionHeaderTitle")
    );

    private TextView contactName(int number) {
        return new TextView(
                byComposeCellTitle("recyclerMembers", number), 0,
                byAccessibilityId("MemberCell.nameLabel"), number - 1
        );
    }

    private TextView contactName(String contactName) {
        return new TextView(
                byEqualsText(contactName),
                byAccessibilityIdAndText("MemberCell.nameLabel", contactName)
        );
    }

    public String getScreenHeaderText() {
        return screenHeader.getText();
    }

    public void clickBackBtn() {
        backBtn.click();
    }

    public void clickSearchBtn() {
        searchBtn.click();
    }

    public void fillSearchInput(String text) {
        searchInput.sendKeys(text);
    }

    public void clearSearchInput() {
        searchInput.clear();
    }

    public void clickAddParticipantsBtn() {
        addParticipantsBtn.click();
    }

    public String getSubTitleText() {
        return subTitleText.getText();
    }

    public String getContactNameText(int number) {
        return normalizeDisplayName(contactName(number).getText());
    }

    public boolean isContactByNameDisplayed(String contactName) {
        for (String needle : displayNameNeedles(contactName)) {
            if (contactName(needle).isDisplayed(8L)) {
                return true;
            }
            TextView contains = new TextView(
                    byContainsText(needle),
                    byAccessibilityIdAndContainsText("MemberCell.nameLabel", needle));
            if (contains.isDisplayed(2L)) {
                return true;
            }
        }
        return false;
    }

    public void clickContactByName(String contactName) {
        for (String needle : displayNameNeedles(contactName)) {
            if (contactName(needle).isDisplayed(3L)) {
                contactName(needle).click();
                return;
            }
            TextView contains = new TextView(
                    byContainsText(needle),
                    byAccessibilityIdAndContainsText("MemberCell.nameLabel", needle));
            if (contains.isDisplayed(2L)) {
                contains.click();
                return;
            }
        }
        contactName(contactName).click();
    }

    private static String normalizeDisplayName(String text) {
        return text.replaceAll("\\s+", " ").replace(" Отчество ", " ").trim();
    }

    private static String[] displayNameNeedles(String name) {
        return org.foxtrot.enums.UsersEnum.uiNameNeedles(name).toArray(String[]::new);
    }
}
