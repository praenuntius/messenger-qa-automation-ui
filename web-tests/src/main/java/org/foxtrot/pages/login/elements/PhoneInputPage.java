package org.foxtrot.pages.login.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class PhoneInputPage {

    private final By languageRuDropdown = cssSelector("[data-cy='welcomeLoginLanguageSelectorRu']");
    private final By languageEnDropDown = cssSelector("[data-cy='welcomeLoginLanguageSelectorEn']");
    private final By changeServerButton = cssSelector("[data-cy='welcomeLoginChangeServer']");
    private final By phonePrefixDropdown = cssSelector("[data-cy='welcomeLoginPhoneCode']");
    private final By nextButton = cssSelector("[data-cy='signInNextButton']");
    private final By phoneNumberField = cssSelector("[data-cy='signInPhoneInput']");
}
