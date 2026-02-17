package org.example.config;
import org.example.utils.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Encryptor {
    private static final String ALGORITHM       = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM   = "AES";
    private static final String KDF_ALGORITHM   = "PBKDF2WithHmacSHA256";
    private static final byte[] SALT            = "adb_automation_salt".getBytes();
    private static final int    ITERATIONS      = 100_000;
    private static final int    KEY_LENGTH      = 256; 
    private static final int    IV_LENGTH       = 16;  
    private static Encryptor instance;
    private SecretKeySpec secretKey;

    private Encryptor() {
        setupEncryption();
    }
    public static synchronized Encryptor getInstance() {
        if (instance == null) {
            instance = new Encryptor();
        }
        return instance;
    }

    private void setupEncryption() {
        try {
            String encryptionKey = Settings.ENCRYPTION_KEY;
            if (encryptionKey == null || encryptionKey.isEmpty()) {
                throw new IllegalStateException("ENCRYPTION_KEY is not set in environment variables");
            }
            PBEKeySpec spec = new PBEKeySpec(
                    encryptionKey.toCharArray(),
                    SALT,
                    ITERATIONS,
                    KEY_LENGTH
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KDF_ALGORITHM);
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            this.secretKey = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            Logger.getInstance().debug("Encryption initialized successfully");
        } catch (Exception e) {
            Logger.getInstance().error("Failed to initialize encryption: " + e.getMessage());
            throw new RuntimeException("Failed to initialize encryption", e);
        }
    }

    public String encrypt(String data) {
        if (data == null || data.isEmpty()) return "";
        try {
            
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));
            
            ByteBuffer buffer = ByteBuffer.allocate(iv.length + encryptedBytes.length);
            buffer.put(iv);
            buffer.put(encryptedBytes);
            return Base64.getUrlEncoder().encodeToString(buffer.array());
        } catch (Exception e) {
            Logger.getInstance().error("Encryption failed: " + e.getMessage());
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) return "";
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(encryptedData);
            
            ByteBuffer buffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);
            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(ciphertext);
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            Logger.getInstance().error("Decryption failed: " + e.getMessage());
            throw new RuntimeException("Decryption failed", e);
        }
    }

    public Map<String, Object> encryptMap(Map<String, Object> data, List<String> keysToEncrypt) {
        Map<String, Object> result = new HashMap<>(data);
        for (String key : keysToEncrypt) {
            Object value = result.get(key);
            if (value != null && !value.toString().isEmpty()) {
                result.put(key, encrypt(value.toString()));
            }
        }
        return result;
    }
    public Map<String, Object> decryptMap(Map<String, Object> data, List<String> keysToDecrypt) {
        Map<String, Object> result = new HashMap<>(data);
        for (String key : keysToDecrypt) {
            Object value = result.get(key);
            if (value != null && !value.toString().isEmpty()) {
                result.put(key, decrypt(value.toString()));
            }
        }
        return result;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }
}