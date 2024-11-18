package com.teste.task_manager.service;

import com.teste.task_manager.model.Tarefa;
import com.teste.task_manager.model.departamento.Departamento;
import com.teste.task_manager.model.pessoa.Pessoa;
import com.teste.task_manager.sevice.DepartamentoService;
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
import java.util.List;

import static com.teste.task_manager.model.departamento.Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class DepartamentoServiceTest {

    @Autowired
    private DepartamentoService departamentoService;

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private PessoaService pessoaService;

    @Test
    @Rollback
    @Transactional
    public void quando_busca_departamentos_com_dependencias_entao_ok() {
        val pessoasRH = List.of(
            pessoaService.salvarPessoa(new Pessoa(null, "Carlos Souza", ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of())),
            pessoaService.salvarPessoa(new Pessoa(null, "Maria Jose", ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of())));

        val tarefasRH = List.of(
            tarefaService.salvarTarefa(new Tarefa(null, "Tarefa 1 RH",
                "Tarefa 1 RH descrição", LocalDate.now(),
                ID_DEPARTAMENTO_RECURSOS_HUMANOS, null,
                null, null, null, null)),
            tarefaService.salvarTarefa(new Tarefa(null, "Tarefa 2",
                        "Tarefa 2 descrição", LocalDate.now(),
                        ID_DEPARTAMENTO_RECURSOS_HUMANOS, null,
                        null, null, null, null)));

        val pessoasTI = List.of(pessoaService.salvarPessoa(
                new Pessoa(null, "Mateus Souza", Departamento.ID_DEPARTAMENTO_TI, List.of())));

        val tarefasTI = List.of(
                tarefaService.salvarTarefa(new Tarefa(null, "Tarefa 1 TI",
                        "Tarefa 1 TI descrição", LocalDate.now(),
                        Departamento.ID_DEPARTAMENTO_TI, null,
                        null, null, null, null)));

        val departamentosComDependencias = departamentoService.findAllDepartamentosComDependencias();

        assertEquals(ID_DEPARTAMENTO_RECURSOS_HUMANOS, departamentosComDependencias.get(0).departamento().id());
        assertEquals(pessoasRH.size(), departamentosComDependencias.get(0).quantidadePessoas());
        assertEquals(tarefasRH.size(), departamentosComDependencias.get(0).quantidadeTarefas());

        assertEquals(Departamento.ID_DEPARTAMENTO_TI, departamentosComDependencias.get(1).departamento().id());
        assertEquals(pessoasTI.size(), departamentosComDependencias.get(1).quantidadePessoas());
        assertEquals(tarefasTI.size(), departamentosComDependencias.get(1).quantidadeTarefas());
    }

    @Test
    @Rollback
    @Transactional
    public void quando_busca_departamento_por_id_entao_ok() {

        val departamentoSalvo = departamentoService.findObjById(Departamento.ID_DEPARTAMENTO_TI);

        assertEquals("Tecnologia da Informação", departamentoSalvo.nome());
    }

}
