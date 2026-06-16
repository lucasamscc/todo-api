package com.example.todoapi.board.dto;

import jakarta.validation.constraints.NotBlank;

public record BoardRequestDTO(
        @NotBlank(message = "Board name must not be blank")
        String name
) {
}
