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
public class EditUserProfileTest extends BaseTest {

    private LoginHelper loginHelper;
    private UsersEnum owner;
    private final String changedName = "Павел";
    private final String changedMiddleName = "Павлович";
    private final String changedLastName = "Шумов";
    private final String jobTitle = "Пожарная охрана";
    private final String phone = "+7 904 098-27-47";
    private final String email = "pavelShumOff@gmail.com";

    @BeforeAll
    public void beforeAll() {
        loginHelper = new LoginHelper();
        owner = UsersEnum.TEST50;
    }

    @AfterAll
    public void afterAll() {
        UserPoolManager.releaseCurrentUser();
        UserTestDataCleaner cleaner = new UserTestDataCleaner();
        cleaner.resetUserProfile(owner);
    }

    @AllureId("52374")
    @Test
    @Order(1)
    @DisplayName("Редактировать профиль")
    public void editProfile() {
        loginHelper.performUILogin(owner);
        pages.leftSidebarSteps.openProfileMenu();
        pages.myProfileSteps.openProfileMenu();
        pages.myProfileEditModalSteps.editName(changedName)
                .editLastName(changedLastName)
                .editMiddleName(changedMiddleName)
                .editJobTitle(jobTitle)
                .editEmail(email)
                .addAdditionalPhone(phone)
                .uploadPhoto("images/tinyJPG.jpg");
        pages.myProfileEditModalSteps.clickSaveButton();
        pages.myProfileSteps.clickShowMoreButton();

        pages.myProfileSteps.verifyEditProfile(
                changedName,
                changedMiddleName,
                changedLastName,
                jobTitle,
                phone,
                email);
    }
}
