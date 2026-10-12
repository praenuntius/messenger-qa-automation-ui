package org.foxtrot.pages.user_lists.elements;

import lombok.Getter;
import org.openqa.selenium.By;

import static org.openqa.selenium.By.cssSelector;

@Getter
public class UsersToMentionList {
    private final By mentionListContainer = cssSelector("div.ql-mention-list-container");
    private final By mentionList = cssSelector("div.ql-mention-list-container ul.ql-mention-list");
    private final By mentionListItem = cssSelector("div.ql-mention-list-container li.ql-mention-list-item");
}
