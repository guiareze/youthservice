package br.com.guiareze.youthservice.domain.exception;

public class NomeJaCadastradoException extends DomainException {

    public NomeJaCadastradoException(String nome) {
        super("Já existe uma pessoa cadastrada com o nome '%s'".formatted(nome));
    }
}
