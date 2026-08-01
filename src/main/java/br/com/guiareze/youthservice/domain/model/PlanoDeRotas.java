package br.com.guiareze.youthservice.domain.model;

import java.util.List;

public final class PlanoDeRotas {

    private final List<GrupoDeRota> grupos;

    public PlanoDeRotas(List<GrupoDeRota> grupos) {
        if (grupos == null || grupos.isEmpty()) {
            throw new IllegalArgumentException("O plano deve conter ao menos um grupo");
        }
        this.grupos = List.copyOf(grupos);
    }

    public List<GrupoDeRota> getGrupos() {
        return grupos;
    }
}
