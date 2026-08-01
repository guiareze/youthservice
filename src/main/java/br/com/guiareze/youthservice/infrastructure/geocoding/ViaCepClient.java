package br.com.guiareze.youthservice.infrastructure.geocoding;

import br.com.guiareze.youthservice.domain.exception.EnderecoNaoGeocodificadoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ViaCepClient {

    private static final Logger log = LoggerFactory.getLogger(ViaCepClient.class);

    private final RestClient restClient;

    public ViaCepClient(RestClient.Builder restClientBuilder,
                         @Value("${geocodificacao.viacep.base-url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public ViaCepResponse consultar(String cep, String descritor) {
        String cepNormalizado = normalizar(cep);
        if (!cepNormalizado.matches("\\d{8}")) {
            throw new EnderecoNaoGeocodificadoException(descritor);
        }

        ViaCepResponse resposta;
        try {
            resposta = restClient.get()
                    .uri("/ws/{cep}/json/", cepNormalizado)
                    .retrieve()
                    .body(ViaCepResponse.class);
        } catch (RestClientException ex) {
            log.error("Falha ao consultar ViaCEP para '{}' (CEP normalizado: {})", descritor, cepNormalizado, ex);
            throw new EnderecoNaoGeocodificadoException(descritor, ex);
        }

        if (resposta == null || Boolean.TRUE.equals(resposta.erro())) {
            throw new EnderecoNaoGeocodificadoException(descritor);
        }
        return resposta;
    }

    private String normalizar(String cep) {
        return cep == null ? "" : cep.replaceAll("\\D", "");
    }
}
