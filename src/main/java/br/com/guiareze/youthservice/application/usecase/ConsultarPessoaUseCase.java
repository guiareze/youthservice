package br.com.guiareze.youthservice.application.usecase;

import br.com.guiareze.youthservice.application.port.PessoaRepository;
import br.com.guiareze.youthservice.domain.exception.PessoaNaoEncontradaException;
import br.com.guiareze.youthservice.domain.model.Pessoa;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConsultarPessoaUseCase {

    private final PessoaRepository pessoaRepository;

    public ConsultarPessoaUseCase(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    public Pessoa executar(UUID id) {
        return pessoaRepository.buscarPorId(id)
                .orElseThrow(() -> new PessoaNaoEncontradaException(id));
    }
}
