package com.example.todoapi.task.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public record TaskRequestDTO(
        @NotBlank(message = "Task name must not be blank")
        String name,

        @NotNull(message = "Position is required")
        @Min(value = 0, message = "Position must be >= 0")
        Integer position,

        Instant createdAt,

        Instant dueDate,

        Boolean completed,

        List<String> tags,

        @NotBlank(message = "columnId is required")
        String columnId
) {
}
