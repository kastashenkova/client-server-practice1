package org.example.practice1;

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
                "Astashenkova"));
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
                "Асташенкова"));
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
                .returns(original.uniqueIdentifier(), Message::uniqueIdentifier)
                .returns(original.messageNumber(), Message::messageNumber)
                .returns(original.commandId(), Message::commandId)
                .returns(original.userId(), Message::userId)
                .returns(original.messageString(), Message::messageString);
    }
}
