package practice2.crypt;

import org.example.practice1.Decrypter;
import org.example.practice1.MessageCipher;

public class Decriptor implements Runnable {

    private static final MessageCipher messageCipher = new MessageCipher();

    void decript(byte[] message){
        Decrypter decrypter = new Decrypter(messageCipher);
        decrypter.decrypt(message);
    }

    @Override
    public void run() {

    }
}
