package com.example.todoapi.column.service;

import com.example.todoapi.board.entity.Board;
import com.example.todoapi.board.repository.BoardRepository;
import com.example.todoapi.column.dto.ColumnRequestDTO;
import com.example.todoapi.column.dto.ColumnResponseDTO;
import com.example.todoapi.column.entity.Column;
import com.example.todoapi.column.repository.ColumnRepository;
import com.example.todoapi.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ColumnServiceImpl implements ColumnService {

    private final ColumnRepository columnRepository;
    private final BoardRepository boardRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ColumnResponseDTO> findByBoard(String boardId) {
        return columnRepository.findByBoardIdOrderByPosition(UUID.fromString(boardId))
                .stream()
                .map(ColumnResponseDTO::from)
                .toList();
    }

    @Override
    @Transactional
    public ColumnResponseDTO create(ColumnRequestDTO request) {
        Board board = findBoardById(request.boardId());

        columnRepository.shiftPositionsDown(board.getId(), request.position());

        Column column = Column.builder()
                .name(request.name())
                .position(request.position())
                .board(board)
                .build();
        return ColumnResponseDTO.from(columnRepository.save(column));
    }

    @Override
    @Transactional
    public ColumnResponseDTO update(String id, ColumnRequestDTO request) {
        Column column = findColumnById(id);
        Board newBoard = findBoardById(request.boardId());

        reorderPositions(
                column.getBoard().getId(), newBoard.getId(),
                column.getPosition(), request.position());

        column.setName(request.name());
        column.setPosition(request.position());
        column.setBoard(newBoard);

        return ColumnResponseDTO.from(columnRepository.save(column));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Column column = findColumnById(id);

        columnRepository.shiftPositionsUp(column.getBoard().getId(), column.getPosition());

        columnRepository.delete(column);
    }

    private void reorderPositions(UUID oldBoardId, UUID newBoardId,
            Integer oldPosition, Integer newPosition) {
        boolean boardChanged = !oldBoardId.equals(newBoardId);
        boolean positionChanged = !oldPosition.equals(newPosition);

        if (boardChanged) {
            columnRepository.shiftPositionsUp(oldBoardId, oldPosition);
            columnRepository.shiftPositionsDown(newBoardId, newPosition);
        } else if (positionChanged) {
            if (newPosition < oldPosition) {
                columnRepository.shiftPositionsDownBetween(oldBoardId, newPosition, oldPosition);
            } else {
                columnRepository.shiftPositionsUpBetween(oldBoardId, oldPosition, newPosition);
            }
        }
    }

    private Column findColumnById(String id) {
        return columnRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Column", id));
    }

    private Board findBoardById(String id) {
        return boardRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Board", id));
    }
}
