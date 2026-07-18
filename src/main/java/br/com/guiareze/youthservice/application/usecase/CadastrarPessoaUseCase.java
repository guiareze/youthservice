package br.com.guiareze.youthservice.application.usecase;

import br.com.guiareze.youthservice.application.port.PessoaRepository;
import br.com.guiareze.youthservice.domain.exception.NomeJaCadastradoException;
import br.com.guiareze.youthservice.domain.model.Pessoa;
import org.springframework.stereotype.Service;

@Service
public class CadastrarPessoaUseCase {

    private final PessoaRepository pessoaRepository;

    public CadastrarPessoaUseCase(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    public Pessoa executar(Pessoa pessoa) {
        if (pessoaRepository.existePorNome(pessoa.getNome())) {
            throw new NomeJaCadastradoException(pessoa.getNome());
        }
        return pessoaRepository.salvar(pessoa);
    }
}
