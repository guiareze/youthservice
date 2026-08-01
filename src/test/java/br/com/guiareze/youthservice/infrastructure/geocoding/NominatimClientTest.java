package br.com.guiareze.youthservice.infrastructure.geocoding;

import br.com.guiareze.youthservice.domain.exception.EnderecoNaoGeocodificadoException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class NominatimClientTest {

    private static final String BASE_URL = "https://nominatim.openstreetmap.org";

    private NominatimClient criarClient(MockRestServiceServer[] serverHolder) {
        RestClient.Builder builder = RestClient.builder();
        serverHolder[0] = MockRestServiceServer.bindTo(builder).build();
        return new NominatimClient(builder, BASE_URL, "youthservice/1.0-test");
    }

    @Test
    void deveGeocodificarComSucesso() {
        MockRestServiceServer[] serverHolder = new MockRestServiceServer[1];
        NominatimClient client = criarClient(serverHolder);
        serverHolder[0].expect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"lat":"-23.5613","lon":"-46.6565","address":{"city":"São Paulo"}}]
                        """, MediaType.APPLICATION_JSON));

        Coordenadas coordenadas = client.geocodificar("Avenida Paulista, 100, Bela Vista, São Paulo, Brasil", "São Paulo", "descritor");

        assertThat(coordenadas.latitude()).isEqualTo(-23.5613);
        assertThat(coordenadas.longitude()).isEqualTo(-46.6565);
    }

    @Test
    void deveAceitarResultadoSemDetalheDeCidade() {
        MockRestServiceServer[] serverHolder = new MockRestServiceServer[1];
        NominatimClient client = criarClient(serverHolder);
        serverHolder[0].expect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"lat":"-23.5613","lon":"-46.6565"}]
                        """, MediaType.APPLICATION_JSON));

        Coordenadas coordenadas = client.geocodificar("Avenida Paulista, 100, São Paulo, Brasil", "São Paulo", "descritor");

        assertThat(coordenadas.latitude()).isEqualTo(-23.5613);
    }

    @Test
    void deveRejeitarResultadoDeCidadeDiferenteDaEsperada() {
        MockRestServiceServer[] serverHolder = new MockRestServiceServer[1];
        NominatimClient client = criarClient(serverHolder);
        serverHolder[0].expect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"lat":"-23.6072","lon":"-46.7689","address":{"city":"Taboão da Serra"}}]
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.geocodificar("Rua Líbia, 50, São Paulo, Brasil", "São Paulo", "descritor"))
                .isInstanceOf(EnderecoNaoGeocodificadoException.class);
    }

    @Test
    void deveRejeitarEnderecoNaoLocalizado() {
        MockRestServiceServer[] serverHolder = new MockRestServiceServer[1];
        NominatimClient client = criarClient(serverHolder);
        serverHolder[0].expect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.geocodificar("Endereço inexistente, 0, Nenhum lugar", "Nenhum lugar", "descritor"))
                .isInstanceOf(EnderecoNaoGeocodificadoException.class);
    }
}
