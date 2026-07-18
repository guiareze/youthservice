package br.com.guiareze.youthservice.presentation.controller;

import br.com.guiareze.youthservice.application.usecase.CadastrarPessoaUseCase;
import br.com.guiareze.youthservice.application.usecase.ConsultarPessoaUseCase;
import br.com.guiareze.youthservice.application.usecase.ListarPessoasUseCase;
import br.com.guiareze.youthservice.domain.model.Pessoa;
import br.com.guiareze.youthservice.infrastructure.mapper.PessoaMapper;
import br.com.guiareze.youthservice.presentation.dto.CadastrarPessoaRequest;
import br.com.guiareze.youthservice.presentation.dto.ErroResponse;
import br.com.guiareze.youthservice.presentation.dto.PessoaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/pessoas", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Pessoas", description = "Cadastro e consulta de pessoas")
public class PessoaController {

    private static final Logger log = LoggerFactory.getLogger(PessoaController.class);

    private final CadastrarPessoaUseCase cadastrarPessoaUseCase;
    private final ConsultarPessoaUseCase consultarPessoaUseCase;
    private final ListarPessoasUseCase listarPessoasUseCase;
    private final PessoaMapper mapper;

    public PessoaController(CadastrarPessoaUseCase cadastrarPessoaUseCase,
                             ConsultarPessoaUseCase consultarPessoaUseCase,
                             ListarPessoasUseCase listarPessoasUseCase,
                             PessoaMapper mapper) {
        this.cadastrarPessoaUseCase = cadastrarPessoaUseCase;
        this.consultarPessoaUseCase = consultarPessoaUseCase;
        this.listarPessoasUseCase = listarPessoasUseCase;
        this.mapper = mapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar pessoa", description = "Cadastra uma nova pessoa com nome, idade e telefone")
    @ApiResponse(responseCode = "201", description = "Pessoa cadastrada com sucesso",
            content = @Content(schema = @Schema(implementation = PessoaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "409", description = "Nome já cadastrado",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public PessoaResponse cadastrar(@Valid @RequestBody CadastrarPessoaRequest request) {
        log.info("Recebida requisição de cadastro de pessoa");
        Pessoa pessoa = mapper.toDomain(request);
        Pessoa salvo = cadastrarPessoaUseCase.executar(pessoa);
        log.info("Pessoa cadastrada com sucesso: {}", salvo.getId());
        return mapper.toResponse(salvo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar pessoa por id", description = "Retorna os dados de uma pessoa cadastrada")
    @ApiResponse(responseCode = "200", description = "Pessoa encontrada",
            content = @Content(schema = @Schema(implementation = PessoaResponse.class)))
    @ApiResponse(responseCode = "404", description = "Pessoa não encontrada",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public PessoaResponse consultar(@PathVariable UUID id) {
        Pessoa pessoa = consultarPessoaUseCase.executar(id);
        return mapper.toResponse(pessoa);
    }

    @GetMapping
    @Operation(summary = "Listar pessoas", description = "Lista todas as pessoas cadastradas, sem paginação")
    @ApiResponse(responseCode = "200", description = "Lista de pessoas cadastradas")
    public List<PessoaResponse> listar() {
        return listarPessoasUseCase.executar().stream()
                .map(mapper::toResponse)
                .toList();
    }
}
