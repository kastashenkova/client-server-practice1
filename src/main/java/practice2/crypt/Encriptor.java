package practice2.crypt;

import org.example.practice1.Encrypter;
import org.example.practice1.Message;
import org.example.practice1.MessageCipher;

public class Encriptor implements Runnable {

    private static final MessageCipher messageCipher = new MessageCipher();

    byte[] encrypt(Message message){
        Encrypter encrypter = new Encrypter(messageCipher);
        return encrypter.encrypt(message);
    }

    @Override
    public void run() {

    }
}
