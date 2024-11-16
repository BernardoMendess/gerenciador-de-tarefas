package com.teste.task_manager.sevice;

import com.teste.task_manager.model.Tarefa;
import com.teste.task_manager.model.dao.TarefaDAO;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TarefaService {

    private TarefaDAO tarefaDAO;

    public Tarefa salvarTarefa(Tarefa tarefa){
        return tarefaDAO.save(tarefa.withFinalizado(false));
    }

    public void finalizarTarefa(long id){
        val tarefa = tarefaDAO.findById(id).orElseThrow(() ->
                new RuntimeException("Tarefa não encontrada"));
        tarefaDAO.save(tarefa.withFinalizado(true));
    }
}

