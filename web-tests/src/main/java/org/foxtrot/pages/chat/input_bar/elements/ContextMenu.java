package org.foxtrot.pages.chat.input_bar.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter

public class ContextMenu {
    private final By pasteButton = cssSelector("[data-cy='messageContextMenuInsert']");
    private final By copyButton = cssSelector("[data-cy='messageContextMenuCopy']");
    private final By cutButton = cssSelector("[data-cy='messageContextMenuCut']");
    private final By formattingButton = cssSelector("[data-cy='messageContextMenuFormatting']");
    private final By addLinkButton = cssSelector("[data-cy='messageFormattingSubMenuLink']");
    private final By boldButton = cssSelector("[data-cy='messageFormattingSubMenuBold']");
    private final By italicButton = cssSelector("[data-cy='messageFormattingSubMenuItalic']");
    private final By underlineButton = cssSelector("[data-cy='messageFormattingSubMenuUnderline']");
    private final By strikethroughButton = cssSelector("[data-cy='messageFormattingSubMenuStrike']");
    private final By monospaceButton = cssSelector("[data-cy='messageFormattingSubMenuMonospace']");
    private final By undoAllChangesButton = cssSelector("[data-cy='messageFormattingSubMenuUndo']");
    private final By selectedStyleIndicator = cssSelector("use[href='#icReadSent16']");
}
