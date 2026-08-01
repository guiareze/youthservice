# planejamento-rotas Specification

## Purpose

Planejar rotas de visita domiciliar para grupos de voluntários: recebe um ponto de partida e uma lista de endereços a visitar, geocodifica cada endereço a partir de CEP e número, agrupa os endereços por proximidade geográfica em grupos de tamanho equilibrado e ordena a rota de cada grupo do endereço mais distante ao mais próximo do ponto de partida.

## Requirements

### Requirement: Planejar rotas de visita divididas por grupo
O sistema DEVE (MUST) permitir o planejamento de rotas de visita informando um endereço de partida (CEP e número), uma lista de endereços a visitar (nome do morador, CEP e número) e a quantidade de grupos de voluntários. O sistema DEVE (MUST) dividir os endereços a visitar entre os grupos e retornar, para cada grupo, a lista ordenada de endereços que compõem sua rota.

#### Scenario: Planejamento realizado com sucesso
- **WHEN** o cliente envia um ponto de partida válido, uma lista de endereços a visitar válidos e uma quantidade de grupos válida
- **THEN** o sistema retorna um plano de rotas com a quantidade de grupos solicitada, cada um contendo os endereços que lhe foram atribuídos em ordem de visita

#### Scenario: Quantidade de grupos maior que a quantidade de endereços
- **WHEN** o cliente solicita um planejamento em que a quantidade de grupos é maior que a quantidade de endereços a visitar
- **THEN** o sistema rejeita a requisição e retorna uma mensagem de erro indicando que a quantidade de grupos não pode exceder a quantidade de endereços

#### Scenario: Quantidade de grupos igual a zero ou negativa
- **WHEN** o cliente solicita um planejamento informando quantidade de grupos igual a zero ou negativa
- **THEN** o sistema rejeita a requisição e retorna uma mensagem de erro indicando que a quantidade de grupos deve ser maior que zero

#### Scenario: Lista de endereços a visitar vazia
- **WHEN** o cliente solicita um planejamento sem informar nenhum endereço a visitar
- **THEN** o sistema rejeita a requisição e retorna uma mensagem de erro indicando que ao menos um endereço a visitar é obrigatório

### Requirement: Dividir endereços em grupos por proximidade geográfica
O sistema DEVE (MUST) distribuir os endereços a visitar entre os grupos de forma que endereços próximos entre si (mesma região/direção a partir do ponto de partida) fiquem no mesmo grupo, e DEVE (MUST) manter os grupos com tamanhos o mais equilibrado possível.

#### Scenario: Divisão exata entre os grupos
- **WHEN** a quantidade de endereços a visitar é múltipla da quantidade de grupos (ex: 20 endereços para 4 grupos)
- **THEN** o sistema retorna todos os grupos com a mesma quantidade de endereços (ex: 5 endereços por grupo)

#### Scenario: Divisão não exata entre os grupos
- **WHEN** a quantidade de endereços a visitar não é múltipla da quantidade de grupos (ex: 22 endereços para 4 grupos)
- **THEN** o sistema distribui o excedente entre os grupos de forma que nenhum grupo tenha mais de um endereço a mais que outro

#### Scenario: Endereços do mesmo grupo pertencem à mesma região
- **WHEN** o sistema forma um grupo a partir de endereços geocodificados
- **THEN** os endereços desse grupo estão entre os mais próximos entre si em relação aos endereços de outros grupos, considerando a direção a partir do ponto de partida

### Requirement: Ordenar a rota de cada grupo do endereço mais distante ao mais próximo
Dentro de cada grupo, o sistema DEVE (MUST) ordenar os endereços de visita de forma que o primeiro endereço da rota seja o mais distante do ponto de partida e o último endereço seja o mais próximo do ponto de partida.

#### Scenario: Ordenação decrescente por distância ao ponto de partida
- **WHEN** um grupo é formado com múltiplos endereços a distâncias diferentes do ponto de partida
- **THEN** o sistema retorna a rota do grupo ordenada da maior para a menor distância ao ponto de partida

#### Scenario: Grupo com um único endereço
- **WHEN** um grupo é formado com apenas um endereço
- **THEN** o sistema retorna a rota desse grupo contendo apenas esse endereço, sem necessidade de ordenação adicional

### Requirement: Geocodificar endereços a partir de CEP e número
O sistema DEVE (MUST) resolver cada endereço informado (ponto de partida e endereços a visitar) para uma localização geográfica (latitude e longitude), utilizando o CEP e o número informados, antes de calcular proximidade e distância.

#### Scenario: Geocodificação bem-sucedida
- **WHEN** o cliente informa um CEP e número válidos e localizáveis
- **THEN** o sistema resolve o endereço completo (logradouro, bairro, cidade, UF) e suas coordenadas geográficas para uso no planejamento

#### Scenario: CEP inválido ou inexistente
- **WHEN** o cliente informa um CEP que não corresponde a um endereço válido
- **THEN** o sistema rejeita a requisição e retorna uma mensagem de erro indicando qual endereço possui CEP inválido

#### Scenario: Endereço não localizável geograficamente
- **WHEN** o CEP informado é válido, mas o endereço resultante não pode ser geocodificado em coordenadas
- **THEN** o sistema rejeita a requisição e retorna uma mensagem de erro indicando qual endereço não pôde ser localizado

#### Scenario: Falha em qualquer endereço aborta o planejamento inteiro
- **WHEN** ao menos um endereço da lista (incluindo o ponto de partida) falha na geocodificação
- **THEN** o sistema não retorna nenhum plano parcial e rejeita a requisição inteira, indicando todos os endereços que falharam
