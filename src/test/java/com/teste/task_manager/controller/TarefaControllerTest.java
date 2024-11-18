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
import java.util.List;

import static com.teste.task_manager.model.departamento.Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class TarefaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private TarefaService tarefaService;

    @Test
    @Rollback
    @Transactional
    public void quando_post_salvar_tarefa_entao_ok() throws Exception {
        val tarefa = criaTarefa();

        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "titulo": "%s",
                                "descricao": "%s",
                                "prazo": "2025-11-18",
                                "departamentoId": "%d",
                                "duracao": "%d"
                            }
                            """.formatted(tarefa.getTitulo(), tarefa.getDescricao(), tarefa.getDepartamentoId(), tarefa.getDuracao())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.titulo").value(tarefa.getTitulo()))
                .andExpect(jsonPath("$.descricao").value(tarefa.getDescricao()))
                .andExpect(jsonPath("$.departamentoId").value(tarefa.getDepartamentoId()));
    }

    @Test
    @Rollback
    @Transactional
    public void quando_recebe_tarefa_com_dados_invalidos_entao_quebra() throws Exception {
        val tarefa = criaTarefa();

        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "titulo": null,
                                "descricao": null,
                                "prazo": null,
                                "departamentoId": null,
                                "duracao": null
                            }
                            """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @Transactional
    public void quando_put_alocar_pessoa_a_tarefa_entao_ok() throws Exception {
        val pessoa = pessoaService.salvarPessoa(criaPessoa());
        val tarefa = tarefaService.salvarTarefa(criaTarefa());

        mockMvc.perform(put("/tarefas/alocar/" + tarefa.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(pessoa.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tarefa.getId()))
                .andExpect(jsonPath("$.titulo").value(tarefa.getTitulo()))
                .andExpect(jsonPath("$.descricao").value(tarefa.getDescricao()))
                .andExpect(jsonPath("$.departamentoId").value(tarefa.getDepartamentoId()))
                .andExpect(jsonPath("$.dataInicial").isNotEmpty());
    }

    @Test
    @Rollback
    @Transactional
    public void quando_put_finalizar_tarefa_entao_ok() throws Exception {
        val pessoa = pessoaService.salvarPessoa(criaPessoa());
        val tarefa = tarefaService.salvarTarefa(criaTarefa());
        tarefaService.alocarPessoaNaTarefa(tarefa.getId(), pessoa.getId());

        mockMvc.perform(put("/tarefas/finalizar/" + tarefa.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tarefa.getId()))
                .andExpect(jsonPath("$.titulo").value(tarefa.getTitulo()))
                .andExpect(jsonPath("$.descricao").value(tarefa.getDescricao()))
                .andExpect(jsonPath("$.departamentoId").value(tarefa.getDepartamentoId()))
                .andExpect(jsonPath("$.dataInicial").isNotEmpty())
                .andExpect(jsonPath("$.dataFinal").isNotEmpty())
                .andExpect(jsonPath("$.duracao").isNotEmpty())
                .andExpect(jsonPath("$.finalizado").value(true));
    }

    @Test
    @Rollback
    @Transactional
    public void quando_get_listar_tres_tarefas_mais_antigas_sem_pessoa_alocada_entao_ok() throws Exception {
        //P-plus M-minus
        val tarefaP10Dias = tarefaService.salvarTarefa(criaTarefaRHComTituloEDataInicial("Tarefa 1 RH", LocalDate.now().plusDays(10)));
        val tarefaP15Dias =  tarefaService.salvarTarefa(criaTarefaRHComTituloEDataInicial("Tarefa 4 RH", LocalDate.now().plusDays(15)));
        val tarefaP20Dias =  tarefaService.salvarTarefa(criaTarefaRHComTituloEDataInicial("Tarefa 5 RH", LocalDate.now().plusDays(20)));
        val tarefaHoje =  tarefaService.salvarTarefa(criaTarefaRHComTituloEDataInicial("Tarefa 3 RH", LocalDate.now()));
        val tarefaM5Dias =   tarefaService.salvarTarefa(criaTarefaRHComTituloEDataInicial("Tarefa 2 RH", LocalDate.now().minusDays(5)));

        mockMvc.perform(get("/tarefas/pendentes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(tarefaM5Dias.getId()))
                .andExpect(jsonPath("$[1].id").value(tarefaHoje.getId()))
                .andExpect(jsonPath("$[2].id").value(tarefaP10Dias.getId()));
    }

    private Tarefa criaTarefa() {
        return new Tarefa(null, "Tarefa Exemplo",
                "Tarefa exemplo descrição", LocalDate.now().plusDays(2),
                ID_DEPARTAMENTO_RECURSOS_HUMANOS, 10,
                null, null, null, null);
    }

    private Tarefa criaTarefaRHComTituloEDataInicial(String titulo, LocalDate data) {
        return new Tarefa(null, titulo,
                "Tarefa RH descrição", data,
                ID_DEPARTAMENTO_RECURSOS_HUMANOS, 10,
                null, null, null, null);
    }

    private Pessoa criaPessoa(){
        return new Pessoa(null, "Bernardo Mendes", Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of());
    }
}
