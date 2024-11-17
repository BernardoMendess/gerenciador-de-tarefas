package com.teste.task_manager.model.dao;

import com.teste.task_manager.model.Tarefa;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TarefaDAO extends CrudRepository<Tarefa, Long> {

    @Query("SELECT * FROM tarefa t " +
            "WHERE t.pessoa_id IS NULL " +
            "ORDER BY t.prazo ASC " +
            "LIMIT 3")
    List<Tarefa> findTarefasMaisAntigasSemPessoaAlocada();

    @Query("SELECT COUNT(*) FROM tarefa t " +
            "WHERE t.departamento_id = :id ")
    Integer countByDepartamentoId(long id);

    @Query("SELECT SUM(duracao) FROM tarefa t " +
            "WHERE t.pessoa_id = :pessoaId ")
    Integer findTotalHorasGastasByPessoaId(long pessoaId);

    @Query("SELECT AVG(duracao) FROM tarefa t " +
            "WHERE t.pessoa_id = :pessoaId " +
            "AND t.data_inicial >= :dataInicial " +
            "AND t.data_final <= :dataFinal")
    Double findMediaByPessoaIdAndPeriodo(long pessoaId, LocalDateTime dataInicial, LocalDateTime dataFinal);
}
