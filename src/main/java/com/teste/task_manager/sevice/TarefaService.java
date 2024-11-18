package com.teste.task_manager.sevice;

import com.teste.task_manager.model.pessoa.Pessoa;
import com.teste.task_manager.model.Tarefa;
import com.teste.task_manager.model.dao.TarefaDAO;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.spi.LocaleServiceProvider;

@Service
@AllArgsConstructor
public class TarefaService {

    private TarefaDAO tarefaDAO;

    private PessoaService pessoaService;

    public Tarefa salvarTarefa(Tarefa tarefa){
        return tarefaDAO.save(tarefa.withFinalizado(false));
    }

    public Tarefa finalizarTarefa(long id) {
        val tarefa = findTarefaById(id);

        verificaSePodeFinalizar(tarefa);

        tarefa.setFinalizado(true);
        tarefa.setDataFinal(LocalDateTime.now());
        return tarefaDAO.save(tarefa);
    }

    public Tarefa findTarefaById(long id){
        return tarefaDAO.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada."));
    }

    //considerei que para a tarefa ser finalizada, ela deve ter sido atribuida a alguem, pois isso sinaliza que ela esta em andamento
    private void verificaSePodeFinalizar(Tarefa tarefa) {
        if(tarefa.getPessoaId() == null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A tarefa não pode ser finalizada, pois ainda não foi iniciada.");
        }
    }

    public List<Tarefa> findTarefasMaisAntigasSemPessoaAlocada(){
        return tarefaDAO.findTarefasMaisAntigasSemPessoaAlocada();
    }

    //considerei que a partir do momento em que uma pessoa eh alocada em uma tarefa, a tarefa comeca a ser feita
    public Tarefa alocarPessoaNaTarefa(long id, long pessoaId){
        val tarefa = tarefaDAO.findById(id).orElseThrow();
        verificaSePodeAlocar(tarefa);
        val pessoa = pessoaService.findPessoaById(pessoaId);
        verificaDepartamento(tarefa, pessoa);
        tarefa.setPessoaId(pessoaId);
        tarefa.setDataInicial(LocalDateTime.now());
        return tarefaDAO.save(tarefa);
    }

    private void verificaDepartamento(Tarefa tarefa, Pessoa pessoa) {
        if(tarefa.getDepartamentoId() != pessoa.getDepartamentoId()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A pessoa selecionada não pertence ao departamento relacionado a essa tarefa.");
        }
    }

    private void verificaSePodeAlocar(Tarefa tarefa) {
        if(tarefa.getPessoaId() != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma pessoa alocada para essa tarefa.");
        }
    }

    public Integer countTarefasByDepartamentoId(long id){
        return tarefaDAO.countByDepartamentoId(id);
    }

    public Integer findTotalHorasGastasByPessoaId(long pessoaId){
        return tarefaDAO.findTotalHorasGastasByPessoaId(pessoaId);
    }

    public Double findMediaByPessoaIdAndPeriodo(long pessoaId, LocalDateTime dataInicial, LocalDateTime dataFinal){
        return tarefaDAO.findMediaByPessoaIdAndPeriodo(pessoaId, dataInicial, dataFinal);
    }

}

