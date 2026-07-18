# cadastro-pessoa Specification

## Purpose

TBD - created by archiving change cadastro-pessoas. Update Purpose ahead of next change.

## Requirements

### Requirement: Cadastrar pessoa com informações mínimas
O sistema DEVE permitir o cadastro de uma pessoa informando nome, idade e telefone. O sistema DEVE validar os dados de entrada antes de persistir o cadastro.

#### Scenario: Cadastro realizado com sucesso
- **WHEN** o cliente envia uma requisição de cadastro com nome, idade e telefone válidos
- **THEN** o sistema cria o cadastro da pessoa e retorna os dados com um identificador único

#### Scenario: Cadastro com nome vazio ou ausente
- **WHEN** o cliente envia uma requisição de cadastro sem nome ou com nome vazio
- **THEN** o sistema rejeita o cadastro e retorna uma mensagem de erro indicando que o nome é obrigatório

#### Scenario: Cadastro sem informar idade
- **WHEN** o cliente envia uma requisição de cadastro sem informar o campo idade
- **THEN** o sistema rejeita o cadastro e retorna uma mensagem de erro indicando que a idade é obrigatória

#### Scenario: Cadastro com idade igual a zero
- **WHEN** o cliente envia uma requisição de cadastro informando idade igual a 0
- **THEN** o sistema rejeita o cadastro e retorna uma mensagem de erro indicando que a idade não pode ser zero

#### Scenario: Cadastro com telefone em formato inválido
- **WHEN** o cliente envia uma requisição de cadastro com telefone que não segue o formato internacional E.164 (ex: `+5511952526969`)
- **THEN** o sistema rejeita o cadastro e retorna uma mensagem de erro indicando o formato esperado

### Requirement: Consultar pessoa cadastrada por identificador
O sistema DEVE permitir consultar os dados de uma pessoa cadastrada através do seu identificador único.

#### Scenario: Consulta de pessoa existente
- **WHEN** o cliente solicita os dados de uma pessoa usando um identificador válido e existente
- **THEN** o sistema retorna os dados completos da pessoa (nome, idade, telefone)

#### Scenario: Consulta de pessoa inexistente
- **WHEN** o cliente solicita os dados de uma pessoa usando um identificador que não existe
- **THEN** o sistema retorna uma resposta indicando que a pessoa não foi encontrada

### Requirement: Listar pessoas cadastradas
O sistema DEVE permitir a listagem de todas as pessoas cadastradas, sem paginação.

#### Scenario: Listagem com pessoas cadastradas
- **WHEN** o cliente solicita a listagem de pessoas e existem pessoas cadastradas
- **THEN** o sistema retorna a lista completa de pessoas com seus respectivos dados

#### Scenario: Listagem sem pessoas cadastradas
- **WHEN** o cliente solicita a listagem de pessoas e não existem pessoas cadastradas
- **THEN** o sistema retorna uma lista vazia

### Requirement: Validar unicidade do nome cadastrado
O sistema DEVE impedir o cadastro de mais de uma pessoa com o mesmo nome.

#### Scenario: Tentativa de cadastro com nome já utilizado
- **WHEN** o cliente tenta cadastrar uma pessoa com um nome já associado a outra pessoa cadastrada
- **THEN** o sistema rejeita o cadastro e retorna uma mensagem de erro indicando que o nome já está em uso

### Requirement: Normalizar telefone armazenado
O sistema DEVE armazenar o telefone apenas com dígitos, removendo o prefixo `+` recebido na entrada.

#### Scenario: Armazenamento normalizado
- **WHEN** o cliente cadastra uma pessoa informando o telefone `+5511952526969`
- **THEN** o sistema armazena o telefone como `5511952526969` no banco de dados

#### Scenario: Retorno do telefone na consulta
- **WHEN** o cliente consulta uma pessoa cadastrada
- **THEN** o sistema retorna o telefone conforme armazenado (apenas dígitos)
