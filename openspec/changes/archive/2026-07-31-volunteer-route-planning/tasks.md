## 1. Domínio: modelos e validações

- [x] 1.1 Criar `domain/model/Endereco` (value object): cep, número, logradouro, bairro, cidade, UF, latitude, longitude — com validação de invariantes no construtor/factory.
- [x] 1.2 Criar `domain/model/PontoDeVisita`: nome do morador + `Endereco`.
- [x] 1.3 Criar `domain/model/GrupoDeRota`: número do grupo + lista ordenada de `PontoDeVisita`.
- [x] 1.4 Criar `domain/model/PlanoDeRotas`: lista de `GrupoDeRota`.
- [x] 1.5 Criar `domain/exception/QuantidadeGruposInvalidaException` (grupos <= 0 ou grupos > quantidade de endereços).
- [x] 1.6 Criar `domain/exception/EnderecoNaoGeocodificadoException` (CEP inválido ou endereço não localizável), carregando qual(is) endereço(s) falharam.

## 2. Domínio: algoritmo de agrupamento e ordenação (puro, sem dependência externa)

- [x] 2.1 Implementar cálculo de distância Haversine entre duas coordenadas.
- [x] 2.2 Implementar cálculo de ângulo polar de um ponto em relação ao ponto de partida (sweep).
- [x] 2.3 Implementar `domain/service/PlanejadorDeRotasService`: recebe ponto de partida + lista de `PontoDeVisita` já geocodificados + quantidade de grupos; ordena por ângulo, divide em N grupos contíguos com distribuição equilibrada (`⌈N/K⌉`/`⌊N/K⌋`), e ordena cada grupo por distância decrescente ao ponto de partida.
- [x] 2.4 Testes unitários do serviço: divisão exata, divisão com resto, grupo único, endereço único por grupo, ordenação decrescente por distância.

## 3. Aplicação: porta de geocodificação e caso de uso

- [x] 3.1 Criar `application/port/GeocodificacaoEnderecoPort`: método que recebe CEP + número (+ nome do morador, se aplicável) e retorna `Endereco` completo com coordenadas, lançando `EnderecoNaoGeocodificadoException` em caso de falha.
- [x] 3.2 Criar `application/usecase/PlanejarRotasUseCase`: valida quantidade de grupos e lista não vazia, geocodifica o ponto de partida e cada endereço a visitar (sequencialmente) via `GeocodificacaoEnderecoPort`, agrega falhas de geocodificação em uma única exceção se houver, e delega a divisão/ordenação ao `PlanejadorDeRotasService`.
- [x] 3.3 Testes unitários do use case com `GeocodificacaoEnderecoPort` mockado: caminho feliz, quantidade de grupos inválida, lista vazia, falha de geocodificação (um e múltiplos endereços).

## 4. Infraestrutura: adapters HTTP (ViaCEP + Nominatim)

- [x] 4.1 Criar cliente HTTP para ViaCEP (`infrastructure/geocoding/ViaCepClient`): CEP → logradouro/bairro/cidade/UF; tratar CEP inválido/inexistente (resposta `erro: true` ou 400).
- [x] 4.2 Criar cliente HTTP para Nominatim (`infrastructure/geocoding/NominatimClient`): endereço completo formatado → latitude/longitude; tratar resposta vazia (endereço não localizado) e respeitar rate limit (chamadas sequenciais, sem paralelismo).
- [x] 4.3 Criar `infrastructure/geocoding/GeocodificacaoEnderecoAdapter` implementando `GeocodificacaoEnderecoPort`, encadeando ViaCEP → Nominatim.
- [x] 4.4 Configurar beans HTTP client (`RestClient`) e URLs base (ViaCEP/Nominatim) via `application.properties`, incluindo `User-Agent` exigido pela política de uso do Nominatim. (`RestClient.Builder` não é autoconfigurado neste projeto — bean prototype adicionado em `infrastructure/config/GeocodingConfig`.)
- [x] 4.5 Testes do adapter com chamadas HTTP externas mockadas (ex: `MockRestServiceServer` ou WireMock): sucesso, CEP inválido, endereço não localizado.

## 5. Apresentação: DTOs, controller e tratamento de erro

- [x] 5.1 Criar DTOs de request (`record`): `PlanejarRotasRequest` (endereço de partida: CEP + número; lista de endereços a visitar: nome do morador + CEP + número; quantidade de grupos), com Bean Validation (`@NotBlank`, `@NotNull`, `@Positive`, `@NotEmpty`).
- [x] 5.2 Criar DTOs de response (`record`): `PlanoDeRotasResponse` com lista de grupos, cada grupo com número e lista ordenada de endereços de visita (nome do morador, endereço completo, posição na rota).
- [x] 5.3 Criar `infrastructure/mapper/PlanoDeRotasMapper` (MapStruct) para conversão domínio ↔ DTOs, seguindo o mesmo padrão de `PessoaMapper`.
- [x] 5.4 Criar `presentation/controller/RotaController` com endpoint `POST /rotas/planejamento`.
- [x] 5.5 Atender `GlobalExceptionHandler`: mapear `QuantidadeGruposInvalidaException` (400, novo código `QUANTIDADE_GRUPOS_INVALIDA`) e `EnderecoNaoGeocodificadoException` (422, novo código `ENDERECO_NAO_GEOCODIFICADO`) para o formato padrão `ErroResponse`.
- [x] 5.6 Testes de integração do controller (`@SpringBootTest`, chamando o controller diretamente como nos testes existentes), com a porta de geocodificação mockada: caminho feliz, cada cenário de erro do spec.

## 6. Documentação

- [x] 6.1 Criar `docs/api-planejamento-rotas.md` documentando o endpoint, request/response e códigos de erro, seguindo o padrão de `docs/api-pessoas.md`.
- [x] 6.2 Atualizar `README.md`: adicionar a nova capability à seção de capabilities e mencionar a dependência externa (ViaCEP/Nominatim).
