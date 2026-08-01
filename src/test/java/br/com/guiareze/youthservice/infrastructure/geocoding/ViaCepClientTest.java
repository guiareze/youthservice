package br.com.guiareze.youthservice.infrastructure.geocoding;

import br.com.guiareze.youthservice.domain.exception.EnderecoNaoGeocodificadoException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ViaCepClientTest {

    private static final String BASE_URL = "https://viacep.com.br";

    private ViaCepClient criarClient(MockRestServiceServer[] serverHolder) {
        RestClient.Builder builder = RestClient.builder();
        serverHolder[0] = MockRestServiceServer.bindTo(builder).build();
        return new ViaCepClient(builder, BASE_URL);
    }

    @Test
    void deveConsultarCepComSucesso() {
        MockRestServiceServer[] serverHolder = new MockRestServiceServer[1];
        ViaCepClient client = criarClient(serverHolder);
        serverHolder[0].expect(requestTo(BASE_URL + "/ws/01310100/json/"))
                .andRespond(withSuccess("""
                        {"cep":"01310-100","logradouro":"Avenida Paulista","bairro":"Bela Vista","localidade":"São Paulo","uf":"SP"}
                        """, MediaType.APPLICATION_JSON));

        ViaCepResponse resposta = client.consultar("01310-100", "descritor");

        assertThat(resposta.logradouro()).isEqualTo("Avenida Paulista");
        assertThat(resposta.bairro()).isEqualTo("Bela Vista");
        assertThat(resposta.localidade()).isEqualTo("São Paulo");
        assertThat(resposta.uf()).isEqualTo("SP");
    }

    @Test
    void deveRejeitarCepComFormatoInvalidoSemChamarServidor() {
        MockRestServiceServer[] serverHolder = new MockRestServiceServer[1];
        ViaCepClient client = criarClient(serverHolder);

        assertThatThrownBy(() -> client.consultar("123", "descritor"))
                .isInstanceOf(EnderecoNaoGeocodificadoException.class);

        serverHolder[0].verify();
    }

    @Test
    void deveRejeitarCepInexistente() {
        MockRestServiceServer[] serverHolder = new MockRestServiceServer[1];
        ViaCepClient client = criarClient(serverHolder);
        serverHolder[0].expect(requestTo(BASE_URL + "/ws/99999999/json/"))
                .andRespond(withSuccess("{\"erro\": true}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.consultar("99999999", "descritor"))
                .isInstanceOf(EnderecoNaoGeocodificadoException.class);
    }
}
