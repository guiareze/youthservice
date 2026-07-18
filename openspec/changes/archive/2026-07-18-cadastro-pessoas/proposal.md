## Why

O sistema precisa de uma funcionalidade básica para cadastrar pessoas com informações mínimas de identificação (nome, idade e telefone). Essa é a funcionalidade fundacional do projeto, servindo de base para futuras funcionalidades que dependam do registro de pessoas.

## What Changes

- Nova funcionalidade de cadastro de pessoas via API REST
- Validação de dados de entrada (nome, idade, telefone)
- Persistência de pessoas cadastradas em banco de dados H2
- Consulta de pessoas cadastradas (listagem e busca individual)
- Tratamento de erros e mensagens de feedback ao cliente da API
- Uso de DTOs implementados como `record` para representar requisições e respostas da API
- Conversão entre camadas (domínio, persistência, apresentação) realizada através de mappers dedicados (MapStruct ou padrão Builder)
- Documentação dos endpoints via OpenAPI/Swagger, além da documentação em Markdown

## Capabilities

### New Capabilities

- `cadastro-pessoa`: Fluxo completo de cadastro de pessoa, incluindo validação de dados de entrada, persistência e consulta das informações armazenadas.

### Modified Capabilities

<!-- Nenhuma capability existente sendo modificada, pois este é um projeto novo -->

## Impact

- **Backend**: Nova API REST para cadastro (`POST /pessoas`) e consulta (`GET /pessoas`, `GET /pessoas/{id}`)
- **Banco de Dados**: Nova tabela `pessoas` com campos de nome (único), idade, telefone e timestamps
- **Validação**: Implementação de regras de validação de entrada (formato de telefone em padrão E.164, idade diferente de zero quando informada, nome não vazio e único)
- **Arquitetura**: Estruturação inicial das camadas hexagonais (domínio, aplicação, infraestrutura, apresentação) que servirão de base para funcionalidades futuras
- **Mapeamento entre camadas**: Introdução de mappers (MapStruct ou Builder) para conversão entre entidade de domínio, entidade de persistência e DTOs
- **Documentação de API**: Inclusão de springdoc-openapi para geração automática de documentação OpenAPI/Swagger dos endpoints
