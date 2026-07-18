package br.com.guiareze.youthservice.domain.exception;

import java.util.UUID;

public class PessoaNaoEncontradaException extends DomainException {

    public PessoaNaoEncontradaException(UUID id) {
        super("Pessoa não encontrada para o identificador '%s'".formatted(id));
    }
}
