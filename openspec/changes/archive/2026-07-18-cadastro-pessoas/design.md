## Context

Este é um projeto novo (greenfield) que será construído em Java com Spring Boot, seguindo arquitetura hexagonal (ports and adapters), utilizando banco de dados H2 para a fase inicial de desenvolvimento. O cadastro de pessoas é a primeira funcionalidade do sistema e estabelece a base arquitetural que será reutilizada por funcionalidades futuras.

Não há sistemas legados ou integrações externas a considerar neste momento. A funcionalidade é relativamente simples em termos de regras de negócio, mas a estruturação arquitetural precisa ser bem definida desde o início para facilitar a evolução do projeto.

## Goals / Non-Goals

**Goals:**
- Estabelecer a estrutura de camadas da arquitetura hexagonal (domínio, aplicação, infraestrutura, apresentação) que servirá de padrão para o restante do projeto
- Implementar cadastro, consulta individual e listagem de pessoas
- Garantir validação de dados de entrada (nome, idade, telefone)
- Persistir dados de forma simples no H2, preparando a estrutura para futura migração a um banco de produção
- Prevenir duplicidade de cadastro pelo nome da pessoa
- Documentar os endpoints via OpenAPI/Swagger, além da documentação em Markdown
- Utilizar DTOs como `record` e mappers dedicados para conversão entre camadas

**Non-Goals:**
- Atualização (edição) e exclusão de pessoas cadastradas (CRUD completo fica para uma iteração futura)
- Autenticação e autorização de acesso à API (fora do escopo desta funcionalidade)
- Paginação e filtros avançados na listagem (volumetria baixa esperada; não é necessário no momento)
- Internacionalização de mensagens de erro (inicialmente apenas em português)
- Auditoria detalhada (logs de quem alterou o quê) — logging básico é suficiente por ora
- Regras de negócio sobre faixa etária (idade é apenas um campo numérico simples)

## Decisions

### Decisão 1: Estrutura de Camadas da Arquitetura Hexagonal

**Escolha:** Organizar o código em pacotes representando as camadas hexagonais:
- **Domínio** (`domain/model/Pessoa.java`): entidade de domínio com regras de validação intrínsecas
- **Aplicação** (`application/usecase/`): casos de uso (CadastrarPessoaUseCase, ConsultarPessoaUseCase, ListarPessoasUseCase)
- **Portas** (`application/port/`): interfaces que definem contratos com o mundo externo (ex: PessoaRepository)
- **Infraestrutura** (`infrastructure/persistence/`): implementação concreta dos adapters (ex: JPA/H2)
- **Apresentação** (`presentation/controller/`): controllers REST expondo a API

**Racional:** Essa separação clara de responsabilidades facilita testes unitários isolados, permite trocar a implementação de persistência sem afetar a lógica de negócio, e estabelece um padrão consistente para as próximas funcionalidades do sistema.

**Alternativa considerada:** Estrutura em camadas tradicional (Controller-Service-Repository) sem portas explícitas — mais simples de implementar inicialmente, mas gera acoplamento direto entre lógica de negócio e frameworks, dificultando testes e evolução futura.

---

### Decisão 2: Modelo de Dados da Entidade Pessoa

**Escolha:** Representar a pessoa com os campos: `id` (UUID), `nome` (String, único), `idade` (Integer, opcional), `telefone` (String), `criadoEm` (Timestamp).

```sql
CREATE TABLE pessoas (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    idade INTEGER,
    telefone VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Racional:** UUID como identificador evita colisões e facilita futura distribuição/migração de dados. A restrição `UNIQUE` foi movida do campo `telefone` para o campo `nome`, refletindo a regra de negócio de que o nome é o campo protegido contra duplicidade. O campo `idade` é opcional (nullable), já que não há regra de negócio de faixa etária no momento.

**Alternativa considerada:** Identificador auto-incremento (Long/Integer) — mais simples, mas menos flexível para cenários futuros de integração entre sistemas ou sincronização distribuída.

---

### Decisão 3: Validação de Dados

**Escolha:** Validação em múltiplas camadas:
1. **Camada de apresentação (Controller/DTO)**: validação de formato usando Jakarta Validation (`@NotBlank` para nome, `@Pattern` para telefone no formato E.164)
2. **Camada de domínio (Entidade Pessoa)**: validação de invariantes de negócio reforçada no construtor/factory da entidade:
   - Nome não pode ser vazio
   - Idade, se informada, não pode ser igual a zero (sem faixa mínima ou máxima)
   - Telefone deve seguir o formato E.164 na entrada

**Racional:** Validação na camada de apresentação oferece feedback rápido ao cliente da API. Validação no domínio garante que a entidade nunca exista em estado inválido, independentemente de por onde for criada (proteção contra bypass de validação). A decisão de não aplicar faixa etária reflete a ausência de regra de negócio sobre idade neste momento — o campo é tratado como informação simples, não como dado crítico de negócio.

**Alternativa considerada:** Validação apenas na camada de apresentação — mais simples, porém não garante a integridade do domínio se a entidade for criada por outro caminho (ex.: testes, futuras integrações internas).

---

### Decisão 4: Formato de Telefone

**Escolha:** Aceitar telefone na entrada apenas no formato internacional E.164 (ex: `+5511952526969`). No banco de dados, armazenar apenas os dígitos, sem o prefixo `+` (ex: `5511952526969`).

**Racional:** O padrão E.164 é um formato internacional bem definido e não ambíguo, simplificando a validação de entrada. Armazenar apenas dígitos facilita comparações e buscas futuras, evitando variações de formatação no banco.

**Alternativa considerada:** Armazenar exatamente como o usuário digitou (incluindo o `+`) — mais simples, mas gera inconsistência de formato para buscas e futuras integrações.

---

### Decisão 5: Unicidade pelo Nome

**Escolha:** O campo `nome` é o campo protegido contra duplicidade. O sistema rejeita o cadastro de uma pessoa cujo nome já exista no banco de dados (constraint `UNIQUE` no banco, validada previamente na camada de aplicação).

**Racional:** Conforme definição de negócio, o nome é o identificador natural de unicidade para este cadastro, substituindo a validação anteriormente prevista sobre o telefone.

**Alternativa considerada:** Unicidade composta (nome + telefone) — rejeitada por adicionar complexidade não solicitada pelo negócio no momento.

---

### Decisão 6: DTOs como Records

**Escolha:** Todos os DTOs de entrada e saída da API (ex: `CadastrarPessoaRequest`, `PessoaResponse`) serão implementados como `record` do Java.

```java
public record CadastrarPessoaRequest(
    @NotBlank String nome,
    Integer idade,
    @NotBlank @Pattern(regexp = "^\\+[1-9]\\d{1,14}$") String telefone
) {}

public record PessoaResponse(
    UUID id,
    String nome,
    Integer idade,
    String telefone,
    Instant criadoEm
) {}
```

**Racional:** Records reduzem boilerplate (getters, equals, hashCode, toString automáticos), tornam os DTOs imutáveis por padrão e deixam explícita a intenção de que são apenas estruturas de transporte de dados, sem lógica de negócio.

**Alternativa considerada:** Classes tradicionais com Lombok (`@Data`, `@Value`) — funcional, mas adiciona dependência extra e é menos idiomático para representar dados imutáveis em versões modernas do Java.

---

### Decisão 7: Mappers via MapStruct

**Escolha:** Utilizar **MapStruct** para gerar mappers dedicados que convertem entre entidade de domínio (`Pessoa`), entidade de persistência (`PessoaEntity`) e DTOs (`CadastrarPessoaRequest`, `PessoaResponse`).

```java
@Mapper(componentModel = "spring")
public interface PessoaMapper {
    PessoaEntity toEntity(Pessoa pessoa);
    Pessoa toDomain(PessoaEntity entity);
    PessoaResponse toResponse(Pessoa pessoa);
}
```

**Racional:** Mappers dedicados evitam lógica de conversão espalhada pelo código (ex: dentro de use cases ou controllers), centralizando essa responsabilidade e facilitando manutenção. MapStruct gera código em tempo de compilação, evitando overhead de reflexão em tempo de execução e reduzindo boilerplate manual de mapeamento.

**Alternativas consideradas:**
- Conversão manual inline nos use cases/controllers — mais rápido de escrever inicialmente, mas espalha lógica de mapeamento e dificulta manutenção conforme o modelo cresce.
- Padrão Builder manual — evita dependência externa, mas exige manutenção manual do código de mapeamento a cada mudança de modelo; descartado em favor da geração automática do MapStruct.

---

### Decisão 8: Documentação OpenAPI/Swagger

**Escolha:** Utilizar a biblioteca `springdoc-openapi` para gerar automaticamente a documentação OpenAPI dos endpoints, disponibilizando a interface Swagger UI para exploração interativa da API, além da documentação em Markdown já prevista.

**Racional:** springdoc-openapi se integra nativamente com Spring Boot e Jakarta Validation, gerando documentação a partir das anotações já presentes nos controllers e DTOs, sem exigir manutenção duplicada de contratos de API.

**Alternativa considerada:** Documentação manual apenas em Markdown — mais simples, porém não oferece exploração interativa nem geração automática a partir do código, aumentando risco de desatualização.

---

### Decisão 9: Banco de Dados H2

**Escolha:** Utilizar H2 em modo embarcado (in-memory ou arquivo local) durante o desenvolvimento inicial, com script de inicialização de schema versionado (ex: Flyway ou schema.sql do Spring Boot).

**Racional:** H2 é adequado para desenvolvimento e testes rápidos sem necessidade de infraestrutura externa. Uso de migrations versionadas (mesmo com H2) prepara o projeto para uma futura migração de banco (ex: PostgreSQL) com menor esforço.

**Alternativa considerada:** H2 sem controle de schema versionado (apenas `ddl-auto: update` do Hibernate) — mais rápido para prototipagem, porém não recomendado pois dificulta rastreabilidade de mudanças de schema e migração futura para produção.

## Risks / Trade-offs

| Risco | Mitigação |
|-------|-----------|
| Ausência de autenticação na API expõe endpoint publicamente | Aceitável para esta fase inicial (non-goal); autenticação deve ser tratada em funcionalidade futura antes de expor a API publicamente |
| Falta de suporte a atualização/exclusão pode gerar necessidade de recadastro em caso de erro de digitação | Aceitável como non-goal desta fase; funcionalidade de edição pode ser adicionada rapidamente reaproveitando a estrutura hexagonal já definida |
| Ausência de faixa etária pode permitir valores incomuns (ex: idade negativa ou muito alta) | Aceito deliberadamente conforme decisão de negócio; nenhuma regra além de "diferente de zero" é aplicada no momento |
| H2 embarcado não é adequado para produção | Já esperado e comunicado; H2 é uma escolha deliberada para fase inicial, com plano de migração futura |
| Condição de corrida em cadastros simultâneos com mesmo nome | Constraint `UNIQUE` no banco (campo `nome`) garante consistência final mesmo em caso de race condition na camada de aplicação |
| Nomes duplicados legítimos (ex: duas pessoas reais com o mesmo nome) não poderão ser cadastrados | Aceito conforme decisão de negócio explícita; caso se torne um problema, pode ser revisado em iteração futura |

## Migration Plan

**Fase 1 (Implementação inicial):**
1. Criar schema da tabela `pessoas` via script de migração (Flyway ou schema.sql), com constraint `UNIQUE` em `nome`
2. Implementar estrutura de camadas hexagonais e casos de uso de cadastro/consulta/listagem
3. Implementar DTOs como records e mappers de conversão entre camadas
4. Expor endpoints REST em `/pessoas`, documentados em Markdown e via OpenAPI/Swagger

**Fase 2 (Evoluções futuras, fora do escopo atual):**
1. Adicionar suporte a atualização e exclusão de pessoas
2. Avaliar necessidade de autenticação/autorização
3. Avaliar migração de H2 para banco de produção (PostgreSQL, MySQL, etc.)
4. Reavaliar necessidade de paginação caso volumetria aumente

**Estratégia de Rollback:**
- Como este é o cadastro inicial do sistema sem dependências externas, rollback consiste em reverter a migration do schema e remover os artefatos de código, sem impacto em outros módulos.

## Open Questions

Todas as questões em aberto identificadas anteriormente foram respondidas e incorporadas às decisões acima:
- Formato de telefone (E.164) → Decisão 4
- Faixa de idade (sem limite, apenas diferente de zero) → Decisões 2 e 3
- Campo de unicidade (nome) → Decisão 5
- Necessidade de outros campos (não há, por hora) → refletido no modelo de dados (Decisão 2)
- Formato de resposta da API (sempre `application/json`) → padrão assumido nos controllers REST
- Volumetria e paginação (baixa volumetria, sem paginação) → refletido nos Non-Goals
- Abordagem de mapeamento entre camadas (MapStruct) → Decisão 7

Não há questões em aberto no momento.
