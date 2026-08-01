package br.com.guiareze.youthservice.presentation.controller;

import br.com.guiareze.youthservice.application.port.GeocodificacaoEnderecoPort;
import br.com.guiareze.youthservice.domain.exception.EnderecoNaoGeocodificadoException;
import br.com.guiareze.youthservice.domain.exception.QuantidadeGruposInvalidaException;
import br.com.guiareze.youthservice.domain.model.Endereco;
import br.com.guiareze.youthservice.presentation.dto.EnderecoPartidaRequest;
import br.com.guiareze.youthservice.presentation.dto.EnderecoVisitaRequest;
import br.com.guiareze.youthservice.presentation.dto.PlanejarRotasRequest;
import br.com.guiareze.youthservice.presentation.dto.PlanoDeRotasResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class RotaControllerIntegrationTest {

    @Autowired
    private RotaController controller;

    @MockitoBean
    private GeocodificacaoEnderecoPort geocodificacaoEnderecoPort;

    private static Endereco enderecoEm(double latitude, double longitude) {
        return Endereco.criar("01310100", "100", "Avenida Paulista", "Bela Vista", "São Paulo", "SP", latitude, longitude);
    }

    @Test
    void devePlanejarRotasComSucesso() {
        when(geocodificacaoEnderecoPort.geocodificar(eq("01310-100"), eq("1000"), anyString())).thenReturn(enderecoEm(0, 0));
        when(geocodificacaoEnderecoPort.geocodificar(eq("01310-100"), eq("100"), anyString())).thenReturn(enderecoEm(0.01, 0.01));
        when(geocodificacaoEnderecoPort.geocodificar(eq("01310-100"), eq("200"), anyString())).thenReturn(enderecoEm(0.02, 0.02));

        var request = new PlanejarRotasRequest(
                new EnderecoPartidaRequest("01310-100", "1000"),
                List.of(
                        new EnderecoVisitaRequest("Maria da Silva", "01310-100", "100"),
                        new EnderecoVisitaRequest("João Souza", "01310-100", "200")),
                1);

        PlanoDeRotasResponse response = controller.planejar(request);

        assertThat(response.grupos()).hasSize(1);
        assertThat(response.grupos().get(0).enderecos()).hasSize(2);
        assertThat(response.grupos().get(0).enderecos().get(0).ordemVisita()).isEqualTo(1);
    }

    @Test
    void deveRejeitarQuandoQuantidadeDeGruposExcederQuantidadeDeEnderecos() {
        var request = new PlanejarRotasRequest(
                new EnderecoPartidaRequest("01310-100", "1000"),
                List.of(new EnderecoVisitaRequest("Maria da Silva", "01310-100", "100")),
                2);

        assertThatThrownBy(() -> controller.planejar(request))
                .isInstanceOf(QuantidadeGruposInvalidaException.class);
    }

    @Test
    void deveRejeitarQuandoQuantidadeDeGruposForZero() {
        var request = new PlanejarRotasRequest(
                new EnderecoPartidaRequest("01310-100", "1000"),
                List.of(new EnderecoVisitaRequest("Maria da Silva", "01310-100", "100")),
                0);

        assertThatThrownBy(() -> controller.planejar(request))
                .isInstanceOf(QuantidadeGruposInvalidaException.class);
    }

    @Test
    void deveRejeitarQuandoEnderecoNaoForGeocodificado() {
        when(geocodificacaoEnderecoPort.geocodificar(eq("01310-100"), eq("1000"), anyString())).thenReturn(enderecoEm(0, 0));
        when(geocodificacaoEnderecoPort.geocodificar(eq("00000-000"), eq("100"), anyString()))
                .thenThrow(new EnderecoNaoGeocodificadoException("Maria da Silva (CEP 00000-000, nº 100)"));

        var request = new PlanejarRotasRequest(
                new EnderecoPartidaRequest("01310-100", "1000"),
                List.of(new EnderecoVisitaRequest("Maria da Silva", "00000-000", "100")),
                1);

        assertThatThrownBy(() -> controller.planejar(request))
                .isInstanceOf(EnderecoNaoGeocodificadoException.class);
    }
}
