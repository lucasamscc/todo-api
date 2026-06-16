package com.example.todoapi.task.dto;

import com.example.todoapi.task.entity.Task;

import java.time.Instant;
import java.util.List;

public record TaskResponseDTO(
        String id,
        String name,
        Integer position,
        Instant createdAt,
        Instant dueDate,
        boolean completed,
        List<String> tags,
        String columnId
) {
    public static TaskResponseDTO from(Task task) {
        return new TaskResponseDTO(
                task.getId().toString(),
                task.getName(),
                task.getPosition(),
                task.getCreatedAt(),
                task.getDueDate(),
                task.isCompleted(),
                task.getTags() != null ? List.copyOf(task.getTags()) : List.of(),
                task.getColumn().getId().toString()
        );
    }
}
