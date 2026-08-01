package br.com.guiareze.youthservice.infrastructure.geocoding;

import br.com.guiareze.youthservice.domain.exception.EnderecoNaoGeocodificadoException;
import br.com.guiareze.youthservice.domain.model.Endereco;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeocodificacaoEnderecoAdapterTest {

    @Mock
    private ViaCepClient viaCepClient;

    @Mock
    private NominatimClient nominatimClient;

    @Test
    void deveGeocodificarEnderecoComSucesso() {
        GeocodificacaoEnderecoAdapter adapter = new GeocodificacaoEnderecoAdapter(viaCepClient, nominatimClient);
        ViaCepResponse viaCepResponse = new ViaCepResponse("01310-100", "Avenida Paulista", "Bela Vista", "São Paulo", "SP", null);
        when(viaCepClient.consultar(anyString(), anyString())).thenReturn(viaCepResponse);
        when(nominatimClient.geocodificar(anyString(), anyString(), anyString())).thenReturn(new Coordenadas(-23.5613, -46.6565));

        Endereco endereco = adapter.geocodificar("01310-100", "100", "Maria");

        assertThat(endereco.getCep()).isEqualTo("01310100");
        assertThat(endereco.getLogradouro()).isEqualTo("Avenida Paulista");
        assertThat(endereco.getLatitude()).isEqualTo(-23.5613);
        assertThat(endereco.getLongitude()).isEqualTo(-46.6565);
    }

    @Test
    void devePropagarFalhaQuandoCepForInvalido() {
        GeocodificacaoEnderecoAdapter adapter = new GeocodificacaoEnderecoAdapter(viaCepClient, nominatimClient);
        when(viaCepClient.consultar(anyString(), anyString()))
                .thenThrow(new EnderecoNaoGeocodificadoException("Maria (CEP 00000000, nº 100)"));

        assertThatThrownBy(() -> adapter.geocodificar("00000000", "100", "Maria"))
                .isInstanceOf(EnderecoNaoGeocodificadoException.class);
    }

    @Test
    void deveTentarComBairroSemNumeroQuandoBuscaComNumeroFalhar() {
        GeocodificacaoEnderecoAdapter adapter = new GeocodificacaoEnderecoAdapter(viaCepClient, nominatimClient);
        ViaCepResponse viaCepResponse = new ViaCepResponse("04931-010", "Rua Roberto Selmi-Dei", "Jardim Santa Margarida", "São Paulo", "SP", null);
        when(viaCepClient.consultar(anyString(), anyString())).thenReturn(viaCepResponse);
        when(nominatimClient.geocodificar(contains("191"), anyString(), anyString()))
                .thenThrow(new EnderecoNaoGeocodificadoException("Abdon (CEP 04931-010, nº 191)"));
        when(nominatimClient.geocodificar(argThat(endereco -> !endereco.contains("191")), anyString(), anyString()))
                .thenReturn(new Coordenadas(-23.55, -46.70));

        Endereco endereco = adapter.geocodificar("04931-010", "191", "Abdon");

        assertThat(endereco.getLatitude()).isEqualTo(-23.55);
        assertThat(endereco.getLongitude()).isEqualTo(-46.70);
        verify(nominatimClient).geocodificar(contains("191"), anyString(), anyString());
        verify(nominatimClient).geocodificar(argThat(e -> e.contains("Jardim Santa Margarida") && !e.contains("191")), anyString(), anyString());
    }

    @Test
    void devePropagarFalhaQuandoEnderecoNaoForLocalizadoEmNenhumaTentativa() {
        GeocodificacaoEnderecoAdapter adapter = new GeocodificacaoEnderecoAdapter(viaCepClient, nominatimClient);
        ViaCepResponse viaCepResponse = new ViaCepResponse("01310-100", "Avenida Paulista", "Bela Vista", "São Paulo", "SP", null);
        when(viaCepClient.consultar(anyString(), anyString())).thenReturn(viaCepResponse);
        when(nominatimClient.geocodificar(anyString(), anyString(), anyString()))
                .thenThrow(new EnderecoNaoGeocodificadoException("Maria (CEP 01310100, nº 100)"));

        assertThatThrownBy(() -> adapter.geocodificar("01310-100", "100", "Maria"))
                .isInstanceOf(EnderecoNaoGeocodificadoException.class);
    }
}
