package com.todoai.controller;

import com.todoai.dto.ReminderDTO;
import com.todoai.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for Reminder management
 * Provides endpoints for creating, retrieving, updating, and deleting reminders
 * Also provides endpoint to manually trigger reminder notifications
 */
@RestController
@RequestMapping("/v1/reminder")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    /**
     * GET /api/v1/reminder/list - Get all reminders
     * @return List of all reminders
     */
    @GetMapping("/list")
    public ResponseEntity<List<ReminderDTO>> getAllReminders() {
        List<ReminderDTO> reminders = reminderService.getAllReminders();
        return ResponseEntity.ok(reminders);
    }

    /**
     * GET /api/v1/reminder/todo/{todoId} - Get all reminders for a specific todo
     * @param todoId Todo ID
     * @return List of reminders for the todo
     */
    @GetMapping("/todo/{todoId}")
    public ResponseEntity<List<ReminderDTO>> getRemindersByTodoId(@PathVariable Long todoId) {
        List<ReminderDTO> reminders = reminderService.getRemindersByTodoId(todoId);
        return ResponseEntity.ok(reminders);
    }

    /**
     * GET /api/v1/reminder/{id} - Get a specific reminder by ID
     * @param id Reminder ID
     * @return Reminder details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReminderDTO> getReminderById(@PathVariable Long id) {
        ReminderDTO reminder = reminderService.getReminderById(id);
        return ResponseEntity.ok(reminder);
    }

    /**
     * POST /api/v1/reminder - Create a new reminder
     * @param reminderDTO Reminder data to create
     * @return Created reminder with ID
     */
    @PostMapping
    public ResponseEntity<ReminderDTO> createReminder(@Valid @RequestBody ReminderDTO reminderDTO) {
        ReminderDTO createdReminder = reminderService.createReminder(reminderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReminder);
    }

    /**
     * PUT /api/v1/reminder/{id} - Update an existing reminder
     * @param id Reminder ID
     * @param reminderDTO Updated reminder data
     * @return Updated reminder
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReminderDTO> updateReminder(
            @PathVariable Long id,
            @Valid @RequestBody ReminderDTO reminderDTO) {
        ReminderDTO updatedReminder = reminderService.updateReminder(id, reminderDTO);
        return ResponseEntity.ok(updatedReminder);
    }

    /**
     * DELETE /api/v1/reminder/{id} - Delete a reminder
     * @param id Reminder ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/v1/reminder/{id}/send - Manually trigger notification for a reminder
     * @param id Reminder ID
     * @return Success message
     */
    @PostMapping("/{id}/send")
    public ResponseEntity<String> sendReminderNotification(@PathVariable Long id) {
        reminderService.sendReminderNotification(id);
        return ResponseEntity.ok("Notification sent successfully for reminder: " + id);
    }
}

