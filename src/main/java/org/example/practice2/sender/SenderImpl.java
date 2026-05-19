package org.example.practice2.sender;

import java.net.InetAddress;

public class SenderImpl implements Sender {

    @Override
    public void sendMessage(byte[] message, InetAddress target) {
        System.out.println("Sending message <<" + new String(message)
                + ">> to address " + target.getHostAddress() + "...");
    }

    @Override
    public void run() {

    }
}
