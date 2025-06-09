package org.example.update;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NewsUpdateSchedulerTest {

    private NewsUpdateScheduler scheduler;
    private final AtomicInteger counter = new AtomicInteger();

    @AfterEach
    void tearDown() {
        if (scheduler != null) {
            scheduler.stop();
        }
    }

    @Test
    void start_schedulesTaskAtFixedRate() throws InterruptedException {
        // Настраиваем задачу, которая инкрементирует counter
        Runnable task = counter::incrementAndGet;
        // initialDelay = 0, period = 20 ms
        scheduler = new NewsUpdateScheduler(task, 0, 20, TimeUnit.MILLISECONDS);

        scheduler.start();

        // Подождём чуть больше, чем 3 периода
        Thread.sleep(75);

        // Ожидаем хотя бы 3 запуска
        int runs = counter.get();
        assertTrue(runs >= 3, "Задача должна была выполниться не менее 3 раз, но выполнилась " + runs);
    }

    @Test
    void stop_preventsFurtherExecutions() throws InterruptedException {
        Runnable task = counter::incrementAndGet;
        scheduler = new NewsUpdateScheduler(task, 0, 10, TimeUnit.MILLISECONDS);

        scheduler.start();

        // Дадим задаче сработать пару раз
        Thread.sleep(35);
        scheduler.stop();

        int afterStop = counter.get();
        // Подождём ещё немного — счётчик не должен увеличиваться
        Thread.sleep(30);
        int finalCount = counter.get();

        assertTrue(afterStop >= 2, "Задача должна была выполниться не менее 2 раз до stop()");
        assertTrue(finalCount == afterStop,
                "После stop() число выполнений не должно увеличиваться: было "
                        + afterStop + ", стало " + finalCount);
    }
}
