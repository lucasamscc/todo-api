package com.example.todoapi.board;

import com.example.todoapi.board.controller.BoardController;
import com.example.todoapi.board.dto.BoardRequestDTO;
import com.example.todoapi.board.dto.BoardResponseDTO;
import com.example.todoapi.board.service.BoardService;
import com.example.todoapi.exception.GlobalExceptionHandler;
import com.example.todoapi.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("BoardController Unit Tests")
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BoardService boardService;

    private final String boardId = UUID.randomUUID().toString();

    @Test
    @DisplayName("GET /api/v1/board should return 200 and list of boards")
    void getAll_ShouldReturn200() throws Exception {
        when(boardService.findAll()).thenReturn(List.of(new BoardResponseDTO(boardId, "Projeto A")));

        mockMvc.perform(get("/api/v1/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Projeto A"))
                .andExpect(jsonPath("$[0].id").value(boardId));
    }

    @Test
    @DisplayName("POST /api/v1/board should return 200 with created board")
    void create_ShouldReturn200() throws Exception {
        BoardRequestDTO request = new BoardRequestDTO("Projeto A");
        when(boardService.create(any())).thenReturn(new BoardResponseDTO(boardId, "Projeto A"));

        mockMvc.perform(post("/api/v1/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Projeto A"))
                .andExpect(jsonPath("$.id").value(boardId));
    }

    @Test
    @DisplayName("POST /api/v1/board with blank name should return 400")
    void create_WithBlankName_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/board/{id} should return 200 with updated board")
    void update_ShouldReturn200() throws Exception {
        BoardRequestDTO request = new BoardRequestDTO("Projeto A Atualizado");
        when(boardService.update(eq(boardId), any())).thenReturn(new BoardResponseDTO(boardId, "Projeto A Atualizado"));

        mockMvc.perform(put("/api/v1/board/" + boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Projeto A Atualizado"));
    }

    @Test
    @DisplayName("PUT /api/v1/board/{id} with unknown id should return 404")
    void update_WithUnknownId_ShouldReturn404() throws Exception {
        BoardRequestDTO request = new BoardRequestDTO("X");
        when(boardService.update(eq(boardId), any())).thenThrow(new ResourceNotFoundException("Board", boardId));

        mockMvc.perform(put("/api/v1/board/" + boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/board/{id} should return 200 with status ok")
    void delete_ShouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/v1/board/" + boardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    @DisplayName("DELETE /api/v1/board/{id} with unknown id should return 404")
    void delete_WithUnknownId_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Board", boardId)).when(boardService).delete(boardId);

        mockMvc.perform(delete("/api/v1/board/" + boardId))
                .andExpect(status().isNotFound());
    }
}
