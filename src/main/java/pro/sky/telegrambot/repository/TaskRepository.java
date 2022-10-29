package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.telegrambot.model.Task;

import java.util.Collection;


public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(value = "SELECT * FROM notification_task WHERE schedule <= CURRENT_TIMESTAMP", nativeQuery = true)
    Collection<Task> getScheduledNotifications();
}
