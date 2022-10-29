package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Task {

    public Task () {
    }

    public Task ( String notification, LocalDateTime schedule ) {
        this.notification = notification;
        this.schedule = schedule;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private Long chatId;
    private String notification;
    private LocalDateTime schedule;

    public Long getId () {
        return id;
    }

    public void setId ( Long id ) {
        this.id = id;
    }

    public Long getChatId () {
        return chatId;
    }

    public void setChatId ( Long chatId ) {
        this.chatId = chatId;
    }

    public String getNotification () {
        return notification;
    }

    public void setNotification ( String notification ) {
        this.notification = notification;
    }

    public LocalDateTime getSchedule () {
        return schedule;
    }

    public void setSchedule ( LocalDateTime schedule ) {
        this.schedule = schedule;
    }
}

