package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Task;

import java.util.Optional;
import java.util.function.Consumer;


@Service
public interface TaskService {
    Optional<Task> parse ( String taskMessage );

    Task schedule ( Task task, Long chatId );

    void notifyAllScheduledTasks( Consumer<Task> notifier);
}
