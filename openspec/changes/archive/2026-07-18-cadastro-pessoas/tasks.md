## 1. Configuração Inicial do Projeto

- [x] 1.1 Criar estrutura de pastas do projeto Spring Boot seguindo arquitetura hexagonal (`domain`, `application`, `infrastructure`, `presentation`)
- [x] 1.2 Adicionar dependências no `pom.xml`: Spring Web, Spring Data JPA, Validation, H2 Database
- [x] 1.3 Adicionar dependência `springdoc-openapi-starter-webmvc-ui` para documentação OpenAPI/Swagger
- [x] 1.4 Adicionar dependências do MapStruct (`mapstruct` e `mapstruct-processor`) no `pom.xml`
- [x] 1.5 Configurar banco H2 no `application.properties` (modo embarcado, console habilitado para desenvolvimento)
- [x] 1.6 Criar classe base de exceção de domínio (`DomainException`)

## 2. Esquema de Banco de Dados

- [x] 2.1 Criar script de migração (Flyway ou schema.sql) para a tabela `pessoas` com campos: id (UUID), nome, idade (nullable), telefone, criado_em
- [x] 2.2 Adicionar constraint `UNIQUE` no campo `nome`
- [x] 2.3 Adicionar índice no campo `nome` para otimizar busca de duplicidade

## 3. Domínio e Regras de Negócio

- [x] 3.1 Implementar entidade de domínio `Pessoa` com validações no construtor/factory (nome não vazio; idade, se informada, diferente de zero)
- [x] 3.2 Implementar validação de formato de telefone (padrão E.164) na entidade de domínio
- [x] 3.3 Criar exceções customizadas: `PessoaInvalidaException`, `NomeJaCadastradoException`, `PessoaNaoEncontradaException`
- [x] 3.4 Implementar lógica de normalização de telefone (remover o prefixo `+`, mantendo apenas dígitos)

## 4. Portas (Interfaces)

- [x] 4.1 Criar interface de porta `PessoaRepository` com métodos: salvar(), buscarPorId(), listarTodas(), existePorNome()

## 5. Casos de Uso (Camada de Aplicação)

- [x] 5.1 Implementar `CadastrarPessoaUseCase` com fluxo: validar dados → verificar duplicidade de nome → persistir pessoa
- [x] 5.2 Implementar `ConsultarPessoaUseCase` para busca por identificador
- [x] 5.3 Implementar `ListarPessoasUseCase` para listagem de todas as pessoas cadastradas (sem paginação)

## 6. DTOs (Records)

- [x] 6.1 Criar `record CadastrarPessoaRequest(String nome, Integer idade, String telefone)` com anotações de validação Jakarta
- [x] 6.2 Criar `record PessoaResponse(UUID id, String nome, Integer idade, String telefone, Instant criadoEm)`
- [x] 6.3 Criar `record ErroResponse(String erro, String mensagem, Instant timestamp)` para respostas de erro padronizadas

## 7. Mappers

- [x] 7.1 Implementar mapper (`PessoaMapper`) para conversão entre `Pessoa` (domínio) e `PessoaEntity` (persistência), usando MapStruct
- [x] 7.2 Implementar mapper para conversão entre `CadastrarPessoaRequest` (DTO) e `Pessoa` (domínio)
- [x] 7.3 Implementar mapper para conversão entre `Pessoa` (domínio) e `PessoaResponse` (DTO)

## 8. Adapters - Persistência

- [x] 8.1 Implementar entidade JPA `PessoaEntity` mapeada para a tabela `pessoas`
- [x] 8.2 Implementar interface Spring Data JPA `PessoaJpaRepository` estendendo `JpaRepository`
- [x] 8.3 Implementar `PessoaRepositoryAdapter` adaptando o Spring Data JPA para a porta `PessoaRepository`, utilizando o mapper para conversão entre `Pessoa` e `PessoaEntity`

## 9. Camada de Apresentação (Controllers)

- [x] 9.1 Criar `PessoaController` com endpoint `POST /pessoas` para cadastro
- [x] 9.2 Adicionar anotações de validação (`@NotBlank`, `@Pattern`) no record `CadastrarPessoaRequest`
- [x] 9.3 Criar endpoint `GET /pessoas/{id}` para consulta individual
- [x] 9.4 Criar endpoint `GET /pessoas` para listagem de todas as pessoas
- [x] 9.5 Implementar `@ExceptionHandler` para tratar exceções de domínio e retornar respostas de erro padronizadas (`ErroResponse`)
- [x] 9.6 Adicionar logging básico para operações de cadastro e erros
- [x] 9.7 Garantir que todos os endpoints consomem e produzem `application/json`

## 10. Documentação OpenAPI/Swagger

- [x] 10.1 Configurar springdoc-openapi no projeto (propriedades básicas, título e descrição da API)
- [x] 10.2 Adicionar anotações OpenAPI (`@Operation`, `@ApiResponse`, `@Schema`) nos endpoints e DTOs do `PessoaController`
- [x] 10.3 Validar acesso à interface Swagger UI (ex: `/swagger-ui.html`) exibindo os endpoints de cadastro, consulta e listagem

## 11. Testes - Testes Unitários

- [x] 11.1 Testar validação da entidade `Pessoa` (nome vazio, idade igual a zero, telefone em formato inválido)
- [x] 11.2 Testar `CadastrarPessoaUseCase` com fluxo de sucesso (cadastro válido, com e sem idade informada)
- [x] 11.3 Testar `CadastrarPessoaUseCase` com nome duplicado (deve lançar `NomeJaCadastradoException`)
- [x] 11.4 Testar `ConsultarPessoaUseCase` com pessoa existente e inexistente
- [x] 11.5 Testar `ListarPessoasUseCase` com lista vazia e lista com dados
- [x] 11.6 Testar lógica de normalização de telefone (remoção do prefixo `+`)
- [x] 11.7 Testar mappers (conversão entre `Pessoa`, `PessoaEntity` e DTOs)

## 12. Testes - Testes de Integração

- [x] 12.1 Teste de integração do endpoint `POST /pessoas` com dados válidos retornando 201 e os dados da pessoa criada
- [x] 12.2 Teste de integração do endpoint `POST /pessoas` com nome vazio retornando 400
- [x] 12.3 Teste de integração do endpoint `POST /pessoas` com idade igual a zero retornando 400
- [x] 12.4 Teste de integração do endpoint `POST /pessoas` sem informar idade retornando 201
- [x] 12.5 Teste de integração do endpoint `POST /pessoas` com telefone em formato inválido retornando 400
- [x] 12.6 Teste de integração do endpoint `POST /pessoas` com nome duplicado retornando 409
- [x] 12.7 Teste de integração do endpoint `GET /pessoas/{id}` com id existente e inexistente
- [x] 12.8 Teste de integração do endpoint `GET /pessoas` com lista vazia e lista populada
- [x] 12.9 Teste de integração validando que o telefone é armazenado e retornado apenas com dígitos

## 13. Documentação

- [x] 13.1 Documentar endpoint de cadastro (`POST /pessoas`) com exemplos de requisição e resposta em Markdown
- [x] 13.2 Documentar endpoints de consulta (`GET /pessoas/{id}` e `GET /pessoas`) com exemplos em Markdown
- [x] 13.3 Documentar formato de erros e códigos utilizados (PESSOA_INVALIDA, NOME_JA_CADASTRADO, PESSOA_NAO_ENCONTRADA)
- [x] 13.4 Documentar regras de validação de nome, idade e telefone
- [x] 13.5 Validar que a documentação OpenAPI/Swagger gerada está coerente com a documentação em Markdown

## 14. Validação Final

- [x] 14.1 Executar todos os testes unitários e de integração com sucesso (33/33 testes passando via `mvn test`)
- [x] 14.2 Validar fluxo completo manualmente: cadastrar pessoa → consultar por id → listar todas
- [x] 14.3 Validar interface Swagger UI manualmente, testando os endpoints diretamente pela interface
- [x] 14.4 Revisar código: sem duplicação desnecessária, tratamento de erros consistente, aderência à arquitetura hexagonal definida no design
