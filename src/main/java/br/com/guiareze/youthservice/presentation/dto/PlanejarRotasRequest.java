package br.com.guiareze.youthservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PlanejarRotasRequest(

        @Schema(description = "Endereço de onde os grupos partem para as visitas")
        @NotNull(message = "O ponto de partida é obrigatório")
        @Valid
        EnderecoPartidaRequest pontoPartida,

        @Schema(description = "Lista de endereços a serem visitados")
        @NotEmpty(message = "É necessário informar ao menos um endereço a visitar")
        @Valid
        List<EnderecoVisitaRequest> enderecosVisitar,

        @Schema(description = "Quantidade de grupos de voluntários para dividir as visitas", example = "4")
        @NotNull(message = "A quantidade de grupos é obrigatória")
        @Positive(message = "A quantidade de grupos deve ser maior que zero")
        Integer quantidadeGrupos
) {
}
