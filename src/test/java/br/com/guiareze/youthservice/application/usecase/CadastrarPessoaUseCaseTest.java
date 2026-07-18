package br.com.guiareze.youthservice.application.usecase;

import br.com.guiareze.youthservice.application.port.PessoaRepository;
import br.com.guiareze.youthservice.domain.exception.NomeJaCadastradoException;
import br.com.guiareze.youthservice.domain.exception.PessoaInvalidaException;
import br.com.guiareze.youthservice.domain.model.Pessoa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastrarPessoaUseCaseTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Test
    void deveCadastrarPessoaComSucesso() {
        CadastrarPessoaUseCase useCase = new CadastrarPessoaUseCase(pessoaRepository);
        Pessoa pessoa = Pessoa.criar("Maria da Silva", 30, "+5511952526969");

        when(pessoaRepository.existePorNome("Maria da Silva")).thenReturn(false);
        when(pessoaRepository.salvar(any(Pessoa.class))).thenReturn(pessoa);

        Pessoa resultado = useCase.executar(pessoa);

        assertThat(resultado.getNome()).isEqualTo("Maria da Silva");
    }

    @Test
    void deveRejeitarCadastroSemInformarIdade() {
        assertThatThrownBy(() -> Pessoa.criar("João Souza", null, "+5511952526969"))
                .isInstanceOf(PessoaInvalidaException.class);
    }

    @Test
    void deveRejeitarCadastroComNomeDuplicado() {
        CadastrarPessoaUseCase useCase = new CadastrarPessoaUseCase(pessoaRepository);
        Pessoa pessoa = Pessoa.criar("Maria da Silva", 30, "+5511952526969");

        when(pessoaRepository.existePorNome("Maria da Silva")).thenReturn(true);

        assertThatThrownBy(() -> useCase.executar(pessoa))
                .isInstanceOf(NomeJaCadastradoException.class);
    }
}
