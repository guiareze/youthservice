## Why

O campo `idade` do cadastro de pessoa é hoje opcional. A regra de negócio mudou: a partir de agora, toda pessoa cadastrada deve informar sua idade, tornando esse dado obrigatório tanto na validação de entrada quanto na estrutura de persistência.

## What Changes

- **BREAKING**: o campo `idade` passa a ser obrigatório no cadastro de pessoa (`POST /pessoas`). Requisições sem esse campo serão rejeitadas.
- **BREAKING**: a coluna `idade` no banco de dados passa a ser `NOT NULL`. Como a aplicação é nova e local (ainda sem dados de produção), a estrutura da tabela `pessoas` será alterada diretamente, sem necessidade de migração de dados existentes.
- Adiciona validação de presença do campo `idade` na camada de apresentação (DTO) e reforça a mesma regra na camada de domínio (`Pessoa`), mantendo a defesa em profundidade já usada para nome e telefone.
- Mensagem de erro de validação segue o padrão já existente do projeto (`DADOS_INVALIDOS` / `PESSOA_INVALIDA`), informando que o campo idade passa a ser obrigatório.
- Atualização da documentação Markdown (`docs/api-pessoas.md`) e da anotação OpenAPI/Swagger do campo `idade`, refletindo que ele não é mais opcional.

## Capabilities

### New Capabilities

<!-- Nenhuma capability nova; esta mudança apenas ajusta uma regra de uma capability existente -->

### Modified Capabilities

- `cadastro-pessoa`: o requisito "Cadastrar pessoa com informações mínimas" muda para exigir o campo `idade` (deixa de ser opcional). O cenário "Cadastro sem informar idade" (que hoje aceita o cadastro) passa a ser um cenário de rejeição.

## Impact

- **Backend**: validação do DTO `CadastrarPessoaRequest` (`idade` passa a ter `@NotNull`), validação da entidade de domínio `Pessoa` (rejeita idade ausente, mantendo a regra de "diferente de zero")
- **Banco de Dados**: coluna `idade` da tabela `pessoas` passa de `INTEGER` (nullable) para `INTEGER NOT NULL`; ajuste no `schema.sql`
- **Mapeamento**: `PessoaResponse`/`PessoaEntity` deixam de precisar tratar `idade` como `null` possível vindo de um cadastro válido
- **Testes**: testes unitários e de integração existentes que cadastram pessoa "sem informar idade" precisam ser atualizados para refletir a rejeição (400) em vez do sucesso (201)
- **Documentação**: `docs/api-pessoas.md` e anotações OpenAPI do campo `idade` atualizadas para indicar campo obrigatório
