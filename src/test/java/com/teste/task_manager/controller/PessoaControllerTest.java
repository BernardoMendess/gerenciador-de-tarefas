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
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class PessoaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private TarefaService tarefaService;

    @Test
    @Rollback
    @Transactional
    public void quando_post_salvar_pessoa_entao_ok() throws Exception {
        val pessoa = criaPessoa();

        mockMvc.perform(post("/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nome": "%s",
                                "departamentoId": "%d"
                            }
                            """.formatted(pessoa.getNome(), pessoa.getDepartamentoId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nome").value(pessoa.getNome()))
                .andExpect(jsonPath("$.departamentoId").value(pessoa.getDepartamentoId()));
    }

    @Test
    @Rollback
    @Transactional
    public void quando_put_editar_pessoa_entao_ok() throws Exception {
        val pessoa = pessoaService.salvarPessoa(criaPessoa());

        mockMvc.perform(put("/pessoas/" + pessoa.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "id": "%d",
                                "nome": "%s",
                                "departamentoId": "%d"
                            }
                            """.formatted(pessoa.getId(), pessoa.getNome(), pessoa.getDepartamentoId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pessoa.getId()))
                .andExpect(jsonPath("$.nome").value(pessoa.getNome()))
                .andExpect(jsonPath("$.departamentoId").value(pessoa.getDepartamentoId()));
    }

    @Test
    public void quando_post_salvar_pessoa_com_dados_invalidos_entao_quebra() throws Exception {

        mockMvc.perform(post("/pessoas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {
                                    "nome": null,
                                    "departamentoId": 1
                                }
                            """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @Transactional
    public void quando_delete_pessoa_entao_ok() throws Exception {
        val pessoa = pessoaService.salvarPessoa(criaPessoa());

        mockMvc.perform(delete("/pessoas/" + pessoa.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @Rollback
    @Transactional
    public void quando_listar_pessoas_entao_retorna_200_com_dados() throws Exception {
        val pessoa = pessoaService.salvarPessoa( new Pessoa(null, "Marcia Silva", Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of()));

        val tarefa1 = tarefaService.salvarTarefa(new Tarefa(null, "Tarefa Exemplo", "Tarefa exemplo descrição", LocalDate.now(),
                Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS, 10L, null, pessoa.getId(),
                null, null));

        val tarefa2 = tarefaService.salvarTarefa(new Tarefa(null, "Tarefa Exemplo", "Tarefa exemplo descrição", LocalDate.now(),
                Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS, 5L, null, pessoa.getId(),
                null, null));

        mockMvc.perform(get("/pessoas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value(pessoa.getNome()))
                .andExpect(jsonPath("$[0].departamento").value("Recursos Humanos"))
                .andExpect(jsonPath("$[0].horasGastas").value(tarefa1.getDuracao() + tarefa2.getDuracao()));
    }

    @Test
    @Rollback
    @Transactional
    public void quando_buscar_pessoas_por_nome_e_periodo_entao_retorna_200_com_dados() throws Exception {
        val pessoa1 = pessoaService.salvarPessoa( new Pessoa(null, "João Silva", Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of()));

        val tarefa1P1 = tarefaService.salvarTarefa(new Tarefa(null, "Tarefa Exemplo", "Tarefa exemplo descrição", LocalDate.now(),
                Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS, 10L, null, pessoa1.getId(),
                LocalDateTime.now(), LocalDateTime.now()));

        val tarefa2P1 = tarefaService.salvarTarefa(new Tarefa(null, "Tarefa Exemplo", "Tarefa exemplo descrição", LocalDate.now(),
                Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS, 5L, null, pessoa1.getId(),
                LocalDateTime.now(), LocalDateTime.now()));

        val pessoa2 = pessoaService.salvarPessoa( new Pessoa(null, "Marcos Silva", Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of()));

        val tarefa1P2 = tarefaService.salvarTarefa(new Tarefa(null, "Tarefa Exemplo", "Tarefa exemplo descrição", LocalDate.now(),
                Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS, 10L, null, pessoa2.getId(),
                LocalDateTime.now(), LocalDateTime.now()));

        mockMvc.perform(get("/pessoas/gastos")
                        .param("nome", "Silva")
                        .param("dataInicial", "2024-11-01")
                        .param("dataFinal", "2025-01-31"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value(pessoa1.getNome()))
                .andExpect(jsonPath("$[0].mediaHorasGastas").value((tarefa1P1.getDuracao() + tarefa2P1.getDuracao())/2))

                .andExpect(jsonPath("$[1].nome").value(pessoa2.getNome()))
                .andExpect(jsonPath("$[1].mediaHorasGastas").value(tarefa1P2.getDuracao()));
    }

    private Pessoa criaPessoa(){
        return new Pessoa(null, "Bernardo Mendes", Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of());
    }
}
