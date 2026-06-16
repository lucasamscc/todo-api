package com.example.todoapi.task;

import com.example.todoapi.board.entity.Board;
import com.example.todoapi.column.entity.Column;
import com.example.todoapi.column.repository.ColumnRepository;
import com.example.todoapi.exception.ResourceNotFoundException;
import com.example.todoapi.task.dto.TaskRequestDTO;
import com.example.todoapi.task.dto.TaskResponseDTO;
import com.example.todoapi.task.entity.Task;
import com.example.todoapi.task.repository.TaskRepository;
import com.example.todoapi.task.service.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskServiceImpl Unit Tests")
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ColumnRepository columnRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private UUID columnId;
    private UUID taskId;
    private Column column;
    private Task task;
    private Instant now;

    @BeforeEach
    void setUp() {
        UUID boardId = UUID.randomUUID();
        columnId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        now = Instant.now();

        Board board = Board.builder().id(boardId).name("Board A").build();
        column = Column.builder().id(columnId).name("A Fazer").position(0).board(board).build();
        task = Task.builder()
                .id(taskId)
                .name("Implementar autenticação")
                .position(0)
                .createdAt(now)
                .completed(false)
                .tags(List.of("backend"))
                .column(column)
                .build();
    }

    @Test
    @DisplayName("findByColumn should return tasks ordered by position")
    void findByColumn_ShouldReturnOrderedTasks() {
        when(taskRepository.findByColumnIdOrderByPosition(columnId)).thenReturn(List.of(task));

        List<TaskResponseDTO> result = taskService.findByColumn(columnId.toString());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Implementar autenticação");
        assertThat(result.get(0).columnId()).isEqualTo(columnId.toString());
    }

    @Test
    @DisplayName("create should persist task and open space in column")
    void create_ShouldPersistTask() {
        TaskRequestDTO request = new TaskRequestDTO(
                "Implementar autenticação", 2, now, null, false, List.of("backend"), columnId.toString());
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO result = taskService.create(request);

        assertThat(result.name()).isEqualTo("Implementar autenticação");
        assertThat(result.tags()).contains("backend");
        verify(taskRepository).shiftPositionsDown(columnId, 2);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("create should throw when column not found")
    void create_ShouldThrow_WhenColumnNotFound() {
        when(columnRepository.findById(columnId)).thenReturn(Optional.empty());
        TaskRequestDTO request = new TaskRequestDTO("X", 0, now, null, false, List.of(), columnId.toString());

        assertThatThrownBy(() -> taskService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Column");
    }

    @Test
    @DisplayName("update should close gap and open space when moving between columns")
    void update_ShouldChangeCompletedAndColumn() {
        UUID newColumnId = UUID.randomUUID();
        Column newColumn = Column.builder().id(newColumnId).name("Feito").position(2).board(column.getBoard()).build();

        TaskRequestDTO request = new TaskRequestDTO(
                "Implementar autenticação", 3, now, null, true, List.of("backend"), newColumnId.toString());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(columnRepository.findById(newColumnId)).thenReturn(Optional.of(newColumn));
        Task updated = Task.builder().id(taskId).name("Implementar autenticação").position(3)
                .createdAt(now).completed(true).tags(List.of("backend")).column(newColumn).build();
        when(taskRepository.save(any(Task.class))).thenReturn(updated);

        TaskResponseDTO result = taskService.update(taskId.toString(), request);

        assertThat(result.completed()).isTrue();
        assertThat(result.columnId()).isEqualTo(newColumnId.toString());
        verify(taskRepository).shiftPositionsUp(columnId, 0); // old pos
        verify(taskRepository).shiftPositionsDown(newColumnId, 3); // new pos
    }

    @Test
    @DisplayName("update should shift positions when moving UP in the same column")
    void update_ShouldShiftPositions_WhenMovingUp() {
        task.setPosition(5); // old position
        TaskRequestDTO request = new TaskRequestDTO(
                "Implementar autenticação", 2, now, null, false, List.of("backend"), columnId.toString());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.update(taskId.toString(), request);

        verify(taskRepository).shiftPositionsDownBetween(columnId, 2, 5);
        verify(taskRepository, never()).shiftPositionsUpBetween(any(), any(), any());
    }

    @Test
    @DisplayName("update should shift positions when moving DOWN in the same column")
    void update_ShouldShiftPositions_WhenMovingDown() {
        task.setPosition(1); // old position
        TaskRequestDTO request = new TaskRequestDTO(
                "Implementar autenticação", 4, now, null, false, List.of("backend"), columnId.toString());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.update(taskId.toString(), request);

        verify(taskRepository).shiftPositionsUpBetween(columnId, 1, 4);
        verify(taskRepository, never()).shiftPositionsDownBetween(any(), any(), any());
    }

    @Test
    @DisplayName("update should throw when task not found")
    void update_ShouldThrow_WhenTaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(taskId.toString(),
                new TaskRequestDTO("X", 0, now, null, false, List.of(), columnId.toString())))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task");
    }

    @Test
    @DisplayName("delete should close gap and remove task")
    void delete_ShouldRemoveTask() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.delete(taskId.toString());

        verify(taskRepository).shiftPositionsUp(columnId, 0);
        verify(taskRepository).delete(task);
    }

    @Test
    @DisplayName("delete should throw when task not found")
    void delete_ShouldThrow_WhenTaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.delete(taskId.toString()))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(taskRepository, never()).delete(any());
    }
}
