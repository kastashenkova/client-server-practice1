package org.example.practice2.receiver;

import java.util.UUID;

public class ReceiverImpl implements Receiver {

    @Override
    public void receiveMessage() {
        String message = UUID.randomUUID().toString();
    }

    @Override
    public void run() {

    }
}
