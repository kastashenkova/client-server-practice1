package practice2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.practice1.Encrypter;
import org.example.practice1.Message;
import org.example.practice1.MessageCipher;
import org.example.practice2.Scaling;
import org.example.practice2.SharedQueue;
import org.example.practice2.warehouse.WarehouseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MultithreadMessageSendingTest {
    private Scaling scaling;

    @BeforeEach
    void setUp() {
        scaling = new Scaling(2, 2, 4, 3, 5);
        scaling.start();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        scaling.stop();
    }

    @Test
    void concurrentAddStock_shouldNotLoseUpdates() throws Exception {
        WarehouseService warehouse = scaling.getWarehouseService();
        int threadCount = 20;
        int addPerThread = 10;

        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                warehouse.addProducts("rice", addPerThread);
                latch.countDown();
            });
        }

        pool.shutdown();

        pool.awaitTermination(5, TimeUnit.SECONDS);
        latch.await(5, TimeUnit.SECONDS);

        assertEquals(threadCount * addPerThread, warehouse.getStock("rice"));
    }

    @Test
    void concurrentDeductStock_shouldNotGoNegative() throws Exception {
        WarehouseService warehouse = scaling.getWarehouseService();
        warehouse.addProducts("buckwheat", 50);

        int threadCount = 30;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                warehouse.deductProducts("buckwheat", 5);
                latch.countDown();
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);

        assertTrue(warehouse.getStock("buckwheat") >= 0,
                "Stock must never be negative");
    }

    @Test
    void fullPipeline_concurrentMessages_processedWithoutErrors() throws Exception {
        Encrypter encrypter = new Encrypter(new MessageCipher());
        SharedQueue<byte[]> rawQueue = scaling.getRawQueue();

        int threadCount = 10;
        int messagesPerThread = 5;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> futures = new ArrayList<>();

        for (int t = 0; t < threadCount; t++) {
            futures.add(pool.submit(() -> {
                for (int m = 0; m < messagesPerThread; m++) {
                    Message msg = new Message(
                            (byte) 0x01, System.nanoTime(),
                            3,
                            1,
                            "pasta:10");
                    try {
                        rawQueue.produce(encrypter.encrypt(msg));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }));
        }

        for (Future<?> f : futures) {
            f.get(10, TimeUnit.SECONDS);
        }
        pool.shutdown();

        WarehouseService warehouse = scaling.getWarehouseService();
        int last = 0;
        int sameCount = 0;

        while (sameCount < 5) {
            int current = warehouse.getStock("pasta");

            if (current == last) {
                sameCount++;
            } else {
                sameCount = 0;
                last = current;
            }

            Thread.sleep(50);
        }

        int expected = threadCount * messagesPerThread * 10; // 500
        assertEquals(expected, warehouse.getStock("pasta"),
                "All ADD_PRODUCTS commands must be processed exactly once");
    }

    // race condition test
    @Test
    void concurrentAddAndDeduct_stockRemainsConsistent() throws Exception {
        WarehouseService warehouse = scaling.getWarehouseService();
        warehouse.addProducts("oats", 1000);

        int threadCount = 50;
        AtomicInteger netChange = new AtomicInteger(0);
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            boolean add = (i % 2 == 0);
            pool.submit(() -> {
                if (add) {
                    warehouse.addProducts("oats", 5);
                    netChange.addAndGet(5);
                } else {
                    warehouse.deductProducts("oats", 3);
                    netChange.addAndGet(-3);
                }
                latch.countDown();
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        pool.shutdown();

        int expected = Math.max(0, 1000 + netChange.get());
        assertEquals(expected, warehouse.getStock("oats"));
    }
}
