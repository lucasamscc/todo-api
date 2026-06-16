package com.example.todoapi.column.service;

import com.example.todoapi.column.dto.ColumnRequestDTO;
import com.example.todoapi.column.dto.ColumnResponseDTO;

import java.util.List;

public interface ColumnService {

    List<ColumnResponseDTO> findByBoard(String boardId);

    ColumnResponseDTO create(ColumnRequestDTO request);

    ColumnResponseDTO update(String id, ColumnRequestDTO request);

    void delete(String id);
}
