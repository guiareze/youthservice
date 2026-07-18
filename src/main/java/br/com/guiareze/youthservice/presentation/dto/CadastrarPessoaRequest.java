package br.com.guiareze.youthservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CadastrarPessoaRequest(

        @Schema(description = "Nome completo da pessoa", example = "Maria da Silva")
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @Schema(description = "Idade da pessoa (obrigatória, não pode ser zero)", example = "30")
        @NotNull(message = "A idade é obrigatória")
        Integer idade,

        @Schema(description = "Telefone no formato internacional E.164", example = "+5511952526969")
        @NotBlank(message = "O telefone é obrigatório")
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "O telefone deve seguir o formato internacional E.164, ex: +5511952526969")
        String telefone
) {
}
