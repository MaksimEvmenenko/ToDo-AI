package com.todoai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Boolean completed;

    private Boolean reminderEnabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

