package com.teste.task_manager.service;

import com.teste.task_manager.model.Tarefa;
import com.teste.task_manager.model.pessoa.Pessoa;
import com.teste.task_manager.sevice.DepartamentoService;
import com.teste.task_manager.sevice.PessoaComDependenciaService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.teste.task_manager.model.departamento.Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class PessoaComDependenciaServiceTest {

    @Autowired
    private PessoaComDependenciaService pessoaComDependenciaService;

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private DepartamentoService departamentoService;

    @Test
    @Rollback
    @Transactional
    public void quando_busca_todas_as_pessoas_e_retorna_nome_departamento_e_horas_gastas() {
        val dataAtual = LocalDate.now();
        val pessoa1 = pessoaService.salvarPessoa(new Pessoa(null, "Bernardo Mendes", ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of()));
        val pessoa2 = pessoaService.salvarPessoa( new Pessoa(null, "Marcia Silva", ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of()));

        val tarefa1P1 =  tarefaService.salvarTarefa(new Tarefa(null, "Tarefa Exemplo",
                "Tarefa exemplo descrição", LocalDate.now().plusDays(2),  ID_DEPARTAMENTO_RECURSOS_HUMANOS, null,
                null, pessoa1.getId(), LocalDateTime.of(dataAtual.getYear(), dataAtual.getMonth(), dataAtual.getDayOfMonth(), 0, 0), null));

        val tarefa2P1 =  tarefaService.salvarTarefa(new Tarefa(null, "Tarefa Exemplo",
                "Tarefa exemplo descrição", LocalDate.now().plusDays(2), ID_DEPARTAMENTO_RECURSOS_HUMANOS, null,
                null, pessoa1.getId(), LocalDateTime.of(dataAtual.getYear(), dataAtual.getMonth(), dataAtual.minusDays(1).getDayOfMonth(), 10, 0), null));

        val tarefa1P2 =  tarefaService.salvarTarefa(new Tarefa(null, "Tarefa Exemplo",
                "Tarefa exemplo descrição", LocalDate.now().plusDays(2), ID_DEPARTAMENTO_RECURSOS_HUMANOS, null,
                null, pessoa2.getId(), LocalDateTime.of(dataAtual.getYear(), dataAtual.getMonth(), dataAtual.getDayOfMonth(), 0, 0), null));

        tarefaService.finalizarTarefa(tarefa1P1.getId());
        tarefaService.finalizarTarefa(tarefa2P1.getId());
        tarefaService.finalizarTarefa(tarefa1P2.getId());

        val pessoas = pessoaComDependenciaService.findAllPessoasComDependencias();
        assertEquals(2, pessoas.size());

        assertEquals(pessoa1.getNome(), pessoas.get(0).nome());
        assertEquals(departamentoService.findObjById(pessoa1.getDepartamentoId()).nome(), pessoas.get(0).departamento());
        assertTrue(pessoas.get(0).horasGastas() > 0);

        assertEquals(pessoa2.getNome(), pessoas.get(1).nome());
        assertEquals(departamentoService.findObjById(pessoa2.getDepartamentoId()).nome(), pessoas.get(1).departamento());
        assertTrue(pessoas.get(1).horasGastas() > 0);

        assertTrue(pessoas.get(0).horasGastas() > pessoas.get(1).horasGastas());
    }

    @Test
    @Rollback
    @Transactional
    public void quando_busca_pessoas_em_um_periodo_entao_retorna_nome_e_media_horas_gastas() {
        val dataAtual = LocalDate.now();
        val pessoa1 = pessoaService.salvarPessoa(new Pessoa(null, "Bernardo Mendes", ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of()));
        val pessoa2 = pessoaService.salvarPessoa( new Pessoa(null, "Marcia Silva", ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of()));

        val tarefa1P1 =  tarefaService.salvarTarefa(new Tarefa(null, "Tarefa Exemplo", "Tarefa exemplo descrição", LocalDate.now().plusDays(2),
                ID_DEPARTAMENTO_RECURSOS_HUMANOS, null, null, pessoa1.getId(),
                LocalDateTime.of(dataAtual.getYear(), dataAtual.getMonth(), dataAtual.getDayOfMonth(), 0, 0), null));

        val tarefa2P1 =  tarefaService.salvarTarefa(new Tarefa(null, "Tarefa Exemplo",
                "Tarefa exemplo descrição", LocalDate.now().plusDays(2), ID_DEPARTAMENTO_RECURSOS_HUMANOS, null,
                null, pessoa1.getId(), LocalDateTime.of(dataAtual.getYear(), dataAtual.getMonth(), dataAtual.minusDays(1).getDayOfMonth(), 10, 0), null));

        val tarefa1P2 =  tarefaService.salvarTarefa(new Tarefa(null, "Tarefa Exemplo",
                "Tarefa exemplo descrição", LocalDate.now().plusDays(2), ID_DEPARTAMENTO_RECURSOS_HUMANOS, null,
                null, pessoa2.getId(), LocalDateTime.of(dataAtual.getYear(), dataAtual.getMonth(), dataAtual.getDayOfMonth(), 0, 0), null));

        tarefaService.finalizarTarefa(tarefa1P1.getId());
        tarefaService.finalizarTarefa(tarefa2P1.getId());
        tarefaService.finalizarTarefa(tarefa1P2.getId());

        val pessoas = pessoaComDependenciaService.findPessoasByNomeEPeriodo("Bernardo", LocalDate.now(), LocalDate.now());
        assertEquals(1, pessoas.size());

        assertEquals(pessoa1.getNome(), pessoas.get(0).nome());
        //aqui eu considerei que a media vai ser sempre a hora atual, pois o tarefa1 da pessoa 1 comeca na hora 0 do dia, e so estou pegando essa tarefa
        assertEquals(LocalDateTime.now().getHour(), pessoas.get(0).mediaHorasGastas());

    }

}
