package br.com.guiareze.youthservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GrupoDeRotaResponse(

        @Schema(description = "Número do grupo de voluntários", example = "1")
        Integer numeroGrupo,

        @Schema(description = "Endereços a visitar, na ordem da rota (do mais distante ao mais próximo do ponto de partida)")
        List<PontoDeVisitaResponse> enderecos
) {
}
