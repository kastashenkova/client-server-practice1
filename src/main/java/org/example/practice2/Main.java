package org.example.practice2;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Scaling scaling = new Scaling(2, 2, 4, 3, 5);
        scaling.start();

        Thread.sleep(5000);
        scaling.stop();
    }

}
