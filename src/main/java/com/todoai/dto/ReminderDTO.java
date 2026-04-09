package com.todoai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderDTO {

    private Long id;

    @NotNull(message = "Todo ID is required")
    private Long todoId;

    @NotNull(message = "Scheduled time is required")
    private LocalDateTime scheduledTime;

    private String notificationMessage;

    private Boolean sent;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
