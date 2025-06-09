package org.example.update;

import java.util.concurrent.*;

/**
 * Запускает Runnable по расписанию.
 */
public class NewsUpdateScheduler {

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private final Runnable updateTask;
    private final long initialDelay;
    private final long period;
    private final TimeUnit timeUnit;

    public NewsUpdateScheduler(Runnable updateTask,
                               long initialDelay,
                               long period,
                               TimeUnit timeUnit) {
        this.updateTask   = updateTask;
        this.initialDelay = initialDelay;
        this.period       = period;
        this.timeUnit     = timeUnit;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Запускаем обновление RSS...");
            try {
                updateTask.run();
            } catch (Exception e) {
                System.err.println("Ошибка обновления:");
                e.printStackTrace();
            }
        }, initialDelay, period, timeUnit);
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}
