## Context

O cadastro de pessoa (`POST /pessoas`) foi implementado com o campo `idade` opcional (`Integer`, nullable), com a única regra de negócio de que, se informado, não poderia ser igual a zero. A regra de negócio mudou: `idade` agora é um dado obrigatório do cadastro, no mesmo nível de `nome` e `telefone`.

A aplicação ainda está em desenvolvimento local, sem dados de produção no H2, então a alteração de schema (`idade` de nullable para `NOT NULL`) pode ser feita diretamente, sem necessidade de migração de dados existentes ou estratégia de backfill.

O projeto já segue um padrão estabelecido de validação em duas camadas (apresentação via Jakarta Validation + domínio via `Pessoa`) e de tratamento de erros centralizado via `GlobalExceptionHandler`. Esta mudança deve seguir exatamente esse padrão já existente, sem introduzir novos mecanismos.

## Goals / Non-Goals

**Goals:**
- Tornar `idade` obrigatória na validação de entrada (`CadastrarPessoaRequest`) e na entidade de domínio (`Pessoa`), seguindo o mesmo padrão de validação em duas camadas já usado para `nome` e `telefone`
- Tornar a coluna `idade` da tabela `pessoas` `NOT NULL`
- Retornar mensagem de erro no padrão já existente do projeto quando `idade` não for enviada
- Atualizar todos os testes unitários e de integração afetados pela mudança de comportamento
- Atualizar a documentação Markdown (`docs/api-pessoas.md`) e as anotações OpenAPI do campo `idade`

**Non-Goals:**
- Migração de dados existentes (não há dados em produção; aplicação local e nova)
- Alterar o tipo do campo `idade` de `Integer` para `int` primitivo (mantém `Integer` no DTO/entidade para não introduzir mudança de tipo fora de escopo; a obrigatoriedade é garantida por validação, não pelo tipo)
- Mudar a regra de "idade diferente de zero" (permanece como está, apenas passa a coexistir com a obrigatoriedade)
- Introduzir uma nova classe de exceção ou um novo código de erro (reutiliza `PESSOA_INVALIDA` e `DADOS_INVALIDOS`, já existentes)

## Decisions

### Decisão 1: Validação de obrigatoriedade em duas camadas (consistente com o padrão existente)

**Escolha:**
1. **DTO (`CadastrarPessoaRequest`)**: adicionar `@NotNull(message = "A idade é obrigatória")` no campo `idade`, ao lado do `@NotBlank` já existente em `nome` e `telefone`. Isso faz a requisição sem `idade` ser rejeitada pelo Bean Validation antes de chegar ao controller, retornando 400 com código `DADOS_INVALIDOS` (mesmo fluxo que já existe para `nome` vazio).
2. **Domínio (`Pessoa.criar`)**: adicionar validação de que `idade` não pode ser `null`, lançando `PessoaInvalidaException` com a mensagem "A idade é obrigatória". Isso mantém a defesa em profundidade já usada para `nome` e telefone, garantindo que a entidade nunca exista em estado inválido independentemente do caminho de criação (ex: futuros consumidores internos do domínio, testes).

**Racional:** Replica exatamente o padrão de validação dupla já estabelecido no projeto para `nome` (obrigatório) e `telefone` (formato), evitando introduzir uma abordagem divergente só para `idade`.

**Alternativa considerada:** Validar apenas no domínio, sem anotação no DTO — rejeitada por quebrar a consistência com o padrão já usado para `nome`, que valida em ambas as camadas.

---

### Decisão 2: Alteração direta do schema (`idade` NOT NULL)

**Escolha:** Alterar `schema.sql` para declarar a coluna `idade` como `NOT NULL`, sem script de migração incremental.

```sql
CREATE TABLE IF NOT EXISTS pessoas (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    idade INTEGER NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP NOT NULL
);
```

E o mapeamento JPA em `PessoaEntity` (`@Column(nullable = false)` no campo `idade`).

**Racional:** Como a aplicação é local, nova e usa H2 sem dados persistidos entre reinicializações relevantes, alterar a definição da tabela diretamente é mais simples e direto do que criar uma migration incremental (`ALTER TABLE`) para lidar com dados legados que não existem.

**Alternativa considerada:** Criar uma migration incremental com Flyway (`ALTER TABLE pessoas ALTER COLUMN idade SET NOT NULL`) — desnecessário no momento, já que não há Flyway no projeto (usa apenas `schema.sql`) nem dados a preservar. Pode ser reavaliado quando o projeto migrar para um banco de produção persistente.

---

### Decisão 3: Reaproveitar códigos de erro existentes

**Escolha:** Não criar um novo código de erro. A ausência de `idade` é tratada como:
- `DADOS_INVALIDOS` (400), quando pega pela validação Bean Validation do DTO (fluxo HTTP normal via `GlobalExceptionHandler.handleValidacao`)
- `PESSOA_INVALIDA` (400), quando pega pela validação de domínio (`Pessoa.criar`), consistente com o tratamento já existente para "idade igual a zero"

**Racional:** Mantém o padrão de erro já documentado e usado pelo projeto, evitando multiplicar códigos de erro para o mesmo tipo de falha (dado obrigatório ausente).

## Risks / Trade-offs

| Risco | Mitigação |
|-------|-----------|
| Testes existentes que cadastram pessoa "sem informar idade" esperando sucesso (201) vão quebrar | Esperado e intencional: esses testes serão atualizados para esperar rejeição (400), refletindo a nova regra de negócio |
| Mudança é **breaking** para qualquer cliente da API que hoje não envia `idade` | Aceito conforme solicitado — aplicação local/nova, sem consumidores externos em produção ainda |
| Divergência entre validação do DTO (`DADOS_INVALIDOS`) e validação do domínio (`PESSOA_INVALIDA`) para o mesmo problema (idade ausente), dependendo do caminho de entrada | Já é o comportamento existente do projeto para outros campos (ex: `nome`); não é uma regressão introduzida por esta mudança |

## Migration Plan

**Passos:**
1. Atualizar `schema.sql` (coluna `idade NOT NULL`)
2. Atualizar `PessoaEntity` (`@Column(nullable = false)`)
3. Atualizar `CadastrarPessoaRequest` (`@NotNull` em `idade`)
4. Atualizar `Pessoa.criar`/validação de domínio (rejeitar `idade == null`)
5. Atualizar testes unitários e de integração afetados (`PessoaTest`, `CadastrarPessoaUseCaseTest`, `PessoaControllerIntegrationTest`, `PessoaMapperTest` se aplicável)
6. Atualizar `docs/api-pessoas.md` e anotação `@Schema` de `idade` no DTO

**Rollback:** Reverter as alterações de código e do `schema.sql`; como não há dados persistidos relevantes, não há necessidade de rollback de dados.

## Open Questions

Nenhuma questão em aberto — a regra de negócio, o comportamento esperado e o tratamento de erro já foram definidos pelo usuário na solicitação da mudança.
