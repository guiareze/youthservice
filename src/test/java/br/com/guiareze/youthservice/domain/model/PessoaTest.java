package br.com.guiareze.youthservice.domain.model;

import br.com.guiareze.youthservice.domain.exception.PessoaInvalidaException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PessoaTest {

    @Test
    void deveCriarPessoaComDadosValidos() {
        Pessoa pessoa = Pessoa.criar("Maria da Silva", 30, "+5511952526969");

        assertThat(pessoa.getNome()).isEqualTo("Maria da Silva");
        assertThat(pessoa.getIdade()).isEqualTo(30);
        assertThat(pessoa.getTelefone()).isEqualTo("5511952526969");
        assertThat(pessoa.getId()).isNotNull();
        assertThat(pessoa.getCriadoEm()).isNotNull();
    }

    @Test
    void deveRejeitarIdadeAusente() {
        assertThatThrownBy(() -> Pessoa.criar("João Souza", null, "+5511952526969"))
                .isInstanceOf(PessoaInvalidaException.class);
    }

    @Test
    void deveRejeitarNomeVazio() {
        assertThatThrownBy(() -> Pessoa.criar("", 30, "+5511952526969"))
                .isInstanceOf(PessoaInvalidaException.class);
    }

    @Test
    void deveRejeitarNomeNulo() {
        assertThatThrownBy(() -> Pessoa.criar(null, 30, "+5511952526969"))
                .isInstanceOf(PessoaInvalidaException.class);
    }

    @Test
    void deveRejeitarIdadeIgualAZero() {
        assertThatThrownBy(() -> Pessoa.criar("Maria da Silva", 0, "+5511952526969"))
                .isInstanceOf(PessoaInvalidaException.class);
    }

    @Test
    void deveRejeitarTelefoneForaDoFormatoE164() {
        assertThatThrownBy(() -> Pessoa.criar("Maria da Silva", 30, "11952526969"))
                .isInstanceOf(PessoaInvalidaException.class);
    }

    @Test
    void deveNormalizarTelefoneRemovendoPrefixo() {
        Pessoa pessoa = Pessoa.criar("Maria da Silva", 30, "+5511952526969");

        assertThat(pessoa.getTelefone()).doesNotContain("+");
        assertThat(pessoa.getTelefone()).isEqualTo("5511952526969");
    }

    @Test
    void deveReconstituirPessoaComTelefoneJaNormalizado() {
        var id = java.util.UUID.randomUUID();
        var criadoEm = java.time.Instant.now();

        Pessoa pessoa = Pessoa.reconstituir(id, "Maria da Silva", 30, "5511952526969", criadoEm);

        assertThat(pessoa.getId()).isEqualTo(id);
        assertThat(pessoa.getTelefone()).isEqualTo("5511952526969");
    }
}
