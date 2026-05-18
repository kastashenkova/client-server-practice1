package org.example;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.apache.commons.codec.DecoderException;
import org.junit.jupiter.api.Test;

public class DecrypterTest {

    private static final MessageCipher messageCipher = new MessageCipher();
    private static final Decrypter SUT = new Decrypter(messageCipher);
    private static final Encrypter encrypter = new Encrypter(messageCipher);
    private static final String testMessageText = "Hello World!";
    private static final Message TEST_MESSAGE = new Message((byte) 0x12,
            128L,
            4,
            67,
            testMessageText);
    private static final byte[] TEST_PACKET = encrypter.encrypt(TEST_MESSAGE);

    @Test
    void shouldDecryptMessage() throws DecoderException {
        Message actual = SUT.decrypt(TEST_PACKET);

        assertThat(actual)
                .returns((byte) 0x12, Message::getUniqueIdentifier)
                .returns(128L, Message::getMessageNumber)
                .returns(4, Message::getCommandId)
                .returns(67, Message::getUserId)
                .returns(testMessageText, Message::getMessageString);
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
        packet[20] ^= (byte) 0xFF;

        assertThatThrownBy(() -> SUT.decrypt(packet))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
