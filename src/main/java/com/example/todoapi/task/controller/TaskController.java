package com.example.todoapi.task.controller;

import com.example.todoapi.common.DeleteResponse;
import com.example.todoapi.task.dto.TaskRequestDTO;
import com.example.todoapi.task.dto.TaskResponseDTO;
import com.example.todoapi.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
@Tag(name = "Task", description = "Operações de gerenciamento de tarefas")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/from/{column_id}")
    @Operation(summary = "Listar tarefas de uma coluna (ordenadas por posição)")
    public ResponseEntity<List<TaskResponseDTO>> findByColumn(@PathVariable("column_id") String columnId) {
        return ResponseEntity.ok(taskService.findByColumn(columnId));
    }

    @PostMapping
    @Operation(summary = "Criar nova tarefa")
    public ResponseEntity<TaskResponseDTO> create(@Valid @RequestBody TaskRequestDTO request) {
        return ResponseEntity.ok(taskService.create(request));
    }

    @PutMapping("/{task_id}")
    @Operation(summary = "Atualizar tarefa (suporta mover entre colunas e reposicionar)")
    public ResponseEntity<TaskResponseDTO> update(
            @PathVariable("task_id") String taskId,
            @Valid @RequestBody TaskRequestDTO request) {
        return ResponseEntity.ok(taskService.update(taskId, request));
    }

    @PatchMapping("/{task_id}")
    @Operation(summary = "Atualização parcial de tarefa")
    public ResponseEntity<TaskResponseDTO> patch(
            @PathVariable("task_id") String taskId,
            @Valid @RequestBody com.example.todoapi.task.dto.TaskPatchDTO request) {
        return ResponseEntity.ok(taskService.patch(taskId, request));
    }

    @DeleteMapping("/{task_id}")
    @Operation(summary = "Deletar tarefa")
    public ResponseEntity<DeleteResponse> delete(@PathVariable("task_id") String taskId) {
        taskService.delete(taskId);
        return ResponseEntity.ok(DeleteResponse.ok());
    }
}
