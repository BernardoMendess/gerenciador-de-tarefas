package com.teste.task_manager.model.departamento;

public record DepartamentoComDependencias (
        Departamento departamento,
        int quantidadePessoas,
        int quantidadeTarefas
){
}
