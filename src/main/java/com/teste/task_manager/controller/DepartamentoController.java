package com.teste.task_manager.controller;

import com.teste.task_manager.model.departamento.DepartamentoComDependencias;
import com.teste.task_manager.sevice.DepartamentoService;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/departamentos")
@AllArgsConstructor
public class DepartamentoController {

    private DepartamentoService departamentoService;

    //Listar departamento e quantidade de pessoas e tarefas (get/departamentos)
    @GetMapping("")
    public ResponseEntity<List<DepartamentoComDependencias>> listTarefasAntigasSemPessoaAlocada() {
        val departamentos =  departamentoService.findAllDepartamentosComDependencias();
        return ResponseEntity.status(HttpStatus.OK).body(departamentos);
    }
}
