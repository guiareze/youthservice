package br.com.guiareze.youthservice.domain.exception;

public class QuantidadeGruposInvalidaException extends DomainException {

    private QuantidadeGruposInvalidaException(String message) {
        super(message);
    }

    public static QuantidadeGruposInvalidaException listaEnderecosVazia() {
        return new QuantidadeGruposInvalidaException("É necessário informar ao menos um endereço a visitar");
    }

    public static QuantidadeGruposInvalidaException quantidadeNaoPositiva() {
        return new QuantidadeGruposInvalidaException("A quantidade de grupos deve ser maior que zero");
    }

    public static QuantidadeGruposInvalidaException excedeQuantidadeEnderecos(int quantidadeGrupos, int quantidadeEnderecos) {
        return new QuantidadeGruposInvalidaException(
                "A quantidade de grupos (%d) não pode exceder a quantidade de endereços a visitar (%d)"
                        .formatted(quantidadeGrupos, quantidadeEnderecos));
    }
}
