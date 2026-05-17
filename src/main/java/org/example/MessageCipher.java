package org.example;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

// using Jakob Jenkov's tutorial
// Electronic CodeBook
public class MessageCipher {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    private static final byte[] SECRET_KEY =
            "MyS3cr3tK3y1234!".getBytes(StandardCharsets.UTF_8);

    private final SecretKeySpec keySpec;

    public MessageCipher() {
        this.keySpec = new SecretKeySpec(SECRET_KEY, "AES");
    }

    public byte[] encrypt(byte[] plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return cipher.doFinal(plainText);
        } catch (Exception e) {
            throw new MessageCipherException("Encryption failed for text: "
                    + Arrays.toString(plainText));
        }
    }

    public byte[] decrypt(byte[] cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            throw new MessageCipherException("Decryption failed for message: "
                    + Arrays.toString(cipherText));
        }
    }
}
