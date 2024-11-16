package com.teste.task_manager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "departamento")
public record Departamento (
        @Id long id,
        String nome
) {}
