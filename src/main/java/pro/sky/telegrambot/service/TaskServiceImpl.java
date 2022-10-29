package pro.sky.telegrambot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.repository.TaskRepository;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TaskServiceImpl implements TaskService {
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private TaskRepository taskRepository;

    public TaskServiceImpl ( TaskRepository taskRepository ) {
        this.taskRepository = taskRepository;
    }

    public Optional<Task> parse ( String notificationBotMessage ) {
        Pattern savePattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
        Matcher saveMatcher = savePattern.matcher(notificationBotMessage);
        Task result = null;
        try {
            if (saveMatcher.find()) {
                LocalDateTime taskDateTime = LocalDateTime.parse(saveMatcher.group(1), DATE_TIME_FORMATTER);
                String taskNotification = saveMatcher.group(3);
                result = new Task(taskNotification, taskDateTime);
            }
        } catch (Exception e) {
            logger.error("Cannot parse notifBotMsg: " + notificationBotMessage, e);
        }
        return Optional.ofNullable(result);
    }

    public Task schedule ( Task task, Long chatId ) {
        task.setChatId(chatId);
        Task storedTask = taskRepository.save(task);
        return storedTask;
    }

    public void notifyAllScheduledTasks ( Consumer<Task> notifier ) {
        Collection<Task> notifications = taskRepository.getScheduledNotifications();
        notifications.forEach(task -> {
            notifier.accept(task);
        });
        taskRepository.saveAll(notifications);
    }
}
