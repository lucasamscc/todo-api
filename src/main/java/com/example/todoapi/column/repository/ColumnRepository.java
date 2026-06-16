package com.example.todoapi.column.repository;

import com.example.todoapi.column.entity.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ColumnRepository extends JpaRepository<Column, UUID> {

    List<Column> findByBoardIdOrderByPosition(UUID boardId);

    @Modifying
    @Query("UPDATE Column c SET c.position = c.position + 1 WHERE c.board.id = :boardId AND c.position >= :startPosition")
    void shiftPositionsDown(@Param("boardId") UUID boardId, @Param("startPosition") Integer startPosition);

    @Modifying
    @Query("UPDATE Column c SET c.position = c.position - 1 WHERE c.board.id = :boardId AND c.position >= :startPosition")
    void shiftPositionsUp(@Param("boardId") UUID boardId, @Param("startPosition") Integer startPosition);

    @Modifying
    @Query("UPDATE Column c SET c.position = c.position + 1 WHERE c.board.id = :boardId AND c.position >= :startPosition AND c.position < :endPosition")
    void shiftPositionsDownBetween(@Param("boardId") UUID boardId, @Param("startPosition") Integer startPosition, @Param("endPosition") Integer endPosition);

    @Modifying
    @Query("UPDATE Column c SET c.position = c.position - 1 WHERE c.board.id = :boardId AND c.position > :startPosition AND c.position <= :endPosition")
    void shiftPositionsUpBetween(@Param("boardId") UUID boardId, @Param("startPosition") Integer startPosition, @Param("endPosition") Integer endPosition);
}
