package br.com.guiareze.youthservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EnderecoVisitaRequest(

        @Schema(description = "Nome do morador a ser visitado", example = "Maria da Silva")
        @NotBlank(message = "O nome do morador é obrigatório")
        String nomeMorador,

        @Schema(description = "CEP do endereço a visitar", example = "01310-100")
        @NotBlank(message = "O CEP é obrigatório")
        @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "O CEP deve estar no formato 00000-000 ou 00000000")
        String cep,

        @Schema(description = "Número da residência", example = "100")
        @NotBlank(message = "O número é obrigatório")
        String numero
) {
}
