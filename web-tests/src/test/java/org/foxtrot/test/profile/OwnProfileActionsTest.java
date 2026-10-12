package org.foxtrot.test.profile;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.test.BaseTest;
import org.foxtrot.utils.LoginHelper;
import org.foxtrot.utils.UserPoolManager;
import org.foxtrot.utils.UserTestDataCleaner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AllureJunit5.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OwnProfileActionsTest extends BaseTest{

    private LoginHelper loginHelper;
    private UsersEnum owner;

    @BeforeAll
    public void beforeAll() {

        loginHelper = new LoginHelper();
        UserPoolManager userPoolManager = new UserPoolManager();
        owner = userPoolManager.getFreeUser();
    }

    @AfterAll
    public void afterAll() {
        UserPoolManager.releaseCurrentUser();
        deleteTestData();
    }

    @AllureId("52380")
    @Test
    @Order(1)
    @DisplayName("Открыть чат 'Сохраненные'")
    public void openSavedChat() {
        loginHelper.performUILogin(owner);
        pages.leftSidebarSteps.openProfileMenu();
        pages.myProfileSteps.openSavedChat();
        pages.topbarSteps.verifySavedChatOpened();
    }

    @AllureId("52376")
    @Test
    @Order(2)
    @DisplayName("Скопировать ссылку на профиль")
    public void copyOwnProfileLink() {
        loginHelper.performUILogin(owner);
        pages.leftSidebarSteps.openProfileMenu();
        pages.myProfileSteps.copyProfileLink();
        pages.snackbarsSteps.verifyCopiedSnackbarVisible();
    }

    @AllureId("52378")
    @Test
    @Order(3)
    @DisplayName("Открыть FAQ")
    public void openFAQ() {
        loginHelper.performUILogin(owner);
        pages.leftSidebarSteps.openFaqMenu();
        pages.faqPageSteps.verifyFaqTitleHeaderVisible();
    }

    private void deleteTestData() {
        UserTestDataCleaner userTestDataCleaner = new UserTestDataCleaner();
        userTestDataCleaner.clean(owner);
    }
}
