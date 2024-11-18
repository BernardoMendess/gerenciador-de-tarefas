package com.teste.task_manager.model.departamento;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "departamento")
public record Departamento (
        @Id long id,
        String nome
) {

    //SALVO NA BASE DE TESTE COM NOME "Recursos Humanos"
    public static Long ID_DEPARTAMENTO_RECURSOS_HUMANOS = 1L;
    //SALVO NA BASE DE TESTE COM NOME "Tecnologia da Informação"
    public static Long ID_DEPARTAMENTO_TI = 2L;
    //SALVO NA BASE DE TESTE COM NOME "Marketing"
    public static Long ID_DEPARTAMENTO_MARKETING = 3L;

}
