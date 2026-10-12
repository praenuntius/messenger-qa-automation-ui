package org.foxtrot.utils.factories.contexts;

import lombok.Builder;
import lombok.Getter;
import org.foxtrot.enums.UsersEnum;
import org.foxtrot.utils.TestDataBuilder;

@Getter
@Builder(toBuilder = true)
public class WorkspaceContext {

    private final UsersEnum owner;
    private final UsersEnum admin;
    private final UsersEnum participant;

    private final Long wsId;
    private final String wsName;

    private final TestDataBuilder ownerBuilder;
    private final TestDataBuilder adminBuilder;
    private final TestDataBuilder participantBuilder;
}