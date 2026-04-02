package com.todoai.repository;

import com.todoai.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    
    List<Reminder> findByTodoId(Long todoId);
    
    List<Reminder> findBySentFalse();
    
    List<Reminder> findBySentFalseAndScheduledTimeBefore(LocalDateTime dateTime);
}

