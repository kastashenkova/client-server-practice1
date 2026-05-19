package org.example.practice2.processor;

import org.example.practice1.Message;

public class Processor implements Runnable {

    void process(Message message) {
        System.out.println(HttpStatusCode.OK);
    }

    @Override
    public void run() {

    }
}
