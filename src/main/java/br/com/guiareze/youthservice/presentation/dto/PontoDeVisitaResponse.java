package br.com.guiareze.youthservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PontoDeVisitaResponse(

        @Schema(description = "Nome do morador a ser visitado", example = "Maria da Silva")
        String nomeMorador,

        @Schema(description = "Endereço completo resolvido a partir do CEP e número", example = "Avenida Paulista, 100 - Bela Vista, São Paulo/SP, CEP 01310-100")
        String enderecoCompleto,

        @Schema(description = "Posição do endereço na rota do grupo (1 = primeira parada)", example = "1")
        Integer ordemVisita
) {
}
