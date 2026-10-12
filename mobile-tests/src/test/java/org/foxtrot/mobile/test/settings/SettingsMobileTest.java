package org.foxtrot.mobile.test.settings;

import org.foxtrot.mobile.allure.AllureIds;
import org.foxtrot.mobile.test.BaseMobileTest;
import org.foxtrot.mobile.testdata.dictionary.AccountDictionary;
import org.foxtrot.mobile.testdata.dictionary.LoginWelcomeDictionary;
import org.foxtrot.utils.factories.core.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SettingsMobileTest extends BaseMobileTest {
    private final UserFactory userFactory = new UserFactory();

    @AfterEach
    public void tearDown() {
        userFactory.releaseAll();
    }

    @Test
    @DisplayName("Выход из приложения")
    @AllureIds(android = "48776", ios = "51303")
    public void logoutFromApplication() {
        var user = userFactory.takeUser();
        pages.mobileLogin().loginViaUi(user);
        pages.bottomMenu().clickAccountButton();
        pages.account().checkAccountScreenPresent(AccountDictionary.SCREEN_TITLE)
                .checkAccountName(user.getName())
                .clickExitButton();
        pages.modal().checkModalContent(
                        AccountDictionary.LOGOUT_MODAL_TITLE, AccountDictionary.CANCEL, AccountDictionary.EXIT)
                .clickActionButton();
        pages.loginWelcome().checkLoginWelcomeTitle(LoginWelcomeDictionary.SCREEN_TITLE);
    }
}
