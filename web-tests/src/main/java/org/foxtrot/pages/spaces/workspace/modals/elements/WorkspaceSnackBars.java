package org.foxtrot.pages.spaces.workspace.modals.elements;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class WorkspaceSnackBars {

    private final By spaceDeletedSnackBar = By.xpath("//div[contains(text(), 'удалено')]");
}
