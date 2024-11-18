package com.teste.task_manager.sevice;

import com.teste.task_manager.model.pessoa.Pessoa;
import com.teste.task_manager.model.dao.PessoaDAO;
import com.teste.task_manager.model.pessoa.PessoaComDependencias;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class PessoaService {

    private PessoaDAO pessoaDAO;

    public Pessoa salvarPessoa(Pessoa pessoa){
        return pessoaDAO.save(pessoa);
    }

    public Pessoa alterarPessoa(Pessoa pessoa){
        verificaSePessoaExiste(pessoa.getId());
        return pessoaDAO.save(pessoa);
    }

    public void deletarPessoa(long id){
        verificaSePessoaExiste(id);
        pessoaDAO.deleteById(id);
    }

    private void verificaSePessoaExiste(long id){
        pessoaDAO.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa não encontrada")
        );
    }

    public List<Pessoa> findAll(){
        return pessoaDAO.findAll();
    }

    public Pessoa findById(long id){
        return pessoaDAO.findById(id).orElseThrow();
    }

    public Integer countPessoasByDepartamentoId(long id){
        return pessoaDAO.countPessoasByDepartamentoId(id);
    }

    public List<Pessoa> findByNomeLike(String nome){
        return pessoaDAO.findByNomeLike("%" + nome + "%");
    }
}
