package com.teste.task_manager.controller;

import com.teste.task_manager.model.pessoa.Pessoa;
import com.teste.task_manager.model.pessoa.PessoaComDependencias;
import com.teste.task_manager.model.pessoa.PessoaGastos;
import com.teste.task_manager.sevice.DepartamentoService;
import com.teste.task_manager.sevice.PessoaComDependenciaService;
import com.teste.task_manager.sevice.PessoaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/pessoas")
@AllArgsConstructor
public class PessoaController {

    private PessoaService pessoaService;

    private PessoaComDependenciaService pessoaComDependenciaService;

    //Adicionar um pessoa (post/pessoas)
    @PostMapping("")
    public ResponseEntity<Pessoa> save(@RequestBody @Valid Pessoa pessoa) {
        val pessoaSalva = pessoaService.salvarPessoa(pessoa);
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
    }

    //Alterar um pessoa (put/pessoas/{id})
    @PutMapping("/{id}")
    public ResponseEntity<Pessoa> edit(@PathVariable Long id, @RequestBody @Valid Pessoa pessoa) {
        val pessoaSalva = pessoaService.alterarPessoa(pessoa.withId(id));
        return ResponseEntity.status(HttpStatus.OK).body(pessoaSalva);
    }

    //Remover pessoa (delete/pessoas/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pessoaService.deletarPessoa(id);
        return ResponseEntity.ok().build();
    }

    //Listar pessoas trazendo nome, departamento, total horas gastas nas tarefas.(get/pessoas)
    @GetMapping("")
    public ResponseEntity<List<PessoaComDependencias>> listPessoas(){
        val pessoas = pessoaComDependenciaService.findAllPessoas();
        return ResponseEntity.status(HttpStatus.OK).body(pessoas);
    }

    //Buscar pessoas por nome e período, retorna média de horas gastas por tarefa. (get/pessoas/gastos)
    @GetMapping("/gastos")
    public ResponseEntity<List<PessoaGastos>> buscaPessoas(@RequestParam String nome,
                                                           @RequestParam String dataInicial,
                                                           @RequestParam String dataFinal) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate inicio = LocalDate.parse(dataInicial, formatter);
        LocalDate fim = LocalDate.parse(dataFinal, formatter);
        val pessoas = pessoaComDependenciaService.findPessoasByNomeEPeriodo(nome, inicio, fim);
        return ResponseEntity.status(HttpStatus.OK).body(pessoas);
    }
}
