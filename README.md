# youthservice

API REST em Java/Spring Boot para cadastro de pessoas, construída com arquitetura hexagonal (ports & adapters) e banco H2. Projeto novo, ainda em fase inicial — a primeira (e única, até o momento) capability implementada é o **cadastro de pessoas**.

## Stack

- **Java 25**, **Spring Boot 4.1.0** (Spring Framework 7)
- **H2** (embarcado, em memória) — schema versionado manualmente em `src/main/resources/schema.sql`
- **Spring Data JPA** / Hibernate ORM para persistência
- **MapStruct 1.6.3** para conversão entre camadas (domínio ↔ entidade JPA ↔ DTOs)
- **springdoc-openapi 2.8.5** para documentação OpenAPI/Swagger (`/swagger-ui.html`, `/v3/api-docs`)
- **Jakarta Validation** (Bean Validation) para validação de entrada
- Testes: JUnit 5, Mockito, AssertJ

> Nota de ambiente: os starters de teste deste projeto (`spring-boot-starter-data-jpa-test`, `spring-boot-starter-webmvc-test`) **não** trazem `MockMvc`/`TestRestTemplate` tradicionais. Os testes de integração chamam os `@RestController` diretamente via `@SpringBootTest` + `@Autowired`, e o mapeamento de exceção → status HTTP é testado isoladamente em `GlobalExceptionHandlerTest`.

## Arquitetura

Estrutura de pacotes sob `br.com.guiareze.youthservice`, seguindo hexagonal:

```
domain/          → entidades e regras de negócio puras (sem dependência de framework)
  model/            Pessoa (validação de nome, idade, telefone no construtor/factory)
  exception/        DomainException e subtipos (PessoaInvalidaException, NomeJaCadastradoException, PessoaNaoEncontradaException)
application/     → casos de uso e portas (interfaces)
  usecase/          CadastrarPessoaUseCase, ConsultarPessoaUseCase, ListarPessoasUseCase
  port/             PessoaRepository (interface implementada pela infra)
infrastructure/  → adapters concretos
  persistence/      PessoaEntity (JPA), PessoaJpaRepository (Spring Data), PessoaRepositoryAdapter (implementa a porta)
  mapper/           PessoaMapper (MapStruct: Pessoa ↔ PessoaEntity ↔ DTOs)
  config/           OpenApiConfig
presentation/    → camada HTTP
  controller/       PessoaController (endpoints REST)
  dto/              CadastrarPessoaRequest, PessoaResponse, ErroResponse (todos `record`)
  exception/        GlobalExceptionHandler (@RestControllerAdvice — mapeia exceções para respostas HTTP padronizadas)
```

**Regra de dependência**: `domain` não depende de nada externo; `application` depende só de `domain`; `infrastructure` e `presentation` implementam/consomem as portas de `application`. Toda nova capability deve seguir esse mesmo padrão de camadas.

**Padrão de validação em dupla camada** (aplicado a todo campo de `Pessoa`): validação de formato/presença no DTO (`@NotBlank`, `@NotNull`, `@Pattern`) **e** validação de invariante no construtor/factory da entidade de domínio (`Pessoa.criar`). Isso garante que `Pessoa` nunca exista em estado inválido, mesmo se criada por um caminho que não passe pelo controller.

## Capability atual: cadastro de pessoas

Endpoints (base path `/pessoas`, sempre `application/json`):

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/pessoas` | Cadastra uma pessoa (nome, idade, telefone — todos obrigatórios) |
| `GET` | `/pessoas/{id}` | Consulta uma pessoa por id |
| `GET` | `/pessoas` | Lista todas as pessoas (sem paginação) |

Regras de negócio principais:
- `nome`: obrigatório, não vazio, **único** (constraint `UNIQUE` no banco + checagem na aplicação)
- `idade`: obrigatória, não pode ser `0` (sem limite mínimo/máximo)
- `telefone`: entrada no formato E.164 (ex: `+5511952526969`); armazenado normalizado, só com dígitos

Erros seguem um formato padrão (`ErroResponse`: `erro`, `mensagem`, `timestamp`) com códigos `DADOS_INVALIDOS` (400, falha de Bean Validation), `PESSOA_INVALIDA` (400, falha de regra de domínio), `NOME_JA_CADASTRADO` (409), `PESSOA_NAO_ENCONTRADA` (404).

Documentação detalhada da API: [`docs/api-pessoas.md`](docs/api-pessoas.md).

## Rodando o projeto

```bash
./mvnw spring-boot:run       # sobe a aplicação (porta padrão 8080)
./mvnw test                  # roda a suíte de testes
./mvnw clean install         # build completo (compila, testa, empacota)
```

Console H2 disponível em `/h2-console` (JDBC URL: `jdbc:h2:mem:youthservice`).

## Fluxo de trabalho: OpenSpec (SDD)

Este projeto usa **OpenSpec** (spec-driven development) para planejar e rastrear mudanças. Antes de qualquer contribuição maior, vale entender essa estrutura — ela é a forma mais barata (em contexto) de saber "o que o sistema já faz" sem precisar reler todo o código-fonte:

- **`openspec/specs/<capability>/spec.md`** — fonte de verdade **atual** do comportamento do sistema, por capability. Hoje existe apenas `openspec/specs/cadastro-pessoa/spec.md`. Sempre consultar isso primeiro para entender requisitos e regras já implementadas.
- **`openspec/changes/archive/<data>-<nome>/`** — histórico de mudanças já implementadas e arquivadas (proposal, design, specs delta, tasks). Útil para entender o *porquê* de uma decisão (ex: por que a idade virou obrigatória, por que a duplicidade é por nome e não telefone), mas não é lido automaticamente — só quando necessário investigar histórico.
- **`openspec/changes/<nome>/`** — mudanças em andamento (ainda não arquivadas).

Comandos do fluxo (slash commands): `/opsx:propose` (nova mudança) → `/opsx:apply` (implementar) → `/opsx:archive` (fechar e sincronizar specs). `/opsx:update` revisa artefatos de uma mudança em andamento sem tocar em código.

## Convenções para quem for continuar este projeto (humano ou agente)

- Siga o padrão hexagonal já estabelecido (seção Arquitetura acima) para qualquer nova capability — não introduza uma camada de serviço tradicional (Controller-Service-Repository) misturada com isso.
- DTOs sempre como `record`; conversão entre camadas sempre via `PessoaMapper`/MapStruct (ou um novo mapper equivalente por capability), nunca inline no controller ou use case.
- Toda nova regra de validação segue o padrão de dupla camada (DTO + domínio) e reaproveita os códigos de erro já existentes (`DADOS_INVALIDOS`, `PESSOA_INVALIDA`, etc.) antes de criar um novo.
- Antes de propor uma mudança, leia o spec principal da capability afetada em `openspec/specs/`; ele resume o comportamento atual sem precisar reler o código-fonte inteiro.
- H2 é uma escolha deliberada da fase atual (app local, sem produção); mudanças de schema podem ser feitas diretamente em `schema.sql` sem migration incremental, até que o projeto migre para um banco persistente real.
