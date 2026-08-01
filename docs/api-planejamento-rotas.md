# API de Planejamento de Rotas

Base path: `/rotas`

Todas as requisições e respostas usam `Content-Type: application/json`.

## POST /rotas/planejamento

Divide uma lista de endereços a visitar entre grupos de voluntários por proximidade geográfica, e ordena a rota de cada grupo do endereço mais distante para o mais próximo do ponto de partida.

### Requisição

```json
{
  "pontoPartida": {
    "cep": "01310-100",
    "numero": "1000"
  },
  "enderecosVisitar": [
    { "nomeMorador": "Maria da Silva", "cep": "01310-100", "numero": "100" },
    { "nomeMorador": "João Souza", "cep": "01310-200", "numero": "200" }
  ],
  "quantidadeGrupos": 4
}
```

Campos:
- `pontoPartida` (objeto, obrigatório): endereço de onde os grupos partem.
  - `cep` (string, obrigatório): CEP no formato `00000-000` ou `00000000`.
  - `numero` (string, obrigatório): número do endereço de partida.
- `enderecosVisitar` (lista, obrigatório): endereços a visitar. Deve conter ao menos um item.
  - `nomeMorador` (string, obrigatório): nome do morador a ser visitado.
  - `cep` (string, obrigatório): CEP no formato `00000-000` ou `00000000`.
  - `numero` (string, obrigatório): número da residência.
- `quantidadeGrupos` (number, obrigatório): quantidade de grupos de voluntários. Deve ser maior que zero e não pode exceder a quantidade de endereços a visitar.

Cada endereço (ponto de partida e endereços a visitar) é geocodificado a partir do CEP e do número informados, usando ViaCEP (resolução do CEP para logradouro/bairro/cidade/UF) e Nominatim/OpenStreetMap (endereço completo para latitude/longitude).

### Resposta - 200 OK

```json
{
  "grupos": [
    {
      "numeroGrupo": 1,
      "enderecos": [
        {
          "nomeMorador": "João Souza",
          "enderecoCompleto": "Rua Exemplo, 200 - Bairro Exemplo, São Paulo/SP, CEP 01310-200",
          "ordemVisita": 1
        },
        {
          "nomeMorador": "Maria da Silva",
          "enderecoCompleto": "Avenida Paulista, 100 - Bela Vista, São Paulo/SP, CEP 01310-100",
          "ordemVisita": 2
        }
      ]
    }
  ]
}
```

Cada grupo contém sua lista de endereços já ordenada: `ordemVisita` começa em `1` (endereço mais distante do ponto de partida) e termina no endereço mais próximo do ponto de partida.

### Respostas de erro

| Status | Código | Quando ocorre |
|--------|--------|----------------|
| 400 | `DADOS_INVALIDOS` | Falha de validação de formato (ex: campo obrigatório ausente, CEP fora do padrão) |
| 400 | `QUANTIDADE_GRUPOS_INVALIDA` | Quantidade de grupos igual a zero/negativa, ou maior que a quantidade de endereços a visitar |
| 422 | `ENDERECO_NAO_GEOCODIFICADO` | Algum endereço (incluindo o ponto de partida) possui CEP inválido/inexistente ou não pôde ser localizado geograficamente; a mensagem lista todos os endereços que falharam |

## Formato de erro padrão

```json
{
  "erro": "ENDERECO_NAO_GEOCODIFICADO",
  "mensagem": "Não foi possível localizar geograficamente os seguintes endereços: Maria da Silva (CEP 00000-000, nº 100)",
  "timestamp": "2026-07-31T14:00:00Z"
}
```

## Documentação interativa (OpenAPI/Swagger)

Além desta documentação, o endpoint está documentado via OpenAPI, disponível em:
- Swagger UI: `/swagger-ui.html`
- Especificação OpenAPI (JSON): `/v3/api-docs`
