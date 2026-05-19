package org.example.practice2.sender;

import java.net.InetAddress;

public interface Sender extends Runnable {

    void sendMessage(byte[] message, InetAddress target);
}
