package org.example.practice2.sender;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.example.practice2.SharedQueue;

public class SenderImpl implements Sender {
    private final SharedQueue<byte[]> inputQueue;
    private volatile boolean active = true;

    public SenderImpl(SharedQueue<byte[]> inputQueue) {
        this.inputQueue = inputQueue;
    }

    @Override
    public void sendMessage(byte[] message, InetAddress target) {
        System.out.printf("[SEND] %d bytes to %s%n",
                message.length, target.getHostAddress());
    }

    @Override
    public void run() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            while (active && !Thread.currentThread().isInterrupted()) {
                try {
                    byte[] data = inputQueue.consume();
                    sendMessage(data, localhost);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unknown host exception", e);
        }
    }

    @Override
    public void stop() {
        active = false;
    }
}
