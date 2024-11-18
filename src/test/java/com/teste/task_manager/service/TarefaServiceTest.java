package com.teste.task_manager.service;

import com.teste.task_manager.model.Tarefa;
import com.teste.task_manager.model.departamento.Departamento;
import com.teste.task_manager.model.pessoa.Pessoa;
import com.teste.task_manager.sevice.PessoaService;
import com.teste.task_manager.sevice.TarefaService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static com.teste.task_manager.model.departamento.Departamento.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class TarefaServiceTest {

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private PessoaService pessoaService;

    @Test
    @Rollback
    @Transactional
    public void quando_salva_tarefa_nova_entao_ok() {

        val tarefa = criaTarefa();
        val tarefaSalva = tarefaService.salvarTarefa(tarefa);

        assertEquals(tarefa.getDescricao(), tarefaSalva.getDescricao());
        assertEquals(tarefa.getDepartamentoId(), tarefaSalva.getDepartamentoId());
        assertFalse(tarefaSalva.isFinalizado());
        assertNotNull(tarefaSalva.getId());
    }

    @Test
    @Rollback
    @Transactional
    public void quando_aloca_pessoa_na_tarefa_entao_ok() {

        val pessoa = pessoaService.salvarPessoa(criaPessoa());
        val tarefa = tarefaService.salvarTarefa(criaTarefa());

        val tarefaAlocada = tarefaService.alocarPessoaNaTarefa(tarefa.getId(), pessoa.getId());

        assertEquals(pessoa.getId(), tarefaAlocada.getPessoaId());
        assertNotNull(tarefaAlocada.getDataInicial());
    }

    @Test
    @Rollback
    @Transactional
    public void quando_tenta_alocar_pessoa_na_tarefa_mas_tarefa_ja_pertence_a_outra_pessoa() {

        val pessoa1 = pessoaService.salvarPessoa(criaPessoa());
        val tarefa = tarefaService.salvarTarefa(criaTarefa());
        tarefaService.alocarPessoaNaTarefa(tarefa.getId(), pessoa1.getId());

        val pessoa2 = pessoaService.salvarPessoa(criaPessoa());
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> tarefaService.alocarPessoaNaTarefa(tarefa.getId(), pessoa2.getId()))
                .withMessageContaining("Já existe uma pessoa alocada para essa tarefa.")
                .withMessageContaining("409")
                .withNoCause();
    }

    @Test
    @Rollback
    @Transactional
    public void quando_tenta_alocar_pessoa_na_tarefa_mas_pessoa_nao_pertence_ao_departamento_da_tarefa() {

        val pessoa = pessoaService.salvarPessoa(criaPessoa().withDepartamentoId(ID_DEPARTAMENTO_TI));
        val tarefa = tarefaService.salvarTarefa(criaTarefa().withDepartamentoId(ID_DEPARTAMENTO_MARKETING));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> tarefaService.alocarPessoaNaTarefa(tarefa.getId(), pessoa.getId()))
                .withMessageContaining("A pessoa selecionada não pertence ao departamento relacionado a essa tarefa.")
                .withMessageContaining("409")
                .withNoCause();
    }

    @Test
    @Rollback
    @Transactional
    public void quando_finaliza_tarefa_entao_ok() {

        val pessoa = pessoaService.salvarPessoa(criaPessoa());
        val tarefa = tarefaService.salvarTarefa(criaTarefa());
        tarefaService.alocarPessoaNaTarefa(tarefa.getId(), pessoa.getId());

        val tarefaFinalizada = tarefaService.finalizarTarefa(tarefa.getId());

        assertTrue(tarefaFinalizada.isFinalizado());
        assertNotNull(tarefaFinalizada.getDataFinal());
        assertNotNull(tarefaFinalizada.getDuracao());
    }


    @Test
    @Rollback
    @Transactional
    public void quando_tenta_finalizar_tarefa_sem_pessoa_alocada_entao_da_erro() {

        val tarefa = tarefaService.salvarTarefa(criaTarefa());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> tarefaService.finalizarTarefa(tarefa.getId()))
                .withMessageContaining("A tarefa não pode ser finalizada, pois ainda não foi iniciada.")
                .withMessageContaining("409")
                .withNoCause();
    }

    @Test
    @Rollback
    @Transactional
    public void contar_tarefas_por_departamento_id() {

        tarefaService.salvarTarefa(criaTarefa().withDepartamentoId(ID_DEPARTAMENTO_MARKETING));
        tarefaService.salvarTarefa(criaTarefa().withDepartamentoId(ID_DEPARTAMENTO_MARKETING));
        tarefaService.salvarTarefa(criaTarefa().withDepartamentoId(ID_DEPARTAMENTO_TI));
        assertEquals(2, tarefaService.countTarefasByDepartamentoId(ID_DEPARTAMENTO_MARKETING));
    }

    @Test
    @Rollback
    @Transactional
    public void quando_busca_tarefas_mais_antigas_sem_pessoa_alocada() {

        val tarefaP10Dias = tarefaService.salvarTarefa(criaTarefaRHComTituloEDataInicial("Tarefa 1 RH", LocalDate.now().plusDays(10)));
        val tarefaP15Dias =  tarefaService.salvarTarefa(criaTarefaRHComTituloEDataInicial("Tarefa 4 RH", LocalDate.now().plusDays(15)));
        val tarefaP20Dias =  tarefaService.salvarTarefa(criaTarefaRHComTituloEDataInicial("Tarefa 5 RH", LocalDate.now().plusDays(20)));
        val tarefaHoje =  tarefaService.salvarTarefa(criaTarefaRHComTituloEDataInicial("Tarefa 3 RH", LocalDate.now()));
        val tarefaM5Dias =   tarefaService.salvarTarefa(criaTarefaRHComTituloEDataInicial("Tarefa 2 RH", LocalDate.now().minusDays(5)));

        val tarefasEsperadas = List.of(tarefaM5Dias, tarefaHoje, tarefaP10Dias);

        assertEquals(tarefasEsperadas, tarefaService.findTarefasMaisAntigasSemPessoaAlocada());
    }

    @Test
    public void quando_tenta_buscar_tarefa_que_nao_existe_entao_da_erro() {

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> tarefaService.findTarefaById(985296295357L))
                .withMessageContaining("Tarefa não encontrada.")
                .withMessageContaining("404")
                .withNoCause();
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
        return new Pessoa(null, "Bernardo Mendes", ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of());
    }
}
