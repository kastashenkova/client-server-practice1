package org.example;

import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncrypterTest {

    private static final Encrypter SUT = new Encrypter();

    @Test
    void shouldEncryptMessage() {
        Message message = new Message((byte) 0x12, 128, 4, 67, "Hello World!");

        assertEquals("1312000000000000008000000014d02a000000040000004348656c6c6f20576f726c64219507", Hex.encodeHexString(SUT.encrypt(message)));
    }
}
