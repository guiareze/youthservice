package br.com.guiareze.youthservice.application.usecase;

import br.com.guiareze.youthservice.application.port.PessoaRepository;
import br.com.guiareze.youthservice.domain.exception.PessoaNaoEncontradaException;
import br.com.guiareze.youthservice.domain.model.Pessoa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarPessoaUseCaseTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Test
    void deveConsultarPessoaExistente() {
        ConsultarPessoaUseCase useCase = new ConsultarPessoaUseCase(pessoaRepository);
        Pessoa pessoa = Pessoa.criar("Maria da Silva", 30, "+5511952526969");

        when(pessoaRepository.buscarPorId(pessoa.getId())).thenReturn(Optional.of(pessoa));

        Pessoa resultado = useCase.executar(pessoa.getId());

        assertThat(resultado.getNome()).isEqualTo("Maria da Silva");
    }

    @Test
    void deveLancarExcecaoParaPessoaInexistente() {
        ConsultarPessoaUseCase useCase = new ConsultarPessoaUseCase(pessoaRepository);
        UUID id = UUID.randomUUID();

        when(pessoaRepository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(id))
                .isInstanceOf(PessoaNaoEncontradaException.class);
    }
}
