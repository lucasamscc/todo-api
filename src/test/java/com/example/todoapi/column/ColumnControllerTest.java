package com.example.todoapi.column;

import com.example.todoapi.column.controller.ColumnController;
import com.example.todoapi.column.dto.ColumnRequestDTO;
import com.example.todoapi.column.dto.ColumnResponseDTO;
import com.example.todoapi.column.service.ColumnService;
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

@WebMvcTest(ColumnController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("ColumnController Unit Tests")
class ColumnControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ColumnService columnService;

    private final String boardId = UUID.randomUUID().toString();
    private final String columnId = UUID.randomUUID().toString();

    @Test
    @DisplayName("GET /api/v1/column/from/{boardId} should return 200")
    void findByBoard_ShouldReturn200() throws Exception {
        when(columnService.findByBoard(boardId)).thenReturn(
                List.of(new ColumnResponseDTO(columnId, "A Fazer", 0, boardId)));

        mockMvc.perform(get("/api/v1/column/from/" + boardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("A Fazer"))
                .andExpect(jsonPath("$[0].position").value(0))
                .andExpect(jsonPath("$[0].boardId").value(boardId));
    }

    @Test
    @DisplayName("POST /api/v1/column should return 200 with created column")
    void create_ShouldReturn200() throws Exception {
        ColumnRequestDTO request = new ColumnRequestDTO("A Fazer", 0, boardId);
        when(columnService.create(any())).thenReturn(new ColumnResponseDTO(columnId, "A Fazer", 0, boardId));

        mockMvc.perform(post("/api/v1/column")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("A Fazer"))
                .andExpect(jsonPath("$.boardId").value(boardId));
    }

    @Test
    @DisplayName("POST /api/v1/column with invalid body should return 400")
    void create_WithInvalidBody_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/column")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"position\":0,\"boardId\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/column/{id} should return 200 with updated column")
    void update_ShouldReturn200() throws Exception {
        ColumnRequestDTO request = new ColumnRequestDTO("A Fazer Urgente", 0, boardId);
        when(columnService.update(eq(columnId), any()))
                .thenReturn(new ColumnResponseDTO(columnId, "A Fazer Urgente", 0, boardId));

        mockMvc.perform(put("/api/v1/column/" + columnId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("A Fazer Urgente"));
    }

    @Test
    @DisplayName("PUT /api/v1/column/{id} with unknown id should return 404")
    void update_WithUnknownId_ShouldReturn404() throws Exception {
        ColumnRequestDTO request = new ColumnRequestDTO("X", 0, boardId);
        when(columnService.update(eq(columnId), any()))
                .thenThrow(new ResourceNotFoundException("Column", columnId));

        mockMvc.perform(put("/api/v1/column/" + columnId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/column/{id} should return 200 with status ok")
    void delete_ShouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/v1/column/" + columnId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    @DisplayName("DELETE /api/v1/column/{id} with unknown id should return 404")
    void delete_WithUnknownId_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Column", columnId)).when(columnService).delete(columnId);

        mockMvc.perform(delete("/api/v1/column/" + columnId))
                .andExpect(status().isNotFound());
    }
}
