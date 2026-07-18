package br.com.guiareze.youthservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record ErroResponse(

        @Schema(description = "Código do erro", example = "NOME_JA_CADASTRADO")
        String erro,

        @Schema(description = "Mensagem descritiva do erro", example = "Já existe uma pessoa cadastrada com o nome 'Maria da Silva'")
        String mensagem,

        @Schema(description = "Data e hora em que o erro ocorreu")
        Instant timestamp
) {
}
