package com.teste.task_manager.sevice;

import com.teste.task_manager.model.Tarefa;
import com.teste.task_manager.model.departamento.Departamento;
import com.teste.task_manager.model.dao.DepartamentoDAO;
import com.teste.task_manager.model.departamento.DepartamentoComDependencias;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DepartamentoService {

    private DepartamentoDAO departamentoDAO;

    private TarefaService tarefaService;

    private PessoaService pessoaService;

    public List<Departamento> findAll(){
        return departamentoDAO.findAll();
    }

    public List<DepartamentoComDependencias> findAllDepartamentosComDependencias(){
        val departamentos = departamentoDAO.findAll();
        return departamentos.stream().map(departamento->
                new DepartamentoComDependencias(
                        departamento,
                        pessoaService.countPessoasByDepartamentoId(departamento.id()),
                        tarefaService.countTarefasByDepartamentoId(departamento.id())
                )).toList();
    }

    public Departamento findObjById(long id){
        return departamentoDAO.findById(id).get();
    }
}
