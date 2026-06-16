package com.example.todoapi.task.dto;

import jakarta.validation.constraints.Min;
import java.time.Instant;
import java.util.List;

public record TaskPatchDTO(
        String name,

        @Min(value = 0, message = "Position must be >= 0")
        Integer position,

        Instant createdAt,

        Instant dueDate,

        Boolean completed,

        List<String> tags,

        String columnId
) {
}
