package com.example.todoapi.board.dto;

import com.example.todoapi.board.entity.Board;

public record BoardResponseDTO(
        String id,
        String name
) {
    public static BoardResponseDTO from(Board board) {
        return new BoardResponseDTO(board.getId().toString(), board.getName());
    }
}
