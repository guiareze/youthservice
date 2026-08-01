package br.com.guiareze.youthservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PlanoDeRotasResponse(

        @Schema(description = "Grupos de voluntários com suas respectivas rotas de visita")
        List<GrupoDeRotaResponse> grupos
) {
}
