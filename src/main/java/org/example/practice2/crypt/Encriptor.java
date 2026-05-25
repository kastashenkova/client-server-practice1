package org.example.practice2.crypt;

import org.example.practice1.Encrypter;
import org.example.practice1.Message;
import org.example.practice1.MessageCipher;
import org.example.practice2.SharedQueue;

public class Encriptor implements Runnable {
    private final Encrypter encrypter = new Encrypter(new MessageCipher());
    private final SharedQueue<Message> inputQueue;
    private final SharedQueue<byte[]> outputQueue;
    private volatile boolean active = true;

    public Encriptor(SharedQueue<Message> inputQueue, SharedQueue<byte[]> outputQueue) {
        this.inputQueue  = inputQueue;
        this.outputQueue = outputQueue;
    }

    public byte[] encrypt(Message message) {
        return encrypter.encrypt(message);
    }

    @Override
    public void run() {
        while (active && !Thread.currentThread().isInterrupted()) {
            try {
                Message message = inputQueue.consume();
                outputQueue.produce(encrypt(message));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() {
        active = false;
    }
}
