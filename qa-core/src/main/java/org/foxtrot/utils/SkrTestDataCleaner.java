package org.foxtrot.utils;

import io.restassured.response.Response;
import org.foxtrot.api.dto.group.ClearHistoryRequest;
import org.foxtrot.api.dto.group.DeleteGroupRequest;
import org.foxtrot.api.dto.group.HideGroupRequest;
import org.foxtrot.api.dto.group.response.GetUserGroupStatesResponse;
import org.foxtrot.api.service.EncryptedApiService;
import org.foxtrot.enums.UsersEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.foxtrot.api.constants.ApiEndpoints.CLEAR_HISTORY;
import static org.foxtrot.api.constants.ApiEndpoints.DELETE_GROUP;
import static org.foxtrot.api.constants.ApiEndpoints.GET_USER_GROUP_STATES;
import static org.foxtrot.api.constants.ApiEndpoints.HIDE_GROUP;

/**
 * Очистка данных SKR-тестов на stage14.
 * <p>
 * Обычный {@link UserTestDataCleaner} ходит через plain {@code GroupApiSteps}
 * (X-Disable-Encryption) и на stage14 падает на auth/start — поэтому здесь
 * только encrypted-сессия ({@link SymmetricKeySessionFactory}).
 */
public final class SkrTestDataCleaner {

    private static final Logger log = LoggerFactory.getLogger(SkrTestDataCleaner.class);

    private static final String SAVED_CHAT_NAME = "Сохраненные";
    private static final Set<String> DELETABLE_OWNER_TYPES = Set.of("GROUP", "INFO_CHANNEL");

    private final SymmetricKeySessionFactory sessionFactory = new SymmetricKeySessionFactory();
    private final EncryptedApiService encryptedApi = new EncryptedApiService();

    public void clean(UsersEnum... users) {
        cleanPrefix("skr", users);
    }

    /** Только группы/спейсы с этим префиксом имени — безопаснее при параллельных soak. */
    public void cleanPrefix(String namePrefix, UsersEnum... users) {
        if (users == null) {
            return;
        }
        String prefix = namePrefix == null ? "skr" : namePrefix.toLowerCase(Locale.ROOT);
        for (UsersEnum user : users) {
            if (user == null) {
                continue;
            }
            try {
                SymmetricKeySession session = sessionFactory.login(user);
                cleanSession(session, prefix);
            } catch (Exception e) {
                log.warn("SKR clean failed for {}: {}", user, e.getMessage());
            }
        }
    }

    public void cleanSession(SymmetricKeySession session) {
        cleanSession(session, "skr");
    }

    private void cleanSession(SymmetricKeySession session, String namePrefix) {
        List<GetUserGroupStatesResponse> states = loadGroupStates(session);
        if (states == null || states.isEmpty()) {
            return;
        }
        for (GetUserGroupStatesResponse state : states) {
            try {
                cleanOne(session, state, namePrefix);
            } catch (Exception e) {
                log.warn("SKR clean group {} failed: {}", state.getGroupId(), e.getMessage());
            }
        }
    }

    private void cleanOne(SymmetricKeySession session, GetUserGroupStatesResponse state, String namePrefix) {
        long groupId = state.getGroupId();
        String type = state.getGroup() != null ? state.getGroup().getType() : null;
        String role = state.getRole();
        String groupName = state.getGroup() != null ? state.getGroup().getName() : null;
        long lastMessageId = state.getLastMessageId();

        Object wsHeader = -1;
        if (state.getGroup() != null && state.getGroup().getWorkspaceId() != null
                && state.getGroup().getWorkspaceId() != -1L) {
            wsHeader = state.getGroup().getWorkspaceId();
        }

        if ("P2P".equals(type) && lastMessageId > 0) {
            encryptedApi.post(HIDE_GROUP, new HideGroupRequest(groupId), session, wsHeader);
            return;
        }
        if (SAVED_CHAT_NAME.equals(groupName)) {
            encryptedApi.post(CLEAR_HISTORY, new ClearHistoryRequest(groupId), session, wsHeader);
            return;
        }
        if ("OWNER".equals(role) && isSkRDeletableGroup(type, groupName, namePrefix)) {
            Response delete = encryptedApi.post(
                    DELETE_GROUP, new DeleteGroupRequest(groupId), session, wsHeader);
            if (delete.statusCode() >= 400) {
                log.warn("deleteGroup {} status={}: {}", groupId, delete.statusCode(), delete.getBody().asString());
            }
        }
    }

    /**
     * Удаляем SKR-группы ({@code skr-...}) и любые OWNER GROUP/INFO_CHANNEL
     * без «системных» имён — чтобы подчищать и UI-созданные чаты.
     */
    private boolean isSkRDeletableGroup(String type, String groupName, String namePrefix) {
        if (type == null || SAVED_CHAT_NAME.equals(groupName)) {
            return false;
        }
        if (!DELETABLE_OWNER_TYPES.contains(type)) {
            return false;
        }
        if (groupName == null) {
            return false;
        }
        return groupName.toLowerCase(Locale.ROOT).startsWith(namePrefix);
    }

    private List<GetUserGroupStatesResponse> loadGroupStates(SymmetricKeySession session) {
        Response response = encryptedApi.post(GET_USER_GROUP_STATES, Collections.emptyMap(), session);
        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "getUserGroupStates failed: status=" + response.statusCode()
                            + ", body=" + response.getBody().asString()
            );
        }
        return encryptedApi.parseList(response, session.getMessageKey(), GetUserGroupStatesResponse.class);
    }
}
