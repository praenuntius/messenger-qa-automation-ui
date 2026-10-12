package org.foxtrot.utils.email;

import io.qameta.allure.Step;
import jakarta.mail.Address;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.config.EmailConfig;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class ImapEmailClient {

    private static final Pattern WORKSPACE_INVITE_LINK = Pattern.compile(
            "https://web-preprod\\.tty\\.chat/link/invite/[\\w-]+"
    );
    private static final int WAIT_TIMEOUT_SECONDS = 60;
    private static final int POLL_INTERVAL_SECONDS = 5;

    private final EmailConfig config = ConfigFactory.create(EmailConfig.class);

    /**
     * Метод создает уникальный email-адрес для тестов. - "Плюсовая адресация"
     * Письма на такой адрес всё равно придут на основной ящик, но мы можем удобно фильтровать по получателю
     */
    public static String uniqueAddress(String baseEmail) {
        int atIndex = baseEmail.indexOf('@');
        String tag = UUID.randomUUID().toString().substring(0, 8);
        return baseEmail.substring(0, atIndex) + "+" + tag + baseEmail.substring(atIndex);
    }

    /**
     * Основной метод
     * Периодически опрашивает почтовый ящик через IMAP, пока не найдёт ссылку.
     */
    @Step("Получить ссылку-приглашение из письма, отправленного на {recipientEmail}")
    public String waitForWorkspaceInviteLink(String recipientEmail) {
        return await()
                .atMost(WAIT_TIMEOUT_SECONDS, SECONDS)
                .pollInterval(POLL_INTERVAL_SECONDS, SECONDS)
                .until(() -> findInviteLink(recipientEmail), link -> link != null && !link.isBlank());
    }

    /**
     * Однократно подключается к IMAP и ищет invite-link в INBOX.
     * Письма перебираются от новых к старым; учитываются только те, где в поле «Кому» указан {@code recipientEmail}.
     */
    private String findInviteLink(String recipientEmail) throws Exception {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", config.imapHost());
        properties.put("mail.imaps.port", String.valueOf(config.imapPort()));
        properties.put("mail.imaps.ssl.enable", "true");

        Store store = Session.getInstance(properties).getStore("imaps");
        store.connect(config.imapHost(), config.imapPort(), config.imapUser(), config.imapPassword());

        Folder inbox = store.getFolder("INBOX");
        try {
            inbox.open(Folder.READ_ONLY);

            for (int i = inbox.getMessageCount(); i >= 1; i--) {
                Message message = inbox.getMessage(i);
                Address[] to = message.getRecipients(Message.RecipientType.TO);
                if (to == null || Arrays.stream(to).map(Address::toString).noneMatch(recipientEmail::equalsIgnoreCase)) {
                    continue;
                }

                String inviteLink = extractInviteLink(extractContent(message));
                if (inviteLink != null) {
                    return inviteLink;
                }
            }
            return null;
        } finally {
            if (inbox.isOpen()) {
                inbox.close(false);
            }
            if (store.isConnected()) {
                store.close();
            }
        }
    }

    /**
     * Рекурсивно извлекает текстовое содержимое части письма.
     * Поддерживает {@code text/plain}, {@code text/html} и вложенные {@code multipart/*} — тело собирается из всех частей.
     */
    private String extractContent(Part part) throws Exception {
        if (part.isMimeType("text/plain") || part.isMimeType("text/html")) {
            Object content = part.getContent();
            return content != null ? content.toString() : "";
        }

        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                builder.append(extractContent(multipart.getBodyPart(i)));
            }
            return builder.toString();
        }
        return "";
    }

    /**
     * Ищет в тексте письма ссылку-приглашение в workspace по шаблону {@link #WORKSPACE_INVITE_LINK}.
     * Возвращает первое совпадение или {@code null}, если ссылка не найдена либо контент пустой.
     */
    private String extractInviteLink(String content) {
        if (content == null || content.isBlank()) {
            return null;
        }
        var matcher = WORKSPACE_INVITE_LINK.matcher(content);
        return matcher.find() ? matcher.group() : null;
    }
}
