## MODIFIED Requirements

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
