package org.foxtrot.pages.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;
import static org.openqa.selenium.By.cssSelector;

@Getter
public class CreatePollModal {
    private final By pollNameField = cssSelector("[data-cy='pollCreationThemeInput']");
    private final By closePollModal = By.xpath("//*[@id=\"dialog-root\"]/div/div/div/div[2]/section/div[1]/button");
    private final By nextButton = By.cssSelector("[data-cy='createPollButton']");
    private final By cancelPollModalButton = By.cssSelector("[data-cy='createPollCancelButton']");
    private final By anonymousPollToggle = By.xpath("//*[@id=\"dialog-root\"]/div/div/div/div[2]/section/form/section[2]/label[1]");
    private final By multiAnswersPollToggle = By.xpath("//*[@id='dialog-root']//label[.//span[contains(., 'Выбор нескольких ответов')]]");
    private final By expirationTimerToggle = By.xpath("//*[@id='dialog-root']//label[.//span[contains(., 'Таймер завершения опроса')]]");
    private final By addAnswerOptionButton = By.xpath("//*[@id=\"dialog-root\"]/div/div/div/div[2]/section/form/section[1]/div[4]/button");
    private final By expirationTimerSelect = By.xpath("//*[@id='dialog-root']//div[contains(@class, 'selector__control')]");
    private final By pollOptionInput = cssSelector("[data-cy='pollOptionInput']");
    private final By pollErrorMessage = By.xpath("//*[contains(@class, 'dialog') or @id='dialog-root']//div[contains(text(), 'минимум 2')]");
    private final By createPollButton = cssSelector("[data-cy='createPollButton']");
}
