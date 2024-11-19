package com.teste.task_manager.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import lombok.val;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
@Data
@Table(name = "tarefa")
public class Tarefa {

    @Id private Long id;

    @NotNull private String titulo;

    @NotNull private String descricao;

    @NotNull private LocalDate prazo;

    @NotNull @With private long departamentoId;

    @NotNull private int duracao;

    @With private Boolean finalizado;

    @With private Long pessoaId;

    //Adicionei os campos data inicial e data final para filtrar as tarefas pelo prazo
    @With private LocalDateTime dataInicial;

    private LocalDateTime dataFinal;

    public boolean isFinalizado() {
        return finalizado;
    }
}
