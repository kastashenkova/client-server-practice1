package org.example.practice2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.example.practice1.Message;
import org.example.practice2.crypt.Decriptor;
import org.example.practice2.crypt.Encriptor;
import org.example.practice2.processor.Processor;
import org.example.practice2.receiver.Receiver;
import org.example.practice2.receiver.ReceiverImpl;
import org.example.practice2.sender.Sender;
import org.example.practice2.sender.SenderImpl;
import org.example.practice2.warehouse.WarehouseService;

public class Scaling {
    private final SharedQueue<byte[]> rawQueue  = new SharedQueue<>();
    private final SharedQueue<Message> messageQueue = new SharedQueue<>();
    private final SharedQueue<Message> responseQueue = new SharedQueue<>();
    private final SharedQueue<byte[]> sendQueue = new SharedQueue<>();

    private final WarehouseService warehouseService = new WarehouseService();
    private final ExecutorService executor;

    private final List<Receiver> receivers = new ArrayList<>();
    private final List<Decriptor> decriptors = new ArrayList<>();
    private final List<Processor> processors = new ArrayList<>();
    private final List<Encriptor> encriptors = new ArrayList<>();
    private final List<Sender> senders = new ArrayList<>();

    public Scaling(int receiverCount,
                   int decriptorCount,
                   int processorCount,
                   int encriptorCount,
                   int senderCount) {

        int total = receiverCount + decriptorCount + processorCount + encriptorCount + senderCount;
        this.executor = Executors.newFixedThreadPool(total);

        for (int i = 0; i < receiverCount; i++) {
            receivers .add(new ReceiverImpl(rawQueue));
        }
        for (int i = 0; i < decriptorCount; i++) {
            decriptors.add(new Decriptor(rawQueue, messageQueue));
        }
        for (int i = 0; i < processorCount; i++) {
            processors.add(new Processor(messageQueue, responseQueue, warehouseService));
        }
        for (int i = 0; i < encriptorCount; i++) {
            encriptors.add(new Encriptor(responseQueue, sendQueue));
        }
        for (int i = 0; i < senderCount; i++) {
            senders.add(new SenderImpl(sendQueue));
        }
    }

    public void start() {
        receivers.forEach(executor::submit);
        decriptors.forEach(executor::submit);
        processors.forEach(executor::submit);
        encriptors.forEach(executor::submit);
        senders.forEach(executor::submit);
        System.out.println("Pipeline started!");
    }

    public void stop() throws InterruptedException {
        receivers.forEach(Receiver::stop);

        rawQueue.shutdown();
        messageQueue.shutdown();
        responseQueue.shutdown();
        sendQueue.shutdown();

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        decriptors.forEach(Decriptor::stop);
        processors.forEach(Processor::stop);
        encriptors.forEach(Encriptor::stop);
        senders.forEach(Sender::stop);

        System.out.println("Pipeline stopped!");
    }

    public SharedQueue<byte[]> getRawQueue() {
        return rawQueue;
    }

    public WarehouseService getWarehouseService() {
        return warehouseService;
    }
}
