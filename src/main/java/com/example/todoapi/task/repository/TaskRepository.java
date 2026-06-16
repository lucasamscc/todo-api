package com.example.todoapi.task.repository;

import com.example.todoapi.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByColumnIdOrderByPosition(UUID columnId);

    // Abre espaço na coluna: empurra para baixo (position + 1) todas as tarefas com posição >= à desejada
    @Modifying
    @Query("UPDATE Task t SET t.position = t.position + 1 " +
           "WHERE t.column.id = :columnId AND t.position >= :position")
    void shiftPositionsDown(@Param("columnId") UUID columnId,
                            @Param("position") Integer position);

    // Fecha buraco na coluna: puxa para cima (position - 1) todas as tarefas com posição > à que saiu
    @Modifying
    @Query("UPDATE Task t SET t.position = t.position - 1 " +
           "WHERE t.column.id = :columnId AND t.position > :position")
    void shiftPositionsUp(@Param("columnId") UUID columnId,
                          @Param("position") Integer position);

    // Mesma coluna - tarefa subiu: empurra para baixo as tarefas entre a nova e a antiga posição
    @Modifying
    @Query("UPDATE Task t SET t.position = t.position + 1 " +
           "WHERE t.column.id = :columnId AND t.position >= :newPosition AND t.position < :oldPosition")
    void shiftPositionsDownBetween(@Param("columnId") UUID columnId,
                                   @Param("newPosition") Integer newPosition,
                                   @Param("oldPosition") Integer oldPosition);

    // Mesma coluna - tarefa desceu: puxa para cima as tarefas entre a antiga e a nova posição
    @Modifying
    @Query("UPDATE Task t SET t.position = t.position - 1 " +
           "WHERE t.column.id = :columnId AND t.position > :oldPosition AND t.position <= :newPosition")
    void shiftPositionsUpBetween(@Param("columnId") UUID columnId,
                                 @Param("oldPosition") Integer oldPosition,
                                 @Param("newPosition") Integer newPosition);
}
