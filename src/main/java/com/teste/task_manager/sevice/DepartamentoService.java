package com.teste.task_manager.sevice;

import com.teste.task_manager.model.Departamento;
import com.teste.task_manager.model.dao.DepartamentoDAO;
import com.teste.task_manager.model.dao.PessoaDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DepartamentoService {

    private DepartamentoDAO departamentoDAO;

    public List<Departamento> findAll(){
        return departamentoDAO.findAll();
    }
}
