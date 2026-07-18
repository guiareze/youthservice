# API de Cadastro de Pessoas

Base path: `/pessoas`

Todas as requisições e respostas usam `Content-Type: application/json`.

## POST /pessoas

Cadastra uma nova pessoa.

### Requisição

```json
{
  "nome": "Maria da Silva",
  "idade": 30,
  "telefone": "+5511952526969"
}
```

Campos:
- `nome` (string, obrigatório): nome completo da pessoa. Não pode ser vazio. Deve ser único entre as pessoas cadastradas.
- `idade` (number, obrigatório): idade da pessoa. Não pode ser igual a `0`. Não há limite mínimo ou máximo.
- `telefone` (string, obrigatório): telefone no formato internacional E.164 (ex: `+5511952526969`). Armazenado internamente apenas com dígitos.

### Resposta - 201 Created

```json
{
  "id": "5f8d0d55-1c3e-4b8a-9f3e-2a1b3c4d5e6f",
  "nome": "Maria da Silva",
  "idade": 30,
  "telefone": "5511952526969",
  "criadoEm": "2026-07-18T14:00:00Z"
}
```

### Respostas de erro

| Status | Código | Quando ocorre |
|--------|--------|----------------|
| 400 | `DADOS_INVALIDOS` | Falha de validação de formato (ex: nome vazio, idade ausente, telefone fora do padrão E.164) |
| 400 | `PESSOA_INVALIDA` | Falha de regra de domínio (ex: idade ausente ou igual a zero) |
| 409 | `NOME_JA_CADASTRADO` | Já existe uma pessoa cadastrada com o mesmo nome |

## GET /pessoas/{id}

Consulta uma pessoa cadastrada pelo identificador.

### Resposta - 200 OK

```json
{
  "id": "5f8d0d55-1c3e-4b8a-9f3e-2a1b3c4d5e6f",
  "nome": "Maria da Silva",
  "idade": 30,
  "telefone": "5511952526969",
  "criadoEm": "2026-07-18T14:00:00Z"
}
```

### Resposta de erro

| Status | Código | Quando ocorre |
|--------|--------|----------------|
| 404 | `PESSOA_NAO_ENCONTRADA` | Não existe pessoa cadastrada com o identificador informado |

## GET /pessoas

Lista todas as pessoas cadastradas. Não há paginação.

### Resposta - 200 OK

```json
[
  {
    "id": "5f8d0d55-1c3e-4b8a-9f3e-2a1b3c4d5e6f",
    "nome": "Maria da Silva",
    "idade": 30,
    "telefone": "5511952526969",
    "criadoEm": "2026-07-18T14:00:00Z"
  }
]
```

Retorna uma lista vazia (`[]`) quando não há pessoas cadastradas.

## Formato de erro padrão

```json
{
  "erro": "NOME_JA_CADASTRADO",
  "mensagem": "Já existe uma pessoa cadastrada com o nome 'Maria da Silva'",
  "timestamp": "2026-07-18T14:00:00Z"
}
```

## Documentação interativa (OpenAPI/Swagger)

Além desta documentação, os endpoints estão documentados via OpenAPI, disponível em:
- Swagger UI: `/swagger-ui.html`
- Especificação OpenAPI (JSON): `/v3/api-docs`
