package org.foxtrot.utils.factories.contexts;

import lombok.Builder;
import lombok.Getter;
import org.foxtrot.enums.UsersEnum;

@Getter
@Builder(toBuilder = true)
public class UserContext {
    UsersEnum user;
}