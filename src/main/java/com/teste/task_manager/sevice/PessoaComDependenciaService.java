package com.teste.task_manager.sevice;

import com.teste.task_manager.model.pessoa.PessoaComDependencias;
import com.teste.task_manager.model.pessoa.PessoaGastos;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
public class PessoaComDependenciaService {

    private PessoaService pessoaService;

    private TarefaService tarefaService;

    private DepartamentoService departamentoService;

    public List<PessoaComDependencias> findAllPessoas(){
        val pessoas = pessoaService.findAll();
        return pessoas.stream().map(pessoa ->
                new PessoaComDependencias(
                    pessoa.getNome(),
                    departamentoService.findObjById(pessoa.getDepartamentoId()).nome(),
                    tarefaService.findTotalHorasGastasByPessoaId(pessoa.getId())
                )
        ).toList();
    }

    public List<PessoaGastos> findPessoasByNomeEPeriodo(String nome, LocalDate dataInicial, LocalDate dataFinal){
        val pessoas = pessoaService.findByNomeLike(nome);
        return pessoas.stream().map(pessoa ->
                new PessoaGastos(
                        pessoa.getNome(),
                        tarefaService.findMediaByPessoaIdAndPeriodo
                                (pessoa.getId(), dataFinal.atStartOfDay(), dataFinal.atTime(23, 59, 59))
                )).toList();
    }
}
