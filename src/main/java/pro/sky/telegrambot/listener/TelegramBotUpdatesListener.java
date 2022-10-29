package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Task;
import pro.sky.telegrambot.service.TaskServiceImpl;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private static final String START = "/start";
    private static final String GREETING_MSG = "Hello, you are using this new bot!";

    private static final String WRONG_INPUT_MESSAGE = "Incorrect input, try again";

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init () {
        telegramBot.setUpdatesListener(this);
    }

    private final TaskServiceImpl taskService;

    public TelegramBotUpdatesListener ( TelegramBot telegramBot, TaskServiceImpl taskService ) {
        this.telegramBot = telegramBot;
        this.taskService = taskService;
    }

    @Override
    public int process ( List<Update> updates ) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            Message message = update.message();
            if (message.text().startsWith(START)) {
                logger.info(GREETING_MSG);
                sendMessage(extractChatId(message), GREETING_MSG);
            } else {
                Optional<Task> parseResult = taskService.parse(message.text());
                if (parseResult.isPresent()) {
                    scheduleNotification(extractChatId(message), parseResult.get());
                } else {
                    sendMessage(extractChatId(message), WRONG_INPUT_MESSAGE);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void scheduleNotification ( Long chatId, Task task ) {
        taskService.schedule(task, chatId);
        sendMessage(chatId, "Notification is scheduled");
    }

    private void sendMessage ( Long chatId, String messageText ) {
        SendMessage sendMessage = new SendMessage(chatId, messageText);
        telegramBot.execute(sendMessage);
    }

    private void sendMessage ( Task task ) {
        sendMessage(task.getChatId(), task.getNotification());
    }

    private Long extractChatId ( Message message ) {
        return message.chat().id();
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void notifyScheduledTasks () {
        taskService.notifyAllScheduledTasks(this::sendMessage);
    }
}
