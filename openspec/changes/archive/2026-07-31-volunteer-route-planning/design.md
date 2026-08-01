## Context

O projeto segue arquitetura hexagonal (`domain` → `application` → `infrastructure`/`presentation`), com uma única capability hoje (`cadastro-pessoa`). Esta mudança introduz a primeira capability que depende de serviços HTTP externos, então o desenho do port de geocodificação e o isolamento do domínio em relação a essa dependência são as decisões mais importantes.

Escopo confirmado com o usuário: geocodificação via **ViaCEP** (CEP → logradouro/bairro/cidade/UF) + **Nominatim/OpenStreetMap** (endereço completo → latitude/longitude), ambos gratuitos e sem chave de API.

## Goals / Non-Goals

**Goals:**
- Dividir uma lista de endereços em N grupos geograficamente coesos (por proximidade/bairro).
- Dentro de cada grupo, ordenar a rota do endereço mais distante do ponto de partida para o mais próximo.
- Manter a lógica de agrupamento/ordenação como domínio puro, testável sem rede.
- Entregar um MVP simples: uma chamada síncrona, sem persistência, sem cache, sem otimização de rota tipo TSP.

**Non-Goals:**
- Não calcular distância real de rota (ruas, trânsito) — usar distância em linha reta (Haversine) é suficiente para o MVP.
- Não persistir o plano de rotas gerado (sem nova tabela).
- Não otimizar a ordem interna do grupo além da heurística "mais longe → mais perto" (não é um TSP completo).
- Não lidar com geocodificação parcial/fallback manual — se um endereço não geocodifica, a requisição inteira falha.
- Não implementar cache de geocodificação nem retry/backoff sofisticado neste MVP.

## Decisions

### 1. Algoritmo de agrupamento: Sweep Algorithm (varredura angular)
Para cada endereço a visitar, calcular o ângulo polar (`atan2`) em relação ao ponto de partida (a "origem"). Ordenar todos os endereços por esse ângulo (0 a 360°) e dividir a lista ordenada em N grupos contíguos.

**Por quê**: é o algoritmo clássico e mais simples para clusterização geográfica em problemas de roteirização (VRP) sem exigir k-means, matriz de distância completa ou bibliotecas externas de otimização. Endereços com ângulo parecido em relação ao ponto de partida tendem a estar no mesmo bairro/direção, que é exatamente o critério de proximidade pedido. É determinístico e O(n log n).

**Alternativas consideradas**:
- *k-means com k=N grupos*: mais "correto" estatisticamente, mas não garante grupos de tamanho igual (requisito explícito do usuário: 20 endereços / 4 grupos = 5 cada) e adiciona complexidade (iteração, convergência) desnecessária para o MVP.
- *Agrupar por bairro retornado pelo ViaCEP*: simples, mas não garante N grupos nem tamanhos equilibrados (bairros têm tamanhos desiguais); pode gerar grupos vazios ou com dezenas de endereços.

### 2. Distribuição do tamanho dos grupos
Quando a quantidade de endereços não é múltipla da quantidade de grupos, distribuir o resto entre os primeiros grupos da varredura: os primeiros `(N mod K)` grupos recebem `⌈N/K⌉` endereços, os demais recebem `⌊N/K⌋`.

**Por quê**: mantém os grupos o mais equilibrado possível (diferença máxima de 1 endereço entre grupos) com uma regra simples e determinística.

### 3. Ordenação da rota dentro do grupo: distância decrescente ao ponto de partida
Dentro de cada grupo já formado, ordenar os endereços por distância Haversine ao ponto de partida em ordem **decrescente** (mais longe primeiro, mais perto por último).

**Por quê**: é exatamente a heurística de logística descrita pelo usuário, e evita implementar um solver de TSP (custo/complexidade incompatível com o escopo de MVP). É uma ordenação O(n log n) sem dependências extras.

**Trade-off aceito**: não é a rota mais curta possível dentro do grupo (isso exigiria TSP), apenas uma heurística de distância radial. Documentado como Non-Goal.

### 4. Port de geocodificação único, com dois adapters HTTP encadeados
Definir uma porta `GeocodificacaoEndereco` em `application/port` com um único método (CEP + número → endereço completo com latitude/longitude). A implementação em `infrastructure` encadeia duas chamadas HTTP: ViaCEP (CEP → logradouro/bairro/cidade/UF) e depois Nominatim (endereço completo formatado → lat/long).

**Por quê**: mantém o domínio e os casos de uso agnósticos de qual serviço externo é usado — segue o mesmo padrão de portas/adapters já usado para `PessoaRepository`. Encadear as duas chamadas dentro de um único adapter evita vazar detalhes de dois provedores para a camada de aplicação.

**Alternativas consideradas**:
- *Duas portas separadas (CEP→endereço e endereço→coordenadas)*: mais "puro" em termos de responsabilidade única, mas adiciona uma porta extra sem benefício prático no MVP, já que nenhum caso de uso precisa de um resultado intermediário isolado.

### 5. Falha de geocodificação aborta a requisição inteira
Se qualquer endereço (incluindo o ponto de partida) não puder ser geocodificado (CEP inválido no ViaCEP, ou Nominatim não retorna resultado), a operação inteira falha com uma exceção de domínio listando os endereços problemáticos — nenhum plano parcial é retornado.

**Por quê**: simplicidade pedida explicitamente pelo usuário ("MVP, foco na entrega"). Sucesso parcial exigiria decidir o que fazer com o endereço órfão (novo grupo? descartar? redistribuir?), o que é complexidade desnecessária agora.

### 6. Sem persistência do plano de rotas
O plano é calculado e retornado na resposta HTTP; nada é gravado no banco. Não há nova tabela em `schema.sql`.

**Por quê**: o pedido do usuário é sobre a funcionalidade de cálculo/otimização, não sobre histórico de planos. Persistência pode ser um change futuro se necessário.

### 7. Rate limit do Nominatim tratado de forma simples (síncrono, sequencial)
As chamadas ao Nominatim são feitas sequencialmente (uma por endereço, respeitando ~1 req/s) dentro do próprio use case, sem paralelismo nem fila.

**Por quê**: para o volume esperado de um MVP (dezenas de endereços por requisição, uso interno de uma igreja), uma chamada sequencial é simples e previsível. Paralelizar com controle de rate limit é complexidade prematura.

**Trade-off aceito**: uma requisição com muitos endereços (ex: 50+) pode demorar dezenas de segundos. Aceitável para o MVP; documentado como risco abaixo.

## Risks / Trade-offs

- **[Risco] Nominatim pode não encontrar coordenadas para um endereço formatado a partir do retorno do ViaCEP** (endereços rurais, CEPs genéricos de cidade) → Mitigação: erro claro na resposta indicando qual endereço falhou, para correção manual do dado de entrada; sem fallback automático no MVP.
- **[Risco] Latência alta em requisições com muitos endereços**, por causa do rate limit de 1 req/s do Nominatim (chamadas sequenciais) → Mitigação: aceito para o MVP (uso interno, poucas dezenas de endereços); documentar limite recomendado na doc da API. Otimização futura (paralelismo controlado, cache, provedor pago) fica fora de escopo.
- **[Risco] Dependência de disponibilidade de serviços externos gratuitos (ViaCEP/Nominatim) sem SLA** → Mitigação: aceito para o MVP; falha externa retorna erro claro (503/502) em vez de comportamento silencioso.
- **[Trade-off] Distância em linha reta (Haversine) não reflete a distância real de deslocamento a pé/carro** → aceito conforme Non-Goals; pode ser revisitado com uma API de rotas se necessário no futuro.

## Migration Plan

Não aplicável — capability nova, sem dado existente para migrar. Deploy é a adição do novo endpoint; nenhuma mudança em capabilities existentes, sem necessidade de rollback especial além de reverter o deploy.

## Open Questions

Nenhuma pendente — decisões de geocodificação confirmadas com o usuário antes deste documento.
