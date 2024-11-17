package com.teste.task_manager.model.pessoa;

import com.teste.task_manager.model.Tarefa;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@AllArgsConstructor
@Data
@Table(name = "pessoa")
public class Pessoa {

    @Id
    private Long id;

    @NotNull
    private String nome;

    @NotNull
    private Long departamentoId;

    @MappedCollection(idColumn = "pessoa_id", keyColumn = "pessoa_id")
    private List<Tarefa> tarefas;
}
