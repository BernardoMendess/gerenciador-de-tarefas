package com.teste.task_manager.model.dao;

import com.teste.task_manager.model.Tarefa;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TarefaDAO extends CrudRepository<Tarefa, Long> {

    @Query("SELECT * FROM tarefa t " +
            "ORDER BY t.prazo ASC " +
            "LIMIT 3")
    List<Tarefa> findTarefasMaisAntigasSemPessoaAlocada();
}
