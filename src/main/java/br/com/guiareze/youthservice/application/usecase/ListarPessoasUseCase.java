package br.com.guiareze.youthservice.application.usecase;

import br.com.guiareze.youthservice.application.port.PessoaRepository;
import br.com.guiareze.youthservice.domain.model.Pessoa;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarPessoasUseCase {

    private final PessoaRepository pessoaRepository;

    public ListarPessoasUseCase(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    public List<Pessoa> executar() {
        return pessoaRepository.listarTodas();
    }
}
