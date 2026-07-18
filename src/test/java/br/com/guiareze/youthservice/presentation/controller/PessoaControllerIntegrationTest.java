package br.com.guiareze.youthservice.presentation.controller;

import br.com.guiareze.youthservice.domain.exception.NomeJaCadastradoException;
import br.com.guiareze.youthservice.domain.exception.PessoaInvalidaException;
import br.com.guiareze.youthservice.domain.exception.PessoaNaoEncontradaException;
import br.com.guiareze.youthservice.presentation.dto.CadastrarPessoaRequest;
import br.com.guiareze.youthservice.presentation.dto.PessoaResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PessoaControllerIntegrationTest {

    @Autowired
    private PessoaController controller;

    @Test
    void deveCadastrarPessoaComDadosValidos() {
        PessoaResponse response = controller.cadastrar(
                new CadastrarPessoaRequest("Maria da Silva", 30, "+5511952526969"));

        assertThat(response.id()).isNotNull();
        assertThat(response.nome()).isEqualTo("Maria da Silva");
        assertThat(response.telefone()).isEqualTo("5511952526969");
    }

    @Test
    void deveRejeitarCadastroComNomeVazio() {
        var request = new CadastrarPessoaRequest("", 30, "+5511952526969");

        assertThatThrownBy(() -> controller.cadastrar(request))
                .isInstanceOf(PessoaInvalidaException.class);
    }

    @Test
    void deveRejeitarCadastroComIdadeIgualAZero() {
        var request = new CadastrarPessoaRequest("Carlos Souza", 0, "+5511952526969");

        assertThatThrownBy(() -> controller.cadastrar(request))
                .isInstanceOf(PessoaInvalidaException.class);
    }

    @Test
    void deveRejeitarCadastroSemInformarIdade() {
        var request = new CadastrarPessoaRequest("Ana Paula", null, "+5511952526969");

        assertThatThrownBy(() -> controller.cadastrar(request))
                .isInstanceOf(PessoaInvalidaException.class);
    }

    @Test
    void deveRejeitarCadastroComTelefoneEmFormatoInvalido() {
        var request = new CadastrarPessoaRequest("Pedro Lima", 25, "11952526969");

        assertThatThrownBy(() -> controller.cadastrar(request))
                .isInstanceOf(PessoaInvalidaException.class);
    }

    @Test
    void deveRejeitarCadastroComNomeDuplicado() {
        controller.cadastrar(new CadastrarPessoaRequest("Bruna Alves", 28, "+5511911112222"));

        var requestDuplicado = new CadastrarPessoaRequest("Bruna Alves", 40, "+5511933334444");

        assertThatThrownBy(() -> controller.cadastrar(requestDuplicado))
                .isInstanceOf(NomeJaCadastradoException.class);
    }

    @Test
    void deveConsultarPessoaExistentePorId() {
        PessoaResponse cadastro = controller.cadastrar(
                new CadastrarPessoaRequest("Fernanda Costa", 22, "+5511955556666"));

        PessoaResponse consulta = controller.consultar(cadastro.id());

        assertThat(consulta.nome()).isEqualTo("Fernanda Costa");
    }

    @Test
    void deveLancarExcecaoParaPessoaInexistente() {
        UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> controller.consultar(id))
                .isInstanceOf(PessoaNaoEncontradaException.class);
    }

    @Test
    void deveListarPessoasCadastradas() {
        controller.cadastrar(new CadastrarPessoaRequest("Gustavo Pereira", 35, "+5511977778888"));

        List<PessoaResponse> resultado = controller.listar();

        assertThat(resultado).isNotEmpty();
    }
}
