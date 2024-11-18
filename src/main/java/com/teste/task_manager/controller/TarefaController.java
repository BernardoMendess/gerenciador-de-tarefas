package com.teste.task_manager.controller;

import com.teste.task_manager.model.Tarefa;
import com.teste.task_manager.sevice.DepartamentoService;
import com.teste.task_manager.sevice.PessoaService;
import com.teste.task_manager.sevice.TarefaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/tarefas")
@AllArgsConstructor
public class TarefaController {

    private TarefaService tarefaService;

    private DepartamentoService departamentoService;

    private PessoaService pessoaService;

    //Adicionar um tarefa (post/tarefas)
    @PostMapping("")
    public ResponseEntity<Tarefa> save(@RequestBody @Valid Tarefa tarefa) {
        val tarefaSalva = tarefaService.salvarTarefa(tarefa);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarefaSalva);
    }

    //Alocar uma pessoa na tarefa que tenha o mesmo departamento (put/tarefas/alocar/{id})
    @PutMapping("/alocar/{id}")
    public ResponseEntity<Tarefa> alocarPessoaNaTarefa(@PathVariable long id, @RequestBody Long pessoaId){
         val tarefaSalva = tarefaService.alocarPessoaNaTarefa(id, pessoaId);
         return ResponseEntity.status(HttpStatus.OK).body(tarefaSalva);
    }

    //Finalizar a tarefa (put/tarefas/finalizar/{id})
    @PutMapping("/finalizar/{id}")
    public ResponseEntity<Tarefa> finalizarTarefa(@PathVariable long id) {
         val tarefaFinalizada = tarefaService.finalizarTarefa(id);
         return ResponseEntity.status(HttpStatus.OK).body(tarefaFinalizada);
    }

    //Listar 3 tarefas que estejam sem pessoa alocada com os prazos mais antigos. (get/tarefas/pendentes)
    @GetMapping("/pendentes")
    public ResponseEntity<List<Tarefa>> listTarefasAntigasSemPessoaAlocada() {
        val tarefasAntigas =  tarefaService.findTarefasMaisAntigasSemPessoaAlocada();
        return ResponseEntity.status(HttpStatus.OK).body(tarefasAntigas);
    }
}
