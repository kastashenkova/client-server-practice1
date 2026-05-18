package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class EncrypterTest {

    private static final MessageCipher messageCipher = new MessageCipher();
    private static final Encrypter SUT = new Encrypter(messageCipher);
    private static final String testMessageText = "Hello World!";
    private static final Message TEST_MESSAGE = new Message((byte) 0x12,
            128L,
            4,
            67,
            testMessageText);
    private static final byte[] TEST_PACKET = SUT.encrypt(TEST_MESSAGE);

    @Test
    void shouldSetMagicByte() {
        assertEquals((byte) 0x13, TEST_PACKET[0]);
    }

    @Test
    void shouldSetUniqueIdentifier() {
        assertEquals((byte) 0x12, TEST_PACKET[1]);
    }

    @Test
    void shouldSetMessageNumber() {
        ByteBuffer buffer = ByteBuffer.wrap(TEST_PACKET);
        buffer.position(2);
        assertEquals(128L, buffer.getLong());
    }

    @Test
    void shouldProduceDifferentPacketsForSameMessage() {
        byte[] first  = SUT.encrypt(TEST_MESSAGE);
        byte[] second = SUT.encrypt(TEST_MESSAGE);
        assertFalse(Arrays.equals(first, second));
    }
}

