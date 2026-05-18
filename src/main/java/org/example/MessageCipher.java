package org.example;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

// using Jakob Jenkov's tutorial
public class MessageCipher {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding"; // Cipher Block Chaining

    private static final byte[] SECRET_KEY =
            System.getenv("SECRET_KEY").getBytes(StandardCharsets.UTF_8);

    private final SecretKeySpec keySpec;

    public MessageCipher() {
        this.keySpec = new SecretKeySpec(SECRET_KEY, "AES");
    }

    public byte[] encrypt(byte[] plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[16]; // Initialization Vector for same messages giving different cipher text
            new SecureRandom().nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(plainText);
            byte[] result = new byte[16 + encrypted.length];
            System.arraycopy(iv, 0, result, 0, 16);
            System.arraycopy(encrypted, 0, result, 16, encrypted.length);
            return result;
        } catch (Exception e) {
            throw new MessageCipherException("Encryption failed for text: "
                    + Arrays.toString(plainText));
        }
    }

    public byte[] decrypt(byte[] cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = Arrays.copyOfRange(cipherText, 0, 16);
            byte[] encryptedText = Arrays.copyOfRange(cipherText, 16, cipherText.length);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
            return cipher.doFinal(encryptedText);
        } catch (Exception e) {
            throw new MessageCipherException("Decryption failed for message: "
                    + Arrays.toString(cipherText));
        }
    }
}
