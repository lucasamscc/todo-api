package com.example.todoapi.column.dto;

import com.example.todoapi.column.entity.Column;

public record ColumnResponseDTO(
        String id,
        String name,
        Integer position,
        String boardId
) {
    public static ColumnResponseDTO from(Column column) {
        return new ColumnResponseDTO(
                column.getId().toString(),
                column.getName(),
                column.getPosition(),
                column.getBoard().getId().toString()
        );
    }
}
