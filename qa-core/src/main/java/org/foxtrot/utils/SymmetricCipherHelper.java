package org.foxtrot.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

/**
 * Симметричное шифрование/расшифровка payload для REST API (GOST3412-2015/ECB/PKCS7Padding).
 */
public final class SymmetricCipherHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String KEY_ALGORITHM = "GOST3412-2015";

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private SymmetricCipherHelper() {}

    public static String encryptJson(String plainJson, SymmetricKeyMaterial key) {
        try {
            Cipher cipher = createCipher(key, Cipher.ENCRYPT_MODE);
            byte[] encrypted = cipher.doFinal(plainJson.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось зашифровать JSON payload", e);
        }
    }

    public static String decryptResponseBody(String responseBody, SymmetricKeyMaterial key) {
        if (responseBody == null || responseBody.isBlank()) {
            return responseBody;
        }
        try {
            JsonNode root = MAPPER.readTree(responseBody);
            if (!root.has("content") || root.get("content").isNull()) {
                return responseBody;
            }
            String encrypted = root.get("content").asText();
            Cipher cipher = createCipher(key, Cipher.DECRYPT_MODE);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось расшифровать response body", e);
        }
    }

    private static Cipher createCipher(SymmetricKeyMaterial key, int mode) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key.getKeyBase64());
        Cipher cipher = Cipher.getInstance(key.getTransformation(), BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(mode, new SecretKeySpec(keyBytes, KEY_ALGORITHM));
        return cipher;
    }
}
