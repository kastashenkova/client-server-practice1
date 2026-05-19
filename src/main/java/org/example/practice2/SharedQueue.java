package org.example.practice2;

import java.util.LinkedList;
import java.util.Queue;

public class SharedQueue<T> {
    private static final int CAPACITY = 5;

    private final Queue<T> queue = new LinkedList<>();

    public synchronized void produce(T value) throws InterruptedException {
        while (queue.size() == CAPACITY) {
            wait();
        }
        queue.add(value);
        notifyAll();
    }

    public synchronized T consume() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        T value = queue.poll();
        notifyAll();
        return value;
    }
}