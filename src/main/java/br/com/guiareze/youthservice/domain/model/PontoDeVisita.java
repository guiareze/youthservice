package br.com.guiareze.youthservice.domain.model;

public final class PontoDeVisita {

    private final String nomeMorador;
    private final Endereco endereco;

    public PontoDeVisita(String nomeMorador, Endereco endereco) {
        if (nomeMorador == null || nomeMorador.isBlank()) {
            throw new IllegalArgumentException("O nome do morador é obrigatório");
        }
        if (endereco == null) {
            throw new IllegalArgumentException("O endereço é obrigatório");
        }
        this.nomeMorador = nomeMorador;
        this.endereco = endereco;
    }

    public String getNomeMorador() {
        return nomeMorador;
    }

    public Endereco getEndereco() {
        return endereco;
    }
}
