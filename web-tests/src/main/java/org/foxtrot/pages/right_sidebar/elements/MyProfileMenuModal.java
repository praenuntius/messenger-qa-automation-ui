package org.foxtrot.pages.right_sidebar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

@Getter
public class MyProfileMenuModal {

    private final By editPhotoButton = xpath("//span[text() = 'Загрузить фото']");
    private final By editFirstNameInput = cssSelector("[data-cy='editProfileFirstNameInput']");
    private final By editLastNameInput = cssSelector("[data-cy='editProfileLastNameInput']");
    private final By editMiddleNameInput = cssSelector("[data-cy='editProfileMiddleNameInput']");
    private final By editProfileJobTittleInput = cssSelector("[data-cy='editProfileJobTitleInput']");
    private final By editProfileEmailInput = cssSelector("[data-cy='editProfileEmailInput']");
    private final By addEmailButton = cssSelector("button[class*= 'AddEmailButton']");
    private final By addPhoneButton = cssSelector("button[class*= 'AddPhoneButton']");
    private final By addPhoneInput0 = cssSelector("input[id*='0additionalPhones']");
    private final By saveButton = cssSelector("[data-cy='editProfileUserSaveButton']");
    private final By fileInput = cssSelector("input[type='file']");
    private final By deletePhotoButton = cssSelector("button[class*='IconButtonSecondary']");
    private final By deleteEmailButton = cssSelector("[data-cy='editProfileDeleteEmailButton']");
    private final By deletePhoneButton = cssSelector("[data-cy='editProfileDeletePhoneNumber']");
}
