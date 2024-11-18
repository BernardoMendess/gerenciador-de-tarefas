package com.teste.task_manager.controller;

import com.teste.task_manager.model.Tarefa;
import com.teste.task_manager.model.departamento.Departamento;
import com.teste.task_manager.model.pessoa.Pessoa;
import com.teste.task_manager.sevice.PessoaService;
import com.teste.task_manager.sevice.TarefaService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.teste.task_manager.model.departamento.Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS;
import static com.teste.task_manager.model.departamento.Departamento.ID_DEPARTAMENTO_TI;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class DepartamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private PessoaService pessoaService;

    @Test
    @Rollback
    @Transactional
    public void quando_buscar_pessoas_por_nome_e_periodo_entao_retorna_200_com_dados() throws Exception {

        val pessoasRH = List.of(
                pessoaService.salvarPessoa(new Pessoa(null, "Carlos Souza", ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of())),
                pessoaService.salvarPessoa(new Pessoa(null, "Maria Jose", ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of())));

        val tarefasRH = List.of(
                tarefaService.salvarTarefa(new Tarefa(null, "Tarefa 1 RH",
                        "Tarefa 1 RH descrição", LocalDate.now(),
                        ID_DEPARTAMENTO_RECURSOS_HUMANOS, 10,
                        null, null, null, null)),
                tarefaService.salvarTarefa(new Tarefa(null, "Tarefa 2",
                        "Tarefa 2 descrição", LocalDate.now(),
                        ID_DEPARTAMENTO_RECURSOS_HUMANOS, 10,
                        null, null, null, null)));

        val pessoasTI = List.of(pessoaService.salvarPessoa(
                new Pessoa(null, "Mateus Souza", Departamento.ID_DEPARTAMENTO_TI, List.of())));

        val tarefasTI = List.of(
                tarefaService.salvarTarefa(new Tarefa(null, "Tarefa 1 TI",
                        "Tarefa 1 TI descrição", LocalDate.now(),
                        Departamento.ID_DEPARTAMENTO_TI, 10,
                        null, null, null, null)));

        mockMvc.perform(get("/departamentos"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].departamento.id").value(ID_DEPARTAMENTO_RECURSOS_HUMANOS))
                .andExpect(jsonPath("$[0].quantidadePessoas").value(pessoasRH.size()))
                .andExpect(jsonPath("$[0].quantidadeTarefas").value(tarefasRH.size()))
                .andExpect(jsonPath("$[1].departamento.id").value(ID_DEPARTAMENTO_TI))
                .andExpect(jsonPath("$[1].quantidadePessoas").value(pessoasTI.size()))
                .andExpect(jsonPath("$[1].quantidadePessoas").value(tarefasTI.size()));
    }
}
