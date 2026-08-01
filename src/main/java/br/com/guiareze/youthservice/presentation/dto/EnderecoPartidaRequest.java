package br.com.guiareze.youthservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EnderecoPartidaRequest(

        @Schema(description = "CEP do ponto de partida", example = "01310-100")
        @NotBlank(message = "O CEP do ponto de partida é obrigatório")
        @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "O CEP deve estar no formato 00000-000 ou 00000000")
        String cep,

        @Schema(description = "Número do ponto de partida", example = "1000")
        @NotBlank(message = "O número do ponto de partida é obrigatório")
        String numero
) {
}
