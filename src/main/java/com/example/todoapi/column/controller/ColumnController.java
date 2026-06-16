package com.example.todoapi.column.controller;

import com.example.todoapi.column.dto.ColumnRequestDTO;
import com.example.todoapi.column.dto.ColumnResponseDTO;
import com.example.todoapi.column.service.ColumnService;
import com.example.todoapi.common.DeleteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/column")
@RequiredArgsConstructor
@Tag(name = "Column", description = "Operações de gerenciamento de colunas")
public class ColumnController {

    private final ColumnService columnService;

    @GetMapping("/from/{board_id}")
    @Operation(summary = "Listar colunas de um quadro (ordenadas por posição)")
    public ResponseEntity<List<ColumnResponseDTO>> findByBoard(@PathVariable("board_id") String boardId) {
        return ResponseEntity.ok(columnService.findByBoard(boardId));
    }

    @PostMapping
    @Operation(summary = "Criar nova coluna")
    public ResponseEntity<ColumnResponseDTO> create(@Valid @RequestBody ColumnRequestDTO request) {
        return ResponseEntity.ok(columnService.create(request));
    }

    @PutMapping("/{column_id}")
    @Operation(summary = "Atualizar coluna")
    public ResponseEntity<ColumnResponseDTO> update(
            @PathVariable("column_id") String columnId,
            @Valid @RequestBody ColumnRequestDTO request) {
        return ResponseEntity.ok(columnService.update(columnId, request));
    }

    @DeleteMapping("/{column_id}")
    @Operation(summary = "Deletar coluna")
    public ResponseEntity<DeleteResponse> delete(@PathVariable("column_id") String columnId) {
        columnService.delete(columnId);
        return ResponseEntity.ok(DeleteResponse.ok());
    }
}
