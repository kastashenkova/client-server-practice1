package org.example;

import java.nio.ByteBuffer;

public class Encrypter {

    public byte[] encrypt(Message message){
        ByteBuffer buffer = ByteBuffer.allocate(
                1 + 1 + 8 + 4 + 2 + 4 + 4
                        + message.getMessageString().getBytes().length + 2);
        buffer.put((byte) 0x13);
        buffer.put(message.getUniqueIdentifier());
        buffer.putLong(message.getMessageNumber());
        buffer.putInt(message.getMessageString().getBytes().length + 4 + 4);

        // 1st Crc
        byte[] header = new byte[14];
        buffer.get(0, header, 0, 14);
        buffer.putShort(Crc16.calculateCrc(header));

        // 2nd table
        buffer.putInt(message.getCommandId());
        buffer.putInt(message.getUserId());
        buffer.put(message.getMessageString().getBytes());

        // 2nd Crc
        byte[] payload = new byte[4 + 4 +message.getMessageString().getBytes().length];
        buffer.get(16, payload, 0, payload.length);
        buffer.putShort(Crc16.calculateCrc(payload));

        return buffer.array();
    }
}
