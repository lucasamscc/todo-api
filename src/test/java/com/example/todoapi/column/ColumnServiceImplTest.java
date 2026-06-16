package com.example.todoapi.column;

import com.example.todoapi.board.entity.Board;
import com.example.todoapi.board.repository.BoardRepository;
import com.example.todoapi.column.dto.ColumnRequestDTO;
import com.example.todoapi.column.dto.ColumnResponseDTO;
import com.example.todoapi.column.entity.Column;
import com.example.todoapi.column.repository.ColumnRepository;
import com.example.todoapi.column.service.ColumnServiceImpl;
import com.example.todoapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ColumnServiceImpl Unit Tests")
class ColumnServiceImplTest {

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private ColumnServiceImpl columnService;

    private UUID boardId;
    private UUID columnId;
    private Board board;
    private Column column;

    @BeforeEach
    void setUp() {
        boardId = UUID.randomUUID();
        columnId = UUID.randomUUID();
        board = Board.builder().id(boardId).name("Board A").build();
        column = Column.builder().id(columnId).name("A Fazer").position(0).board(board).build();
    }

    @Test
    @DisplayName("findByBoard should return columns ordered by position")
    void findByBoard_ShouldReturnOrderedColumns() {
        when(columnRepository.findByBoardIdOrderByPosition(boardId)).thenReturn(List.of(column));

        List<ColumnResponseDTO> result = columnService.findByBoard(boardId.toString());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("A Fazer");
        assertThat(result.get(0).position()).isEqualTo(0);
        assertThat(result.get(0).boardId()).isEqualTo(boardId.toString());
    }

    @Test
    @DisplayName("create should persist column linked to board")
    void create_ShouldPersistAndReturnColumn() {
        ColumnRequestDTO request = new ColumnRequestDTO("A Fazer", 0, boardId.toString());
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(columnRepository.save(any(Column.class))).thenReturn(column);

        ColumnResponseDTO result = columnService.create(request);

        assertThat(result.name()).isEqualTo("A Fazer");
        assertThat(result.boardId()).isEqualTo(boardId.toString());
        verify(columnRepository).save(any(Column.class));
    }

    @Test
    @DisplayName("create should throw when board not found")
    void create_ShouldThrow_WhenBoardNotFound() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());
        ColumnRequestDTO request = new ColumnRequestDTO("A Fazer", 0, boardId.toString());

        assertThatThrownBy(() -> columnService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Board");
    }

    @Test
    @DisplayName("update should modify column fields")
    void update_ShouldUpdateColumn() {
        ColumnRequestDTO request = new ColumnRequestDTO("A Fazer Urgente", 1, boardId.toString());
        Column updated = Column.builder().id(columnId).name("A Fazer Urgente").position(1).board(board).build();

        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(columnRepository.save(any(Column.class))).thenReturn(updated);

        ColumnResponseDTO result = columnService.update(columnId.toString(), request);

        assertThat(result.name()).isEqualTo("A Fazer Urgente");
        assertThat(result.position()).isEqualTo(1);
    }

    @Test
    @DisplayName("update should throw when column not found")
    void update_ShouldThrow_WhenColumnNotFound() {
        when(columnRepository.findById(columnId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> columnService.update(columnId.toString(), new ColumnRequestDTO("X", 0, boardId.toString())))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Column");
    }

    @Test
    @DisplayName("delete should remove column")
    void delete_ShouldRemoveColumn() {
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));

        columnService.delete(columnId.toString());

        verify(columnRepository).delete(column);
    }

    @Test
    @DisplayName("delete should throw when column not found")
    void delete_ShouldThrow_WhenColumnNotFound() {
        when(columnRepository.findById(columnId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> columnService.delete(columnId.toString()))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(columnRepository, never()).delete(any());
    }
}
