package com.todoai.service;

import com.todoai.dto.TodoDTO;
import com.todoai.model.Todo;
import com.todoai.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public List<TodoDTO> getAllTodos() {
        return todoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TodoDTO getTodoById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));
        return convertToDTO(todo);
    }

    public List<TodoDTO> getActiveTodos() {
        return todoRepository.findByCompletedFalse().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TodoDTO> getCompletedTodos() {
        return todoRepository.findByCompletedTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TodoDTO createTodo(TodoDTO todoDTO) {
        Todo todo = Todo.builder()
                .title(todoDTO.getTitle())
                .description(todoDTO.getDescription())
                .completed(false)
                .build();
        Todo savedTodo = todoRepository.save(todo);
        return convertToDTO(savedTodo);
    }

    public TodoDTO updateTodo(Long id, TodoDTO todoDTO) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        if (todoDTO.getTitle() != null) {
            todo.setTitle(todoDTO.getTitle());
        }
        if (todoDTO.getDescription() != null) {
            todo.setDescription(todoDTO.getDescription());
        }
        if (todoDTO.getCompleted() != null) {
            todo.setCompleted(todoDTO.getCompleted());
        }

        Todo updatedTodo = todoRepository.save(todo);
        return convertToDTO(updatedTodo);
    }

    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new RuntimeException("Todo not found with id: " + id);
        }
        todoRepository.deleteById(id);
    }

    private TodoDTO convertToDTO(Todo todo) {
        return TodoDTO.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .completed(todo.getCompleted())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt())
                .build();
    }
}

