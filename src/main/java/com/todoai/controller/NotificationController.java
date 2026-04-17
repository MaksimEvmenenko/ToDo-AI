package com.todoai.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

/**
 * Controller for handling notification endpoints
 * Receives notification payloads from the reminder scheduler
 */
@Slf4j
@RestController
@RequestMapping("/v1/notification")
public class NotificationController {

    /**
     * POST /api/v1/notification/send - Receive notification from reminder service
     * This endpoint is called by the reminder scheduler when a reminder notification needs to be sent
     * 
     * @param notification Notification payload containing todo and reminder details
     * @return Success response
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest notification) {
        log.info("Received notification for Todo: {} (Reminder: {})", 
                 notification.getTodoId(), notification.getReminderId());
        log.info("Title: {}, Message: {}", notification.getTitle(), notification.getMessage());
        
        // TODO: Implement actual notification delivery (email, SMS, push notification, etc.)
        // For now, we just log the notification
        log.info("Notification queued for delivery - Timestamp: {}", notification.getTimestamp());
        
        return ResponseEntity.status(HttpStatus.OK)
                .body("Notification received and queued for delivery");
    }

    /**
     * Notification request payload
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotificationRequest {
        private Long todoId;
        private Long reminderId;
        private String title;
        private String message;
        private LocalDateTime timestamp;
    }
}

