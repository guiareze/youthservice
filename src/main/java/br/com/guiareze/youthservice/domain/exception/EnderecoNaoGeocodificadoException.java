package br.com.guiareze.youthservice.domain.exception;

import java.util.List;

public class EnderecoNaoGeocodificadoException extends DomainException {

    private final List<String> enderecosComFalha;

    public EnderecoNaoGeocodificadoException(List<String> enderecosComFalha) {
        super("Não foi possível localizar geograficamente os seguintes endereços: "
                + String.join("; ", enderecosComFalha));
        this.enderecosComFalha = List.copyOf(enderecosComFalha);
    }

    public EnderecoNaoGeocodificadoException(String enderecoComFalha) {
        this(List.of(enderecoComFalha));
    }

    public EnderecoNaoGeocodificadoException(String enderecoComFalha, Throwable cause) {
        super("Não foi possível localizar geograficamente os seguintes endereços: " + enderecoComFalha, cause);
        this.enderecosComFalha = List.of(enderecoComFalha);
    }

    public List<String> getEnderecosComFalha() {
        return enderecosComFalha;
    }
}
