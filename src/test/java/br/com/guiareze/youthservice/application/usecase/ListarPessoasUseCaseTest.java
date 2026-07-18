package br.com.guiareze.youthservice.application.usecase;

import br.com.guiareze.youthservice.application.port.PessoaRepository;
import br.com.guiareze.youthservice.domain.model.Pessoa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarPessoasUseCaseTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Test
    void deveRetornarListaVaziaQuandoNaoHaPessoasCadastradas() {
        ListarPessoasUseCase useCase = new ListarPessoasUseCase(pessoaRepository);

        when(pessoaRepository.listarTodas()).thenReturn(List.of());

        assertThat(useCase.executar()).isEmpty();
    }

    @Test
    void deveRetornarTodasAsPessoasCadastradas() {
        ListarPessoasUseCase useCase = new ListarPessoasUseCase(pessoaRepository);
        Pessoa pessoa = Pessoa.criar("Maria da Silva", 30, "+5511952526969");

        when(pessoaRepository.listarTodas()).thenReturn(List.of(pessoa));

        assertThat(useCase.executar()).hasSize(1);
        assertThat(useCase.executar().get(0).getNome()).isEqualTo("Maria da Silva");
    }
}
