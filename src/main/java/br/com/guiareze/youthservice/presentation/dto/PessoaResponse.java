package br.com.guiareze.youthservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record PessoaResponse(

        @Schema(description = "Identificador único da pessoa")
        UUID id,

        @Schema(description = "Nome completo da pessoa", example = "Maria da Silva")
        String nome,

        @Schema(description = "Idade da pessoa", example = "30")
        Integer idade,

        @Schema(description = "Telefone armazenado apenas com dígitos", example = "5511952526969")
        String telefone,

        @Schema(description = "Data e hora do cadastro")
        Instant criadoEm
) {
}
