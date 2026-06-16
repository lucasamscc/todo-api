package com.example.todoapi.board.service;

import com.example.todoapi.board.dto.BoardRequestDTO;
import com.example.todoapi.board.dto.BoardResponseDTO;
import com.example.todoapi.board.entity.Board;
import com.example.todoapi.board.repository.BoardRepository;
import com.example.todoapi.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponseDTO> findAll() {
        return boardRepository.findAll()
                .stream()
                .map(BoardResponseDTO::from)
                .toList();
    }

    @Override
    @Transactional
    public BoardResponseDTO create(BoardRequestDTO request) {
        Board board = Board.builder()
                .name(request.name())
                .build();
        return BoardResponseDTO.from(boardRepository.save(board));
    }

    @Override
    @Transactional
    public BoardResponseDTO update(String id, BoardRequestDTO request) {
        Board board = findBoardById(id);
        board.setName(request.name());
        return BoardResponseDTO.from(boardRepository.save(board));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Board board = findBoardById(id);
        boardRepository.delete(board);
    }

    private Board findBoardById(String id) {
        return boardRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Board", id));
    }
}
