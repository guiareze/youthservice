package br.com.guiareze.youthservice.infrastructure.geocoding;

import br.com.guiareze.youthservice.domain.exception.EnderecoNaoGeocodificadoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.text.Normalizer;
import java.util.Locale;

@Component
public class NominatimClient {

    private static final Logger log = LoggerFactory.getLogger(NominatimClient.class);

    private final RestClient restClient;

    public NominatimClient(RestClient.Builder restClientBuilder,
                            @Value("${geocodificacao.nominatim.base-url}") String baseUrl,
                            @Value("${geocodificacao.nominatim.user-agent}") String userAgent) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .build();
    }

    /**
     * @param cidadeEsperada usada para rejeitar resultados de homônimos em outra cidade (ex: uma rua com
     *                       nome genérico que existe em mais de um município) — sem essa validação, uma
     *                       busca sem bairro pode silenciosamente retornar coordenadas de outro lugar.
     */
    public Coordenadas geocodificar(String enderecoCompleto, String cidadeEsperada, String descritor) {
        NominatimResultado[] resultados;
        try {
            resultados = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/search")
                            .queryParam("format", "json")
                            .queryParam("limit", 1)
                            .queryParam("addressdetails", 1)
                            .queryParam("q", enderecoCompleto)
                            .build())
                    .retrieve()
                    .body(NominatimResultado[].class);
        } catch (RestClientException ex) {
            log.error("Falha ao consultar Nominatim para '{}' (endereço: {})", descritor, enderecoCompleto, ex);
            throw new EnderecoNaoGeocodificadoException(descritor, ex);
        }

        if (resultados == null || resultados.length == 0) {
            log.warn("Nominatim não retornou resultados para '{}' (endereço: {})", descritor, enderecoCompleto);
            throw new EnderecoNaoGeocodificadoException(descritor);
        }

        NominatimResultado resultado = resultados[0];
        validarCidade(resultado, cidadeEsperada, descritor, enderecoCompleto);

        try {
            return new Coordenadas(Double.parseDouble(resultado.lat()), Double.parseDouble(resultado.lon()));
        } catch (NumberFormatException ex) {
            log.error("Nominatim retornou coordenadas em formato inesperado para '{}': lat={}, lon={}",
                    descritor, resultado.lat(), resultado.lon(), ex);
            throw new EnderecoNaoGeocodificadoException(descritor, ex);
        }
    }

    private void validarCidade(NominatimResultado resultado, String cidadeEsperada, String descritor, String enderecoCompleto) {
        String cidadeResultado = resultado.address() != null ? resultado.address().cidade() : null;
        if (cidadeResultado == null) {
            return;
        }
        if (!normalizar(cidadeResultado).equals(normalizar(cidadeEsperada))) {
            log.warn("Nominatim retornou resultado em cidade diferente da esperada para '{}': esperado '{}', obtido '{}' (endereço buscado: {})",
                    descritor, cidadeEsperada, cidadeResultado, enderecoCompleto);
            throw new EnderecoNaoGeocodificadoException(descritor);
        }
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcentos.trim().toLowerCase(Locale.ROOT);
    }
}
