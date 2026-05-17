package org.example;

import java.nio.ByteBuffer;

public class Decrypter {

    public Message decrypt(byte[] messageToDecrypt){
        ByteBuffer buffer = ByteBuffer.wrap(messageToDecrypt);
        byte magicByte = buffer.get();
        byte uniqueIdentifierByte = buffer.get();
        long messageNumber = buffer.getLong();
        int wlen = buffer.getInt();
        short firstCrc = buffer.getShort();

        short checkSum = Crc16.calculateCrc(messageToDecrypt, 0, 14);
        validateChecksum(checkSum, firstCrc);

        int commandId = buffer.getInt();
        int userId = buffer.getInt();

        byte[] payload = new byte[wlen];
        buffer.get(16, payload, 0, wlen);

        short secondCrc = buffer.getShort(16 + wlen);
        short checkSum2 = Crc16.calculateCrc(payload);
        validateChecksum(checkSum2, secondCrc);

        return new Message(uniqueIdentifierByte, messageNumber, commandId, userId, new String(payload, 8, wlen - 8));
    }

    private void validateChecksum(short checkSum, short secondCrc){
        if(checkSum != secondCrc){
            throw new IllegalArgumentException("Checksum does not match");
        }
    }
}
