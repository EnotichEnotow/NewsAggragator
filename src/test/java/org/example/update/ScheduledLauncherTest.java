package org.example.update;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ScheduledLauncherTest {

    @Test
    void launch_startsAndStopsSchedulerAroundApp() {
        AtomicInteger started = new AtomicInteger();
        AtomicInteger stopped = new AtomicInteger();
        AtomicInteger runCount = new AtomicInteger();

        // фабрика, которая выдаёт "планировщик" с подсчётом start/stop
        ScheduledLauncher.SchedulerFactory fakeFactory = task -> new NewsUpdateScheduler(
                () -> { started.incrementAndGet(); /* не запускаем периодически */ },
                0, 1, java.util.concurrent.TimeUnit.SECONDS
        ) {
            @Override public void start() { super.start(); started.incrementAndGet(); }
            @Override public void stop()  { super.stop();  stopped.incrementAndGet(); }
        };

        // вместо App.main будем просто инкрементить runCount
        java.util.function.Consumer<String[]> fakeApp = args -> runCount.incrementAndGet();

        // запустим
        ScheduledLauncher.launch(new String[]{"foo"}, fakeFactory, fakeApp);

        // проверяем, что:
        // 1) приложение действительно вызвано
        assertEquals(1, runCount.get(), "App должен быть запущен ровно один раз");
        // 2) планировщик стартовал и остановился
        assertEquals(1, started.get(), "start() должен быть вызван один раз");
        assertEquals(1, stopped.get(), "stop() должен быть вызван один раз");
    }
}
