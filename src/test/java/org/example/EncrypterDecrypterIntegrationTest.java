package org.example;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

public class EncrypterDecrypterIntegrationTest {

    private static final MessageCipher messageCipher = new MessageCipher();
    private static final Encrypter encrypter = new Encrypter(messageCipher);
    private static final Decrypter decrypter = new Decrypter(messageCipher);

    @Test
    void shouldEncryptAndDecryptMessage() {
        assertRoundTrip(new Message((byte) 0x12,
                128L,
                4,
                67,
                "Hello World!"));
    }

    @Test
    void shouldHandleEmptyMessage() {
        assertRoundTrip(new Message((byte) 0x01,
                1L,
                1,
                1,
                ""));
    }

    @Test
    void shouldHandleUnicodeMessage() {
        assertRoundTrip(new Message((byte) 0x12,
                128L,
                4,
                67,
                "Привіт Світ!"));
    }

    @Test
    void shouldHandleMaxValues() {
        assertRoundTrip(new Message((byte) 0xFF,
                Long.MAX_VALUE,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE,
                "max"));
    }

    // helper
    private void assertRoundTrip(Message original) {
        byte[] encrypted = encrypter.encrypt(original);
        Message decrypted = decrypter.decrypt(encrypted);

        assertThat(decrypted)
                .returns(original.getUniqueIdentifier(), Message::getUniqueIdentifier)
                .returns(original.getMessageNumber(), Message::getMessageNumber)
                .returns(original.getCommandId(), Message::getCommandId)
                .returns(original.getUserId(), Message::getUserId)
                .returns(original.getMessageString(), Message::getMessageString);
    }
}
