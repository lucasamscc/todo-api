package com.example.todoapi.column.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ColumnRequestDTO(
        @NotBlank(message = "Column name must not be blank") 
        String name,

        @NotNull(message = "Position is required") 
        @Min(value = 0, message = "Position must be >= 0") 
        Integer position,

        @NotBlank(message = "boardId is required") 
        String boardId
) {
}
