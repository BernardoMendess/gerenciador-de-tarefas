package com.teste.task_manager.controller;

import com.teste.task_manager.model.Pessoa;
import com.teste.task_manager.sevice.DepartamentoService;
import com.teste.task_manager.sevice.PessoaService;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/pessoas")
@AllArgsConstructor
public class PessoaController {

    private PessoaService pessoaService;

    private DepartamentoService departamentoService;

    //Adicionar um pessoa (post/pessoas)
    @PostMapping("")
    public String save(Pessoa pessoa) {
        val pessoaSalva = pessoaService.salvarPessoa(pessoa);
        return "redirect:/pessoas/" + pessoaSalva.getId() + "/edit";
    }

    //Alterar um pessoa (put/pessoas/{id})
    @PutMapping("/{id}")
    public String edit(@PathVariable Long id, Pessoa pessoa) {
        val pessoaSalva = pessoaService.alterarPessoa(pessoa);
        return "redirect:/pessoas/" + pessoaSalva.getId() + "/edit";
    }

    //Remover pessoa (delete/pessoas/{id})
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        pessoaService.deletarPessoa(id);
        return "redirect:/pessoas";
    }

    //Listar pessoas trazendo nome, departamento, total horas gastas nas tarefas.(get/pessoas)
    @GetMapping("")
    public ModelAndView list(){
        return new ModelAndView("pessoas/list")
                .addObject("pessoa", new Pessoa(null, "", null, List.of()))
                .addObject("departamentos", departamentoService.findAll());
    }


    @GetMapping("/new")
    public ModelAndView newPessoa(){
        return new ModelAndView("pessoas/new")
                .addObject("departamentos", departamentoService.findAll());
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editPessoa(@PathVariable Long id){
        return new ModelAndView("pessoas/edit")
                .addObject("pessoa", pessoaService.findById(id))
                .addObject("departamentos", departamentoService.findAll());
    }
}
