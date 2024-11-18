package com.teste.task_manager.service;

import com.teste.task_manager.model.departamento.Departamento;
import com.teste.task_manager.model.pessoa.Pessoa;
import com.teste.task_manager.sevice.PessoaService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class PessoaServiceTest {

    @Autowired
    private PessoaService pessoaService;


    @Test
    @Rollback
    @Transactional
    public void quando_salva_pessoa_nova_entao_ok() {

        val pessoa = criaPessoa();
        val pessoaSalva = pessoaService.salvarPessoa(pessoa);

        assertEquals(pessoa.getNome(),pessoaSalva.getNome());
        assertEquals(pessoa.getDepartamentoId(), pessoaSalva.getDepartamentoId());
        assertEquals(pessoa.getTarefas(), pessoaSalva.getTarefas());
        assertNotNull(pessoaSalva.getId());
    }

    @Test
    @Rollback
    @Transactional
    public void quando_edita_pessoa_entao_ok() {

        val pessoa = pessoaService.salvarPessoa(criaPessoa());
        val pessoaAlterada = new Pessoa(pessoa.getId(), "Bernardo Siqueira", Departamento.ID_DEPARTAMENTO_MARKETING, pessoa.getTarefas());
        val pessoaSalva = pessoaService.salvarPessoa(pessoaAlterada);

        assertEquals(pessoaAlterada.getId(), pessoaSalva.getId());
        assertEquals(pessoaAlterada.getNome(),pessoaSalva.getNome());
        assertEquals(pessoaAlterada.getDepartamentoId(), pessoaSalva.getDepartamentoId());
        assertEquals(pessoaAlterada.getTarefas(), pessoaSalva.getTarefas());
    }

    @Test
    public void quando_tenta_editar_mas_pessoa_nao_existe_entao_da_erro(){
        val pessoa = criaPessoa().withId(32452325325225L);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> pessoaService.alterarPessoa(pessoa))
                .withMessageContaining("Pessoa não encontrada")
                .withMessageContaining("404")
                .withNoCause();
    }

    @Test
    @Rollback
    @Transactional
    public void quando_busca_por_todas_as_pessoas_salvas_entao_ok() {

        val pessoa1 = pessoaService.salvarPessoa(new Pessoa(null, "Bernardo Mendes", Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of()));
        val pessoa2 = pessoaService.salvarPessoa(new Pessoa(null, "Joao Silva", Departamento.ID_DEPARTAMENTO_MARKETING, List.of()));
        val pessoa3 = pessoaService.salvarPessoa(new Pessoa(null, "Jose Junior", Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of()));

        val listaSalvos = List.of(pessoa1, pessoa2, pessoa3);

        assertEquals(listaSalvos, pessoaService.findAll());
    }

    @Test
    @Rollback
    @Transactional
    public void quando_busca_por_todas_as_pessoas_salvas_e_nao_tem_ninguem_entao_retorna_vazio() {

        assertTrue(pessoaService.findAll().isEmpty());

    }

    @Test
    @Rollback
    @Transactional
    public void quando_exclui_pessoa_entao_ok() {

        val pessoa = pessoaService.salvarPessoa(criaPessoa());
        assertEquals(List.of(pessoa), pessoaService.findAll());

        pessoaService.deletarPessoa(pessoa.getId());
        assertEquals(List.of(), pessoaService.findAll());
    }

    @Test
    public void quando_tenta_deletar_mas_pessoa_nao_existe_entao_da_erro(){
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> pessoaService.deletarPessoa(32452325325225L))
                .withMessageContaining("Pessoa não encontrada")
                .withMessageContaining("404")
                .withNoCause();
    }

    @Test
    @Rollback
    @Transactional
    public void contar_pessoas_pelo_departamento_id() {

        pessoaService.salvarPessoa(new Pessoa(null, "Bernardo Mendes", Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of()));
        pessoaService.salvarPessoa(new Pessoa(null, "Joao Silva", Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of()));
        pessoaService.salvarPessoa(new Pessoa(null, "Jose Junior", Departamento.ID_DEPARTAMENTO_MARKETING, List.of()));

        assertEquals(2, pessoaService.countPessoasByDepartamentoId(Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS));
    }

    @Test
    @Rollback
    @Transactional
    public void quando_busca_por_pessoa_salva_entao_ok() {

        val pessoa = pessoaService.salvarPessoa(criaPessoa());
        val pessoaSalva = pessoaService.findPessoaById(pessoa.getId());

        assertEquals(pessoa, pessoaSalva);
    }

    @Test
    public void quando_tenta_buscar_pessoa_que_nao_existe_entao_da_erro() {
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> pessoaService.findPessoaById(234872394239L))
                .withMessageContaining("Pessoa não encontrada")
                .withMessageContaining("404")
                .withNoCause();
    }

    private Pessoa criaPessoa(){
        return new Pessoa(null, "Bernardo Mendes", Departamento.ID_DEPARTAMENTO_RECURSOS_HUMANOS, List.of());
    }

}
