package com.teste.task_manager.sevice;

import com.teste.task_manager.model.pessoa.Pessoa;
import com.teste.task_manager.model.Tarefa;
import com.teste.task_manager.model.dao.TarefaDAO;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

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

    public Tarefa finalizarTarefa(long id) throws Exception {
        val tarefa = tarefaDAO.findById(id).orElseThrow(() ->
                new RuntimeException("Tarefa não encontrada"));

        verificaSePodeFinalizar(tarefa);

        tarefa.setFinalizado(true);
        tarefa.setDataFinal(LocalDateTime.now());
        tarefa.calculaDuracao();
        return tarefaDAO.save(tarefa);
    }

    //considerei que para a tarefa ser finalizada, ela deve ter sido atribuida a alguem, pois isso sinaliza que ela esta em andamento
    private void verificaSePodeFinalizar(Tarefa tarefa) throws Exception {
        if(tarefa.getPessoaId() == null){
            throw new Exception("A tarefa não pode ser finalizada.");
        }
    }

    public List<Tarefa> findTarefasMaisAntigasSemPessoaAlocada(){
        return tarefaDAO.findTarefasMaisAntigasSemPessoaAlocada();
    }

    //considerei que a partir do momento em que uma pessoa eh alocada em uma tarefa, a tarefa comeca a ser feita
    public Tarefa alocarPessoaNaTarefa(long id, long pessoaId) throws Exception {
        val tarefa = tarefaDAO.findById(id).orElseThrow();
        val pessoa = pessoaService.findById(pessoaId);
        verificaDepartamento(tarefa, pessoa);
        tarefa.setPessoaId(pessoaId);
        tarefa.setDataInicial(LocalDateTime.now());
        return tarefaDAO.save(tarefa);
    }

    private void verificaDepartamento(Tarefa tarefa, Pessoa pessoa) throws Exception {
        if(tarefa.getDepartamentoId() != pessoa.getDepartamentoId()){
            throw new Exception("A pessoa não pertence ao departamento relacionado a tarefa.");
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
