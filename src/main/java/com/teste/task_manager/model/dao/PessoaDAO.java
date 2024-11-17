package com.teste.task_manager.model.dao;

import com.teste.task_manager.model.pessoa.Pessoa;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PessoaDAO extends CrudRepository<Pessoa, Long>{

    List<Pessoa> findAll();

    @Query("SELECT COUNT(*) FROM pessoa p " +
            "WHERE p.departamento_id = :id ")
    Integer countPessoasByDepartamentoId(long id);

    @Query("SELECT * FROM pessoa p " +
            "WHERE p.nome LIKE :nome ")
    List<Pessoa> findByNomeLike(String nome);
}
