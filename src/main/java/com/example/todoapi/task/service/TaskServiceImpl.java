package com.example.todoapi.task.service;

import com.example.todoapi.column.entity.Column;
import com.example.todoapi.column.repository.ColumnRepository;
import com.example.todoapi.exception.ResourceNotFoundException;
import com.example.todoapi.task.dto.TaskPatchDTO;
import com.example.todoapi.task.dto.TaskRequestDTO;
import com.example.todoapi.task.dto.TaskResponseDTO;
import com.example.todoapi.task.entity.Task;
import com.example.todoapi.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ColumnRepository columnRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> findByColumn(String columnId) {
        return taskRepository.findByColumnIdOrderByPosition(UUID.fromString(columnId))
                .stream()
                .map(TaskResponseDTO::from)
                .toList();
    }

    @Override
    @Transactional
    public TaskResponseDTO create(TaskRequestDTO request) {
        Column column = findColumnById(request.columnId());

        taskRepository.shiftPositionsDown(column.getId(), request.position());

        Task task = Task.builder()
                .name(request.name())
                .position(request.position())
                .createdAt(request.createdAt())
                .dueDate(request.dueDate())
                .completed(request.completed() != null && request.completed())
                .tags(request.tags() != null ? new ArrayList<>(request.tags()) : new ArrayList<>())
                .column(column)
                .build();
        return TaskResponseDTO.from(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponseDTO update(String id, TaskRequestDTO request) {
        Task task = findTaskById(id);
        Column newColumn = findColumnById(request.columnId());

        reorderPositions(
                task.getColumn().getId(), newColumn.getId(),
                task.getPosition(), request.position());

        task.setName(request.name());
        task.setPosition(request.position());
        task.setCreatedAt(request.createdAt());
        task.setDueDate(request.dueDate());
        task.setCompleted(request.completed() != null && request.completed());
        task.setTags(request.tags() != null ? new ArrayList<>(request.tags()) : new ArrayList<>());
        task.setColumn(newColumn);

        return TaskResponseDTO.from(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponseDTO patch(String id, TaskPatchDTO request) {
        Task task = findTaskById(id);

        Integer newPosition = request.position() != null ? request.position() : task.getPosition();
        UUID newColumnId = request.columnId() != null ? UUID.fromString(request.columnId()) : task.getColumn().getId();

        reorderPositions(task.getColumn().getId(), newColumnId, task.getPosition(), newPosition);

        if (request.name() != null)
            task.setName(request.name());
        if (request.position() != null)
            task.setPosition(request.position());
        if (request.createdAt() != null)
            task.setCreatedAt(request.createdAt());
        if (request.dueDate() != null)
            task.setDueDate(request.dueDate());
        if (request.completed() != null)
            task.setCompleted(request.completed());
        if (request.tags() != null)
            task.setTags(new ArrayList<>(request.tags()));
        if (request.columnId() != null)
            task.setColumn(findColumnById(request.columnId()));

        return TaskResponseDTO.from(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Task task = findTaskById(id);
        taskRepository.shiftPositionsUp(task.getColumn().getId(), task.getPosition());
        taskRepository.delete(task);
    }

    private void reorderPositions(UUID oldColumnId, UUID newColumnId,
            Integer oldPosition, Integer newPosition) {
        boolean columnChanged = !oldColumnId.equals(newColumnId);
        boolean positionChanged = !oldPosition.equals(newPosition);

        if (columnChanged) {
            taskRepository.shiftPositionsUp(oldColumnId, oldPosition);
            taskRepository.shiftPositionsDown(newColumnId, newPosition);
        } else if (positionChanged) {
            if (newPosition < oldPosition) {
                taskRepository.shiftPositionsDownBetween(oldColumnId, newPosition, oldPosition);
            } else {
                taskRepository.shiftPositionsUpBetween(oldColumnId, oldPosition, newPosition);
            }
        }
    }

    private Task findTaskById(String id) {
        return taskRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
    }

    private Column findColumnById(String id) {
        return columnRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Column", id));
    }
}
