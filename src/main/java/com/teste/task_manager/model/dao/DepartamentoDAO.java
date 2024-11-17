package com.teste.task_manager.model.dao;

import com.teste.task_manager.model.departamento.Departamento;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DepartamentoDAO extends CrudRepository<Departamento, Long> {

    List<Departamento> findAll();

}
