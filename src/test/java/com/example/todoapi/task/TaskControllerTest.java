package com.example.todoapi.task;

import com.example.todoapi.exception.GlobalExceptionHandler;
import com.example.todoapi.exception.ResourceNotFoundException;
import com.example.todoapi.task.controller.TaskController;
import com.example.todoapi.task.dto.TaskRequestDTO;
import com.example.todoapi.task.dto.TaskResponseDTO;
import com.example.todoapi.task.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("TaskController Unit Tests")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    private final String columnId = UUID.randomUUID().toString();
    private final String taskId = UUID.randomUUID().toString();
    private final Instant now = Instant.parse("2026-02-05T10:00:00Z");

    private TaskResponseDTO sampleResponse() {
        return new TaskResponseDTO(taskId, "Implementar autenticação", 0,
                now, null, false, List.of("backend"), columnId);
    }

    @Test
    @DisplayName("GET /api/v1/task/from/{columnId} should return 200")
    void findByColumn_ShouldReturn200() throws Exception {
        when(taskService.findByColumn(columnId)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/v1/task/from/" + columnId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Implementar autenticação"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[0].tags[0]").value("backend"));
    }

    @Test
    @DisplayName("POST /api/v1/task should return 200 with created task")
    void create_ShouldReturn200() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO(
                "Implementar autenticação", 0, now, null, false, List.of("backend"), columnId);
        when(taskService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/v1/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Implementar autenticação"))
                .andExpect(jsonPath("$.columnId").value(columnId));
    }

    @Test
    @DisplayName("POST /api/v1/task with blank name should return 400")
    void create_WithBlankName_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"position\":0,\"columnId\":\"" + columnId + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/task/{id} should return 200 with updated task")
    void update_ShouldReturn200() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO(
                "Implementar autenticação", 0, now, null, true, List.of("backend"), columnId);
        TaskResponseDTO updated = new TaskResponseDTO(taskId, "Implementar autenticação", 0,
                now, null, true, List.of("backend"), columnId);
        when(taskService.update(eq(taskId), any())).thenReturn(updated);

        mockMvc.perform(put("/api/v1/task/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/task/{id} with unknown id should return 404")
    void update_WithUnknownId_ShouldReturn404() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO("X", 0, now, null, false, List.of(), columnId);
        when(taskService.update(eq(taskId), any())).thenThrow(new ResourceNotFoundException("Task", taskId));

        mockMvc.perform(put("/api/v1/task/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/task/{id} should return 200 with status ok")
    void delete_ShouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/v1/task/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    @DisplayName("DELETE /api/v1/task/{id} with unknown id should return 404")
    void delete_WithUnknownId_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Task", taskId)).when(taskService).delete(taskId);

        mockMvc.perform(delete("/api/v1/task/" + taskId))
                .andExpect(status().isNotFound());
    }
}
