package com.example.todoapi.board.controller;

import com.example.todoapi.board.dto.BoardRequestDTO;
import com.example.todoapi.board.dto.BoardResponseDTO;
import com.example.todoapi.board.service.BoardService;
import com.example.todoapi.common.DeleteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
@Tag(name = "Board", description = "Operações de gerenciamento de quadros Kanban")
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    @Operation(summary = "Listar todos os quadros")
    public ResponseEntity<List<BoardResponseDTO>> findAll() {
        return ResponseEntity.ok(boardService.findAll());
    }

    @PostMapping
    @Operation(summary = "Criar novo quadro")
    public ResponseEntity<BoardResponseDTO> create(@Valid @RequestBody BoardRequestDTO request) {
        return ResponseEntity.ok(boardService.create(request));
    }

    @PutMapping("/{board_id}")
    @Operation(summary = "Atualizar quadro")
    public ResponseEntity<BoardResponseDTO> update(
            @PathVariable("board_id") String boardId,
            @Valid @RequestBody BoardRequestDTO request) {
        return ResponseEntity.ok(boardService.update(boardId, request));
    }

    @DeleteMapping("/{board_id}")
    @Operation(summary = "Deletar quadro")
    public ResponseEntity<DeleteResponse> delete(@PathVariable("board_id") String boardId) {
        boardService.delete(boardId);
        return ResponseEntity.ok(DeleteResponse.ok());
    }
}
