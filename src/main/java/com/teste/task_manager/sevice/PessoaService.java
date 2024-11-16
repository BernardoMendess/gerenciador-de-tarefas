package com.teste.task_manager.sevice;

import com.teste.task_manager.model.Pessoa;
import com.teste.task_manager.model.dao.PessoaDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
                new RuntimeException("Pessoa com id " + id + " não encontrada"));
    }

    public Pessoa findById(long id){
        return pessoaDAO.findById(id).orElseThrow();
    }

}
