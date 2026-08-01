package br.com.guiareze.youthservice.domain.model;

import java.util.List;

public final class GrupoDeRota {

    private final int numero;
    private final List<PontoDeVisita> pontosDeVisita;

    public GrupoDeRota(int numero, List<PontoDeVisita> pontosDeVisita) {
        if (numero <= 0) {
            throw new IllegalArgumentException("O número do grupo deve ser maior que zero");
        }
        if (pontosDeVisita == null || pontosDeVisita.isEmpty()) {
            throw new IllegalArgumentException("O grupo deve conter ao menos um ponto de visita");
        }
        this.numero = numero;
        this.pontosDeVisita = List.copyOf(pontosDeVisita);
    }

    public int getNumero() {
        return numero;
    }

    public List<PontoDeVisita> getPontosDeVisita() {
        return pontosDeVisita;
    }
}
