package org.foxtrot.utils;

import org.foxtrot.api.dto.encryption.GetPublicKeyResponse;
import org.foxtrot.api.dto.encryption.SaveSymmetricKeyRequest;
import org.foxtrot.api.dto.encryption.SaveSymmetricKeyResponse;
import org.foxtrot.api.service.EncryptionApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Вспомогательный класс для настройки шифрования:
 * 1) получаем RSA-публичный ключ сервера;
 * 2) генерируем два симметричных ключа (основной и файловый);
 * 3) шифруем их RSA и сохраняем через /encryption/saveSymmetricKey;
 * 4) возвращаем JSON для localStorage.encryptKey и id основного ключа.
 */
public class EncryptionHelper {

    private static final Logger logger = LoggerFactory.getLogger(EncryptionHelper.class);
    private static final String SYMMETRIC_TRANSFORMATION = "GOST3412-2015/ECB/PKCS7Padding";

    private final EncryptionApiService encryptionApiService = new EncryptionApiService();
    private final SecureRandom secureRandom = new SecureRandom();

    public EncryptKeyInfo setupEncryptionKeys() {
        GetPublicKeyResponse publicKeyResponse = encryptionApiService.getPublicKey();
        logger.info("Получен публичный ключ шифрования: id={}, keySize={}, transformation={}",
                publicKeyResponse.getId(), publicKeyResponse.getKeySize(), publicKeyResponse.getTransformation());

        String symmetricKey = generateSymmetricKeyBase64();
        String symmetricFileKey = generateSymmetricKeyBase64();

        String encryptedKey = encryptRsa(symmetricKey, publicKeyResponse.getKey());
        String encryptedFileKey = encryptRsa(symmetricFileKey, publicKeyResponse.getKey());

        SaveSymmetricKeyResponse keyResponse = encryptionApiService.saveSymmetricKey(
                new SaveSymmetricKeyRequest(encryptedKey, SYMMETRIC_TRANSFORMATION, publicKeyResponse.getId())
        );
        SaveSymmetricKeyResponse fileKeyResponse = encryptionApiService.saveSymmetricKey(
                new SaveSymmetricKeyRequest(encryptedFileKey, SYMMETRIC_TRANSFORMATION, publicKeyResponse.getId())
        );

        logger.info("Симметричные ключи сохранены: encryptKeyId={}, encryptFileKeyId={}",
                keyResponse.getId(), fileKeyResponse.getId());

        SymmetricKeyMaterial messageKey = new SymmetricKeyMaterial(
                keyResponse.getId(), symmetricKey, keyResponse.getTransformation()
        );
        SymmetricKeyMaterial fileKey = new SymmetricKeyMaterial(
                fileKeyResponse.getId(), symmetricFileKey, fileKeyResponse.getTransformation()
        );

        String encryptKeyJson = buildEncryptKeyJson(messageKey, fileKey);

        EncryptKeyInfo info = new EncryptKeyInfo();
        info.setMessageKey(messageKey);
        info.setFileKey(fileKey);
        info.setEncryptionKeyId(messageKey.getId());
        info.setEncryptKeyJson(encryptKeyJson);
        return info;
    }

    /**
     * Генерирует новый симметричный ключ и сохраняет его на сервере (для ротации).
     */
    public SymmetricKeyMaterial generateAndSaveSymmetricKey() {
        return generateAndSaveSymmetricKey(null, null);
    }

    /**
     * Генерирует и сохраняет ключ. При ротации передаётся accessToken и текущий encryptionKeyId,
     * чтобы ключ был привязан к сессии пользователя.
     */
    public SymmetricKeyMaterial generateAndSaveSymmetricKey(String accessToken, Long currentEncryptionKeyId) {
        GetPublicKeyResponse publicKeyResponse = encryptionApiService.getPublicKey();
        String symmetricKey = generateSymmetricKeyBase64();
        String encryptedKey = encryptRsa(symmetricKey, publicKeyResponse.getKey());
        SaveSymmetricKeyRequest request = new SaveSymmetricKeyRequest(
                encryptedKey, SYMMETRIC_TRANSFORMATION, publicKeyResponse.getId()
        );

        SaveSymmetricKeyResponse keyResponse = accessToken != null && currentEncryptionKeyId != null
                ? encryptionApiService.saveSymmetricKey(request, accessToken, currentEncryptionKeyId)
                : encryptionApiService.saveSymmetricKey(request);

        return new SymmetricKeyMaterial(keyResponse.getId(), symmetricKey, keyResponse.getTransformation());
    }

    private String generateSymmetricKeyBase64() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private String encryptRsa(String plainBase64Key, String publicKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(plainBase64Key);
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = cipher.doFinal(keyBytes);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось зашифровать симметричный ключ RSA", e);
        }
    }

    private String buildEncryptKeyJson(SymmetricKeyMaterial messageKey, SymmetricKeyMaterial fileKey) {
        String safeKey = escapeJson(messageKey.getKeyBase64());
        String safeFileKey = escapeJson(fileKey.getKeyBase64());
        String safeTrans = messageKey.getTransformation() != null ? escapeJson(messageKey.getTransformation()) : "";
        String safeFileTrans = fileKey.getTransformation() != null ? escapeJson(fileKey.getTransformation()) : "";

        return String.format(
                "{\"encryptKey\":{\"id\":%d,\"key\":\"%s\",\"transformation\":\"%s\"}," +
                        "\"encryptFileKey\":{\"id\":%d,\"key\":\"%s\",\"transformation\":\"%s\"}}",
                messageKey.getId(), safeKey, safeTrans,
                fileKey.getId(), safeFileKey, safeFileTrans
        );
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    /**
     * Результат настройки ключей шифрования для пользователя.
     */
    public static class EncryptKeyInfo {
        private long encryptionKeyId;
        private String encryptKeyJson;
        private SymmetricKeyMaterial messageKey;
        private SymmetricKeyMaterial fileKey;

        public long getEncryptionKeyId() {
            return encryptionKeyId;
        }

        public void setEncryptionKeyId(long encryptionKeyId) {
            this.encryptionKeyId = encryptionKeyId;
        }

        public String getEncryptKeyJson() {
            return encryptKeyJson;
        }

        public void setEncryptKeyJson(String encryptKeyJson) {
            this.encryptKeyJson = encryptKeyJson;
        }

        public SymmetricKeyMaterial getMessageKey() {
            return messageKey;
        }

        public void setMessageKey(SymmetricKeyMaterial messageKey) {
            this.messageKey = messageKey;
        }

        public SymmetricKeyMaterial getFileKey() {
            return fileKey;
        }

        public void setFileKey(SymmetricKeyMaterial fileKey) {
            this.fileKey = fileKey;
        }
    }
}
