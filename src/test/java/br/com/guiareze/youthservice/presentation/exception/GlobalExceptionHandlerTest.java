package br.com.guiareze.youthservice.presentation.exception;

import br.com.guiareze.youthservice.domain.exception.EnderecoNaoGeocodificadoException;
import br.com.guiareze.youthservice.domain.exception.NomeJaCadastradoException;
import br.com.guiareze.youthservice.domain.exception.PessoaInvalidaException;
import br.com.guiareze.youthservice.domain.exception.PessoaNaoEncontradaException;
import br.com.guiareze.youthservice.domain.exception.QuantidadeGruposInvalidaException;
import br.com.guiareze.youthservice.presentation.dto.ErroResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveRetornarBadRequestParaPessoaInvalida() {
        ResponseEntity<ErroResponse> response =
                handler.handlePessoaInvalida(new PessoaInvalidaException("A idade não pode ser zero"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().erro()).isEqualTo("PESSOA_INVALIDA");
    }

    @Test
    void deveRetornarConflictParaNomeJaCadastrado() {
        ResponseEntity<ErroResponse> response =
                handler.handleNomeJaCadastrado(new NomeJaCadastradoException("Maria da Silva"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().erro()).isEqualTo("NOME_JA_CADASTRADO");
    }

    @Test
    void deveRetornarNotFoundParaPessoaNaoEncontrada() {
        ResponseEntity<ErroResponse> response =
                handler.handlePessoaNaoEncontrada(new PessoaNaoEncontradaException(UUID.randomUUID()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().erro()).isEqualTo("PESSOA_NAO_ENCONTRADA");
    }

    @Test
    void deveRetornarConflictParaViolacaoDeIntegridade() {
        ResponseEntity<ErroResponse> response =
                handler.handleIntegridade(new DataIntegrityViolationException("unique constraint violation"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().erro()).isEqualTo("NOME_JA_CADASTRADO");
    }

    @Test
    void deveRetornarBadRequestParaQuantidadeDeGruposInvalida() {
        ResponseEntity<ErroResponse> response =
                handler.handleQuantidadeGruposInvalida(QuantidadeGruposInvalidaException.quantidadeNaoPositiva());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().erro()).isEqualTo("QUANTIDADE_GRUPOS_INVALIDA");
    }

    @Test
    void deveRetornarUnprocessableEntityParaEnderecoNaoGeocodificado() {
        ResponseEntity<ErroResponse> response =
                handler.handleEnderecoNaoGeocodificado(new EnderecoNaoGeocodificadoException(List.of("Maria (CEP 00000000, nº 100)")));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().erro()).isEqualTo("ENDERECO_NAO_GEOCODIFICADO");
    }
}
