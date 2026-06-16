package com.example.todoapi.board.service;

import com.example.todoapi.board.dto.BoardRequestDTO;
import com.example.todoapi.board.dto.BoardResponseDTO;

import java.util.List;

public interface BoardService {

    List<BoardResponseDTO> findAll();

    BoardResponseDTO create(BoardRequestDTO request);

    BoardResponseDTO update(String id, BoardRequestDTO request);

    void delete(String id);
}
