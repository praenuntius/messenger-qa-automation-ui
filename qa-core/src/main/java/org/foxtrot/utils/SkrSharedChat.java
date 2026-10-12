package org.foxtrot.utils;

import io.restassured.response.Response;
import lombok.Builder;
import lombok.Getter;
import org.foxtrot.api.dto.group.AddUsersRequest;
import org.foxtrot.api.dto.group.response.GetUserGroupStatesResponse;
import org.foxtrot.api.dto.message.SendTextMessageRequest;
import org.foxtrot.api.dto.workSpace.AddUserToWorkspaceRequest;
import org.foxtrot.api.dto.workSpace.DeleteWorkspaceRequest;
import org.foxtrot.api.service.EncryptedApiService;
import org.foxtrot.enums.UsersEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.foxtrot.api.constants.ApiEndpoints.ADD_USERS;
import static org.foxtrot.api.constants.ApiEndpoints.ADD_USER_TO_WORKSPACE;
import static org.foxtrot.api.constants.ApiEndpoints.DELETE_WORKSPACE;
import static org.foxtrot.api.constants.ApiEndpoints.GET_USER_GROUP_STATES;

/**
 * Общая группа для SKR E2E (Web/Mobile): owner + peer.
 * Homespace ({@code workspaceId == null}) или группа в пространстве.
 * <p>
 * Account id peer берём из signIn на текущем стенде — UsersEnum.id на stage14
 * может не совпадать с preprod.
 * <p>
 * На stage14 plain GroupApiSteps (X-Disable-Encryption) падает на auth/start —
 * только encrypted-сессия.
 * <p>
 * Peer/owner API-сессии кэшируются в JVM: повторный {@code login()} = новый
 * {@code saveSymmetricKey}, а после TTL (~1 мин на stage14) старый
 * {@code encryption-key-id} даёт 401 UNAUTHORIZED. При 401/493 — re-login и retry.
 */
@Getter
@Builder
public final class SkrSharedChat {

    private static final Logger log = LoggerFactory.getLogger(SkrSharedChat.class);
    private static final EncryptedApiService ENCRYPTED_API = new EncryptedApiService();
    private static final SymmetricKeySessionFactory SESSION_FACTORY = new SymmetricKeySessionFactory();
    private static final Object SESSION_LOCK = new Object();

    private static UsersEnum cachedOwnerUser;
    private static SymmetricKeySession cachedOwnerSession;
    private static UsersEnum cachedPeerUser;
    private static SymmetricKeySession cachedPeerSession;

    private final UsersEnum owner;
    private final UsersEnum peer;
    private final long groupId;
    private final String groupName;
    private final Long workspaceId;
    private SymmetricKeySession ownerSession;
    private SymmetricKeySession peerSession;

    /**
     * Web: группа через API — веб-клиент быстро подтягивает чат в список.
     */
    public static SkrSharedChat create(UsersEnum owner, UsersEnum peer) {
        SymmetricKeySession peerSession = requirePeerSession(peer);
        SymmetricKeySession ownerSession = requireOwnerSession(owner);

        String groupName = "skr-" + System.currentTimeMillis();
        long groupId = ENCRYPTED_API.createHomespaceGroup(ownerSession, groupName).getGroupId();
        addPeer(ownerSession, peerSession, groupId, null);

        return SkrSharedChat.builder()
                .owner(owner)
                .peer(peer)
                .groupId(groupId)
                .groupName(groupName)
                .workspaceId(null)
                .ownerSession(ownerSession)
                .peerSession(peerSession)
                .build();
    }

    /**
     * Группа уже создана в UI. groupId (и workspaceId) ищем через encrypted getUserGroupStates.
     */
    public static SkrSharedChat attachPeerToUiGroup(UsersEnum owner, UsersEnum peer, String groupName) {
        SymmetricKeySession peerSession = requirePeerSession(peer);
        FoundGroup found = findGroupByName(owner, groupName);
        SymmetricKeySession ownerSession = requireOwnerSession(owner);
        if (isRealWorkspace(found.workspaceId())) {
            addPeerToWorkspace(ownerSession, peerSession, found.workspaceId());
        }
        addPeer(ownerSession, peerSession, found.groupId(), found.workspaceId());

        return SkrSharedChat.builder()
                .owner(owner)
                .peer(peer)
                .groupId(found.groupId())
                .groupName(groupName)
                .workspaceId(found.workspaceId())
                .ownerSession(ownerSession)
                .peerSession(peerSession)
                .build();
    }

    private static SymmetricKeySession requirePeerSession(UsersEnum peer) {
        synchronized (SESSION_LOCK) {
            if (cachedPeerSession == null || cachedPeerUser != peer) {
                cachedPeerSession = loginOrThrow(peer, "peer");
                cachedPeerUser = peer;
            }
            return cachedPeerSession;
        }
    }

    private static SymmetricKeySession requireOwnerSession(UsersEnum owner) {
        synchronized (SESSION_LOCK) {
            if (cachedOwnerSession == null || cachedOwnerUser != owner) {
                cachedOwnerSession = loginOrThrow(owner, "owner");
                cachedOwnerUser = owner;
            }
            return cachedOwnerSession;
        }
    }

    private static SymmetricKeySession reloginPeer(UsersEnum peer) {
        synchronized (SESSION_LOCK) {
            cachedPeerSession = loginOrThrow(peer, "peer");
            cachedPeerUser = peer;
            return cachedPeerSession;
        }
    }

    private static SymmetricKeySession reloginOwner(UsersEnum owner) {
        synchronized (SESSION_LOCK) {
            cachedOwnerSession = loginOrThrow(owner, "owner");
            cachedOwnerUser = owner;
            return cachedOwnerSession;
        }
    }

    private static SymmetricKeySession loginOrThrow(UsersEnum user, String who) {
        SymmetricKeySession session = SESSION_FACTORY.login(user);
        if (session.getAccountId() == null) {
            throw new IllegalStateException(who + " signIn returned null accountId for " + user);
        }
        return session;
    }

    private static void addPeerToWorkspace(SymmetricKeySession ownerSession,
                                           SymmetricKeySession peerSession,
                                           long workspaceId) {
        Response add = postAsOwner(
                ADD_USER_TO_WORKSPACE,
                new AddUserToWorkspaceRequest(List.of(peerSession.getAccountId())),
                ownerSession,
                workspaceId
        );
        if (add.statusCode() == 200 || add.statusCode() == 201) {
            return;
        }
        String body = add.getBody() != null ? add.getBody().asString() : "";
        if (add.statusCode() == 400 || add.statusCode() == 409) {
            if (body.toLowerCase().contains("already") || body.toLowerCase().contains("exist")) {
                return;
            }
        }
        throw new IllegalStateException(
                "addUsersToWorkspace failed: status=" + add.statusCode() + ", body=" + body
        );
    }

    private static void addPeer(SymmetricKeySession ownerSession,
                                SymmetricKeySession peerSession,
                                long groupId,
                                Long workspaceId) {
        Response addUsers = postAsOwner(
                ADD_USERS,
                new AddUsersRequest(groupId, List.of(peerSession.getAccountId())),
                ownerSession,
                workspaceHeader(workspaceId)
        );
        if (addUsers.statusCode() != 200 && addUsers.statusCode() != 201) {
            throw new IllegalStateException(
                    "addUsers failed: status=" + addUsers.statusCode()
                            + ", body=" + addUsers.getBody().asString()
            );
        }
    }

    private static FoundGroup findGroupByName(UsersEnum owner, String groupName) {
        SymmetricKeySession ownerSession = requireOwnerSession(owner);
        Response response = ENCRYPTED_API.post(
                GET_USER_GROUP_STATES,
                Collections.emptyMap(),
                ownerSession
        );
        if (isAuthOrKeyExpired(response)) {
            log.warn("owner getUserGroupStates expired — re-login {}", owner);
            ownerSession = reloginOwner(owner);
            response = ENCRYPTED_API.post(
                    GET_USER_GROUP_STATES,
                    Collections.emptyMap(),
                    ownerSession
            );
        }
        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "getUserGroupStates failed: status=" + response.statusCode()
                            + ", body=" + response.getBody().asString()
            );
        }
        final SymmetricKeySession session = ownerSession;
        List<GetUserGroupStatesResponse> states = ENCRYPTED_API.parseList(
                response,
                session.getMessageKey(),
                GetUserGroupStatesResponse.class
        );
        if (states == null || states.isEmpty()) {
            throw new IllegalStateException(
                    "getUserGroupStates empty for " + session.getUser() + ", looking for " + groupName
            );
        }
        GetUserGroupStatesResponse match = states.stream()
                .filter(s -> s.getGroup() != null && groupName.equals(s.getGroup().getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Group '" + groupName + "' not found in getUserGroupStates for " + session.getUser()
                                + " (states=" + states.size() + ")"
                ));
        Long wsId = match.getGroup().getWorkspaceId();
        return new FoundGroup(match.getGroupId(), isRealWorkspace(wsId) ? wsId : null);
    }

    public void ownerSendText(String text) {
        sendAs(true, text);
    }

    public void peerSendText(String text) {
        sendAs(false, text);
    }

    private void sendAs(boolean owner, String text) {
        UsersEnum user = owner ? this.owner : this.peer;
        String who = owner ? "owner" : "peer";
        SymmetricKeySession session = owner ? requireOwnerSession(user) : requirePeerSession(user);
        bindSession(owner, session);

        Response response = trySend(session, text);
        if (isAuthOrKeyExpired(response)) {
            log.warn("{} sendTextMessage {} — re-login {} and retry", who, describeFailure(response), user);
            session = owner ? reloginOwner(user) : reloginPeer(user);
            bindSession(owner, session);
            response = trySend(session, text);
        }
        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    who + " sendTextMessage failed: status=" + response.statusCode()
                            + ", body=" + response.getBody().asString()
            );
        }
    }

    private void bindSession(boolean owner, SymmetricKeySession session) {
        if (owner) {
            this.ownerSession = session;
        } else {
            this.peerSession = session;
        }
    }

    private Response trySend(SymmetricKeySession session, String text) {
        return ENCRYPTED_API.sendTextMessage(
                new SendTextMessageRequest(
                        text,
                        ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE),
                        groupId
                ),
                session,
                workspaceHeader(workspaceId)
        );
    }

    /** Best-effort удаление группы owner-сессией. */
    public void deleteQuietly() {
        try {
            SymmetricKeySession session = requireOwnerSession(owner);
            bindSession(true, session);
            Response response = ENCRYPTED_API.post(
                    org.foxtrot.api.constants.ApiEndpoints.DELETE_GROUP,
                    new org.foxtrot.api.dto.group.DeleteGroupRequest(groupId),
                    session,
                    workspaceHeader(workspaceId)
            );
            if (isAuthOrKeyExpired(response)) {
                session = reloginOwner(owner);
                bindSession(true, session);
                ENCRYPTED_API.post(
                        org.foxtrot.api.constants.ApiEndpoints.DELETE_GROUP,
                        new org.foxtrot.api.dto.group.DeleteGroupRequest(groupId),
                        session,
                        workspaceHeader(workspaceId)
                );
            }
        } catch (Exception ignored) {
            // AfterAll cleaner подчистит
        }
    }

    public void deleteWorkspaceQuietly() {
        if (!isRealWorkspace(workspaceId)) {
            return;
        }
        try {
            SymmetricKeySession session = requireOwnerSession(owner);
            bindSession(true, session);
            Response response = ENCRYPTED_API.post(
                    DELETE_WORKSPACE,
                    new DeleteWorkspaceRequest(workspaceId),
                    session
            );
            if (isAuthOrKeyExpired(response)) {
                session = reloginOwner(owner);
                bindSession(true, session);
                ENCRYPTED_API.post(
                        DELETE_WORKSPACE,
                        new DeleteWorkspaceRequest(workspaceId),
                        session
                );
            }
        } catch (Exception ignored) {
            // ignore
        }
    }

    public static EncryptedApiService encryptedApi() {
        return ENCRYPTED_API;
    }

    public static UsersEnum resolveOwner() {
        return UsersEnum.valueOf(System.getProperty("encryption.test.user", "TEST49"));
    }

    public static UsersEnum resolvePeer() {
        return UsersEnum.valueOf(System.getProperty("encryption.peer.user", "TEST48"));
    }

    static boolean isAuthOrKeyExpired(Response response) {
        if (response == null) {
            return false;
        }
        int status = response.statusCode();
        if (status == 401 || status == 403 || status == 493) {
            return true;
        }
        String body = response.getBody() != null ? response.getBody().asString() : "";
        String upper = body.toUpperCase();
        return upper.contains("UNAUTHORIZED")
                || upper.contains("KEY_MISMATCH")
                || upper.contains("TOKEN_EXPIRED");
    }

    private static String describeFailure(Response response) {
        return "status=" + response.statusCode() + ", body=" + response.getBody().asString();
    }

    private static Response postAsOwner(String endpoint,
                                        Object body,
                                        SymmetricKeySession ownerSession,
                                        Object workspaceId) {
        Response response = workspaceId == null
                ? ENCRYPTED_API.post(endpoint, body, ownerSession)
                : ENCRYPTED_API.post(endpoint, body, ownerSession, workspaceId);
        if (!isAuthOrKeyExpired(response) || ownerSession.getUser() == null) {
            return response;
        }
        log.warn("owner {} expired — re-login {}", describeFailure(response), ownerSession.getUser());
        SymmetricKeySession fresh = reloginOwner(ownerSession.getUser());
        return workspaceId == null
                ? ENCRYPTED_API.post(endpoint, body, fresh)
                : ENCRYPTED_API.post(endpoint, body, fresh, workspaceId);
    }

    private static Object workspaceHeader(Long workspaceId) {
        return isRealWorkspace(workspaceId) ? workspaceId : -1;
    }

    private static boolean isRealWorkspace(Long workspaceId) {
        return workspaceId != null && workspaceId != -1L && workspaceId != 0L;
    }

    private record FoundGroup(long groupId, Long workspaceId) {
    }
}
