package org.example.update;

import org.example.App;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ScheduledLauncher {

    // Запускает приложение с планировщиком обновлений
    public static void launch(String[] args,
                              SchedulerFactory schedulerFactory,
                              Consumer<String[]> appRunner) {
        // Создаём планировщик, передаём задачу обновления RSS
        var scheduler = schedulerFactory.create(App::fetchToDatabase);

        scheduler.start(); // Запускаем расписание обновлений
        appRunner.accept(args); // Запускаем основную логику приложения
        scheduler.stop(); // Останавливаем планировщик
    }

    public static void main(String[] args) {
        // Запуск с интервалом 30 минут между обновлениями через 30 минут
        launch(args,
                task -> new NewsUpdateScheduler(task, 30, 30, TimeUnit.MINUTES),
                App::main);
    }

    // Создание планировщика с задачей обновления
    @FunctionalInterface
    public interface SchedulerFactory {
        NewsUpdateScheduler create(Runnable updateTask);
    }
}
