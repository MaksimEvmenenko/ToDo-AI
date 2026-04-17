package com.todoai.service;

import com.todoai.dto.ReminderDTO;
import com.todoai.model.Reminder;
import com.todoai.model.Todo;
import com.todoai.repository.ReminderRepository;
import com.todoai.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Reminder management
 * Handles reminder creation, retrieval, and scheduled notifications
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final TodoRepository todoRepository;
    private final RestTemplate restTemplate;

    public List<ReminderDTO> getAllReminders() {
        return reminderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReminderDTO> getRemindersByTodoId(Long todoId) {
        return reminderRepository.findByTodoId(todoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReminderDTO getReminderById(Long id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found with id: " + id));
        return convertToDTO(reminder);
    }

    public ReminderDTO createReminder(ReminderDTO reminderDTO) {
        // Verify that the todo exists
        Todo todo = todoRepository.findById(reminderDTO.getTodoId())
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + reminderDTO.getTodoId()));

        // Enable reminder flag on the todo
        todo.setReminderEnabled(true);
        todoRepository.save(todo);

        Reminder reminder = Reminder.builder()
                .todoId(reminderDTO.getTodoId())
                .scheduledTime(reminderDTO.getScheduledTime())
                .notificationMessage(reminderDTO.getNotificationMessage())
                .sent(false)
                .build();

        Reminder savedReminder = reminderRepository.save(reminder);
        return convertToDTO(savedReminder);
    }

    public ReminderDTO updateReminder(Long id, ReminderDTO reminderDTO) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found with id: " + id));

        if (reminderDTO.getScheduledTime() != null) {
            reminder.setScheduledTime(reminderDTO.getScheduledTime());
        }
        if (reminderDTO.getNotificationMessage() != null) {
            reminder.setNotificationMessage(reminderDTO.getNotificationMessage());
        }

        Reminder updatedReminder = reminderRepository.save(reminder);
        return convertToDTO(updatedReminder);
    }

    public void deleteReminder(Long id) {
        if (!reminderRepository.existsById(id)) {
            throw new RuntimeException("Reminder not found with id: " + id);
        }
        reminderRepository.deleteById(id);
    }

    /**
     * Scheduled task that runs every minute to check for pending reminders
     * and send notifications for reminders whose scheduled time has passed
     */
    @Scheduled(fixedRate = 60000) // Run every 60 seconds (in milliseconds)
    public void sendPendingReminders() {
        log.debug("Checking for pending reminders...");

        List<Reminder> pendingReminders = reminderRepository
                .findBySentFalseAndScheduledTimeBefore(LocalDateTime.now());

        for (Reminder reminder : pendingReminders) {
            try {
                sendNotification(reminder);
                reminder.setSent(true);
                reminderRepository.save(reminder);
                log.info("Reminder notification sent for todo: {}", reminder.getTodoId());
            } catch (Exception e) {
                log.error("Failed to send reminder notification for todo: {}", reminder.getTodoId(), e);
            }
        }
    }

    /**
     * Manually trigger a notification for a specific reminder
     */
    public void sendReminderNotification(Long reminderId) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Reminder not found with id: " + reminderId));

        sendNotification(reminder);
        reminder.setSent(true);
        reminderRepository.save(reminder);
        log.info("Manual notification sent for reminder: {}", reminderId);
    }

    /**
     * Internal method to send notification
     * Triggers an endpoint that handles notification delivery
     */
    @Transactional
    private void sendNotification(Reminder reminder) {
        try {
            Todo todo = todoRepository.findById(reminder.getTodoId())
                    .orElseThrow(() -> new RuntimeException("Todo not found"));

            String message = reminder.getNotificationMessage() != null 
                    ? reminder.getNotificationMessage() 
                    : "Reminder: " + todo.getTitle();

            NotificationPayload payload = NotificationPayload.builder()
                    .todoId(reminder.getTodoId())
                    .reminderId(reminder.getId())
                    .title(todo.getTitle())
                    .message(message)
                    .timestamp(LocalDateTime.now())
                    .build();

            String notificationEndpoint = "http://localhost:8080/api/v1/notification/send";
            restTemplate.postForObject(notificationEndpoint, payload, Void.class);

            log.debug("Notification sent via endpoint for reminder: {}", reminder.getId());
        } catch (Exception e) {
            log.error("Error sending notification for reminder: {}", reminder.getId(), e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }

    private ReminderDTO convertToDTO(Reminder reminder) {
        return ReminderDTO.builder()
                .id(reminder.getId())
                .todoId(reminder.getTodoId())
                .scheduledTime(reminder.getScheduledTime())
                .sent(reminder.getSent())
                .notificationMessage(reminder.getNotificationMessage())
                .createdAt(reminder.getCreatedAt())
                .updatedAt(reminder.getUpdatedAt())
                .build();
    }

    /**
     * Inner class for notification payload
     */
    public static class NotificationPayload {
        public Long todoId;
        public Long reminderId;
        public String title;
        public String message;
        public LocalDateTime timestamp;

        public NotificationPayload() {}

        public NotificationPayload(Long todoId, Long reminderId, String title, String message, LocalDateTime timestamp) {
            this.todoId = todoId;
            this.reminderId = reminderId;
            this.title = title;
            this.message = message;
            this.timestamp = timestamp;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Long todoId;
            private Long reminderId;
            private String title;
            private String message;
            private LocalDateTime timestamp;

            public Builder todoId(Long todoId) {
                this.todoId = todoId;
                return this;
            }

            public Builder reminderId(Long reminderId) {
                this.reminderId = reminderId;
                return this;
            }

            public Builder title(String title) {
                this.title = title;
                return this;
            }

            public Builder message(String message) {
                this.message = message;
                return this;
            }

            public Builder timestamp(LocalDateTime timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public NotificationPayload build() {
                return new NotificationPayload(todoId, reminderId, title, message, timestamp);
            }
        }
    }
}

