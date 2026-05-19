package org.example.practice2;

import org.example.practice1.Message;
import org.example.practice2.crypt.Decriptor;
import org.example.practice2.crypt.Encriptor;
import org.example.practice2.processor.Processor;
import org.example.practice2.receiver.Receiver;
import org.example.practice2.receiver.ReceiverImpl;
import org.example.practice2.sender.Sender;
import org.example.practice2.sender.SenderImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static final Receiver receiver = new ReceiverImpl();
    public static final Processor processor = new Processor();
    public static final Sender sender = new SenderImpl();
    public static final Encriptor encriptor = new Encriptor();
    public static final Decriptor decriptor = new Decriptor();

    public static final ExecutorService executorService
            = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {
        SharedQueue<byte[]> byteQueue = new SharedQueue<>();
        SharedQueue<Message> messageQueue = new SharedQueue<>();

        executorService.submit(receiver);
        executorService.submit(decriptor);
        executorService.submit(processor);
        executorService.submit(encriptor);
        executorService.submit(sender);

        executorService.shutdown();
    }

}
