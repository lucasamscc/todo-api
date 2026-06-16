package com.example.todoapi.task.service;

import com.example.todoapi.task.dto.TaskPatchDTO;
import com.example.todoapi.task.dto.TaskRequestDTO;
import com.example.todoapi.task.dto.TaskResponseDTO;

import java.util.List;

public interface TaskService {

    List<TaskResponseDTO> findByColumn(String columnId);

    TaskResponseDTO create(TaskRequestDTO request);

    TaskResponseDTO update(String id, TaskRequestDTO request);

    TaskResponseDTO patch(String id, TaskPatchDTO request);

    void delete(String id);
}
