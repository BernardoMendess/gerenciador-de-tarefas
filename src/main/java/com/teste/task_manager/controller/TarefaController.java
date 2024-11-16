package com.teste.task_manager.controller;

import com.teste.task_manager.model.Tarefa;
import com.teste.task_manager.sevice.DepartamentoService;
import com.teste.task_manager.sevice.TarefaService;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/tarefas")
@AllArgsConstructor
public class TarefaController {

    private TarefaService tarefaService;

    private DepartamentoService departamentoService;

    //Adicionar um tarefa (post/tarefas)
    @PostMapping("")
    public String save(Tarefa tarefa) {
        val tarefaSalva = tarefaService.salvarTarefa(tarefa);
        return "redirect:/tarefas/" + tarefaSalva.getId() + "/edit";
    }

    @PutMapping("/finalizar/{id}")
    public String finalizarTarefa(@PathVariable long id) {
        tarefaService.finalizarTarefa(id);
        return "redirect:/tarefas";
    }

    @GetMapping("/new")
    public ModelAndView newTarefa(){
        return new ModelAndView("tarefas/new")
                .addObject("departamentos", departamentoService.findAll());
    }
}
