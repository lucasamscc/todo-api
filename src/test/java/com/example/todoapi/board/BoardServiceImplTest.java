package com.example.todoapi.board;

import com.example.todoapi.board.dto.BoardRequestDTO;
import com.example.todoapi.board.dto.BoardResponseDTO;
import com.example.todoapi.board.entity.Board;
import com.example.todoapi.board.repository.BoardRepository;
import com.example.todoapi.board.service.BoardServiceImpl;
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
@DisplayName("BoardServiceImpl Unit Tests")
class BoardServiceImplTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardServiceImpl boardService;

    private Board board;
    private UUID boardId;

    @BeforeEach
    void setUp() {
        boardId = UUID.randomUUID();
        board = Board.builder()
                .id(boardId)
                .name("Projeto A")
                .build();
    }

    @Test
    @DisplayName("findAll should return list of all boards")
    void findAll_ShouldReturnAllBoards() {
        when(boardRepository.findAll()).thenReturn(List.of(board));

        List<BoardResponseDTO> result = boardService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Projeto A");
        assertThat(result.get(0).id()).isEqualTo(boardId.toString());
        verify(boardRepository).findAll();
    }

    @Test
    @DisplayName("findAll should return empty list when no boards exist")
    void findAll_ShouldReturnEmptyList_WhenNoBoardsExist() {
        when(boardRepository.findAll()).thenReturn(List.of());

        List<BoardResponseDTO> result = boardService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("create should persist and return new board")
    void create_ShouldPersistAndReturnBoard() {
        BoardRequestDTO request = new BoardRequestDTO("Projeto A");
        when(boardRepository.save(any(Board.class))).thenReturn(board);

        BoardResponseDTO result = boardService.create(request);

        assertThat(result.name()).isEqualTo("Projeto A");
        assertThat(result.id()).isEqualTo(boardId.toString());
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    @DisplayName("update should change name and return updated board")
    void update_ShouldUpdateNameAndReturn() {
        BoardRequestDTO request = new BoardRequestDTO("Projeto A Atualizado");
        Board updated = Board.builder().id(boardId).name("Projeto A Atualizado").build();

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(boardRepository.save(any(Board.class))).thenReturn(updated);

        BoardResponseDTO result = boardService.update(boardId.toString(), request);

        assertThat(result.name()).isEqualTo("Projeto A Atualizado");
        verify(boardRepository).findById(boardId);
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    @DisplayName("update should throw ResourceNotFoundException when board not found")
    void update_ShouldThrow_WhenBoardNotFound() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.update(boardId.toString(), new BoardRequestDTO("X")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Board");
    }

    @Test
    @DisplayName("delete should remove board when it exists")
    void delete_ShouldRemoveBoard() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        boardService.delete(boardId.toString());

        verify(boardRepository).delete(board);
    }

    @Test
    @DisplayName("delete should throw ResourceNotFoundException when board not found")
    void delete_ShouldThrow_WhenBoardNotFound() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.delete(boardId.toString()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Board");

        verify(boardRepository, never()).delete(any());
    }
}
