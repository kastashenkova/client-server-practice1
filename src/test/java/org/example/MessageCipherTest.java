package org.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class MessageCipherTest {

    private static final MessageCipher messageCipher = new MessageCipher();
    private static final String testPlainText = "Astashenkova";

    @Test
    void shouldEncryptMessageText() {
        byte[] plainText = testPlainText.getBytes(StandardCharsets.UTF_8);
        byte[] actual = messageCipher.encrypt(plainText);

        assertThat(actual).isNotNull();
        assertThat(actual.length).isGreaterThan(16);
        assertThat(actual).isNotEqualTo(plainText);
    }

    @Test
    void shouldDecryptMessageText() {
        byte[] plainText = testPlainText.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedText = messageCipher.encrypt(plainText);
        byte[] decryptedText = messageCipher.decrypt(encryptedText);

        assertThat(decryptedText).isNotNull();
        assertThat(decryptedText).isEqualTo(plainText);
    }

    // sanity check
    @Test
    void shouldProduceDifferentEncryptionForSameMessage() {
        byte[] plainText = testPlainText.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedText = messageCipher.encrypt(plainText);

        assertThat(encryptedText).isNotNull();
        assertThat(encryptedText).isNotEqualTo(messageCipher.encrypt(plainText));
    }

    @Test
    void shouldThrowExceptionForTruncatedCipherText() {
        // shorter than 16 byte
        byte[] truncated = new byte[8];

        assertThatThrownBy(() -> messageCipher.decrypt(truncated))
                .isInstanceOf(MessageCipherException.class);
    }
}
