package com.teste.task_manager.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@Table(name = "tarefa")
public class Tarefa {

    @Id
    private Long id;

    @NotNull
    private String titulo;

    @NotNull
    private String descricao;

    @NotNull
    private LocalDate prazo;

    @NotNull
    private long departamentoId;

    private Integer duracao;

    @With
    private Boolean finalizado;

    private Long pessoaId;
}
