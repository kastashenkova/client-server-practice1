package org.example;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

public class DecrypterTest {

    private static final Decrypter SUT = new Decrypter();

    @Test
    void shouldDecryptMessage() throws DecoderException {
        Message actual = SUT.decrypt(Hex.decodeHex("1312000000000000008000000014d02a000000040000004348656c6c6f20576f726c64219507"));

        assertThat(actual)
                .returns((byte) 0x12, Message::getUniqueIdentifier)
                .returns(128L, Message::getMessageNumber)
                .returns(4, Message::getCommandId);
    }
}
