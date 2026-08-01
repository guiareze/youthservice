package br.com.guiareze.youthservice.presentation.controller;

import br.com.guiareze.youthservice.application.usecase.PlanejarRotasUseCase;
import br.com.guiareze.youthservice.domain.model.PlanoDeRotas;
import br.com.guiareze.youthservice.infrastructure.mapper.PlanoDeRotasMapper;
import br.com.guiareze.youthservice.presentation.dto.ErroResponse;
import br.com.guiareze.youthservice.presentation.dto.PlanejarRotasRequest;
import br.com.guiareze.youthservice.presentation.dto.PlanoDeRotasResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rotas", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Rotas", description = "Planejamento de rotas de visita por grupo de voluntários")
public class RotaController {

    private static final Logger log = LoggerFactory.getLogger(RotaController.class);

    private final PlanejarRotasUseCase planejarRotasUseCase;
    private final PlanoDeRotasMapper mapper;

    public RotaController(PlanejarRotasUseCase planejarRotasUseCase, PlanoDeRotasMapper mapper) {
        this.planejarRotasUseCase = planejarRotasUseCase;
        this.mapper = mapper;
    }

    @PostMapping(value = "/planejamento", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Planejar rotas de visita",
            description = "Divide os endereços a visitar entre os grupos de voluntários por proximidade geográfica e ordena cada rota do endereço mais distante ao mais próximo do ponto de partida")
    @ApiResponse(responseCode = "200", description = "Plano de rotas gerado com sucesso",
            content = @Content(schema = @Schema(implementation = PlanoDeRotasResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Endereço não localizado geograficamente",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public PlanoDeRotasResponse planejar(@Valid @RequestBody PlanejarRotasRequest request) {
        log.info("Recebida requisição de planejamento de rotas: {} endereços, {} grupos",
                request.enderecosVisitar().size(), request.quantidadeGrupos());
        PlanoDeRotas plano = planejarRotasUseCase.executar(
                mapper.toEntrada(request.pontoPartida()),
                mapper.toEntrada(request.enderecosVisitar()),
                request.quantidadeGrupos());
        return mapper.toResponse(plano);
    }
}
