## Why

Hoje não existe nenhuma forma de organizar visitas domiciliares por grupos de jovens voluntários: a distribuição de endereços e a ordem de visita são feitas manualmente, sem critério de proximidade. Isso gera rotas ineficientes (grupos cruzando a cidade) e divisão desigual de trabalho entre os grupos.

## What Changes

- Novo endpoint que recebe um endereço de partida, uma lista de endereços a visitar (nome do morador, CEP, número) e a quantidade de grupos de voluntários, e retorna um plano de rotas dividido por grupo.
- Cada endereço da lista é geocodificado (CEP + número → coordenadas) via ViaCEP (CEP → logradouro/bairro/cidade/UF) e Nominatim/OpenStreetMap (endereço → latitude/longitude).
- Os endereços são agrupados por proximidade geográfica (algoritmo *sweep*: ângulo polar em relação ao ponto de partida) em N grupos de tamanho o mais equilibrado possível.
- Dentro de cada grupo, a rota é ordenada do endereço mais distante do ponto de partida para o mais próximo (heurística "mais longe primeiro, mais perto por último").
- Falha de geocodificação de qualquer endereço (CEP inválido ou endereço não localizado) rejeita a requisição inteira com um erro indicando quais endereços falharam.

## Capabilities

### New Capabilities
- `planejamento-rotas`: recebe ponto de partida, lista de endereços a visitar e quantidade de grupos; geocodifica os endereços, agrupa por proximidade e ordena cada rota por distância decrescente ao ponto de partida.

### Modified Capabilities
_Nenhuma — `cadastro-pessoa` não é afetada; esta é uma capability nova e independente._

## Impact

- Novo pacote de domínio, casos de uso, portas e controller seguindo a arquitetura hexagonal já estabelecida (mesmo padrão de `cadastro-pessoa`).
- Nova dependência de infraestrutura: chamadas HTTP externas para ViaCEP e Nominatim (sem chave de API, mas com rate limit no Nominatim — 1 req/s).
- Nenhuma mudança em `Pessoa` ou nas tabelas existentes; o plano de rotas é calculado sob demanda e não é persistido no MVP (sem nova tabela no `schema.sql`).
