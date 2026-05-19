package org.example.practice1;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class DecrypterTest {

    private static final MessageCipher messageCipher = new MessageCipher();
    private static final Decrypter SUT = new Decrypter(messageCipher);
    private static final Encrypter encrypter = new Encrypter(messageCipher);
    private static final String testMessageText = "Astashenkova";
    private static final Message TEST_MESSAGE = new Message((byte) 0x12,
            128L,
            4,
            67,
            testMessageText);
    private static final byte[] TEST_PACKET = encrypter.encrypt(TEST_MESSAGE);

    @Test
    void shouldDecryptMessage() {
        Message actual = SUT.decrypt(TEST_PACKET);

        assertThat(actual)
                .returns((byte) 0x12, Message::uniqueIdentifier)
                .returns(128L, Message::messageNumber)
                .returns(4, Message::commandId)
                .returns(67, Message::userId)
                .returns(testMessageText, Message::messageString);
    }

    @Test
    void shouldThrowOnInvalidMagicByte() {
        byte[] packet = TEST_PACKET.clone();
        packet[0] = 0x00;

        assertThatThrownBy(() -> SUT.decrypt(packet))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowOnCorruptedHeader() {
        byte[] packet = TEST_PACKET.clone();
        packet[3] ^= (byte) 0xFF;

        assertThatThrownBy(() -> SUT.decrypt(packet))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowOnCorruptedPayload() {
        byte[] packet = TEST_PACKET.clone();
        packet[20] ^= (byte) 0xFF; // corrupt userId field inside payload (offset 20)

        assertThatThrownBy(() -> SUT.decrypt(packet))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
