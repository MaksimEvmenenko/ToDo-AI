package com.todoai.controller;

import com.todoai.dto.TodoDTO;
import com.todoai.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    /**
     * GET /api/v1/todo/list - Get all todos
     * @return List of all todos
     */
    @GetMapping("/list")
    public ResponseEntity<List<TodoDTO>> getAllTodos() {
        List<TodoDTO> todos = todoService.getAllTodos();
        return ResponseEntity.ok(todos);
    }

    /**
     * GET /api/v1/todo/list/active - Get all active (incomplete) todos
     * @return List of active todos
     */
    @GetMapping("/list/active")
    public ResponseEntity<List<TodoDTO>> getActiveTodos() {
        List<TodoDTO> todos = todoService.getActiveTodos();
        return ResponseEntity.ok(todos);
    }

    /**
     * GET /api/v1/todo/list/completed - Get all completed todos
     * @return List of completed todos
     */
    @GetMapping("/list/completed")
    public ResponseEntity<List<TodoDTO>> getCompletedTodos() {
        List<TodoDTO> todos = todoService.getCompletedTodos();
        return ResponseEntity.ok(todos);
    }

    /**
     * GET /api/v1/todo/{id} - Get a specific todo by ID
     * @param id Todo ID
     * @return Todo details
     */
    @GetMapping("/{id}")
    public ResponseEntity<TodoDTO> getTodoById(@PathVariable Long id) {
        TodoDTO todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todo);
    }

    /**
     * POST /api/v1/todo - Create a new todo
     * @param todoDTO Todo data to create
     * @return Created todo with ID
     */
    @PostMapping
    public ResponseEntity<TodoDTO> createTodo(@Valid @RequestBody TodoDTO todoDTO) {
        TodoDTO createdTodo = todoService.createTodo(todoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }

    /**
     * PUT /api/v1/todo/{id} - Update an existing todo
     * @param id Todo ID
     * @param todoDTO Updated todo data
     * @return Updated todo
     */
    @PutMapping("/{id}")
    public ResponseEntity<TodoDTO> updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody TodoDTO todoDTO) {
        TodoDTO updatedTodo = todoService.updateTodo(id, todoDTO);
        return ResponseEntity.ok(updatedTodo);
    }

    /**
     * DELETE /api/v1/todo/{id} - Delete a todo
     * @param id Todo ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}

