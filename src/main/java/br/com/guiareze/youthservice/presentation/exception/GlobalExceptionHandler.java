package br.com.guiareze.youthservice.presentation.exception;

import br.com.guiareze.youthservice.domain.exception.EnderecoNaoGeocodificadoException;
import br.com.guiareze.youthservice.domain.exception.NomeJaCadastradoException;
import br.com.guiareze.youthservice.domain.exception.PessoaInvalidaException;
import br.com.guiareze.youthservice.domain.exception.PessoaNaoEncontradaException;
import br.com.guiareze.youthservice.domain.exception.QuantidadeGruposInvalidaException;
import br.com.guiareze.youthservice.presentation.dto.ErroResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PessoaInvalidaException.class)
    public ResponseEntity<ErroResponse> handlePessoaInvalida(PessoaInvalidaException ex) {
        log.warn("Dados de pessoa inválidos: {}", ex.getMessage());
        return responder(HttpStatus.BAD_REQUEST, "PESSOA_INVALIDA", ex.getMessage());
    }

    @ExceptionHandler(NomeJaCadastradoException.class)
    public ResponseEntity<ErroResponse> handleNomeJaCadastrado(NomeJaCadastradoException ex) {
        log.warn("Tentativa de cadastro com nome duplicado: {}", ex.getMessage());
        return responder(HttpStatus.CONFLICT, "NOME_JA_CADASTRADO", ex.getMessage());
    }

    @ExceptionHandler(PessoaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handlePessoaNaoEncontrada(PessoaNaoEncontradaException ex) {
        log.warn("Pessoa não encontrada: {}", ex.getMessage());
        return responder(HttpStatus.NOT_FOUND, "PESSOA_NAO_ENCONTRADA", ex.getMessage());
    }

    @ExceptionHandler(QuantidadeGruposInvalidaException.class)
    public ResponseEntity<ErroResponse> handleQuantidadeGruposInvalida(QuantidadeGruposInvalidaException ex) {
        log.warn("Quantidade de grupos inválida: {}", ex.getMessage());
        return responder(HttpStatus.BAD_REQUEST, "QUANTIDADE_GRUPOS_INVALIDA", ex.getMessage());
    }

    @ExceptionHandler(EnderecoNaoGeocodificadoException.class)
    public ResponseEntity<ErroResponse> handleEnderecoNaoGeocodificado(EnderecoNaoGeocodificadoException ex) {
        log.error("Falha ao geocodificar endereço(s): {}", ex.getMessage(), ex);
        return responder(HttpStatus.UNPROCESSABLE_ENTITY, "ENDERECO_NAO_GEOCODIFICADO", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> handleIntegridade(DataIntegrityViolationException ex) {
        log.warn("Violação de integridade ao persistir pessoa: {}", ex.getMessage());
        return responder(HttpStatus.CONFLICT, "NOME_JA_CADASTRADO", "Já existe uma pessoa cadastrada com este nome");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidacao(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("Dados de requisição inválidos: {}", mensagem);
        return responder(HttpStatus.BAD_REQUEST, "DADOS_INVALIDOS", mensagem);
    }

    private ResponseEntity<ErroResponse> responder(HttpStatus status, String erro, String mensagem) {
        return ResponseEntity.status(status)
                .body(new ErroResponse(erro, mensagem, Instant.now()));
    }
}
